package fr.ipi.pokemon.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Dresseur {

    private Long id;
    private String nom;
    private String region;
    private List<Pokemon> pokemons = new ArrayList<>();

    public Dresseur(String nom, String region) {
        this.nom = nom;
        this.region = region;
    }
}
