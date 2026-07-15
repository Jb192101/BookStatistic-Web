package org.jedi_bachelor.bookstatistic.accountservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.jedi_bachelor.bookstatistic.accountservice.language.Language;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserProfile implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_sub", unique = true, nullable = false)
    private String keycloakSub;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Size(min = 1)
    @Column(name = "fisrt_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName = "";

    @Size(min = 1)
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language = Language.EN;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "birthday")
    private LocalDate birthDay;

    @Column(name = "country_of_residence")
    private String residenceCountry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
