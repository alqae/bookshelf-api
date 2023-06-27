package com.alqae.bookshelf.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class TokenEntity {
    @Id

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String token;

    @Enumerated(EnumType.STRING)
    ETokenType type;

    private boolean isActive;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;
}
