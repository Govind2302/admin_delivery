package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "social_media_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private SocialMediaPosts post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.VISIBLE;

    public enum CommentStatus {
        VISIBLE, HIDDEN, DELETED
    }

}
