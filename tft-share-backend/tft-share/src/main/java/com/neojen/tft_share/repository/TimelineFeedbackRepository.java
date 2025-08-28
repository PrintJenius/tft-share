package com.neojen.tft_share.repository;

import com.neojen.tft_share.entity.TimelineFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineFeedbackRepository extends JpaRepository<TimelineFeedback, Long> {
    
    List<TimelineFeedback> findByTimelineId(Long timelineId);
    
    List<TimelineFeedback> findByTimelineIdOrderByCreatedAtDesc(Long timelineId);
    
    long countByTimelineId(Long timelineId);
    
    void deleteByTimelineId(Long timelineId);
}
