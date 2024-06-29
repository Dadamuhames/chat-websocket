package com.msd.chat.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MessageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String message;
    private String image;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity fromUser;


    @AssertTrue(message = "Message or Image required")
    private boolean isMessageOrImage() {
        return (image.isEmpty() && !message.isEmpty()) || (!image.isEmpty() && message.isEmpty()) || !image.isEmpty();
    }

    @PrePersist
    private void onPrePersist() {
        this.setUuid(UUID.randomUUID());
    }
}
