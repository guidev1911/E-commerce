package com.guidev1911.ecommerce.controller.swagger;


import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.exception.global.ApiError;
import com.guidev1911.ecommerce.model.MetodoPagamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Pagamento", description = "Simulação e gerenciamento de pagamentos")
@RequestMapping("/pagamentos")
public interface PagamentoControllerDoc {

    @Operation(summary = "Iniciar pagamento", description = "Cria um pagamento para um pedido existente")
    @ApiResponse(responseCode = "200", description = "Pagamento iniciado com sucesso",
            content = @Content(schema = @Schema(implementation = PagamentoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Pedido inválido ou não pendente",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    PagamentoDTO iniciarPagamento(@PathVariable Long pedidoId,
                                                  @RequestParam MetodoPagamento metodo) throws InterruptedException;

    @Operation(summary = "Callback de pagamento", description = "Processa o callback do pagamento (simulação do processador)")
    @ApiResponse(responseCode = "200", description = "Callback processado com sucesso")
    @ApiResponse(responseCode = "400", description = "Pagamento inválido ou já processado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    void callback(@PathVariable Long pagamentoId, @RequestParam boolean aprovado);
}