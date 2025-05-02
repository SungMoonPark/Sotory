package com.sotory.paymentDiary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotory.common.exception.CustomException;
import com.sotory.core.auditing.BaseTimeEntity;
import com.sotory.common.exception.ErrorCode;
import com.sotory.paymentDiary.dto.request.PaymentsPatchRequest;
import com.sotory.paymentDiary.dto.response.PaymentDiaryDTO;
import com.sotory.paymentDiary.dto.request.PaymentsRequest;
import com.sotory.paymentDiary.dto.response.PaymentsCreateResponseDTO;
import com.sotory.paymentDiary.entity.PaymentDiary;
import com.sotory.paymentDiary.repository.PaymentDiaryRepository;
import com.sotory.paymentDiary.util.DateTimeUtil;
import com.sotory.user.entity.User;
import com.sotory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentDiaryRepository paymentDiaryRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentDiaryService paymentDiaryService;

    private static final String KEY_PREFIX = "payment_diary:";
    public String buildKey(UUID userId, LocalDate date){
        return KEY_PREFIX + userId + ":" +date.toString();
    }

    public PaymentsCreateResponseDTO addPayment(UUID userId, PaymentsRequest request) {
        log.info("소비내역 추가 시작");
        
        String dateTimeStr = request.transactionTime(); // 예: "2025-04-08 14:30:00"
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("날짜 포메팅 시작");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
        
        LocalDate transactionDate = dateTime.toLocalDate();
        LocalTime transactionTime = dateTime.toLocalTime();

        log.info("포메팅 날짜:{}, 포메팅 시간:{} ", transactionDate, transactionTime);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        PaymentDiary paymentDiary = PaymentDiary.builder()
                .merchantName(request.merchantName())
                .categoryName(request.categoryName())
                .transactionBalance(Integer.parseInt(request.transactionBalance()))
                .transactionDate(transactionDate)
                .transactionTime(transactionTime)
                .user(user)
                .paymentId(UUID.randomUUID())
                .isUserAdded(true)
                .build();

        PaymentDiary savedPaymentDiary = paymentDiaryRepository.save(paymentDiary);
        log.info("소비내역 추가 성공");
        log.info("소비내역 UUID : {}", savedPaymentDiary.getPaymentId());


        return new PaymentsCreateResponseDTO( savedPaymentDiary.getPaymentDiaryId().toString(), savedPaymentDiary.getPaymentId().toString());
    }

    public void updatePayment(UUID userId, UUID paymentId, PaymentsPatchRequest request) {
        log.info("소비내역 수정 시작 - paymentId: {}", paymentId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 기존 소비내역 조회
        PaymentDiary existingPayment = paymentDiaryRepository.findByPaymentIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> {
                    log.error("소비내역이 존재하지 않습니다. paymentId: {}", paymentId);
                    throw new CustomException(ErrorCode.DATA_NOT_FOUND);
                });

        // 시간 처리
        LocalTime transactionTime = request.transactionTime() != null
                ? LocalTime.parse(DateTimeUtil.formatTime(request.transactionTime()))
                : existingPayment.getTransactionTime();

        // 업데이트 빌더
        PaymentDiary updatedPayment = PaymentDiary.builder()
                .user(user)
                .paymentDiaryId(existingPayment.getPaymentDiaryId())  // 원래의 PK 유지
                .merchantName(request.merchantName() != null ? request.merchantName() : existingPayment.getMerchantName())
                .categoryName(request.categoryName() != null ? request.categoryName() : existingPayment.getCategoryName())
                .transactionBalance(request.transactionBalance() != null ?
                        Integer.parseInt(request.transactionBalance()) : existingPayment.getTransactionBalance())
                .transactionTime(transactionTime)
                .transactionDate(existingPayment.getTransactionDate())
                .diary(existingPayment.getDiary())
                .paymentId(existingPayment.getPaymentId())
                .isDeleted(existingPayment.isDeleted())
                .isUserAdded(existingPayment.isUserAdded())
                .build();

        // 감사 필드 복사
        Field createdAtField = ReflectionUtils.findField(BaseTimeEntity.class, "createdAt");
        Field lastModifiedAtField = ReflectionUtils.findField(BaseTimeEntity.class, "lastModifiedAt");

        if (createdAtField != null && lastModifiedAtField != null) {
            ReflectionUtils.makeAccessible(createdAtField);
            ReflectionUtils.makeAccessible(lastModifiedAtField);

            ReflectionUtils.setField(createdAtField, updatedPayment,
                    ReflectionUtils.getField(createdAtField, existingPayment));
            ReflectionUtils.setField(lastModifiedAtField, updatedPayment,
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        }

        PaymentDiary savedPayment = paymentDiaryRepository.save(updatedPayment);

        // 캐시 갱신
        String key = buildKey(userId, existingPayment.getTransactionDate());
        List<PaymentDiaryDTO> cachedList = paymentDiaryService.getCachedDiaryList(userId);

        List<PaymentDiaryDTO> updatedList = cachedList.stream()
                .map(dto -> dto.getPaymentId().equals(savedPayment.getPaymentId())
                        ? PaymentDiaryDTO.fromEntity(savedPayment)
                        : dto)
                .toList();

        paymentDiaryService.saveDiaryInRedis(userId, savedPayment.getTransactionDate(), updatedList, Duration.ofDays(1));

        log.info("소비내역 수정 완료 - paymentId: {}", paymentId);
    }

    public void deletePayment(UUID userId, UUID paymentId) {
        log.info("소비내역 삭제 시작 - paymentId: {}", paymentId);

        // 기존 소비내역 조회
        PaymentDiary existingPayment = paymentDiaryRepository.findByPaymentIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> {
                    log.error("소비내역이 존재하지 않습니다. paymentId: {}", paymentId);
                    throw new CustomException(ErrorCode.DATA_NOT_FOUND);
                });

        // 이미 삭제된 소비내역인지 확인
        if (existingPayment.isDeleted()) {
            log.error("이미 삭제된 소비내역입니다. paymentId: {}", paymentId);
            throw new CustomException(ErrorCode.DATA_DELETE_FAILED);
        }

        // 소프트 삭제
        existingPayment.setIsDeleted(true);
        paymentDiaryRepository.save(existingPayment);

        // Redis 캐시에서 해당 항목 제거
        String key = buildKey(userId, existingPayment.getTransactionDate());
        List<PaymentDiaryDTO> cachedList = paymentDiaryService.getCachedDiaryList(userId);

        List<PaymentDiaryDTO> updatedList = cachedList.stream()
                .filter(dto -> !dto.getPaymentId().equals(existingPayment.getPaymentId()))
                .toList();

        paymentDiaryService.saveDiaryInRedis(userId, existingPayment.getTransactionDate(), updatedList, Duration.ofDays(1));

        log.info("소비내역 삭제 완료 - paymentId: {}", paymentId);
    }



}
