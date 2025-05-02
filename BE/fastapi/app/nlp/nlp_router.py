import os
from fastapi import APIRouter, HTTPException, Depends
from config import settings
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from nlp.service.service import check_text
from nlp.schemas.schemas import  CheckBadWordsRequest, CheckBadWordsResponse

router = APIRouter(prefix="/api/nlp", tags=["AI Models"])

@router.post("/check_badwords", response_model=CheckBadWordsResponse)
async def check_bad_words(
    request: CheckBadWordsRequest,
    db: AsyncSession = Depends(get_async_db)):
    """
    비속어 검사를 실시하는 api 

    입력:
        input_text(str): 입력 텍스트 
    
     출력: 
        결과 반환 (has_bad_words: bool, bad_words: list[str])
    """
    response = await check_text(
        text = request.text, 
        db = db)
    return response
