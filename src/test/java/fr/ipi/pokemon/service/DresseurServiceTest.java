package fr.ipi.pokemon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import fr.ipi.pokemon.dto.DresseurDTO;
import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import fr.ipi.pokemon.repository.DresseurRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DresseurServiceTest {

    private static final Long ID = 1L;

    @Mock
    private DresseurRepository repository;

    @InjectMocks
    private DresseurService service;

    /** Le repository simulé renvoie Sacha avec ces Pokémon. */
    private void sachaPossede(Pokemon... pokemons) {
        Dresseur sacha = new Dresseur("Sacha", "Kanto");
        sacha.getPokemons().addAll(List.of(pokemons));
        when(repository.findById(ID)).thenReturn(Optional.of(sacha));
    }

    @Test
    void should_return_1_when_dresseur_has_no_pokemon() {
        // Arrange
        sachaPossede();

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert
        assertEquals(1, niveau);
    }

    @Test
    void should_return_sum_of_levels_when_no_bonus_applies() {
        // Arrange : 2 types, aucun élite
        sachaPossede(new Pokemon("Pikachu", "Électrik", 10), new Pokemon("Salamèche", "Feu", 20));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 10 + 20
        assertEquals(30, niveau);
    }

    @Test
    void should_apply_diversity_bonus_when_dresseur_has_3_different_types() {
        // Arrange
        sachaPossede(new Pokemon("Pikachu", "Électrik", 10), new Pokemon("Salamèche", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 60 + 20
        assertEquals(80, niveau);
    }

    @Test
    void should_not_apply_diversity_bonus_when_3_pokemons_share_2_types() {
        // Arrange : 3 Pokémon mais 2 types distincts
        sachaPossede(new Pokemon("Salamèche", "Feu", 10), new Pokemon("Goupix", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 60, sans bonus
        assertEquals(60, niveau);
    }

    @Test
    void should_apply_elite_bonus_when_pokemon_level_is_50() {
        // Arrange : borne basse des élites
        sachaPossede(new Pokemon("Dracaufeu", "Feu", 50));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 50 + 10
        assertEquals(60, niveau);
    }

    @Test
    void should_not_apply_elite_bonus_when_pokemon_level_is_49() {
        // Arrange : juste sous la borne
        sachaPossede(new Pokemon("Reptincel", "Feu", 49));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert
        assertEquals(49, niveau);
    }

    @Test
    void should_apply_elite_bonus_per_pokemon_when_2_pokemons_are_elite() {
        // Arrange : 2 élites, 2 types
        sachaPossede(new Pokemon("Dracaufeu", "Feu", 50), new Pokemon("Tortank", "Eau", 60));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 110 + 2 × 10
        assertEquals(130, niveau);
    }

    @Test
    void should_cumulate_all_bonuses_when_3_types_and_2_elites() {
        // Arrange
        sachaPossede(new Pokemon("Raichu", "Électrik", 50), new Pokemon("Dracaufeu", "Feu", 60),
                new Pokemon("Carapuce", "Eau", 10));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 120 + 20 (diversité) + 2 × 10 (élites)
        assertEquals(160, niveau);
    }

    @Test
    void should_throw_when_dresseur_does_not_exist() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.empty());

        // Act
        Executable calcul = () -> service.calculerNiveauDresseur(ID);

        // Assert
        assertThrows(NoSuchElementException.class, calcul);
    }

    @Test
    void should_return_dto_when_dresseur_exists() {
        // Arrange
        sachaPossede(new Pokemon("Pikachu", "Électrik", 10), new Pokemon("Salamèche", "Feu", 20));

        // Act
        Optional<DresseurDTO> dto = service.trouverDresseur(ID);

        // Assert : 2 Pokémon, niveau 10 + 20
        assertEquals(Optional.of(new DresseurDTO("Sacha", 2, 30)), dto);
    }

    @Test
    void should_return_empty_when_dresseur_is_unknown() {
        // Arrange
        when(repository.findById(ID)).thenReturn(Optional.empty());

        // Act
        Optional<DresseurDTO> dto = service.trouverDresseur(ID);

        // Assert
        assertEquals(Optional.empty(), dto);
    }

    @Test
    void should_apply_diversity_bonus_when_dresseur_has_more_than_3_types() {
        // Arrange : 4 types, « au moins 3 » et pas « exactement 3 »
        sachaPossede(new Pokemon("Pikachu", "Électrik", 10), new Pokemon("Salamèche", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30), new Pokemon("Bulbizarre", "Plante", 40));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 100 + 20
        assertEquals(120, niveau);
    }

    @Test
    void should_count_types_case_sensitively_when_types_differ_only_by_case() {
        // Arrange : « Feu » et « feu » sont deux types (hypothèse du plan)
        sachaPossede(new Pokemon("Salamèche", "Feu", 10), new Pokemon("Goupix", "feu", 20),
                new Pokemon("Carapuce", "Eau", 30));

        // Act
        int niveau = service.calculerNiveauDresseur(ID);

        // Assert : 60 + 20, 3 types distincts
        assertEquals(80, niveau);
    }
}
