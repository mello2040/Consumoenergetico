package br.com.fiap.consumoenergetico.repo;

import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsumoRepo extends JpaRepository<ConsumoEnergetico, Long> {
}
