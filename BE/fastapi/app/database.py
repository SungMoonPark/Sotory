from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
import os
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

# 환경 변수에서 DB 정보 가져오기
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME")

# 동기식 데이터베이스 URL 설정
SQLALCHEMY_DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

# 비동기식 데이터베이스 URL 설정
ASYNC_SQLALCHEMY_DATABASE_URL = f"postgresql+asyncpg://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

# 동기식 엔진 및 세션
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 비동기식 엔진 및 세션
async_engine = create_async_engine(
    ASYNC_SQLALCHEMY_DATABASE_URL,
    echo=True,         # SQL 실행 로그 출력 (디버깅용)
    pool_size=10,      # 최대 연결 개수
    max_overflow=20    # 초과 연결 개수
)

AsyncSessionLocal = sessionmaker(
    bind=async_engine,
    class_=AsyncSession,
    expire_on_commit=False
)

# ORM 모델의 기본 클래스
Base = declarative_base()

# 동기식 DB 세션을 위한 의존성 함수
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# 비동기식 DB 세션을 위한 의존성 함수
async def get_async_db():
    async with AsyncSessionLocal() as session:
        yield session