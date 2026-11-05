package fr.ipi.pokemon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    // STRING : la base stocke le nom de la constante, pas sa position (ORDINAL casse si on réordonne)
    // NOT NULL : un type absent compterait comme un type distinct dans le bonus de diversité
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypePokemon type;
    private int niveau;

    @ManyToOne
    private Dresseur dresseur;

    public Pokemon(String nom, TypePokemon type, int niveau) {
        this.nom = nom;
        this.type = type;
        this.niveau = niveau;
    }
}
