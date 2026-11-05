# Plan de test : Pokémon Management API

Microservice Spring Boot de la Ligue Pokémon : des dresseurs, leurs Pokémon, et le calcul du niveau d'un dresseur exposé par `GET /dresseurs/{id}`.
Ce plan liste les cas de test **avant** le code (TDD) : chaque cas devient un test rouge, puis le code minimal qui le fait passer.

## Périmètre

| Couche | Classe testée | Outils | Type de test |
|---|---|---|---|
| Repository | `DresseurRepository` | `@DataJpaTest`, H2, `src/test/resources/data.sql` | Intégration (vraie base en mémoire) |
| Service | `DresseurService` | Mockito (`@Mock`, `when(...).thenReturn(...)`) | Unitaire solitaire (repository simulé) |
| Controller | `DresseurController` | `@WebMvcTest`, `MockMvc`, `@MockitoBean` | Unitaire de la couche web (service simulé) |

Chaque couche est testée **isolée** : le service ne touche pas la base, le contrôleur ne calcule rien. Une règle n'est vérifiée qu'à un seul étage : le calcul du niveau dans le service, pas dans le contrôleur.

**Hors périmètre** : création / modification / suppression (le sujet ne demande que la lecture), validation des données (niveau négatif, type vide), sécurité.

## Règle testée

```
niveau = somme des niveaux
       + 20  si au moins 3 types distincts
       + 10  par Pokémon de niveau ≥ 50
niveau = 1   si le dresseur n'a aucun Pokémon
```

## Hypothèses et écarts au sujet

- **Dresseur inexistant → 204 No Content** : le sujet écrit « 201 », qui veut dire *Created* (RFC 9110, §15.3.2). Retenu : 204, « requête réussie, aucune ressource à renvoyer » (§15.3.5). Le 404 a été écarté parce qu'il ne distingue pas une route inconnue d'un dresseur inconnu.
- **Types distincts** : comparaison exacte des chaînes (`"Feu"` ≠ `"feu"`).
- **`Pokemon.dresseur`** : le sujet écrit « @One », qui n'existe pas en JPA ; c'est un `@ManyToOne`.
- **Service, dresseur inexistant** : `calculerNiveauDresseur` lève une exception (`NoSuchElementException`) ; le contrôleur passe par une méthode qui renvoie un `Optional<DresseurDTO>`.

## Conception des cas

### Partitions et valeurs limites

| Donnée | Partitions | Valeurs limites testées |
|---|---|---|
| Nombre de Pokémon | 0 / au moins 1 | 0 |
| Types distincts | moins de 3 / au moins 3 | 2 et 3 ; 3 Pokémon mais 2 types (piège : compter les Pokémon au lieu des types) |
| Niveau d'un Pokémon | < 50 / ≥ 50 | 49 et 50 |
| Nombre d'élites | 0 / 1 / plusieurs | 0, 1, 2 (le bonus est **par** Pokémon) |
| Dresseur | existe / n'existe pas | — |

### Table de décision du calcul

| | R1 | R2 | R3 | R4 | R5 | R6 |
|---|---|---|---|---|---|---|
| Aucun Pokémon | V | F | F | F | F | F |
| Au moins 3 types | – | F | V | F | F | V |
| Pokémon élites | – | 0 | 0 | 1 | 2 | ≥ 1 |
| **Résultat** | 1 | somme | somme + 20 | somme + 10 | somme + 20 (2 × 10) | somme + 20 + 10 × élites |
| **Cas** | S1 | S2, S4, S6 | S3 | S5 | S7 | S8 |

Toutes les colonnes ont au moins un cas.

## Cas de test

Résultats attendus **calculés à la main** et écrits en dur dans les tests (pas de calcul dans un test).

### Repository : `DresseurRepositoryTest`

Jeu de données (`data.sql`) : **Sacha** (Kanto) avec Pikachu (Électrik, 55), Salamèche (Feu, 30), Carapuce (Eau, 20) ; **Ondine** (Azuria) sans Pokémon. Insertion sans id explicite (ids générés par la base), pour ne pas gêner les `save` des tests.

