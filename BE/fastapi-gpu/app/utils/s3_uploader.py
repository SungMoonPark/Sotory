import boto3
import uuid
from io import BytesIO
from config import settings

def upload_image_to_s3(image, folder: str = "diary-images") -> str:
    # 이미지 → Bytes
    buffer = BytesIO()
    image.save(buffer, format="PNG")
    buffer.seek(0)

    # 파일명 설정
    filename = f"{folder}/{uuid.uuid4().hex}.png"

    # S3 클라이언트 생성
    s3 = boto3.client(
        "s3",
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        region_name=settings.AWS_REGION,
    )

    # 업로드
    s3.upload_fileobj(buffer, settings.S3_BUCKET_NAME, filename, ExtraArgs={"ContentType": "image/png"})

    # 공개 URL 반환
    image_url = f"https://{settings.S3_BUCKET_NAME}.s3.{settings.AWS_REGION}.amazonaws.com/{filename}"
    return image_url
