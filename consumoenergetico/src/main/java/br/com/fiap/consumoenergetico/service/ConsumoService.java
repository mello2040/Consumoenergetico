package br.com.fiap.consumoenergetico.service;

import br.com.fiap.consumoenergetico.dto.ConsumoExibicaoDto;
import br.com.fiap.consumoenergetico.exception.ConsumoNaoEncontradoException;
import br.com.fiap.consumoenergetico.model.ConsumoEnergetico;
import br.com.fiap.consumoenergetico.repo.ConsumoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConsumoService {
    @Autowired
    private ConsumoRepo consumoRepo;

    public ConsumoExibicaoDto gravar(ConsumoEnergetico consumoEnergetico){
        return new ConsumoExibicaoDto(consumoRepo.save(consumoEnergetico));
    }

    public ConsumoExibicaoDto BuscarPorId(long id){
        Optional<ConsumoEnergetico> consumoOptional = consumoRepo.findById(id);
        if(consumoOptional.isPresent()){
            return new ConsumoExibicaoDto(consumoOptional.get());
        }else {
            throw new ConsumoNaoEncontradoException("Consumo não encontrado");
        }
    }
    public Page<ConsumoExibicaoDto> listarConsumos(Pageable pageable){
        return consumoRepo.findAll(pageable).map(ConsumoExibicaoDto::new);
    }

    public void remover(long id){
        Optional<ConsumoEnergetico> consumoOptional = consumoRepo.findById(id);
        if(consumoOptional.isPresent()){
            consumoRepo.delete(consumoOptional.get());
        }else {
            throw new ConsumoNaoEncontradoException("Contato nâo encontrado");
        }

    }
    public ConsumoEnergetico atualizar(ConsumoEnergetico consumoEnergetico){
        Optional<ConsumoEnergetico> consumoOptional = consumoRepo.findById(consumoEnergetico.getId());
        if(consumoOptional.isPresent()){
            return consumoRepo.save(consumoEnergetico);
        }else{
            throw new ConsumoNaoEncontradoException("Contato nâo encontrado");
        }
    }
}
