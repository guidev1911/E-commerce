package com.guidev1911.ecommerce.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

public class ResponseUtil {
    public static <T> ResponseEntity<Object> singleOrList(List<T> lista) {
        Object retorno = (lista.size() == 1) ? lista.getFirst() : lista;
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
    }
}
