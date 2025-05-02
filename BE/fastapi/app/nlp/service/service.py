# nlp/service/service.py
from nlp.schemas.schemas import CheckBadWordsResponse
from nlp.utils.badwords_loader import ac  # 아호-코라식 트라이 가져오기
from sqlalchemy.ext.asyncio import AsyncSession

async def check_text(
    text: str,
    db: AsyncSession) -> CheckBadWordsResponse:

    # 텍스트를 소문자로 바꿔서 검사하면 정확도 높아짐
    text = text.lower()

    # 아호-코라식으로 검색
    matches = ac.search(text)

    # 중복 제거해서 bad words 리스트로 만들기
    found_words = list({word for pos, word in matches})

    # 결과 리턴
    return CheckBadWordsResponse(
        has_bad_words=len(found_words) > 0,
        bad_words=found_words
    )
