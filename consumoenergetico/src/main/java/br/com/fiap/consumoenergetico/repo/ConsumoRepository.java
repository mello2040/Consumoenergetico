package br.com.fiap.consumoenergetico.repo;

import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumoRepository extends JpaRepository<ConsumoEnergetico, Long> {
    List<ConsumoEnergetico> findByQtdConsumoLessThan(double valor);
}
