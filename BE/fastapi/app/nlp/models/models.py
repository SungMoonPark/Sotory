from sqlalchemy import Column, Integer, String
from database import Base

class BadWord(Base):
    __tablename__ = "bad_words"

    id = Column(Integer, primary_key=True, index=True)
    word = Column(String(50), unique=True, nullable=False)
