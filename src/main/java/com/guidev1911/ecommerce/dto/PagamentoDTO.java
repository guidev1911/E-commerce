package com.guidev1911.ecommerce.dto;


import com.guidev1911.ecommerce.model.MetodoPagamento;
import com.guidev1911.ecommerce.model.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoDTO(
        Long id,
        StatusPagamento status,
        MetodoPagamento metodo,
        BigDecimal valor,
        LocalDateTime confirmadoEm
) {}