package fr.ipi.pokemon.repository;

import fr.ipi.pokemon.entity.Dresseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DresseurRepository extends JpaRepository<Dresseur, Long> {
}
