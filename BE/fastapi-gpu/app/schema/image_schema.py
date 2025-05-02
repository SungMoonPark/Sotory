from pydantic import BaseModel, Field
from typing import Optional, Dict

class ImageGenerationRequest(BaseModel):
    prompt: str = Field(..., description="이미지 생성을 위한 프롬프트")
    negative_prompt: str = Field("", description="네거티브 프롬프트")
    height: int = Field(512, ge=64, le=1024)
    width: int = Field(512, ge=64, le=1024)
    num_inference_steps: int = Field(30, ge=1, le=150)
    guidance_scale: float = Field(7.5, ge=1.0, le=20.0)
    lora_id: Optional[str] = Field(None, description="사용할 LoRA 모델 ID")
    lora_weight: float = Field(0.7, ge=0.0, le=1.0)
    sampler: str = Field("euler", description="샘플러 (euler, ddim, dpm++)")
    seed: Optional[int] = Field(None, description="시드 (None이면 랜덤)")
    use_local_lora: Optional[bool] = Field(None, description="로컬 LoRA 사용 여부")

class ImageGenerationResponseWithTiming(BaseModel):
    image_id: str
    image_path: str
    timing: Dict
    generation_params: Dict