| # | Test | Arrange | Act | Assert |
|---|---|---|---|---|
| R1 | `should_find_dresseur_with_his_pokemons_when_id_exists` | `data.sql` | `findById(id de Sacha)` | nom `Sacha`, région `Kanto`, 3 Pokémon |
| R2 | `should_find_dresseur_without_pokemon_when_he_has_none` | `data.sql` | `findById(id d'Ondine)` | 0 Pokémon |
| R3 | `should_return_empty_when_dresseur_does_not_exist` | — | `findById(999)` | `Optional` vide |
| R4 | `should_persist_pokemons_when_dresseur_is_saved` | nouveau dresseur + 1 Pokémon | `save`, `flush`, `clear`, `findById` | le Pokémon est relu depuis la base, rattaché au dresseur |

### Service : `DresseurServiceTest`

`DresseurRepository` simulé : `when(repository.findById(1L)).thenReturn(Optional.of(dresseur))`.

| # | Test | Pokémon du dresseur | Attendu |
|---|---|---|---|
| S1 | `should_return_1_when_dresseur_has_no_pokemon` | aucun | **1** |
| S2 | `should_return_sum_of_levels_when_no_bonus_applies` | Électrik 10, Feu 20 | **30** |
| S3 | `should_apply_diversity_bonus_when_dresseur_has_3_different_types` | Électrik 10, Feu 20, Eau 30 | 60 + 20 = **80** |
| S4 | `should_not_apply_diversity_bonus_when_3_pokemons_share_2_types` | Feu 10, Feu 20, Eau 30 | **60** |
| S5 | `should_apply_elite_bonus_when_pokemon_level_is_50` | Feu 50 | 50 + 10 = **60** |
| S6 | `should_not_apply_elite_bonus_when_pokemon_level_is_49` | Feu 49 | **49** |
| S7 | `should_apply_elite_bonus_per_pokemon_when_2_pokemons_are_elite` | Feu 50, Eau 60 | 110 + 2 × 10 = **130** |
| S8 | `should_cumulate_all_bonuses_when_3_types_and_2_elites` | Électrik 50, Feu 60, Eau 10 | 120 + 20 + 2 × 10 = **160** |
| S9 | `should_throw_when_dresseur_does_not_exist` | `findById` → `Optional.empty()` | `NoSuchElementException` (`assertThrows`) |
| S10 | `should_return_dto_when_dresseur_exists` | Sacha : Électrik 10, Feu 20 | DTO (`Sacha`, 2, 30) |
| S11 | `should_return_empty_when_dresseur_is_unknown` | `findById` → `Optional.empty()` | `Optional` vide |

### Controller : `DresseurControllerTest`

`DresseurService` simulé avec `@MockitoBean`.

| # | Test | Service simulé | Attendu |
|---|---|---|---|
| C1 | `should_return_200_and_dto_when_dresseur_exists` | renvoie `DresseurDTO("Sacha", 3, 135)` | **200**, JSON `nomDresseur`, `nombrePokemons`, `niveauDresseur` |
| C2 | `should_return_204_when_dresseur_does_not_exist` | renvoie `Optional.empty()` | **204**, corps vide |

## Conventions

- Nom : `should_[comportement_attendu]_when_[condition]()`.
- Corps en trois blocs commentés `// Arrange`, `// Act`, `// Assert` ; un seul *Act* par test ; assertions `assertEquals`.
- Données minimales : chaque test ne crée que ce dont il a besoin.

## Ordre TDD

Une couche à la fois, un cas à la fois, du plus simple au plus riche : R1 → R4, puis S1 → S11, puis C1 → C2.
Chaque cycle : test rouge, code minimal vert, refactor ; un commit quand tout est vert.

## Critères de sortie

- Tous les cas de ce plan écrits et verts.
- Chaque colonne de la table de décision et chaque valeur limite couvertes.
- `mvn install` réussit à 100 %.
