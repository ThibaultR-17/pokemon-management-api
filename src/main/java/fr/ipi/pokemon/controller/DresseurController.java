package fr.ipi.pokemon.controller;

import fr.ipi.pokemon.dto.DresseurDTO;
import fr.ipi.pokemon.service.DresseurService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dresseurs")
public class DresseurController {

    private final DresseurService service;

    public DresseurController(DresseurService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DresseurDTO> trouverDresseur(@PathVariable @Positive Long id) {
        // id <= 0 : 400, vérifié par Bean Validation avant d'entrer dans la méthode
        // 204 si absent : requête réussie, aucune ressource (le sujet écrit 201, qui signifie Created)
        return service.trouverDresseur(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
