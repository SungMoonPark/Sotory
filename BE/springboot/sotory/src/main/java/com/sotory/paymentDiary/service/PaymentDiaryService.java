package com.sotory.paymentDiary.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotory.common.exception.CustomException;
import com.sotory.common.exception.ErrorCode;
import com.sotory.core.config.WebClientProvider;
import com.sotory.paymentDiary.dto.response.*;
import com.sotory.paymentDiary.entity.DiaryCard;
import com.sotory.paymentDiary.entity.PaymentDiary;
import com.sotory.paymentDiary.repository.DiaryCardRepository;
import com.sotory.paymentDiary.repository.PaymentDiaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDiaryService {
    private final PaymentDiaryRepository paymentDiaryRepository;
    private final DiaryCardRepository diaryCardRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final WebClientProvider webClientProvider;

    private static final String KEY_PREFIX = "payment_diary:";
    public String buildKey(UUID userId, LocalDate date){
        return KEY_PREFIX + userId + ":" +date.toString();
    }


    public DiaryPaymentsListResponseDTO getPaymentsDiaries(UUID userId, LocalDate date) {
        log.info("소비내역 및 일기 조회 시작 - userId: {}, date: {}", userId, date);
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("날짜: {}", date);
        List<PaymentDiary> paymentDiaries = paymentDiaryRepository.findAllWithUserByUseridAndTransactionDate(userId, date);
        log.info("조회된 결제일기 개수: {}", paymentDiaries.size());
        List<PaymentDiaryDTO> paymentDiaryDtoList = paymentDiaries.stream()
                .map(PaymentDiaryDTO::fromEntity)
                .toList();
        log.info("소비내역 및 일기 조회 완료");
        return new DiaryPaymentsListResponseDTO(paymentDiaryDtoList);
    }

    public DiaryTodayPaymentsListResponseDTO getTodayPaymentsDiaries(UUID userId) {
        LocalDate today = LocalDate.now();
        List<PaymentDiaryDTO> cachedDiaryList = getCachedDiaryList(userId);

        boolean cardMarked = diaryCardRepository
                .findByUser_UserIdAndCreatedAt(userId, today)
                .isPresent();

        if(cachedDiaryList == null || cachedDiaryList.isEmpty()){
            log.info("[오늘의 소비/일기] Redis 캐시 없음 - DB에서 조회");
            List<PaymentDiaryDTO> paymentDiaryDtoList = paymentDiaryRepository.findAllWithUserByUseridAndTransactionDate(userId, today)
                    .stream()
                    .map(PaymentDiaryDTO::fromEntity)
                    .toList();
            saveDiaryInRedis(userId,today,paymentDiaryDtoList,Duration.ofDays(1));

            return new DiaryTodayPaymentsListResponseDTO(paymentDiaryDtoList, cardMarked);
        }

        Set<UUID> cachedDiaryIds = cachedDiaryList.stream()
                .map(PaymentDiaryDTO::getPaymentId)
                .collect(Collectors.toSet());

        List<PaymentDiaryDTO> uncachedDiaries = paymentDiaryRepository.findAllByUser_UserIdAndTransactionDateAndIsDeletedFalseAndPaymentIdNotIn(userId, today, cachedDiaryIds)
                .stream()
                .map(PaymentDiaryDTO::fromEntity)
                .toList();

        if(!uncachedDiaries.isEmpty()){
            log.info("오늘 소비 내역 - 새 항목 {}건 병합", uncachedDiaries.size());
            cachedDiaryList.addAll(uncachedDiaries);
            saveDiaryInRedis(userId,today,cachedDiaryList,Duration.ofDays(1));
        }

        return new DiaryTodayPaymentsListResponseDTO(cachedDiaryList, cardMarked);
    }

    public List<PaymentDiaryDTO> getCachedDiaryList(UUID userId) {
        String key = buildKey(userId, LocalDate.now());
        String cached = redisTemplate.opsForValue().get(key);

        if(cached==null)
            return Collections.emptyList();

        try{
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, PaymentDiaryDTO.class);
            return objectMapper.readValue(cached, type);
        } catch (Exception e){
            log.error("Redis 일기 불러오기 역직렬화 실패");
            return Collections.emptyList();
        }

    }

    public void saveDiaryInRedis(UUID userId, LocalDate date, List<PaymentDiaryDTO> list, Duration ttl) {
        String key = buildKey(userId, date);
        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, ttl);
            log.info("캐시 저장 완료 - key:{}", key);
        } catch (Exception e) {
            log.error("일기 Redis 저장 직렬화 실패: {}", e.getMessage());
        }
    }

    public void updateSingleDiaryInCache(UUID userId, LocalDate date, PaymentDiary updatedEntity, Duration ttl) {
        List<PaymentDiaryDTO> cachedList = getCachedDiaryList(userId);

        List<PaymentDiaryDTO> updatedList = cachedList.stream()
                .map(dto -> dto.getPaymentId().equals(updatedEntity.getPaymentId())
                        ? PaymentDiaryDTO.fromEntity(updatedEntity)
                        : dto)
                .toList();

        saveDiaryInRedis(userId, date, updatedList, ttl);
    }

    @Transactional
    public void updateDiaryContent(UUID userId, UUID paymentDiaryId, String diary) {
//        // 비속어 필터 호출
//        log.info("비속어 필터 호출");
//        BadWordCheckResponse result = webClientProvider.fastapiClient().post()
//                .uri("/api/nlp/check_badwords")
//                .bodyValue(Map.of("text", diary))
//                .retrieve()
//                .bodyToMono(BadWordCheckResponse.class)
//                .block();
//
//        log.info("text : {}" + diary);
//        log.info("result : {}" + result);
//
//        if (result != null && result.has_bad_words()) {
//            log.warn("비속어 감지됨: {}", result.bad_words());
//            throw new CustomException(ErrorCode.DIARY_CONTAINS_BAD_WORDS); // 필요 시 새 에러코드 추가해도 됨
//        }
//
//        log.info("비속어 없음");
        PaymentDiary paymentDiary = paymentDiaryRepository
                .findByPaymentDiaryIdAndIsDeletedFalse(paymentDiaryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 결제 내역이 존재하지 않습니다."));

        paymentDiary.updateDiary(diary);

        updateSingleDiaryInCache(userId, LocalDate.now(), paymentDiary, Duration.ofDays(1));
    }

    @Transactional
    public void deleteDiaryContent(UUID userId, UUID paymentId) {
        PaymentDiary paymentDiary = paymentDiaryRepository
                .findByPaymentDiaryIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 결제 내역이 존재하지 않습니다."));
        paymentDiary.deleteDiary();

        updateSingleDiaryInCache(userId, LocalDate.now(), paymentDiary, Duration.ofDays(1));
    }

    public List<DiaryListResponseDTO> getAllDiaries(UUID userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate today = LocalDate.now();

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate;

        if(today.getYear() == year && today.getMonthValue() == month){
            endDate = today;
        } else {
            endDate = yearMonth.atEndOfMonth();
        }

        List<DiaryCard> paymentDiaries = diaryCardRepository
                .findAllByUser_UserIdAndCreatedAtBetween(userId, startDate, endDate, Sort.by(Sort.Direction.ASC, "createdAt"));

        return paymentDiaries.stream()
                .map(DiaryListResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiaryListResponseDTO getDiaryByDate(UUID userId, LocalDate date) {
        DiaryCard diaryCard = diaryCardRepository
                .findByUser_UserIdAndCreatedAt(userId, date)
                .orElseThrow(()->new EntityNotFoundException("해당 날짜에 일기가 존재하지 않습니다."));

                return DiaryListResponseDTO.fromEntity(diaryCard);
    }

    public List<MonthlyDiaryCardPreviewDTO> getDiaryCardMonthlyPreview(UUID userId, Integer yearParam) {


        int currentYear = LocalDate.now().getYear();
        int year = (yearParam != null) ? yearParam : currentYear;
        int currentMonth = LocalDate.now().getMonthValue();
        int todayDayOfMonth = LocalDate.now().getDayOfMonth();

        List<MonthlyDiaryCardPreviewDTO> result = new ArrayList<>();


        for(int month = 1; month <= 12; month++){

            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = (year ==currentYear && month == currentMonth)
                    ? LocalDate.now()
                    : yearMonth.atEndOfMonth();

            List<DiaryCard> monthCards = diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(userId, startDate, endDate,Sort.by(Sort.Direction.ASC, "createdAt"));
            log.info("유저아이디 : {}", userId);
            log.info("시작일자 : {}, 종료일자: {}", startDate, endDate);
            log.info("리스트 내역 개수 확인: {}", monthCards.size());

            List<DiaryListResponseDTO> previews = monthCards.stream()
                    .sorted(Comparator.comparing(DiaryCard::getCreatedAt).reversed())
                    .limit(2)
                    .map(DiaryListResponseDTO::fromEntity)
                    .toList();

            MonthlyDiaryCardPreviewDTO dto = new MonthlyDiaryCardPreviewDTO(
                    month,
                    monthCards.size(),
                    previews
            );

            result.add(dto);
        }
        log.info("결과값 확인 : "+result);
        return result;
    }



}
