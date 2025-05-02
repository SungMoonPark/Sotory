FROM postgres:15

# 초기화 SQL 복사 (최초 실행 시 자동 실행)
COPY initdb /docker-entrypoint-initdb.d/
COPY ./postgresql.conf /etc/postgresql/postgresql.conf
COPY ./pg_hba.conf /etc/postgresql/pg_hba.conf

CMD ["postgres", "-c", "config_file=/etc/postgresql/postgresql.conf"]