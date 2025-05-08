package br.com.fiap.consumoenergetico.controller;


import br.com.fiap.consumoenergetico.dto.ConsumoExibicaoDto;
import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import br.com.fiap.consumoenergetico.service.ConsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConsumoController {
    @Autowired
    private ConsumoService consumoService;

    @PostMapping("/consumo")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumoExibicaoDto gravar(@RequestBody @Valid ConsumoEnergetico consumoEnergetico){
        return consumoService.gravar(consumoEnergetico);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/consumo")
    public Page<ConsumoExibicaoDto> listarConsumoEnergetico(Pageable pageable){
        return consumoService.listarConsumos(pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/consumo/{id}")
    public void removerConsumo(@PathVariable long id){
        consumoService.remover(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/consumo")
    public ConsumoEnergetico atualizarConsumo(@RequestBody ConsumoEnergetico consumoEnergetico){
        return consumoService.atualizar(consumoEnergetico);
    }
}
