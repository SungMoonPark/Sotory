from nlp.tokenizer import tokenize
from nlp.bad_words_loader import load_bad_words

bad_words = load_bad_words()

async def contains_bad_word(text: str) -> list[str]:
    tokens = tokenize(text)
    return [t for t in tokens if t in bad_words]
