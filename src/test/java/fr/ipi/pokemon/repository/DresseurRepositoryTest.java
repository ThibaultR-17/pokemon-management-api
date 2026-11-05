package fr.ipi.pokemon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class DresseurRepositoryTest {

    // Ids attribués par data.sql
    private static final Long SACHA_ID = 1L;
    private static final Long ONDINE_ID = 2L;
    private static final Long ID_INCONNU = 999L;

    @Autowired
    private DresseurRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_find_dresseur_with_his_pokemons_when_id_exists() {
        // Arrange : Sacha et ses 3 Pokémon, insérés par data.sql

        // Act
        Dresseur sacha = repository.findById(SACHA_ID).orElseThrow();

        // Assert
        assertEquals("Sacha", sacha.getNom());
        assertEquals("Kanto", sacha.getRegion());
        assertEquals(3, sacha.getPokemons().size());
    }

    @Test
    void should_find_dresseur_without_pokemon_when_he_has_none() {
        // Arrange : Ondine, sans Pokémon, insérée par data.sql

        // Act
        Dresseur ondine = repository.findById(ONDINE_ID).orElseThrow();

        // Assert
        assertEquals(0, ondine.getPokemons().size());
    }

    @Test
    void should_return_empty_when_dresseur_does_not_exist() {
        // Arrange : aucun dresseur avec cet id dans data.sql

        // Act
        Optional<Dresseur> resultat = repository.findById(ID_INCONNU);

        // Assert
        assertEquals(Optional.empty(), resultat);
    }

    @Test
    void should_persist_pokemons_when_dresseur_is_saved() {
        // Arrange
        Dresseur pierre = new Dresseur("Pierre", "Argenta");
        Pokemon onix = new Pokemon("Onix", "Roche", 40);
        onix.setDresseur(pierre);
        pierre.getPokemons().add(onix);

        // Act
        Long id = repository.saveAndFlush(pierre).getId();

        // Assert : relecture depuis la base, pas depuis le cache JPA
        entityManager.clear();
        Dresseur relu = repository.findById(id).orElseThrow();
        assertEquals(1, relu.getPokemons().size());
        assertEquals("Onix", relu.getPokemons().get(0).getNom());
    }
}
