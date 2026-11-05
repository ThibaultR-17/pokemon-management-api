package fr.ipi.pokemon.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Pokemon {

    private Long id;
    private String nom;
    private String type;
    private int niveau;
    private Dresseur dresseur;

    public Pokemon(String nom, String type, int niveau) {
        this.nom = nom;
        this.type = type;
        this.niveau = niveau;
    }
}
