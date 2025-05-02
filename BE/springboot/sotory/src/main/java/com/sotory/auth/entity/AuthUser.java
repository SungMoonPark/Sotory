package com.sotory.auth.entity;

import com.sotory.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="auth_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String providerUserId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean isDeleted;

}
