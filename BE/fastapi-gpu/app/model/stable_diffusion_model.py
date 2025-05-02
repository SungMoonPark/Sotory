import os
import torch
import peft, diffusers, transformers
import logging
from typing import Optional
from diffusers import (
    StableDiffusionPipeline,
    DDIMScheduler,
    DPMSolverMultistepScheduler,
    EulerDiscreteScheduler,
)

from config import settings

logger = logging.getLogger(__name__)
logger.info(f"peft: {peft.__version__}, diffusers: {diffusers.__version__}, transformers: {transformers.__version__}")

class StableDiffusionModel:
    def __init__(self):
        self.model_id = settings.SD_MODEL_ID
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.pipeline = None
        self.current_lora = None
        self.lora_loaded = False

    def load(self):
        os.environ["PEFT_BACKEND"] = "TORCH"

        logger.info("🧠 Stable Diffusion 모델 로딩 중...")
        self.pipeline = StableDiffusionPipeline.from_pretrained(
            self.model_id,
            torch_dtype=torch.float16 if torch.cuda.is_available() else torch.float32
        )
        self.pipeline = self.pipeline.to(self.device)
        logger.info("✅ 모델 로딩 완료")
        return self

    def apply_lora(self, lora_id: Optional[str] = None, weight: float = 0.7):
        logger.warning(f"🛠️ apply_lora 호출됨 - lora_id: {lora_id}, weight: {weight}")

        if not settings.USE_LORA:
            logger.info("🚫 LoRA 비활성화 상태 - 기본 모델만 사용")
            return

        if not lora_id:
            logger.warning("⚠️ LoRA ID가 지정되지 않음 - LoRA 적용 생략")
            return

        try:
            if self.lora_loaded and self.current_lora == lora_id:
                logger.info("♻️ 이미 적용된 LoRA 재사용 중...")
                return

            lora_info = settings.get_lora_info(lora_id)
            model_id = lora_info.get("id")
            if not model_id:
                raise ValueError(f"❌ LoRA ID에 해당하는 모델 정보를 찾을 수 없습니다: '{lora_id}'")

            logger.info(f"📥 LoRA 요청 ID: {lora_id}")
            logger.info(f"🔗 Hugging Face에서 적용할 모델 ID: {model_id}")

            if settings.USE_PEFT_BACKEND:
                try:
                    logger.info(f"🌐 Hugging Face에서 PEFT LoRA 로드 시도 중: {model_id}")

                    # 🔥 핵심: pipeline 객체에 직접 LoRA 적용
                    self.pipeline.load_lora_weights(
                        model_id,
                        token=settings.HF_TOKEN
                    )
                    self.pipeline.fuse_lora(lora_scale=weight)

                    logger.info(f"✅ LoRA 적용 완료: {model_id}")

                    self.lora_loaded = True
                    self.current_lora = lora_id

                    return self

                except Exception as e:
                    logger.error(f"❌ PEFT LoRA 적용 실패: {str(e)}")
                    raise e

            else:
                raise NotImplementedError("❌ PEFT backend는 현재 적용 로직이 구현되지 않았습니다.")

        except Exception as e:
            logger.error(f"❌ LoRA 적용 실패: {str(e)}")



    def optimize_prompt(self, prompt: str) -> str:
        quality_terms = settings.IMAGE_QUALITY_KEYWORDS
        max_token_len = 77

        style_tokens = quality_terms.split(",")
        prompt_tokens = prompt.strip().split()

        max_prompt_len = max_token_len - len(style_tokens)
        if len(prompt_tokens) > max_prompt_len:
            prompt_tokens = prompt_tokens[:max_prompt_len]

        final_prompt = " ".join(prompt_tokens) + ", " + ", ".join(style_tokens)
        return final_prompt

    def generate_image(
        self,
        prompt: str,
        negative_prompt: str = "",
        height: int = None,
        width: int = None,
        num_inference_steps: int = None,
        guidance_scale: float = None,
        lora_id: Optional[str] = None,
        lora_weight: float = None,
        sampler: str = "euler",
        seed: Optional[int] = None,
        use_local_lora: bool = False
    ):
        # ✅ 기본값 설정 (config.py에서 가져옴)
        height = height or settings.DEFAULT_IMAGE_HEIGHT
        width = width or settings.DEFAULT_IMAGE_WIDTH
        num_inference_steps = num_inference_steps or settings.DEFAULT_STEPS
        guidance_scale = guidance_scale or settings.DEFAULT_GUIDANCE_SCALE
        lora_weight = lora_weight or settings.DEFAULT_LORA_WEIGHT

        if not self.pipeline:
            self.load()

        # ✅ 샘플러 설정
        if sampler == "ddim":
            self.pipeline.scheduler = DDIMScheduler.from_config(self.pipeline.scheduler.config)
        elif sampler == "dpm++":
            self.pipeline.scheduler = DPMSolverMultistepScheduler.from_config(self.pipeline.scheduler.config)
        elif sampler == "euler":
            self.pipeline.scheduler = EulerDiscreteScheduler.from_config(self.pipeline.scheduler.config)

        # ✅ LoRA 적용
        if lora_id:
            self.apply_lora(lora_id, lora_weight)

        # ✅ 프롬프트 생성
        prompt = self.optimize_prompt(prompt)
        generator = torch.Generator(device=self.device).manual_seed(seed) if seed else None

        with torch.autocast(self.device):
            result = self.pipeline(
                prompt=prompt,
                negative_prompt=negative_prompt,
                height=height,
                width=width,
                num_inference_steps=num_inference_steps,
                guidance_scale=guidance_scale,
                generator=generator,
            )

        return result.images[0]
