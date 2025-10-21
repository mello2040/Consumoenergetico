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

import br.com.fiap.consumoenergetico.dto.ConsumoCadastroDto;
import br.com.fiap.consumoenergetico.dto.ConsumoExibicaoDto;
import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import br.com.fiap.consumoenergetico.service.ConsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConsumoController {

    @Autowired
    private ConsumoService consumoService;

    @PostMapping("/consumo")
    public ResponseEntity<ConsumoExibicaoDto> gravar(@RequestBody @Valid ConsumoCadastroDto dto) {
        // mapeia o DTO de entrada para a entidade que o service já usa
        ConsumoEnergetico entity = new ConsumoEnergetico();
        entity.setQtdConsumo(dto.qtdConsumo());
        entity.setData(dto.data());
        entity.setUnidade(dto.unidade());

        ConsumoExibicaoDto salvo = consumoService.gravar(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/consumo")
    public Page<ConsumoExibicaoDto> listarConsumoEnergetico(Pageable pageable){
        return consumoService.listarConsumos(pageable);
    }

    @DeleteMapping("/consumo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerConsumo(@PathVariable long id){
        consumoService.remover(id);
    }

    @PutMapping("/consumo")
    public ResponseEntity<ConsumoEnergetico> atualizarConsumo(@RequestBody @Valid ConsumoEnergetico consumoEnergetico){
        ConsumoEnergetico atualizado = consumoService.atualizar(consumoEnergetico);
        return ResponseEntity.ok(atualizado);
    }
}
