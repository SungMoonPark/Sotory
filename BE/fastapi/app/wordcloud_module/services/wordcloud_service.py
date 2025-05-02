import matplotlib
matplotlib.use('Agg')

from konlpy.tag import Okt  # 한국어 형태소 분석기
import numpy as np
import matplotlib.pyplot as plt
from wordcloud import WordCloud
from collections import Counter
import io
import base64
from PIL import Image
import os
from typing import List, Optional, Dict, Any, Union
import logging
from ..utils.stopwords import KOREAN_STOPWORDS

# 로깅 기본 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class WordCloudService:

    def __init__(self, font_path: str = None):
        self.okt = Okt()

        if font_path is None:
            font_path = os.path.join(os.getcwd(), 'fonts', 'NanumGothic.ttf')

        if os.path.exists(font_path):
            self.font_path = font_path
            logger.info(f"✅ 폰트 파일 설정 완료: {self.font_path}")
        else:
            self.font_path = None
            logger.warning("🚨 폰트 파일을 찾지 못했습니다. 한글이 깨질 수 있습니다.")

    def extract_nouns(self, text: str) -> List[str]:
        stopwords = KOREAN_STOPWORDS
        logger.info(f"📝 텍스트 명사 추출 시작 - 입력 텍스트 길이: {len(text)}자")

        # nouns = self.okt.nouns(text)
        pos_tags = self.okt.pos(text, norm=True, stem=True)
        nouns = [word for word, pos in pos_tags if pos == 'Noun' and len(word) > 1 and word not in stopwords]

        filtered_nouns = [
            noun for noun in nouns
            if len(noun) > 1 and noun not in stopwords
        ]

        logger.info(f"🛠️ 명사 추출 완료 - 추출된 단어 수: {len(filtered_nouns)}개")
        return filtered_nouns

    def create_wordcloud(
        self,
        texts: List[str],
        max_words: int,
        background_color: str,
        width: int = 720,
        height: int = 400,
        output_path: Optional[str] = None
    ) -> Dict[str, Any]:

        logger.info("🎨 워드클라우드 생성 시작")
        try:
            all_text = ' '.join(texts)
            logger.info(f"📚 입력받은 텍스트 합치기 완료 - 총 길이: {len(all_text)}자")

            nouns = self.extract_nouns(all_text)
            logger.info(f"🔍 명사 추출 완료 - 추출된 단어 수: {len(nouns)}개")

            word_count = Counter(nouns)
            logger.info(f"📊 단어 빈도수 계산 완료 - 총 {len(word_count)}개 단어")

            top_words = dict(word_count.most_common(max_words))
            logger.info(f"🏆 상위 {len(top_words)}개 단어 선택 완료")

            if not top_words:
                logger.warning("⚠️ 분석할 명사가 부족합니다.")
                return {
                    'imgSrc': None,
                    'error': '분석할 명사가 충분하지 않습니다.'
                }

            wc = WordCloud(
                font_path=self.font_path,
                background_color=background_color,
                width=width,
                height=height,
                max_words=max_words,
                mask=None,
                color_func=lambda *args, **kwargs: "hsl(%d, 50%%, 75%%)" % np.random.randint(0, 360),
                relative_scaling=0.5,
                prefer_horizontal=0.9
            )
            wc.generate_from_frequencies(top_words)
            logger.info("🖼️ 워드클라우드 이미지 생성 완료")

            plt.figure(figsize=(10, 8))
            plt.imshow(wc, interpolation='bilinear')
            plt.axis('off')

            if output_path:
                os.makedirs(os.path.dirname(output_path), exist_ok=True)
                plt.savefig(output_path, bbox_inches='tight', pad_inches=0)
                logger.info(f"💾 워드클라우드 이미지 저장 완료 - 저장 경로: {output_path}")
                plt.close()

                return {
                    'imgSrc': output_path,
                }

        except Exception as e:
            logger.error(f"❌ 워드클라우드 생성 중 오류 발생: {str(e)}", exc_info=True)
            return {
                'error': f'워드클라우드 생성 중 오류 발생: {str(e)}'
            }
        finally:
            plt.close('all')
            logger.info("🧹 Matplotlib figure 메모리 해제 완료")

