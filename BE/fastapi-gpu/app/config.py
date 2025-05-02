import os
from dotenv import load_dotenv
from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Dict, List

# .env 로드
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), "..", ".env"))

class Settings(BaseSettings):
    BASE_DIR: str = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    IMAGE_OUTPUT_DIR: str = os.path.join(BASE_DIR, "generated_images")
    
    AWS_ACCESS_KEY_ID: str
    AWS_SECRET_ACCESS_KEY: str
    AWS_REGION: str
    S3_BUCKET_NAME: str


    USE_LORA: bool = True  # 👉 LoRA 전체 사용 여부 (기본 꺼짐)
    USE_PEFT_BACKEND: bool = True
    HF_TOKEN: str

    SD_MODEL_ID: str = "runwayml/stable-diffusion-v1-5"
    

    DEFAULT_SAMPLER: str = "dpm++"
    DEFAULT_STEPS: int = 60
    DEFAULT_GUIDANCE_SCALE: float = 9.0
    DEFAULT_IMAGE_HEIGHT: int = 752
    DEFAULT_IMAGE_WIDTH: int = 512
    MAX_IMAGE_SIZE: int = 1024
    DEFAULT_LORA_WEIGHT: float = 1.0

    IMAGE_QUALITY_KEYWORDS: str = "vintage postcard, wish you were here, warm lighting, crisp lines, subtle shadows, detailed"
    
    DEFAULT_NEGATIVE_PROMPT: str = (
        "text, letters, writing, logo, watermark, product label, signage, brand, packaging, "
        "menu, advertisement, banner, commercial, barcode, QR code, prices, typographic, printed, stamp"
    )

    DIARY_PROMPT_KEYWORDS: List[str] = [
        "emotional moment", "daily life", "meaningful scene"
    ]

    # Hugging Face LoRA 모델 ID들
    @property
    def LORA_MAPPING(self) -> Dict[str, Dict[str, str]]:
        return {
            "default": {
                "id": "artificialguybr/storybookredmond-1-5-version-storybook-kids-lora-style-for-sd-1-5",
                "trigger": "KidsRedmAF, Kids Book"
            },
            "vintage_postcard": {
                "id": "calm-and-collected/wish-you-were-here",
                "trigger": "vintage postcard, wish you were here"
            },
            "kids": {
                "id": "artificialguybr/storybookredmond-1-5-version-storybook-kids-lora-style-for-sd-1-5",
                "trigger": "KidsRedmAF, Kids Book"
            },
            "oilcanvas": {
                "id": "Eunju2834/LoRA_oilcanvas_style",
                "trigger": "(Oil Painting:1.1), (Impressionism:1.2), (oil painting with brush strokes:1.2)"
            },
            "impasto": {
                "id": "ABDALLALSWAITI/impasto-style-lora",
                "trigger": "Impasto Style, thick brush strokes, textured"
            },
            "ghibli": {
                "id": "artificialguybr/studioghibli-redmond-1-5v-studio-ghibli-lora-for-liberteredmond-sd-1-5",
                "trigger": "Studio Ghibli, StdGBRedmAF"
            },
            "pixel": {
                "id": "artificialguybr/pixelartredmond-1-5v-pixel-art-loras-for-sd-1-5",
                "trigger": "Pixel Art, PixArFK"
            }
        }



    def get_lora_info(self, lora_type: str = "default") -> Dict[str, str]:
        if not self.USE_LORA:
            return {}  # LoRA 안쓸 경우 빈 dict 리턴
        return self.LORA_MAPPING.get(lora_type, self.LORA_MAPPING["default"])

    class Config:
        env_file = ".env"

settings = Settings()

# 디렉토리 생성 보장
os.makedirs(settings.IMAGE_OUTPUT_DIR, exist_ok=True)
