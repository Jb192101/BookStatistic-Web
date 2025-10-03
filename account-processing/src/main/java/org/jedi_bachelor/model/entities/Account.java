package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.model.enums.AccountType;

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
    private AccountType accountType;

    @Column(name="card_number")
    private Long cardId;

    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}