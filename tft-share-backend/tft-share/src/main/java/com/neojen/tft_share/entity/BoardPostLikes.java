package com.neojen.tft_share.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_post_likes")
public class BoardPostLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters, setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BoardPost getBoardPost() { return boardPost; }
    public void setBoardPost(BoardPost boardPost) { this.boardPost = boardPost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}