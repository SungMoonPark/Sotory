from datetime import datetime, timedelta
import requests
import json
from sqlalchemy import create_engine, text
import pandas as pd
import time

from airflow import DAG
from airflow.operators.python import PythonOperator

import os

# 환경변수가 없으면 오류 발생
# try:
#     DB_USERNAME = os.environ["DB_USERNAME"]
#     DB_PASSWORD = os.environ["DB_PASSWORD"]
#     DB_HOST = os.environ["DB_HOST"]
#     DB_PORT = os.environ["DB_PORT"]
#     DB_NAME = os.environ["DB_NAME"]
#     SUMMARY_API_URL = os.environ["SUMMARY_API_URL"] 
#     IMAGE_API_URL = os.environ["IMAGE_API_URL"]
    
#     # 연결 문자열 조합
#     DB_CONNECTION_STRING = f"postgresql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
# except KeyError as e:
#     raise Exception(f"필수 환경변수가 설정되지 않았습니다: {e}")
DB_USERNAME = "root"
DB_PASSWORD = "1234"
DB_HOST = "localhost"
DB_PORT = "5432"
DB_NAME = "sotory"
SUMMARY_API_URL = "https://j12a503.p.ssafy.io/api/diary/daily-summary"
IMAGE_API_URL = "https://j12a503.p.ssafy.io/api/summarize-diary"
DB_CONNECTION_STRING = f"postgresql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

default_args = {
    'owner': 'airflow',
    'depends_on_past': False, 
    'retries': 2, # 실패시, 재시도 횟수
    'retry_delay': timedelta(minutes=5), # 재시도 간격
}
   
def check_data_availability(**context):
    # 데이터 가져올 수 있는지 확인
    execution_date = context['execution_date']
    yesterday = (execution_date - timedelta(days=1)).strftime('%Y-%m-%d')

    engine = create_engine(DB_CONNECTION_STRING)

    # 해당 날짜의 일기 존재 여부 확인 (변경된 테이블명과 조건 적용)
    query = text("""
        SELECT COUNT(*) AS cnt FROM payment_diary
        WHERE DATE(created_at) = :yesterday AND "isDeleted" = FALSE
    """)

    with engine.connect() as conn:
        result = conn.execute(query, {"yesterday": yesterday}).fetchone()

    if result and result[0] > 0:
        print(f"{yesterday}에 {result[0]}개의 일기 항목이 있습니다.")
        return True
    else:
        print(f"{yesterday}에 일기 항목이 없습니다.")
        return False


def fetch_diary_data(**context):
    """전날 작성된 일기 항목을 DB에서 가져오기 (변경된 테이블과 필드 적용)"""
    execution_date = context['execution_date']
    yesterday = (execution_date - timedelta(days=1)).strftime('%Y-%m-%d')

    engine = create_engine(DB_CONNECTION_STRING)
    query = text("""
        SELECT payment_diary_id, user_id, diary 
        FROM payment_diary
        WHERE DATE(created_at) = :yesterday AND "isDeleted" = FALSE
    """)

    with engine.connect() as conn:
        diary_data = pd.read_sql(query, conn, params={"yesterday": yesterday})

    if diary_data.empty:
        print(f"{yesterday} 날짜에 일기 항목이 없습니다.")
        return []
    
    # user_id별로 데이터 그룹화
    grouped_data = diary_data.groupby('user_id')
    
    user_diaries = []
    for user_id, group in grouped_data:
                    user_diaries.append({
            'user_id': user_id,
            'diaries': group[['payment_diary_id', 'diary']].to_dict('records')
        })
    
    print(f"{len(diary_data)}개의 일기 항목을 {len(user_diaries)}명의 사용자로 그룹화했습니다.")
    return user_diaries


def generate_summaries(**context):
    """각 사용자의 일기를 요약 API로 전송하여 요약 및 프롬프트 생성"""
    execution_date = context['execution_date']
    yesterday = (execution_date - timedelta(days=1)).strftime('%Y-%m-%d')

    user_diaries = context['task_instance'].xcom_pull(task_ids='fetch_diary_data')
    
    if not user_diaries:
        print("요약할 일기 데이터가 없습니다.")
        return []
    
    results = []
    failures = 0
    
    for user_data in user_diaries:
        try:
            user_id = user_data['user_id']
            diaries = user_data['diaries']
            
            diary_texts = [entry['diary'] for entry in diaries]
            diary_ids = [entry['payment_diary_id'] for entry in diaries]
            
            # 요약 API 호출
            response = requests.post(
                SUMMARY_API_URL,
                json={
                    "user_id": user_id,
                    "diaries": diary_texts
                },
                timeout=180
            )
            
            if response.status_code == 200:
                api_result = response.json()
                
                # API 응답에서 요약 및 프롬프트 추출
                summary = api_result.get('summary', '')
                prompt = api_result.get('prompt', '')
                
                results.append({
                    'user_id': user_id,
                    'diary_ids': diary_ids,
                    'summary': summary,
                    'prompt': prompt,
                    'status': 'success'
                })
                print(f"사용자 ID {user_id}의 일기 요약 성공")
                
                # 요약 결과를 DB에 저장
                engine = create_engine(DB_CONNECTION_STRING)
                query = text("""
                    INSERT INTO diary_card (user_id, summary, created_at)
                    VALUES (:user_id, :summary, :yesterday)
                """)
                
                with engine.begin() as conn:
                    conn.execute(query, {
                        "user_id": user_id,
                        "summary": summary,
                        "yesterday": yesterday
                    })
                
            else:
                failures += 1
                print(f"사용자 ID {user_id}의 일기 요약 실패: {response.text}")
                results.append({
                    'user_id': user_id,
                    'status': 'failed',
                    'error': response.text
                })
                
        except Exception as e:
            failures += 1
            print(f"사용자 ID {user_data['user_id']} 처리 중 오류: {str(e)}")
            results.append({
                'user_id': user_data['user_id'],
                'status': 'error',
                'error': str(e)
            })
    
    print(f"총 {len(results)}명의 사용자 요약 처리 완료, {failures}명 실패")
    return [r for r in results if r['status'] == 'success']


