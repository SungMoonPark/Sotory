FROM nginx:stable-alpine

# conf.d 안의 NGINX 설정 복사
COPY conf.d/ /etc/nginx/

# (선택) SSL 인증서 복사
# COPY ssl/ /etc/nginx/ssl/

EXPOSE 80
EXPOSE 443
