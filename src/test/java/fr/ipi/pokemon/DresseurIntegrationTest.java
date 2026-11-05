package fr.ipi.pokemon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Toutes les couches réelles (controller, service, repository, H2) : vérifie leur assemblage. */
@SpringBootTest
@AutoConfigureMockMvc
class DresseurIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_sacha_with_level_135_when_all_layers_are_wired() throws Exception {
        // Arrange : Sacha (id 1) et ses 3 Pokémon, insérés par data.sql

        // Act
        ResultActions reponse = mockMvc.perform(get("/dresseurs/1"));

        // Assert : 105 (55 + 30 + 20) + 20 (3 types) + 10 (Pikachu, niveau 55)
        reponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDresseur").value("Sacha"))
                .andExpect(jsonPath("$.nombrePokemons").value(3))
                .andExpect(jsonPath("$.niveauDresseur").value(135));
    }

    @Test
    void should_return_204_when_dresseur_is_not_in_database() throws Exception {
        // Arrange : aucun dresseur d'id 999 dans data.sql

        // Act
        ResultActions reponse = mockMvc.perform(get("/dresseurs/999"));

        // Assert
        reponse.andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
