package fr.ipi.pokemon.service;

import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import fr.ipi.pokemon.repository.DresseurRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// readOnly : la liste des Pokémon (chargée à la demande) est lue dans la transaction
@Service
@Transactional(readOnly = true)
public class DresseurService {

    private static final int TYPES_DIVERSITE = 3;
    private static final int BONUS_DIVERSITE = 20;
    private static final int NIVEAU_ELITE = 50;
    private static final int BONUS_ELITE = 10;

    private final DresseurRepository repository;

    public DresseurService(DresseurRepository repository) {
        this.repository = repository;
    }

    public int calculerNiveauDresseur(Long dresseurId) {
        return niveau(repository.findById(dresseurId).orElseThrow());
    }

    public Optional<DresseurDto> trouverDresseur(Long dresseurId) {
        return repository.findById(dresseurId)
                .map(d -> new DresseurDto(d.getNom(), d.getPokemons().size(), niveau(d)));
    }

    private int niveau(Dresseur dresseur) {
        List<Pokemon> pokemons = dresseur.getPokemons();
        if (pokemons.isEmpty()) {
            return 1;
        }
        int somme = pokemons.stream().mapToInt(Pokemon::getNiveau).sum();
        long types = pokemons.stream().map(Pokemon::getType).distinct().count();
        long elites = pokemons.stream().filter(p -> p.getNiveau() >= NIVEAU_ELITE).count();
        return somme + (types >= TYPES_DIVERSITE ? BONUS_DIVERSITE : 0) + (int) elites * BONUS_ELITE;
    }
}
