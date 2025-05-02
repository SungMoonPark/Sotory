import os
import time
import uuid
import logging
from typing import Dict, Any, Optional, List

from gpt.models.gpt_model import gpt_model
from config import settings
from diary.services.image_generation_proxy import image_proxy

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class DiaryService:
    def __init__(self):
        self.diary_dir = settings.DIARY_OUTPUT_DIR
        os.makedirs(self.diary_dir, exist_ok=True)

    def recommend_lora_type(self, user_age: Optional[int], user_gender: Optional[str], analysis: Dict[str, Any]) -> str:
        """사용자 나이, 성별, 분석 결과 기반 LoRA 타입 추천"""
        if user_age is not None and user_age <= 10:
            return "kids"
        if "landscape" in analysis.get("topics", []):
            return "landscape"
        if "pixel" in analysis.get("style_suggestion", "").lower():
            return "pixel"

        # 🎯 기본값을 oilcanvas로 설정
        return "oilcanvas"

    async def process_entries(
        self,
        entries: List[str],
        user_age: Optional[int] = None,
        user_gender: Optional[str] = None,
    ) -> Dict[str, Any]:
        start_time = time.time()
        diary_content = "\n".join(entries)

        logger.info("✏️ 일기 분석 시작")
        analysis = gpt_model.analyze_diary(diary_content)
        logger.info("📌 일기 분석 완료")

        # ✅ 감성적인 일기체로 요약 리라이팅 추가
        original_summary = analysis.get("summary", "")
        rewritten_summary = gpt_model.rewrite_summary_in_diary_style(original_summary)
        analysis["summary"] = rewritten_summary

        # 🔍 LoRA 타입 자동 추천
        lora_type = self.recommend_lora_type(user_age, user_gender, analysis)
        logger.info(f"🤖 추천된 LoRA 타입: {lora_type}")

        # 🎨 GPT 프롬프트 생성 (style trigger 포함)
        image_prompt = gpt_model.generate_image_prompt(
            analysis=analysis,
            user_info={"age": user_age, "gender": user_gender},
            lora_type=lora_type
        )
        logger.info(f"🎯 생성된 프롬프트: {image_prompt}")

        # 🖼️ 이미지 생성 요청
        logger.info("🚀 GPU 서버로 이미지 생성 요청")
        image_result = await image_proxy.request_image_generation({
            "prompt": image_prompt,
            "lora_id": lora_type
        })

        total_time = round(time.time() - start_time, 2)

        return {
            "summary": analysis["summary"],
            "imgSrc": image_result.get("imgSrc", "https://sotorybucket.s3.ap-northeast-2.amazonaws.com/diary-images/sotory_logo.png"),
        }


diary_service = DiaryService()
