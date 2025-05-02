package com.sotory.common.api.dto.request;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
public record CommonHeader(
        String apiName,
        String transmissionDate,
        String transmissionTime,
        String institutionCode,
        String fintechAppNo,
        String apiServiceCode,
        String institutionTransactionUniqueNo,
        String apiKey,
        String userKey
) {
    // 정적 메서드는 그대로 유지할 수 있습니다
    public static CommonHeader createHeader(String apiName, String apiKey, String userKey) {
        return forApiWithKeys(apiName, apiKey, userKey);
    }

    // 빌더 대신 새로운 생성자 스타일의 접근법을 사용합니다
    public static Builder defaultBuilder() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String tDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        log.info("요청하고 있는 현재 날짜 : {}", tDate);
        log.info("요청하고 있는 현재 시간 : {}", tTime);

        return new Builder()
                .transmissionDate(tDate)
                .transmissionTime(tTime)
                .institutionCode("00100")
                .fintechAppNo("001")
                .institutionTransactionUniqueNo(generateUniqueNo(tDate, tTime));
    }

    public static Builder forApi(String apiName) {
        return defaultBuilder()
                .apiName(apiName)
                .apiServiceCode(apiName);
    }

    public static CommonHeader forApiWithKeys(String apiName, String apiKey, String userKey) {
        return forApi(apiName)
                .apiKey(apiKey)
                .userKey(userKey)
                .build();
    }

    private static String generateUniqueNo(String tDate, String tTime) {
        Random random = new Random();
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        return tDate+tTime+randomSuffix;
    }

    // record와 함께 사용할 수 있는 빌더 패턴을 구현합니다
    public static class Builder {
        private String apiName;
        private String transmissionDate;
        private String transmissionTime;
        private String institutionCode;
        private String fintechAppNo;
        private String apiServiceCode;
        private String institutionTransactionUniqueNo;
        private String apiKey;
        private String userKey;

        public Builder apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder transmissionDate(String transmissionDate) {
            this.transmissionDate = transmissionDate;
            return this;
        }

        public Builder transmissionTime(String transmissionTime) {
            this.transmissionTime = transmissionTime;
            return this;
        }

        public Builder institutionCode(String institutionCode) {
            this.institutionCode = institutionCode;
            return this;
        }

        public Builder fintechAppNo(String fintechAppNo) {
            this.fintechAppNo = fintechAppNo;
            return this;
        }

        public Builder apiServiceCode(String apiServiceCode) {
            this.apiServiceCode = apiServiceCode;
            return this;
        }

        public Builder institutionTransactionUniqueNo(String institutionTransactionUniqueNo) {
            this.institutionTransactionUniqueNo = institutionTransactionUniqueNo;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public CommonHeader build() {
            return new CommonHeader(
                    apiName, transmissionDate, transmissionTime,
                    institutionCode, fintechAppNo, apiServiceCode,
                    institutionTransactionUniqueNo, apiKey, userKey
            );
        }
    }
}