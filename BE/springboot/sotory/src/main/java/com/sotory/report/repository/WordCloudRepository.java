package com.sotory.report.repository;

import com.sotory.report.entity.WordCloud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WordCloudRepository extends JpaRepository<WordCloud, UUID> {

    // 1. userId와 yearMonth로 WordCloud 조회
	@Query("SELECT w FROM WordCloud w WHERE w.user.userId = :userId AND w.yearMonth = :yearMonth AND w.isDeleted = false")
	Optional<WordCloud> findByUserIdAndYearMonth(@Param("userId") UUID userId,
	                                             @Param("yearMonth") String yearMonth);


    // 2. 새로운 WordCloud 저장 (save 메소드는 JpaRepository 기본 제공)

    // 3. 특정 WordCloud 업데이트 (예시: diaryCount와 imageUrl 업데이트)
    @Transactional
    @Modifying
    @Query("UPDATE WordCloud w SET w.diaryCount = :diaryCount, w.imageUrl = :imageUrl WHERE w.wordcloudId = :wordcloudId")
    int updateWordCloud(@Param("wordcloudId") UUID wordcloudId,
                        @Param("diaryCount") int diaryCount,
                        @Param("imageUrl") String imageUrl);

}
