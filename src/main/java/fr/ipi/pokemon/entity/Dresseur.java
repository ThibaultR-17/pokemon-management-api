package fr.ipi.pokemon.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Dresseur {

    // IDENTITY : la base fournit l'id, ce qui permet les INSERT sans id de data.sql
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String region;

    @OneToMany(mappedBy = "dresseur", cascade = CascadeType.ALL)
    private List<Pokemon> pokemons = new ArrayList<>();

    public Dresseur(String nom, String region) {
        this.nom = nom;
        this.region = region;
    }
}
