package com.sotory.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert @DynamicUpdate

@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = true)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    private String userKey;

}