package br.com.fiap.consumoenergetico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConsumoNaoEncontradoException extends RuntimeException {

    public ConsumoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
