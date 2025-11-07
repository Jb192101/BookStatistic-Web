package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.model.enums.AccountType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="accounts")
@Data
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="account_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountType accountType;

    @Column(name="rating")
    @PositiveOrZero
    private Float rating;

    @Column(name="distribution")
    @PositiveOrZero
    private Float distribution; // общий количественный рейтинг (не виден обычным пользователям)

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountBookRelationShip> bookRelationships = new ArrayList<>();

    public Account(String username, String password, String email, AccountType type) {
        this.username = username;
        this.password = password;
        this.email = email;

        this.accountType = AccountType.ROLE_ADMIN;

        this.rating = 0.0f;
        this.distribution = 0.0f;
    }
}