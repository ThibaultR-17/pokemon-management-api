package fr.ipi.pokemon.controller;

import fr.ipi.pokemon.service.DresseurDto;
import fr.ipi.pokemon.service.DresseurService;
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
    public ResponseEntity<DresseurDto> trouverDresseur(@PathVariable Long id) {
        throw new UnsupportedOperationException("À implémenter");
    }
}
