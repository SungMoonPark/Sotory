from fastapi import APIRouter, HTTPException
from diary.schemas.diary import DiaryProcessRequest, DiaryProcessResponse
from diary.services.diary_service import diary_service
import logging

router = APIRouter(prefix="/api/diary", tags=["Diary Processing"])

@router.post("/summarize-diary", response_model=DiaryProcessResponse)
async def process_diary(request: DiaryProcessRequest):
    """
    일기 내용을 분석하고 이미지를 생성하는 API 
    """
    try:
        result = await diary_service.process_entries(
            entries=request.content
        )
        return result
    except Exception as e:
        logging.error(f"일기 처리 중 오류 발생: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"일기 처리 실패: {str(e)}")
