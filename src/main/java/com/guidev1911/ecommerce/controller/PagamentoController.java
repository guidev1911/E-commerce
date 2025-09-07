package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.model.MetodoPagamento;
import com.guidev1911.ecommerce.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/iniciar/{pedidoId}")
    public PagamentoDTO iniciarPagamento(@PathVariable Long pedidoId,
                                         @RequestParam MetodoPagamento metodo) throws InterruptedException {
        return pagamentoService.iniciarPagamento(pedidoId, metodo);
    }

    @PostMapping("/callback/{pagamentoId}")
    public void callback(@PathVariable Long pagamentoId,
                         @RequestParam boolean aprovado) {
        pagamentoService.processarCallback(pagamentoId, aprovado);
    }
}

