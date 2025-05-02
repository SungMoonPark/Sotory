from pydantic import BaseModel

# 요청 바디 모델
class CheckBadWordsRequest(BaseModel):
    text: str

# 응답 모델 (선택사항)
class CheckBadWordsResponse(BaseModel):
    has_bad_words: bool
    bad_words: list[str]
