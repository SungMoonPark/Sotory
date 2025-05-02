package com.sotory.paymentDiary.controller;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.common.ApiResponse;
import com.sotory.paymentDiary.dto.request.DiaryPaymentUpdateRequest;
import com.sotory.paymentDiary.dto.request.PaymentsPatchRequest;
import com.sotory.paymentDiary.dto.request.PaymentsRequest;
import com.sotory.paymentDiary.dto.response.*;
import com.sotory.paymentDiary.service.PaymentDiaryService;
import com.sotory.paymentDiary.service.PaymentService;
import com.sotory.paymentDiary.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class PaymentDiaryController {

    private final PaymentService paymentService;
    private final PaymentDiaryService paymentDiaryService;
    private final WeatherService weatherService;

    // 소비내역 추가 - payments
    @PostMapping("/payments")
    public ResponseEntity<?> addPayment(@CurrentUserInfo CurrentUser user, @RequestBody PaymentsRequest request) {
        log.info("소비내역 추가 요청");
        PaymentsCreateResponseDTO response = paymentService.addPayment(user.getUserId(), request);
        log.info("소비내역 추가 완료");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 소비내역 수정
    @PatchMapping("/payments/{paymentId}")
    public ResponseEntity<?> updatePayment(
            @CurrentUserInfo CurrentUser user,
            @Valid @RequestBody PaymentsPatchRequest request,
                                           @PathVariable UUID paymentId) {
        log.info("소비내역 수정 요청");

        paymentService.updatePayment(user.getUserId() , paymentId, request);
        log.info("소비내역 수정 완료");
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 소비내역 삭제
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<?> deletePayment( @CurrentUserInfo CurrentUser user, @PathVariable UUID paymentId) {
        log.info("소비내역 삭제 요청 - ID: {}", paymentId);
        paymentService.deletePayment(user.getUserId(), paymentId);
        log.info("소비내역 삭제 완료");

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "일별 소비일기 조회", description = "선택한 날짜의 일기를 DB에서 불러옵니다")
    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentDiaries(@CurrentUserInfo CurrentUser user,
                                               @RequestParam(value = "date", required = false) LocalDate date)
    {
        log.info("일별 소비일기 조회: {}", date);
        return ResponseEntity.ok(ApiResponse.success(paymentDiaryService.getPaymentsDiaries(user.getUserId(), date)));
    }
    
    @Operation(summary = "금일 소비일기 조회", description = "선택한 날짜의 소비와 일기를 레디스에서 불러오고, 레디스에 없다면 db에서 불러옵니다")
    @GetMapping("/payments/today")
    public ResponseEntity<?> getTodayPaymentDiaries(@CurrentUserInfo CurrentUser user){
        DiaryTodayPaymentsListResponseDTO data = paymentDiaryService.getTodayPaymentsDiaries(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "소비 일기 작성 및 수정", description = "각 소비내역에 대한 일기를 작성하고 수정합니다")
    @PatchMapping("/{paymentDiaryId}")
    public ResponseEntity<?> updateDiaryContent(@CurrentUserInfo CurrentUser user, @PathVariable UUID paymentDiaryId, @Valid @RequestBody DiaryPaymentUpdateRequest request){
        paymentDiaryService.updateDiaryContent(user.getUserId(), paymentDiaryId, request.getDiary());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "소비 일기 삭제", description = "각 소비내역에 대한 일기를 삭제합니다")
    @DeleteMapping("/{paymentDiaryId}")
    public ResponseEntity<?> deleteDiaryContent(@CurrentUserInfo CurrentUser user,@PathVariable UUID paymentDiaryId){
        paymentDiaryService.deleteDiaryContent(user.getUserId(), paymentDiaryId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "모든 일기 카드 조회", description = "모든 카드를 조회합니다")
    @GetMapping
    public ResponseEntity<?> getAllDiariesCard(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @CurrentUserInfo CurrentUser user
    ){
        // year/month가 null이면 현재 날짜 기준으로 설정
        LocalDate now = LocalDate.now();
        int finalYear = (year != null) ? year : now.getYear();
        int finalMonth = (month != null) ? month : now.getMonthValue();

        List<DiaryListResponseDTO> response = paymentDiaryService.getAllDiaries(user.getUserId(), finalYear, finalMonth);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @Operation(summary = "일기카드 단일 조회", description = "원하는 날짜의 일기 카드를 조회합니다")
    @GetMapping("/{year}/{month}/{day}")
    public ResponseEntity<?> getDiaryCardByDate(@CurrentUserInfo CurrentUser user,
                                                            @PathVariable int year, @PathVariable int month, @PathVariable int day) {
        DiaryListResponseDTO response = paymentDiaryService.getDiaryByDate(user.getUserId(), LocalDate.of(year, month, day));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "월별 일키 카드 조회", description = "첫 화면에 표시될 일기카드를 월별로 조회합니다")
    @GetMapping("/monthly-preview")
    public ResponseEntity<?> getDiaryCardMonthlyPreview(@CurrentUserInfo CurrentUser user, @RequestParam(value = "year", required = false) Integer year) {
        List<MonthlyDiaryCardPreviewDTO> result = paymentDiaryService.getDiaryCardMonthlyPreview(user.getUserId(), year);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getWeatherTest(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        WeatherCardDTO weather = weatherService.getWeatherByCoordinates(lat, lon);

        return ResponseEntity.ok(ApiResponse.success(new WeatherCardDTO(weather.main(), weather.description())));
    }


}
