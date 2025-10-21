package br.com.fiap.consumoenergetico.controller;

import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import br.com.fiap.consumoenergetico.repo.ConsumoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/maintenance")
@Profile("test")
public class MaintenanceController {

    private final ConsumoRepository repo;

    public MaintenanceController(ConsumoRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/fix-negativos")
    @Transactional
    public ResponseEntity<?> fixNegativos(@RequestParam(defaultValue = "delete") String strategy) {
        try {
            List<ConsumoEnergetico> negativos = repo.findByQtdConsumoLessThan(0);
            int count = 0;

            if ("zero".equalsIgnoreCase(strategy)) {
                for (ConsumoEnergetico c : negativos) {
                    c.setQtdConsumo(0);
                }
                repo.saveAll(negativos);
                count = negativos.size();
                return ResponseEntity.ok("Negativos zerados: " + count);
            } else {
                repo.deleteAll(negativos);
                count = negativos.size();
                return ResponseEntity.ok("Negativos removidos: " + count);
            }
        } catch (Exception e) {
            // Retorna a causa no body para você enxergar do lado do curl/PowerShell
            return ResponseEntity.internalServerError()
                    .body("Erro ao corrigir negativos: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}