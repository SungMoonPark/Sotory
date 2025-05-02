from mecab import MeCab

mecab = MeCab()

async def tokenize(text: str) -> list[str]:
    return mecab.morphs(text)
