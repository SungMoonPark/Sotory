from pydantic import BaseModel
from typing import List, Optional, Dict, Any, Union

# 요청 스키마
class WordCloudRequest(BaseModel):
    texts: List[str]
    user_id: str
    background_color: Optional[str] = "#050F33"
    max_words: Optional[int] = 20
    year_month: str 

# 기본 워드클라우드 데이터 모델
class wordcloudBase(BaseModel):
    image_url: Optional[str]
    # word_frequencies: Optional[Dict[str, int]]

# 응답 스키마
class wordcloudResponse(wordcloudBase):
    class Config:
        from_attributes = True

# 공통 응답 스키마
class CommonResponse(BaseModel):
    code: str
    message: str
    data: Optional[wordcloudResponse] = None