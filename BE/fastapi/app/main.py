from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from middleware import setup_middleware
import logging

# ✅ 라우터들 import
from diary.diary_router import router as diary_router
from wordcloud_module.routers.wordcloud_router import router as wordcloud_router
from nlp.nlp_router import router as nlp_router  # ✅ 비속어 필터 라우터

# ✅ 금칙어 로더
from nlp.utils.badwords_loader import load_badwords_from_file

logging.basicConfig(level=logging.INFO)

app = FastAPI(
    title="일기 분석 API",
    description="일기를 분석하여 GPT 기반 프롬프트를 생성하는 API",
    version="1.0.0"
)

# ✅ 미들웨어 설정
setup_middleware(app)

# ✅ 정적 파일 서빙
app.mount("/static", StaticFiles(directory="."), name="static")

# ✅ 라우터 등록
app.include_router(diary_router)
app.include_router(wordcloud_router)
app.include_router(nlp_router)  # ✅ 비속어 API 포함

# ✅ 앱 시작 시 금칙어 로드
@app.on_event("startup")
async def startup_event():
    await load_badwords_from_file()
    print("✅ 금칙어 로딩 완료!")

# ✅ 테스트용 루트 엔드포인트
@app.get("/root", tags=["Root"])
async def root():
    return {
        "status": "online",
        "message": "일기 분석 API가 정상적으로 작동 중입니다.",
        "documentation": "/docs",
    }

@app.get("/")
async def root_alias():
    return {"message": "Welcome to FastAPI"}
