import os
import logging
import json
from typing import Dict, Any, List, Optional
from config import settings
import openai

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class GPTModel:
    def __init__(self):
        self.api_key = settings.OPENAI_API_KEY or os.getenv("OPENAI_API_KEY")
        if self.api_key:
            openai.api_key = self.api_key
            logger.info("✅ OpenAI API 키 설정 완료")
        else:
            logger.warning("⚠️ OpenAI API 키가 없습니다")

        self.model = settings.GPT_MODEL
        logger.info(f"🧠 사용할 GPT 모델: {self.model}")

    def call_openai_api(self, prompt: str, system_message: str = "You are a helpful assistant.",
                        temperature: float = 0.7) -> str:
        try:
            response = openai.ChatCompletion.create(
                model=self.model,
                messages=[
                    {"role": "system", "content": system_message},
                    {"role": "user", "content": prompt},
                ],
                temperature=temperature,
                max_tokens=800,
            )
            return response.choices[0].message["content"].strip()
        except Exception as e:
            logger.error(f"OpenAI API 호출 중 오류 발생 : {str(e)}")
            raise Exception(f"OpenAI API 호출 실패: {str(e)}")

    def truncate_prompt_to_clip_limit(self, base: str, style: str, max_tokens: int = 77) -> str:
        style_keywords = list(dict.fromkeys([s.strip() for s in style.split(",") if s.strip()]))
        style_text = ", ".join(style_keywords)
        base_words = base.split()

        for i in range(len(base_words), 0, -1):
            combined = " ".join(base_words[:i]) + ", " + style_text
            if len(combined.split()) <= max_tokens:
                return combined

        logger.warning("⚠️ 프롬프트가 너무 길어 스타일만 유지됩니다.")
        return style_text

    def analyze_diary(self, diary_content: str) -> Dict[str, Any]:
        try:
            response = openai.ChatCompletion.create(
                model=self.model,
                messages=[
                    {
                        "role": "system",
                        "content": """
                                    당신은 일기 분석 전문가입니다.
                                    ❗ 반드시 사용자의 원본 일기 내용만 분석하고, 그 외의 정보는 생성하지 마세요.
                                    ❗ 감정이나 사건을 과장하거나 덧붙이지 마세요.
                                    다음과 같은 JSON 형식으로 반환하세요:

                                    {
                                    "topics": [...],
                                    "emotions": [...],
                                    "key_elements": [...],
                                    "main_scene": "...",
                                    "visual_elements": [...],
                                    "time_of_day": "...",
                                    "weather": "...",
                                    "color_palette": [...],
                                    "style_suggestion": "...",
                                    "summary": "일기의 간결한 요약 (사용자의 표현 기반으로 1-2문장, 반드시 한국어로 작성)",
                                    "selected_sentence": "가장 인상 깊은 문장"
                                    }

                                    일기에 없는 정보는 절대 추론하지 말고 빈 문자열 또는 null로 반환하세요.
                                    """,
                    },
                    {"role": "user", "content": diary_content},
                ],
                temperature=0.3,
                max_tokens=1000,
            )

            content = response.choices[0].message["content"].strip()
            try:
                return json.loads(content)
            except json.JSONDecodeError:
                logger.warning("⚠️ JSON 파싱 실패, 텍스트 기반 파싱 시도")
                return self._extract_from_text(content)

        except Exception as e:
            logger.error(f"❌ 일기 분석 중 오류 발생: {str(e)}")
            return self._fallback_analysis(diary_content)

    def rewrite_summary_in_diary_style(self, summary: str) -> str:
        try:
            system_message = (
                "You're a diary writer. Rewrite the given sentence as if it were part of a personal diary entry. "
                "Use a warm, personal, first-person tone. Do not start with '저자는'."
            )
            prompt = f"Summary: {summary}\nRewrite in diary style (Korean):"

            return self.call_openai_api(prompt, system_message, temperature=0.7)
        except Exception as e:
            logger.warning(f"[⚠️] 일기체 요약 실패 : {e}")
            return summary

    def generate_image_prompt(
        self,
        analysis: Dict[str, Any],
        style: Optional[str] = None,
        user_info: Optional[Dict[str, Any]] = None,
        lora_type: Optional[str] = None
    ) -> str:
        try:
            summary = analysis.get("summary", "")
            key_elements = analysis.get("key_elements", [])
            visual_elements = analysis.get("visual_elements", [])

            combined_keywords = ", ".join((key_elements + visual_elements)[:5])
            prompt_base = f"Diary summary: {summary}\nKey visuals: {combined_keywords}\n"

            lora_info = settings.get_lora_info(lora_type or "default")
            trigger_keywords = lora_info.get("trigger", "")
            style = f"{trigger_keywords}, soft lighting, warm tones, gentle brush strokes, painterly, subtle colors"

            system_message = (
                "You are a prompt engineer for a visual storytelling AI.\n"
                "Generate one short (max 15 words) English prompt that focuses on a specific object or place from the diary.\n"
                "Do not mention people, emotions, or brand names.\n"
                "If a brand or store name appears (like Starbucks, CU, GS25, Baskin Robbins), replace it with a general visual description such as 'coffee cup on a counter', 'convenience store shelf', 'ice cream shop', etc.\n"
                "Do not include any text, logos, or signs."
            )

            user_prompt = prompt_base + "Write a single descriptive image prompt:"

            raw_prompt = self.call_openai_api(
                prompt=user_prompt,
                system_message=system_message,
                temperature=0.4
            )

            final_prompt = self.truncate_prompt_to_clip_limit(raw_prompt.strip(), style, max_tokens=77)
            return final_prompt
        
        except Exception as e:
            logger.error(f"[❌ GPT 프롬프트 생성 실패] {str(e)}")
            return self._fallback_prompt_generation(analysis, style)


    def _fallback_prompt_generation(self, analysis: Dict[str, Any], style: Optional[str] = None) -> str:
        summary = analysis.get("summary", "a meaningful moment from the diary")
        style = style or settings.IMAGE_QUALITY_KEYWORDS
        return self.truncate_prompt_to_clip_limit(summary, style)

    def _fallback_analysis(self, diary_content: str) -> Dict[str, Any]:
        return {
            "topics": [], "emotions": [], "key_elements": [], "main_scene": "",
            "visual_elements": [], "time_of_day": "", "weather": "",
            "color_palette": [], "style_suggestion": "",
            "summary": diary_content[:100] + "...", "selected_sentence": "",
        }

    def _extract_from_text(self, text: str) -> Dict[str, Any]:
        return self._fallback_analysis(text)

# 인스턴스 생성
gpt_model = GPTModel()
