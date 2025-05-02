from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any
from enum import Enum

class LoRAType(str, Enum):
    DEFAULT = "default"
    LANDSCAPE = "landscape"
    PIXEL = "pixel"
    GHIBLI = "ghibli"
    KIDS = "kids"

class DiaryProcessRequest(BaseModel):
    """일기 처리 요청 스키마"""
    content: List[str]

class DiaryAnalysis(BaseModel):
    """일기 분석 결과 스키마 (시각화에 최적화된 버전)"""
    topics: List[str] = Field(..., description="주요 주제")
    emotions: List[str] = Field(..., description="감정 상태")
    key_elements: List[str] = Field(..., description="중요 요소")
    main_scene: Optional[str] = Field(None, description="핵심 장면")
    visual_elements: List[str] = Field(..., description="시각적 요소")
    time_of_day: Optional[str] = Field(None, description="시간대")
    weather: Optional[str] = Field(None, description="날씨/계절")
    color_palette: Optional[List[str]] = Field(None, description="색상 팔레트")
    style_suggestion: Optional[str] = Field(None, description="제안 스타일")
    summary: str = Field(..., description="요약")

class DiaryProcessResponse(BaseModel):
    """일기 처리 응답 (단일 이미지)"""
    summary: str = Field(..., description="GPT 기반 일기 요약 결과")
    imgSrc: str = Field(..., description="S3에 업로드된 생성형 이미지 URL")