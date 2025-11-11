package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Table(name="friends")
@Entity
@Data
public class FriendRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend1_id", nullable = false)
    @NotNull
    private Account friend1;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend2_id", nullable = false)
    @NotNull
    private Account friend2;
}
