package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.controller.swagger.PagamentoControllerDoc;
import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.model.MetodoPagamento;
import com.guidev1911.ecommerce.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PagamentoController implements PagamentoControllerDoc {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @Override
    @PostMapping("/iniciar/{pedidoId}")
    public PagamentoDTO iniciarPagamento(@PathVariable Long pedidoId,
                                         @RequestParam MetodoPagamento metodo) throws InterruptedException {
        return pagamentoService.iniciarPagamento(pedidoId, metodo);
    }

    @Override
    @PostMapping("/callback/{pagamentoId}")
    public void callback(@PathVariable Long pagamentoId,
                         @RequestParam boolean aprovado) {
        pagamentoService.processarCallback(pagamentoId, aprovado);
    }
}