def create_images(**context):
    """각 사용자의 요약으로 생성된 프롬프트로 이미지 생성"""
    summaries = context['task_instance'].xcom_pull(task_ids='generate_summaries')
    
    if not summaries:
        print("이미지 생성할 요약 데이터가 없습니다.")
        return []
    
    results = []
    failures = 0
    
    for summary_data in summaries:
        try:
            user_id = summary_data['user_id']
            prompt = summary_data['prompt']
            
            # API 호출 사이에 짧은 딜레이 추가하여 과부하 방지
            time.sleep(1)
            
            # 이미지 생성 API 호출
            response = requests.post(
                IMAGE_API_URL,
                json={
                    "prompt": prompt,
                    "user_id": user_id
                },
                timeout=180
            )
            
            if response.status_code == 200:
                image_data = response.json()
                imgSrc = image_data.get('imgSrc', '')
                
                results.append({
                    'user_id': user_id,
                    'imgSrc': imgSrc,
                    'status': 'success'
                })
                print(f"사용자 ID {user_id}의 이미지 생성 성공")
            else:
                failures += 1
                print(f"사용자 ID {user_id}의 이미지 생성 실패: {response.text}")
                results.append({
                    'user_id': user_id,
                    'status': 'failed',
                    'error': response.text
                })
                
        except Exception as e:
            failures += 1
            print(f"사용자 ID {summary_data['user_id']} 이미지 생성 중 오류: {str(e)}")
            results.append({
                'user_id': summary_data['user_id'],
                'status': 'error',
                'error': str(e)
            })
    
    print(f"총 {len(results)}명의 사용자 이미지 처리 완료, {failures}명 실패")
    return results


def save_image_urls(**context):
    """생성된 이미지 URL을 DB에 저장"""
    execution_date = context['execution_date']
    yesterday = (execution_date - timedelta(days=1)).strftime('%Y-%m-%d')

    image_results = context['task_instance'].xcom_pull(task_ids='create_images')
    
    if not image_results:
        print("저장할 이미지 결과가 없습니다.")
        return {'saved': 0}
    
    saved_count = 0
    
    engine = create_engine(DB_CONNECTION_STRING)
    
    for result in image_results:
        if result['status'] == 'success':
            try:
                query = text("""
                    UPDATE diary_card
                    SET imgSrc = :imgSrc
                    WHERE user_id = :user_id AND created_at = :yesterday
                """)
                
                with engine.begin() as conn:
                    conn.execute(query, {
                        "imgSrc": result['imgSrc'],
                        "user_id": result['user_id'],
                        "yesterday": yesterday
                    })
                    saved_count += 1
            except Exception as e:
                print(f"이미지 URL 저장 중 오류 (사용자 ID {result['user_id']}): {str(e)}")
    
    print(f"{saved_count}개의 이미지 URL을 DB에 저장했습니다.")
    return {'saved': saved_count}


def notify_completion(**context):
    """DAG 완료 알림 보내기"""
    execution_date = context['execution_date']
    yesterday = (execution_date - timedelta(days=1)).strftime('%Y-%m-%d')
    
    image_results = context['task_instance'].xcom_pull(task_ids='create_images')
    save_results = context['task_instance'].xcom_pull(task_ids='save_image_urls')
    
    total_count = len(image_results) if image_results else 0
    success_count = len([r for r in image_results if r['status'] == 'success']) if image_results else 0
    failure_count = total_count - success_count
    saved_count = save_results.get('saved', 0) if save_results else 0
    
    message = f"""
✅ {yesterday} 일기 요약 및 이미지 생성 완료
- 처리된 사용자: {total_count}명
- 성공: {success_count}명
- 실패: {failure_count}명
- DB 저장: {saved_count}개
    """
    
    print(message)
    # 여기에 실제 알림 로직 추가 (이메일, 슬랙 등)
    # 예: requests.post('슬랙웹훅URL', json={'text': message})


with DAG (
    'diary_card_creation_dag',
    default_args=default_args,
    description='일기 데이터 추출, 요약 및 이미지 생성 파이프라인',
    schedule='0 1 * * *',  # 매일 오전 1시에 실행
    start_date=datetime(2025, 4, 1),
    catchup=False  # 백필 여부
) as dag:
    
    # 데이터 가져오는 것이 가능한지 확인
    check_data = PythonOperator(
        task_id='check_data_availability',
        python_callable=check_data_availability,
    )

    # DB에서 일기 데이터 가져오기
    fetch_data = PythonOperator(
        task_id='fetch_diary_data',
        python_callable=fetch_diary_data,
    )

    # 일기 데이터 요약 및 프롬프트 생성
    generate_summary = PythonOperator(
        task_id='generate_summaries',
        python_callable=generate_summaries,
    )

    # 요약으로 생성된 프롬프트로 이미지 생성
    create_imgs = PythonOperator(
        task_id='create_images',
        python_callable=create_images,
    )

    # 생성된 이미지 URL DB에 저장
    save_urls = PythonOperator(
        task_id='save_image_urls',
        python_callable=save_image_urls,
    )

    # DAG 완료 알림
    notify = PythonOperator(
        task_id='notify_completion',
        python_callable=notify_completion,
    )
    
    # 작업 순서 정의
    check_data >> fetch_data >> generate_summary >> create_imgs >> save_urls >> notify