package com.sotory.card.repository;

import com.sotory.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByUserIdAndIsDeletedFalse(UUID userId);

    Optional<Card> findByUserIdAndCardId(UUID userId, UUID cardId);
}
