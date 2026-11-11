# Pokémon Management API

Microservice Spring Boot de la Ligue Pokémon : des dresseurs, leurs Pokémon, et le calcul du niveau d'un dresseur exposé par `GET /dresseurs/{id}`.
Le sujet du TP porte sur les **tests** : plan de test écrit avant le code, développement en TDD, chaque couche testée isolément, couverture et tests par mutation dans la CI.

## Deux versions, deux branches

| Branche | Contenu | Tests |
|---|---|---|
| [`main`](../../tree/main) (V1) | Le sujet, testé à fond | 21 tests, PIT 15/15 |
| [`v2`](../../tree/v2) | Ce que les tests de la V1 ont révélé : type de Pokémon en enum `TypePokemon` (fini `"Feu"` / `"feu"`), type obligatoire, id non numérique, 204 vérifié de bout en bout | 23 tests, PIT 15/15 |

L'historique montre le TDD : les tests d'abord, rouges (`e55d22c`), puis un commit vert par couche.

## Règle calculée

```
niveau = somme des niveaux
       + 20  si au moins 3 types distincts
       + 10  par Pokémon de niveau ≥ 50
niveau = 1   si le dresseur n'a aucun Pokémon
```

`GET /dresseurs/{id}` répond :

- **200** avec `{"nomDresseur": "Sacha", "nombrePokemons": 3, "niveauDresseur": 135}` ;
- **204** si le dresseur n'existe pas (le sujet écrit 201, qui veut dire *Created* : voir le plan de test) ;
- **400** si l'id n'est pas strictement positif.

## Tests

Le détail (partitions, valeurs limites, table de décision, cas par cas) est dans [PLAN_DE_TEST.md](PLAN_DE_TEST.md).

| Couche | Test | Outils |
|---|---|---|
| Repository | `DresseurRepositoryTest` | `@DataJpaTest`, H2, `data.sql` |
| Service | `DresseurServiceTest` | Mockito, repository simulé |
| Controller | `DresseurControllerTest` | `@WebMvcTest`, `MockMvc`, service simulé |
| Toutes | `DresseurIntegrationTest` | `@SpringBootTest` + `MockMvc` : contrôleur, service et base H2 assemblés |

## Lancer

Prérequis : **JDK 21** (`JAVA_HOME` doit pointer dessus). Maven est fourni par le wrapper.

```sh
./mvnw clean install        # build + tests ; couverture : target/site/jacoco/index.html
./mvnw org.pitest:pitest-maven:mutationCoverage   # après install ; rapport : target/pit-reports/
./mvnw spring-boot:run      # API sur http://localhost:8080
```

La base de l'application est une H2 en mémoire, **vide** au démarrage : le jeu de données (Sacha, Ondine) ne sert qu'aux tests (`src/test/resources/data.sql`). Lancée telle quelle, l'API répond donc 204.

La CI GitHub Actions ([ci.yml](.github/workflows/ci.yml)) lance les deux premières commandes à chaque push ; elle échoue si un seul mutant survit.

## Stack

Java 21, Spring Boot 4.1, Spring Data JPA, H2, Lombok, JUnit 5, Mockito, JaCoCo, PIT.
