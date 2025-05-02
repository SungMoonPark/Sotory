package com.sotory.paymentDiary.controller;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.common.ApiResponse;
import com.sotory.paymentDiary.dto.response.GenerateDiaryCardResponse;
import com.sotory.paymentDiary.dto.response.RandomDiaryListResponseDTO;
import com.sotory.paymentDiary.service.DiaryCardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryCardController {

    private final DiaryCardService diaryCardService;

    @Operation(summary = "일기 카드 생성", description = "일기를 작성하고 난 뒤에 버튼을 눌러 일기 카드를 생성합니다")
    @PostMapping("/generation/card")
    public ResponseEntity<?> summarizeDiary(
            @CurrentUserInfo CurrentUser user
    ) {
        //api/diary/summarize-diary

        log.info("일기 카드 생성을 위한 요약본 & 프롬프트 만들기");

        try {
            GenerateDiaryCardResponse generateDiaryCardResponse = diaryCardService.summarizeDiary(user.getUserId());
            log.info("일기 요약본 : {}", generateDiaryCardResponse.summary());
            log.info("이미지 주소 : {}", generateDiaryCardResponse.imgSrc());
            return ResponseEntity.ok(ApiResponse.success(generateDiaryCardResponse));
        } catch (Exception e) {
            log.error("일기 카드 생성 실패", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "일기 카드 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    @DeleteMapping("")
    @Operation(summary = "일기 카드 삭제", description = "오늘 날짜의 일기 카드를 삭제합니다")
    public ResponseEntity<?> deleteDiary(@CurrentUserInfo CurrentUser user) {
        log.info("일기카드 삭제 - userId: {}", user.getUserId());

        diaryCardService.deleteDiary(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("오늘 일기 카드가 성공적으로 삭제되었습니다."));
    }

    @GetMapping("")
    public ResponseEntity<?> getDiareis() {
        log.info("일기 카드 랜덤으로 10개 조회");
        RandomDiaryListResponseDTO list = diaryCardService.randomGetDiaries();
        log.info("일기 카드 랜덤 조회 10개 완료 - {}", list.imgSrc().size());
        return ResponseEntity.ok(list);
    }
}