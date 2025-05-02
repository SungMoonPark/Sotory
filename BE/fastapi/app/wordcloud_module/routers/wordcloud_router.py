import boto3
import os
import uuid
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from datetime import datetime
import time
import logging

# 상대 경로 대신 절대 경로 사용
from wordcloud_module.services.wordcloud_service import WordCloudService
from wordcloud_module.schemas.wordcloud_schema import CommonResponse, WordCloudRequest, wordcloudResponse
from database import get_db  # app 루트에서 database 가져오기

# 로거 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 환경 변수로 배포 환경 확인
IS_PRODUCTION = os.getenv('ENV', 'development') == 'production'

# 배포 환경인 경우에만 S3 클라이언트 초기화
s3_client = None
if IS_PRODUCTION:
    s3_client = boto3.client('s3', 
        aws_access_key_id=os.environ.get('AWS_ACCESS_KEY_ID'), 
        aws_secret_access_key=os.environ.get('AWS_SECRET_ACCESS_KEY'),
        region_name=os.environ.get('AWS_REGION')
    )
    print(f"Production Mode: {IS_PRODUCTION}")
    print(f"Bucket Name: {os.environ.get('S3_BUCKET_NAME')}")
    print(f"AWS Region: {os.environ.get('AWS_REGION')}")

router = APIRouter(prefix="/api/wordcloud", tags=["wordcloud"])

@router.post("", response_model=CommonResponse)
def generate_wordcloud(
    request: WordCloudRequest,
    db: Session = Depends(get_db)
):
    start_time = time.time()
    logger.info("📥 워드클라우드 생성 요청 수신 - user_id: %s, year_month: %s", request.user_id, request.year_month)

    try:
        wordcloud_service = WordCloudService()
        logger.info("🛠️ WordCloudService 인스턴스 생성 완료")
        
        os.makedirs('wordclouds', exist_ok=True)
        logger.info("📁 'wordclouds' 폴더 준비 완료")
        
        # ✅ 고유한 파일명 생성 (year_month 사용)
        filename = f"wordcloud_{request.user_id}_{request.year_month}.png"
        logger.info("📝 생성할 파일명: %s", filename)

        if IS_PRODUCTION:
            local_output_path = f"wordclouds/{filename}"
            logger.info("🌎 운영 환경 - 로컬 임시 파일 경로 설정: %s", local_output_path)

            result = wordcloud_service.create_wordcloud(
                texts=request.texts,
                background_color=request.background_color,
                max_words=request.max_words,
                output_path=local_output_path
            )
            logger.info("🎨 워드클라우드 생성 완료")

            if 'error' in result:
                logger.error("❗ 워드클라우드 생성 중 오류 발생: %s", result['error'])
                return CommonResponse(code="400", message=result['error'])

            BUCKET_NAME = os.environ.get('S3_BUCKET_NAME')
            S3_FOLDER = 'wordclouds/'
            s3_key = f"{S3_FOLDER}{filename}"
            s3_client.upload_file(
                local_output_path,
                BUCKET_NAME,
                s3_key,
                ExtraArgs={'ContentType': 'image/png'}
            )
            logger.info("📦 S3 업로드 성공 - s3://%s/%s", BUCKET_NAME, s3_key)

            os.remove(local_output_path)
            logger.info("🗑️ 로컬 임시 파일 삭제 완료")

            image_url = f"https://{BUCKET_NAME}.s3.amazonaws.com/{s3_key}"

        else:
            output_path = f"wordclouds/{filename}"
            logger.info("🖥️ 개발 환경 - 로컬 파일 저장 경로: %s", output_path)

            result = wordcloud_service.create_wordcloud(
                texts=request.texts,
                background_color=request.background_color,
                max_words=request.max_words,
                output_path=output_path
            )
            logger.info("🎨 워드클라우드 생성 완료 (개발환경)")

            if 'error' in result:
                logger.error("❗ 워드클라우드 생성 중 오류 발생: %s", result['error'])
                return CommonResponse(code="400", message=result['error'])

            image_url = f"/static/wordclouds/{filename}"

        end_time = time.time()
        logger.info(f"✅ API generate_wordcloud 호출 완료 - 소요 시간: {end_time - start_time:.2f}초")
        
        return CommonResponse(
            code="200",
            message="워드클라우드가 성공적으로 생성되었습니다.",
            data=wordcloudResponse(
                image_url=image_url
            )
        )

    except Exception as e:
        end_time = time.time()
        logger.error(f"❌ API generate_wordcloud 호출 중 예외 발생 - 소요 시간: {end_time - start_time:.2f}초")
        logger.error(f"❗ 예외 내용: {str(e)}", exc_info=True)
        return CommonResponse(
            code="500",
            message=f"워드클라우드 생성 중 오류가 발생했습니다: {str(e)}"
        )

