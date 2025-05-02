# badword_loader.py
from nlp.utils.badwords_filter import ac

async def load_badwords_from_file(filepath: str = "nlp/data/badwords.txt"):
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            bad_words = [line.strip() for line in f if line.strip()]

        for word in bad_words:
            ac.add_word(word)
        ac.build()

        print(f"{len(bad_words)} bad words loaded from file.")

    except FileNotFoundError:
        print(f"[ERROR] Bad words file not found: {filepath}")