# app/common/s3_utils.py

import uuid
import boto3
from fastapi import UploadFile
from app.config import settings

s3_client = boto3.client(
    "s3",
    aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
    aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
    region_name=settings.AWS_REGION
)

def upload_file_to_s3(file: UploadFile, folder: str = "images") -> str:
    file_extension = file.filename.split('.')[-1]
    s3_key = f"{folder}/{uuid.uuid4()}.{file_extension}"

    s3_client.upload_fileobj(
        file.file,
        settings.S3_BUCKET_NAME,
        s3_key,
        ExtraArgs={"ContentType": file.content_type}
    )

    return f"https://{settings.S3_BUCKET_NAME}.s3.{settings.AWS_REGION}.amazonaws.com/{s3_key}"
