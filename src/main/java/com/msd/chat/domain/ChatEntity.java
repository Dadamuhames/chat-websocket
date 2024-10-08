package com.msd.chat.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.msd.chat.domain.enums.ChatTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.apache.catalina.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ChatEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChatTypes type;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "admin_id")
    private UserEntity admin;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<UserEntity> users;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<MessageEntity> messages;

    private String image;

    private LocalDateTime lastMessageAt;

    @AssertTrue(message = "Chat name cannot be empty")
    private boolean isNameNotEmpty() {
        return (type == ChatTypes.PRIVATE && (name == null || name.isEmpty()))
                || (type == ChatTypes.GROUP && name != null && !name.isEmpty());
    }

    @PrePersist
    private void onPrePersist() {
        this.setUuid(UUID.randomUUID());
    }
}
