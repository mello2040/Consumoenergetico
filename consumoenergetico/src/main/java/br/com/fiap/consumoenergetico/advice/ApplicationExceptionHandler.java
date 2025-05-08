package br.com.fiap.consumoenergetico.advice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> manuserarArgumentosInvadlidos(MethodArgumentNotValidException erro){
        Map<String,String> erros = new HashMap<>();
        List<FieldError> fieldErrors = erro.getBindingResult().getFieldErrors();
        for(FieldError fieldError : fieldErrors){
            erros.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return erros;
    }
}
