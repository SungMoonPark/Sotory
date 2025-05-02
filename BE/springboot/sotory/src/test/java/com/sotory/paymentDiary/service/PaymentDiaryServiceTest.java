package com.sotory.paymentDiary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotory.paymentDiary.dto.response.DiaryPaymentsListResponseDTO;
import com.sotory.paymentDiary.dto.response.DiaryListResponseDTO;
import com.sotory.paymentDiary.dto.response.MonthlyDiaryCardPreviewDTO;
import com.sotory.paymentDiary.entity.DiaryCard;
import com.sotory.paymentDiary.entity.PaymentDiary;
import com.sotory.paymentDiary.repository.DiaryCardRepository;
import com.sotory.paymentDiary.repository.PaymentDiaryRepository;
import com.sotory.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentDiaryServiceTest {

    @Mock
    private PaymentDiaryRepository paymentDiaryRepository;

    @InjectMocks
    private PaymentDiaryService paymentDiaryService;

    @InjectMocks
    private DiaryCardRepository diaryCardRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;



    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        //given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }


//    @Test
//    void updateDiaryContent_성공() {
//        // given
//        UUID userId = UUID.randomUUID();
//        String paymentId = "test-id";
//        PaymentDiary diary = PaymentDiary.builder()
//                .paymentId(paymentId)
//                .diary("기존 내용")
//                .transactionDate(LocalDate.now())
//                .user(User.builder().userId(userId).build())
//                .build();
//
//        given(paymentDiaryRepository.findByPaymentIdAndIsDeletedFalse(paymentId))
//                .willReturn(Optional.of(diary));
//
//        // when
//        paymentDiaryService.updateDiaryContent(userId, paymentId, "새로운 내용");
//
//        // then
//        assertThat(diary.getDiary()).isEqualTo("새로운 내용");
//    }
//
//    @Test
//    void getTodayPaymentsDiaries_캐시없을때_DB조회() throws Exception {
//        // given
//        UUID userId = UUID.randomUUID();
//        LocalDate today = LocalDate.now();
//        String key = "payment_diary:" + userId + ":" + today;
//
//        // Redis에 캐시 없음
//        given(valueOperations.get(key)).willReturn(null);
//
//        // DB에서 1건 조회
//        PaymentDiary diary = PaymentDiary.builder()
//                .paymentId("p1")
//                .merchantName("카페")
//                .categoryName("식사")
//                .transactionBalance(5000)
//                .transactionDate(today)
//                .transactionTime(null)
//                .isUserAdded(false)
//                .diary("아아 마심")
//                .user(User.builder().userId(userId).build())
//                .build();
//
//        given(paymentDiaryRepository.findAllWithUserByUseridAndTransactionDate(userId, today))
//                .willReturn(List.of(diary));
//
//        // ObjectMapper 직렬화
//        String json = "[{\"paymentId\":\"p1\"}]";
//        given(objectMapper.writeValueAsString(any())).willReturn(json);
//
//        // when
//        DiaryPaymentsListResponseDTO response = paymentDiaryService.getTodayPaymentsDiaries(userId);
//
//        // then
//        assertThat(response.paymentDiaires()).hasSize(1);
//        verify(valueOperations).set(eq(key), eq(json), any());
//    }
//
//    @Test
//    void updateDiaryContent_존재하지않는_paymentId_예외발생() {
//        // given
//        String paymentId = "non-existent-id";
//        UUID userId = UUID.randomUUID();
//        given(paymentDiaryRepository.findByPaymentIdAndIsDeletedFalse(paymentId))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() ->
//                paymentDiaryService.updateDiaryContent(userId, paymentId, "내용")
//        ).isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void deleteDiaryContent_성공() {
//        // given
//        UUID userId = UUID.randomUUID();
//        String paymentId = "p1";
//        PaymentDiary diary = PaymentDiary.builder()
//                .paymentId(paymentId)
//                .diary("삭제될 내용")
//                .user(User.builder().userId(userId).build())
//                .build();
//
//        given(paymentDiaryRepository.findByPaymentIdAndIsDeletedFalse(paymentId))
//                .willReturn(Optional.of(diary));
//
//        // when
//        paymentDiaryService.deleteDiaryContent(userId, paymentId);
//
//        // then
//        assertThat(diary.getDiary()).isNull();
//    }

    @Test
    void getPaymentsDiaries_특정날짜조회_성공() {
        // given
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 4, 1);

        PaymentDiary diary = PaymentDiary.builder()
                .paymentId(UUID.randomUUID())
                .merchantName("스타벅스")
                .categoryName("카페")
                .transactionBalance(5000) // ← 이거 안 넣으면 NPE
                .transactionDate(date)
                .transactionTime(LocalTime.parse("14:30:00"))
                .isUserAdded(false)
                .diary("오늘 커피 마신 날")
                .user(User.builder().userId(userId).build())
                .build();

        given(paymentDiaryRepository.findAllWithUserByUseridAndTransactionDate(userId, date))
                .willReturn(List.of(diary));

        // when
        DiaryPaymentsListResponseDTO result = paymentDiaryService.getPaymentsDiaries(userId, date);

        // then
        assertThat(result.paymentDiaires()).hasSize(1);
    }

