package com.neojen.tft_share.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_timeline")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VideoTimeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private Integer startTime;

    @Column(name = "end_time", nullable = false)
    private Integer endTime;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
}
