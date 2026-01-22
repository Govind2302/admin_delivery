package com.foodgram.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "social_media_posts")
public class SocialMediaPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurants restaurant;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer commentsCount = 0;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.VISIBLE;

    // Enum for status
    public enum PostStatus {
        VISIBLE, HIDDEN, DELETED
    }

}