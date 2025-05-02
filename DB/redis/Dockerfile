FROM redis:7.2

# redis.conf 파일을 컨테이너에 복사
COPY redis.conf /usr/local/etc/redis/redis.conf

# Redis 서버 실행 시 custom redis.conf를 사용하도록 설정
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
