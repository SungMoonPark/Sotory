from pydantic_settings import BaseSettings
from pydantic import Field
from dotenv import load_dotenv
import os
from typing import Dict, List

load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), "..", ".env"))

class Settings(BaseSettings):
    # ✅ 공통 설정
    APP_NAME: str = "일기 분석 서비스"
    DEBUG: bool = True
    ENV: str = Field(..., alias="ENV")

    # ✅ DB 설정
    DB_USER: str = Field(..., alias="DB_USER")
    DB_PASSWORD: str = Field(..., alias="DB_PASSWORD")
    DB_HOST: str = Field("localhost", alias="DB_HOST")
    DB_PORT: str = Field("5432", alias="DB_PORT")
    DB_NAME: str = Field(..., alias="DB_NAME")

    # ✅ S3 설정
    AWS_ACCESS_KEY_ID: str
    AWS_SECRET_ACCESS_KEY: str
    AWS_REGION: str
    S3_BUCKET_NAME: str

    # ✅ GPU 서버와 통신을 위한 URL
    GPU_SERVER_URL: str = Field(..., alias="GPU_SERVER_URL") 


    # ✅ OpenAI 설정
    OPENAI_API_KEY: str
    GPT_MODEL: str = "gpt-4"

    # ✅ 이미지 프롬프트 스타일 설정
    IMAGE_QUALITY_KEYWORDS: str = "vintage postcard, wish you were here, warm lighting, crisp lines, subtle shadows, detailed"

    # ✅ 분석 결과 저장 디렉토리
    BASE_DIR: str = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    DIARY_OUTPUT_DIR: str = os.path.join(BASE_DIR, "diary_data")

    # ✅ LoRA 설정
    @property
    def LORA_MAPPING(self) -> Dict[str, Dict[str, str]]:
        return {
            "default": {
                "id": "artificialguybr/storybookredmond-1-5-version-storybook-kids-lora-style-for-sd-1-5",
                "trigger": "KidsRedmAF, Kids Book"
            },
            "oilcanvas": {
                "id": "Eunju234/LoRA_oilcanvas_style",
                "trigger": "(Oil Painting:1.1), (Impressionism:1.2), (oil painting with brush strokes:1.2)"
            },
            "vintage_postcard": {
                "id": "TheLastBen/WishYouWereHere",
                "trigger": "vintage postcard, wish you were here"
            },
            "impasto": {
                "id": "ABDALLALSWAITI/impasto-style-lora",
                "trigger": "Impasto Style, thick brush strokes, textured"
            },
            "ghibli": {
                "id": "nitrosocke/Ghibli-Diffusion",
                "trigger": "ghibli style"
            },
            "pixel": {
                "id": "artificialguybr/pixelartredmond-1-5v-pixel-art-loras-for-sd-1-5",
                "trigger": "Pixel Art, PixArFK"
            }
        }

    def get_lora_info(self, lora_type: str = "default") -> Dict[str, str]:
        return self.LORA_MAPPING.get(lora_type, self.LORA_MAPPING["default"])

    class Config:
        env_file = ".env"

settings = Settings()
print(f"✅ Loaded GPU_SERVER_URL: {settings.GPU_SERVER_URL}")
os.makedirs(settings.DIARY_OUTPUT_DIR, exist_ok=True)
