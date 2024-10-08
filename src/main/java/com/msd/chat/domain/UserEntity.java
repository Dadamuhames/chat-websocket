package com.msd.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserEntity implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(unique = true, nullable = false)
  private String username;

  @NotEmpty @NotNull private String name;

  @Column(nullable = false)
  private String password;

  private boolean active;

  @CreationTimestamp private LocalDateTime joinedAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  private String image;

  @JsonIgnore @ManyToMany private Set<ChatEntity> chats;

  @JsonIgnore
  @OneToMany(mappedBy = "admin", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private Set<ChatEntity> createdChats;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public boolean isAccountNonExpired() {
    return isActive();
  }

  @Override
  public boolean isAccountNonLocked() {
    return isActive();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return isActive();
  }

  @Override
  public boolean isEnabled() {
    return isActive();
  }
}
