package com.sotory.paymentDiary.service;

import com.sotory.common.exception.CustomException;
import com.sotory.common.exception.ErrorCode;
import com.sotory.core.config.WebClientProvider;
import com.sotory.paymentDiary.dto.response.GenerateDiaryCardResponse;
import com.sotory.paymentDiary.dto.response.RandomDiaryListResponseDTO;
import com.sotory.paymentDiary.dto.response.WeatherCardDTO;
import com.sotory.paymentDiary.entity.DiaryCard;
import com.sotory.paymentDiary.entity.PaymentDiary;
import com.sotory.paymentDiary.repository.DiaryCardRepository;
import com.sotory.paymentDiary.repository.PaymentDiaryRepository;
import com.sotory.paymentDiary.type.WeatherType;
import com.sotory.user.entity.User;
import com.sotory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryCardService {

    private final WebClientProvider webClientProvider;  // ✅ WebClientProvider 사용

    private final PaymentDiaryRepository paymentDiaryRepository;
    private final DiaryCardRepository diaryCardRepository;
    private final UserRepository userRepository;
    private final WeatherService weatherService;

    public GenerateDiaryCardResponse summarizeDiary(UUID userId) {
        LocalDate today = LocalDate.now();
        int date = today.getDayOfMonth();

        log.info("일기 카드 생성 API 호출 시작 [service 계층] - userId: {}, today: {}", userId, today);

        Optional<DiaryCard> existingCard = diaryCardRepository.findByUser_UserIdAndCreatedAt(userId, today);
        if (existingCard.isPresent()) {
            DiaryCard card = existingCard.get();
            int day = card.getCreatedAt().getDayOfMonth();
            log.info("이미 생성된 일기 카드가 있습니다. - cardId: {}", card.getDiaryCardId());
            return new GenerateDiaryCardResponse(day, card.getSummary(),
                    card.getImgSrc(), card.getDiaryCardId(), card.getWeather());
        }

        List<PaymentDiary> diaries = paymentDiaryRepository.findByUser_UserIdAndTransactionDate(userId, today);

        if (diaries.isEmpty() || diaries.size() == 0) {
            log.warn("해당 날짜에 작성된 일기가 없습니다. userId: {}, date: {}", userId, today);
            return new GenerateDiaryCardResponse(null, null, null, null, null);
        }

        List<String> diaryContents = diaries.stream()
                .map(PaymentDiary::getDiary)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (diaryContents.isEmpty() || diaryContents.size() == 0) {
            log.warn("해당 날짜에 작성된 일기 내용이 없습니다. userId: {}, date: {}", userId, today);
            return new GenerateDiaryCardResponse(null, null, null, null, null);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", diaryContents);


        // ✅ WebClientProvider 사용
        GenerateDiaryCardResponse generateDiaryCardResponse = webClientProvider.fastapiClient().post()
                .uri("/api/diary/summarize-diary")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GenerateDiaryCardResponse.class)
                .doOnSuccess(response -> {
                    if (response.summary() == null || response.imgSrc() == null) {
                        log.error("일기 카드 생성 API 응답이 비어있습니다");
                        throw new RuntimeException("일기 카드 생성에 실패했습니다");
                    }
                    log.info("일기 카드 생성 API 호출 완료");
                })
                .onErrorResume(e -> {
                    log.error("일기 카드 생성 API 호출 중 오류 발생", e);
                    throw new RuntimeException("일기 카드 생성 중 오류가 발생했습니다: " + e.getMessage());
                })
                .block();

        if (generateDiaryCardResponse != null) {
            try {
                User user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                log.info("일기 카드 생성 완료!");

                log.info("날씨 조회 시작");
                WeatherCardDTO weatherCardDTO = weatherService.getWeatherByCoordinates(	37.503325874722, 127.04403462366);

                WeatherType weather = weatherCardDTO.main().contains("Snow") ? WeatherType.SNOW :
                        weatherCardDTO.main().contains("Rain")||weatherCardDTO.main().contains("Drizzle")||weatherCardDTO.main().contains("Thunderstorm") ? WeatherType.RAIN :
                                WeatherType.DEFAULT;

                DiaryCard diaryCard = DiaryCard.builder()
                        .summary(generateDiaryCardResponse.summary())
                        .Weather(weather)
                        .imgSrc(generateDiaryCardResponse.imgSrc())
                        .createdAt(today)
                        .user(user)
                        .build();

                diaryCardRepository.save(diaryCard);
                log.info("일기 카드 저장 완료 - cardId: {}", diaryCard.getDiaryCardId());
                if (diaryCard.getSummary().length() >= 255) {
                    diaryCard.setSummary("요약본 길이 초과");
                }

                generateDiaryCardResponse = new GenerateDiaryCardResponse(
                        date,
                        diaryCard.getSummary(),
                        diaryCard.getImgSrc(),
                        diaryCard.getDiaryCardId(),
                        diaryCard.getWeather()
                );
            } catch (Exception e) {
                log.error("일기 카드 저장 중 오류 발생", e);
            }
        }

        return generateDiaryCardResponse;
    }

    public void deleteDiary(UUID userId) {
        LocalDate today = LocalDate.now();
        log.info("일기 카드 삭제 시작 - userId: {}, today: {}", userId, today);
        DiaryCard card = diaryCardRepository.findByUser_UserIdAndCreatedAt(userId, today)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));  // ✅ 예외 던지기

        diaryCardRepository.delete(card);
        log.info("일기 카드 삭제 완료 - cardId: {}", card.getDiaryCardId());

    }

    public RandomDiaryListResponseDTO randomGetDiaries() {
        log.info("랜덤으로 다이어리 이미지 10개 조회");

        List<DiaryCard> allCards = diaryCardRepository.findAll();

        // 섞고 10개만 뽑기
        Collections.shuffle(allCards);
        List<String> randomImgSrcs = allCards.stream()
                .filter(card -> card.getImgSrc() != null)
                .limit(10)
                .map(DiaryCard::getImgSrc)
                .collect(Collectors.toList());

        return new RandomDiaryListResponseDTO(randomImgSrcs);
    }
}
