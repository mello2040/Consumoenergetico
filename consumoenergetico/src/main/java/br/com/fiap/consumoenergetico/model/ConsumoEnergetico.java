package br.com.fiap.consumoenergetico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "TBL_CONSUMO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoEnergetico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SEQ_CONSUMO")
    @SequenceGenerator(
            name = "SEQ_CONSUMO",
            sequenceName = "SEQ_CONSUMO",
            allocationSize = 1)
    private Long id;

    @Column(name = "QTD_CONSUMO")
    private double qtdConsumo;

    @Column(name = "DATA_CONSUMO")
    private LocalDate data;

    private String unidade;
}
