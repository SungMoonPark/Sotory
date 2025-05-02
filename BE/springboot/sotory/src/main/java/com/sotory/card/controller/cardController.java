package com.sotory.card.controller;

import com.sotory.auth.dto.CurrentUser;
import com.sotory.auth.resolver.CurrentUserInfo;
import com.sotory.card.dto.response.CardListResponse;
import com.sotory.card.service.CardService;
import com.sotory.common.ApiResponse;
import com.sotory.common.api.dto.response.CardListResponseDto;
import com.sotory.common.api.service.FinOpenApiService;
import com.sotory.card.dto.request.RegisterCardRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/cards")
@AllArgsConstructor
public class cardController {

    private final FinOpenApiService finOpenApiService;
    private final CardService cardService;

    // 외부 API에서 값을 호출해온다
    // 유저 키값을 현재는 하드코딩한 상태인데, @Login 어노테이션을 만들고, 유저가 더 많아지는 경우에 변경이 필요함
    @Operation(summary = "카드 목록 조회", description = "사용자의 카드 목록을 조회합니다.")
    @GetMapping("/owned")
    public ResponseEntity<?> getCardList() {
        // 블로킹 방식
        CardListResponseDto response = finOpenApiService.getCardOwnedList("11b5541b-e433-447b-bf0d-56896d08ed49");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 전제 목록에서 카드 선택 -> 한번에 등록 -> sotory와 연동
    @Operation(summary = "카드 등록")
    @PostMapping
    public ResponseEntity<?> registerCard(
            @CurrentUserInfo CurrentUser user,
            @RequestBody List<RegisterCardRequest> cardList
    ) {
        cardService.registerCard(user.getUserId(), cardList);
//        String userKey = "11b5541b-e433-447b-bf0d-56896d08ed49";
//        cardService.registerCard(userKey, cardList);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "연동된 카드 목록 조회", description = "사용자가 sotory에 연동한 카드 목록을 조회합니다.")
    @GetMapping("/connected")
    public ResponseEntity<?> getResteredCardList(
            @CurrentUserInfo CurrentUser user
            ){
        List<CardListResponse> response = cardService.getConnectedCardList(user.getUserId());
//        String userId = "11b5541b-e433-447b-bf0d-56896d08ed49";
//        List<CardListResponse> response = cardService.getConnectedCardList(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "연동된 카드 삭제", description = "sotory에 연동된 카드의 연결을 해제합니다.")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteConnectedCard(
            @CurrentUserInfo CurrentUser user,
            @PathVariable UUID cardId
    ){
        cardService.deleteConnectedCard(user.getUserId(), cardId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
