package fr.ipi.pokemon.service;

import fr.ipi.pokemon.repository.DresseurRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DresseurService {

    private final DresseurRepository repository;

    public DresseurService(DresseurRepository repository) {
        this.repository = repository;
    }

    public int calculerNiveauDresseur(Long dresseurId) {
        throw new UnsupportedOperationException("À implémenter");
    }

    public Optional<DresseurDto> trouverDresseur(Long dresseurId) {
        throw new UnsupportedOperationException("À implémenter");
    }
}
