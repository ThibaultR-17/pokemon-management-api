package fr.ipi.pokemon.dto;

import lombok.*;

// EqualsAndHashCode : les tests comparent deux DTO par valeur (assertEquals)
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DresseurDTO {
    private String nomDresseur;
    private int nombrePokemons;
    private int niveauDresseur;
}
