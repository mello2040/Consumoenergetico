package br.com.fiap.consumoenergetico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ConsumoCadastroDto(
        @NotNull Double qtdConsumo,
        @NotNull LocalDate data,
        @NotBlank String unidade
) { }