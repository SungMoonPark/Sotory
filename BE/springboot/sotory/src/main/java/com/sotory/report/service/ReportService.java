package com.sotory.report.service;

import com.sotory.budget.entity.Budget;
import com.sotory.budget.repository.BudgetRepository;
import com.sotory.report.dto.*;
import com.sotory.report.dto.request.*;
import com.sotory.report.dto.response.*;
import com.sotory.report.entity.WordCloud;
import com.sotory.report.repository.WordCloudRepository;
import com.sotory.paymentDiary.entity.PaymentDiary;
import com.sotory.paymentDiary.repository.PaymentDiaryRepository;
import com.sotory.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

        private final PaymentDiaryRepository paymentDiaryRepository;
        private final BudgetRepository budgetRepository;
        private final WordCloudRepository wordCloudRepository;

        @Value("${fastapi.base.url}")
        private String fastapiUrl;

        @Transactional(readOnly = true)
        public ReportResponse getMonthlyReport(UUID userId, String monthStr) {
                log.info("📌 [Report] 월간 소비 리포트 조회 시작 - userId: {}, month: {}", userId, monthStr);

                YearMonth yearMonth = YearMonth.parse(monthStr);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                // 1. 월별 전체 소비 데이터 불러오기
                List<PaymentDiary> diaries = paymentDiaryRepository.findAllByUser_UserIdAndTransactionDateBetweenAndIsDeletedFalse(userId, startDate, endDate);

                log.info("diaries.size() - {}", diaries.size());
                // ✅ 소비 데이터 없으면 "null 리포트" 반환
                if (diaries.isEmpty()) {
                        log.warn("📭 해당 월 소비 데이터 없음 - userId: {}, month: {}", userId, monthStr);


                        return ReportResponse.builder()
                                .nickname("")
                                .budgetAmount(-1)
                                .totalSpent(-1)
                                .remainingBudget(-1)
                                .monthlyLimit(-1)
                                .budgetPercentage(-1.0)
                                .categoryBreakdown(Collections.emptyList())
                                .frequentMerchants(Collections.emptyList())
                                .dailySpending(Collections.emptyList())
                                .keywordCloudUrl("")
                                .build();
                    }

                log.info("해달 월 소비 데이터 있음");

                String nickname = diaries.get(0).getUser().getNickname();


                String wordcloudImageUrl = createOrUpdateWordCloud(userId, monthStr, diaries);

                log.info("wordcloudImageUrl - {} " + wordcloudImageUrl);
                int totalSpent = diaries.stream()
                        .filter(d -> !d.isDeleted())
                        .mapToInt(PaymentDiary::getTransactionBalance)
                        .sum();

                List<CategoryBreakdown> categoryBreakdowns = diaries.stream()
                        .filter(d -> !d.isDeleted() && d.getCategoryName() != null)
                        .collect(Collectors.groupingBy(PaymentDiary::getCategoryName, Collectors.summingInt(PaymentDiary::getTransactionBalance)))
                        .entrySet().stream()
                        .map(e -> new CategoryBreakdown(e.getKey(), e.getValue(), (totalSpent == 0) ? 0.0 : Math.round(e.getValue() * 1000.0 / totalSpent) / 10.0))
                        .sorted(Comparator.comparing(CategoryBreakdown::getAmount).reversed())
                        .toList();

                List<FrequentMerchants> frequentMerchants = new ArrayList<>(diaries.stream()
                        .filter(d -> !d.isDeleted() && d.getMerchantName() != null)
                        .collect(Collectors.groupingBy(d -> List.of(d.getMerchantName(), d.getCategoryName())))
                        .entrySet().stream()
                        .map(e -> {
                                List<String> keys = e.getKey();
                                long visits = e.getValue().size();
                                int amount = e.getValue().stream().mapToInt(PaymentDiary::getTransactionBalance).sum();
                                return new FrequentMerchants(0, keys.get(0), keys.get(1), (int) visits, amount);
                        })
                        .sorted(
                                Comparator.comparing(FrequentMerchants::getVisits).reversed()
                                        .thenComparing(Comparator.comparing(FrequentMerchants::getAmount).reversed())
                        )
                        .limit(3)
                        .toList());

                for (int i = 0; i < frequentMerchants.size(); i++) {
                        FrequentMerchants merchant = frequentMerchants.get(i);
                        frequentMerchants.set(i, FrequentMerchants.builder()
                                .rank(i + 1)
                                .merchantName(merchant.getMerchantName())
                                .categoryName(merchant.getCategoryName())
                                .visits(merchant.getVisits())
                                .amount(merchant.getAmount())
                                .build());
                }

                List<DailySpending> dailySpendings = diaries.stream()
                        .filter(d -> !d.isDeleted())
                        .collect(Collectors.groupingBy(PaymentDiary::getTransactionDate, Collectors.summingInt(PaymentDiary::getTransactionBalance)))
                        .entrySet().stream()
                        .map(e -> new DailySpending(e.getKey().toString(), e.getValue()))
                        .sorted(Comparator.comparing(DailySpending::getDate))
                        .toList();

                int budgetAmount = budgetRepository.findByUserIdAndYearAndMonth(
                        userId,
                        yearMonth.getYear(),
                        yearMonth.getMonthValue()
                ).map(Budget::getBudget)
                .orElse(0);

                double budgetPercentage = (budgetAmount > 0)
                        ? Math.round((totalSpent * 1000.0 / budgetAmount)) / 10.0
                        : 0.0;

                ReportResponse report = ReportResponse.builder()
                        .nickname(nickname)
                        .budgetAmount(budgetAmount)
                        .totalSpent(totalSpent)
                        .remainingBudget(Math.max(0, budgetAmount - totalSpent))
                        .monthlyLimit(budgetAmount)
                        .budgetPercentage(budgetPercentage)
                        .categoryBreakdown(categoryBreakdowns)
                        .frequentMerchants(frequentMerchants)
                        .dailySpending(dailySpendings)
                        .keywordCloudUrl(wordcloudImageUrl)
                        .build();

                log.info("✅ 리포트 데이터 생성 완료");
                return report;
        }

        // 🖼️ 워드클라우드 생성 또는 업데이트
        @Transactional
        public String createOrUpdateWordCloud(UUID userId, String monthStr, List<PaymentDiary> diaries) {
                YearMonth yearMonth = YearMonth.parse(monthStr);
                String yearMonthStr = yearMonth.toString(); // "2025-04"

                int currentDiaryCount = diaries.size();

                if(currentDiaryCount==0){
                        return null;
                }

                Optional<WordCloud> optionalWordCloud = wordCloudRepository.findByUserIdAndYearMonth(userId, yearMonthStr);

                if (optionalWordCloud.isEmpty()) {
                log.info("📸 워드클라우드 없음, 새로 생성합니다.");

                String imageUrl = generateWordCloudImage(diaries);

                WordCloud newWordCloud = WordCloud.builder()
                        .user(User.builder().userId(userId).build())
                        .yearMonth(yearMonthStr)
                        .imageUrl(imageUrl)
                        .diaryCount(currentDiaryCount)
                        .isDeleted(false)
                        .build();

                wordCloudRepository.save(newWordCloud);
                return imageUrl;
                }

                WordCloud existing = optionalWordCloud.get();

                if (currentDiaryCount > existing.getDiaryCount()) {
                log.info("📈 일기 개수가 증가했습니다. 워드클라우드 갱신합니다.");

                String imageUrl = generateWordCloudImage(diaries);

                log.info("imageUrl - {}", imageUrl);

                wordCloudRepository.updateWordCloud(
                        existing.getWordcloudId(),
                        currentDiaryCount,
                        imageUrl
                );

                return imageUrl;
                }

                log.info("🛑 일기 개수 변동 없음, 워드클라우드 생성/갱신 생략");
                return existing.getImageUrl();
        }

        private String generateWordCloudImage(List<PaymentDiary> diaries) {
                WebClient webClient = WebClient.builder()
                        .baseUrl(fastapiUrl)
                        .build();

                if(diaries.isEmpty()) {
                        return null;
                }

                // ✅ PaymentDiary -> diary 텍스트만 추출
                List<String> texts = diaries.stream()
                        .map(PaymentDiary::getDiary)
                        .filter(diary -> diary != null && !diary.isBlank()) // null이나 빈 문자열 제거
                        .toList();

                // ✅ "year-month" 문자열 추출
                YearMonth yearMonth = YearMonth.from(diaries.get(0).getTransactionDate());
                String yearMonthStr = yearMonth.toString();  // "2025-02" 형식

                // ✅ 요청 객체 만들기
                WordCloudRequest request = new WordCloudRequest(
                        texts,
                        diaries.get(0).getUser().getUserId(), // 다이어리에 user 연결되어 있음
                        "#050F33",                           // 기본 배경색
                        20,                                  // 기본 최대 단어 수
                        yearMonthStr                         // 여기 추가됨!!
                );

                // ✅ FastAPI 호출
                WordCloudResponse response = webClient.post()
                        .uri("/api/wordcloud")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(WordCloudResponse.class)
                        .block();

                return Optional.ofNullable(response.getData())
                        .map(WordCloudResponse.WordCloudData::getImageUrl)
                        .orElse(null);
        }

}