//    @Test
//    void getAllDiaries_이번달_오늘까지조회_성공() {
//        // given
//        UUID userId = UUID.randomUUID();
//        UUID diaryCardId = UUID.randomUUID();
//        LocalDate today = LocalDate.now();
//        int year = today.getYear();
//        int month = today.getMonthValue();
//
//        DiaryCard diary = DiaryCard.builder()
//                .createdAt(today.minusDays(1)) // 테스트 기준 날짜
//                .user(User.builder().userId(userId).build())
//                .diaryCardId(diaryCardId)
//                .summary("이번 달 다이어리 요약")
//                .imgSrc("test-image.jpg")
//                .build();
//
//        given(diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(
//                eq(userId), eq(today.withDayOfMonth(1)), eq(today)))
//                .willReturn(List.of(diary));
//
//        // when
//        var result = paymentDiaryService.getAllDiaries(userId, year, month);
//
//        // then
//        assertThat(result).hasSize(1);
//    }
//
//    @Test
//    void getDiaryByDate_존재할경우_성공() {
//        // given
//        UUID userId = UUID.randomUUID();
//        UUID diaryCardId = UUID.randomUUID();
//        LocalDate date = LocalDate.of(2025, 4, 2);
//
//        DiaryCard diary = DiaryCard.builder()
//                .diaryCardId(diaryCardId)
//                .createdAt(date)
//                .summary("테스트 요약")
//                .imgSrc("이미지.jpg")
//                .user(User.builder().userId(userId).build())
//                .build();
//
//        given(diaryCardRepository.findByUser_UserIdAndCreatedAt(userId, date))
//                .willReturn(Optional.of(diary));
//
//        // when
//        DiaryListResponseDTO result = paymentDiaryService.getDiaryByDate(userId, date);
//
//        // then
//        assertThat(result.diaryCardId()).isEqualTo(diaryCardId.toString());
//    }
//    private DiaryCard createDiaryCard(UUID userId, LocalDate date, String imgSrc) {
//        return DiaryCard.builder()
//                .diaryCardId(UUID.randomUUID())
//                .imgSrc(imgSrc)
//                .createdAt(date)
//                .user(User.builder().userId(userId).build())
//                .build();
//    }
//
//    @Test
//    void getDiaryCardMonthlyPreview_정상작동_여러달데이터() {
//        // given
//        UUID userId = UUID.randomUUID();
//
//        // 오늘 날짜를 2025년 4월 2일로 가정
//        LocalDate today = LocalDate.of(2025, 4, 2);
//
//        // 1~4월의 마지막 이틀을 기준으로 데이터 생성
//        DiaryCard jan30 = createDiaryCard(userId, LocalDate.of(2025, 1, 30), "img1");
//        DiaryCard jan31 = createDiaryCard(userId, LocalDate.of(2025, 1, 31), "img2");
//        DiaryCard feb27 = createDiaryCard(userId, LocalDate.of(2025, 2, 27), "img3");
//        DiaryCard feb28 = createDiaryCard(userId, LocalDate.of(2025, 2, 28), "img4");
//        DiaryCard mar30 = createDiaryCard(userId, LocalDate.of(2025, 3, 30), "img5");
//        DiaryCard mar31 = createDiaryCard(userId, LocalDate.of(2025, 3, 31), "img6");
//        DiaryCard apr01 = createDiaryCard(userId, LocalDate.of(2025, 4, 1), "img7");
//
//        // stub 월별 날짜 범위에 대한 mock 리턴값 설정
//        given(diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(
//                eq(userId), eq(LocalDate.of(2025, 1, 30)), eq(LocalDate.of(2025, 1, 31))
//        )).willReturn(List.of(jan30, jan31));
//
//        given(diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(
//                eq(userId), eq(LocalDate.of(2025, 2, 27)), eq(LocalDate.of(2025, 2, 28))
//        )).willReturn(List.of(feb27, feb28));
//
//        given(diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(
//                eq(userId), eq(LocalDate.of(2025, 3, 30)), eq(LocalDate.of(2025, 3, 31))
//        )).willReturn(List.of(mar30, mar31));
//
//        given(diaryCardRepository.findAllByUser_UserIdAndCreatedAtBetween(
//                eq(userId), eq(LocalDate.of(2025, 3, 31)), eq(LocalDate.of(2025, 4, 1))
//        )).willReturn(List.of(apr01));
//
//        // when
//        List<MonthlyDiaryCardPreviewDTO> result = paymentDiaryService.getDiaryCardMonthlyPreview(userId,2025);
//
//        // then
//        assertThat(result).hasSize(4); // 1월 ~ 4월
//
//        // 각 월별 데이터 검증
//        assertThat(result.get(0).month()).isEqualTo(1);
//        assertThat(result.get(0).count()).isEqualTo(2);
//
//        assertThat(result.get(1).month()).isEqualTo(2);
//        assertThat(result.get(1).count()).isEqualTo(2);
//
//        assertThat(result.get(2).month()).isEqualTo(3);
//        assertThat(result.get(2).count()).isEqualTo(2);
//
//        assertThat(result.get(3).month()).isEqualTo(4);
//        assertThat(result.get(3).count()).isEqualTo(1);
//    }
//




}
