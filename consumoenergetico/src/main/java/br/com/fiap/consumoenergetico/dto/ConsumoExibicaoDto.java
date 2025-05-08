package br.com.fiap.consumoenergetico.dto;

import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsumoExibicaoDto(
        Long id,
        @NotBlank(message = "Quantidade obrigatória")
        double qtdConsumo,
        @NotNull(message = "Data obrigatória")
        LocalDate data,
        @NotBlank(message = "Unidade obrigatória")
        String unidade
) {
    public ConsumoExibicaoDto(ConsumoEnergetico consumoEnergetico){
        this (
                consumoEnergetico.getId(),
                consumoEnergetico.getQtdConsumo(),
                consumoEnergetico.getData(),
                consumoEnergetico.getUnidade());
    }
}
