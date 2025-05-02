import re
async def clean_text(text: str) -> str:
    return re.sub(r"\s+", "", text)  # 공백 제거