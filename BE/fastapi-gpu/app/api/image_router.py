from fastapi import APIRouter, HTTPException, Request
from model.stable_diffusion_model import StableDiffusionModel
from utils.s3_uploader import upload_image_to_s3
import logging

router = APIRouter()
logger = logging.getLogger(__name__)

model = StableDiffusionModel()
model.load()

@router.post("/generate-image")
async def generate_image(request: Request):
    try:
        data = await request.json()
        prompt = data.get("prompt")
        lora_id = data.get("lora_id")
        summary = data.get("summary", "")  # ✅ 요약 받아옴

        if not prompt:
            raise HTTPException(status_code=400, detail="Missing prompt.")

        logger.info("🎨 이미지 생성 시작")
        image = model.generate_image(prompt=prompt, lora_id=lora_id)

        logger.info("☁️ S3 업로드 중...")
        imgSrc = upload_image_to_s3(image)

        logger.info("✅ 업로드 완료: %s", imgSrc)

        # ✅ prompt는 제거하고 summary + image_url만 응답
        return {
            "summary": summary,
            "imgSrc": imgSrc
        }

    except Exception as e:
        logger.exception("이미지 생성 실패")
        raise HTTPException(status_code=500, detail=str(e))
