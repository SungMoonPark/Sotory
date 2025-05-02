package com.sotory.card.service;

import com.sotory.card.dto.request.RegisterCardRequest;
import com.sotory.card.dto.response.CardListResponse;
import com.sotory.card.entity.Card;
import com.sotory.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    // 카드 등록
    public void registerCard(
            UUID userID,
            List<RegisterCardRequest> cardList
    ) {
        if(cardList==null || cardList.isEmpty()) return;

        List<Card> cards = cardList.stream()
                .map(request -> {
                    // RegisterCardRequest DTO를 Card 엔티티로 변환
                    return Card.builder()
//                            .userId(request.getUserId())  // 사용자 ID, 보통 현재 인증된 사용자에서 가져옴
                            .userId(userID)
                            .cardNo(request.cardNo())
                            .cardIssuerCode(request.cardIssuerCode())
                            .cardIssuerName(request.cardIssuerName())
                            .cardName(request.cardName())
                            .isDeleted(false)
                            .build();
                })
                .collect(Collectors.toList());

        // 모든 카드를 한 번에 저장
        cardRepository.saveAll(cards);

    }

    public List<CardListResponse> getConnectedCardList(UUID userId) {
        List<Card> response = cardRepository.findByUserIdAndIsDeletedFalse(userId);

        return response.stream()
                .map(card -> new CardListResponse(
                        card.getCardId(),
                        card.getCardNo(),
                        card.getCardIssuerCode(),
                        card.getCardIssuerName(),
                        card.getCardName()
                ))
                .collect(Collectors.toList());
    }

    public void deleteConnectedCard(UUID userId, UUID cardId) {
        try {
            // 카드 존재 여부 확인
            Card card = cardRepository.findByUserIdAndCardId(userId, cardId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다: " + cardId));
            log.info("카드를 삭제하려고 합니다. 카드 아이디는 {} 입니다.", cardId);
            log.info("카드 정보 : {}", card.getCardNo());
            // 논리적 삭제 (Soft Delete)
            card.setIsDeleted(true);
            cardRepository.save(card);

        } catch (IllegalArgumentException e) {
            // 카드가 존재하지 않는 경우
            throw new IllegalArgumentException("유효하지 않은 카드 ID: " + cardId, e);
        }
    }
}

