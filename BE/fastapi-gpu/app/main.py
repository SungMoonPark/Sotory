from fastapi import FastAPI
from api.image_router import router as image_router
import logging
logging.basicConfig(level=logging.DEBUG)

app = FastAPI(
    title="FastAPI GPU Image Generator",
    description="GPU 기반 이미지 생성 전용 백엔드 서버",
    version="1.0.0"
)

# 라우터 등록
app.include_router(image_router, tags=["Image Generation"])
