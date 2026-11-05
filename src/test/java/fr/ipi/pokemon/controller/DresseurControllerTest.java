package fr.ipi.pokemon.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ipi.pokemon.dto.DresseurDTO;
import fr.ipi.pokemon.service.DresseurService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(DresseurController.class)
class DresseurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DresseurService service;

    @Test
    void should_return_200_and_dto_when_dresseur_exists() throws Exception {
        // Arrange
        when(service.trouverDresseur(1L)).thenReturn(Optional.of(new DresseurDTO("Sacha", 3, 135)));

        // Act
        ResultActions reponse = mockMvc.perform(get("/dresseurs/1"));

        // Assert
        reponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDresseur").value("Sacha"))
                .andExpect(jsonPath("$.nombrePokemons").value(3))
                .andExpect(jsonPath("$.niveauDresseur").value(135));
    }

    @Test
    void should_return_204_when_dresseur_does_not_exist() throws Exception {
        // Arrange
        when(service.trouverDresseur(999L)).thenReturn(Optional.empty());

        // Act
        ResultActions reponse = mockMvc.perform(get("/dresseurs/999"));

        // Assert : 204 plutôt que le « 201 » du sujet (voir PLAN_DE_TEST.md)
        reponse.andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
