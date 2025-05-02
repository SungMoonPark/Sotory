import httpx
from typing import Dict, Any
from config import settings
import logging

logger = logging.getLogger(__name__)

class ImageGenerationProxy:
    def __init__(self):
        self.gpu_server_url = settings.GPU_SERVER_URL

    async def request_image_generation(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        logger.info(f"🎯 GPU 서버 URL: {self.gpu_server_url}")
        try:
            logger.info(f"📤 GPU 서버로 전송할 payload: {payload}")
            async with httpx.AsyncClient(timeout=30.0) as client:
                response = await client.post(f"{self.gpu_server_url}/generate-image", json=payload)
                response.raise_for_status()
                logger.info("🎨 이미지 생성 요청 성공")
                return response.json()
        except httpx.HTTPError as e:
            logger.error(f"🔥 GPU 서버 요청 실패: {str(e)}")
            return {"error": str(e)}

image_proxy = ImageGenerationProxy()
