package com.guidev1911.ecommerce.controller.swagger;


import com.guidev1911.ecommerce.dto.PedidoCreateDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoPreviewDTO;
import com.guidev1911.ecommerce.exception.global.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Pedido", description = "Gerenciamento de pedidos do usuário")
@RequestMapping("/api/v1/pedidos")
public interface PedidoControllerDoc {

    @Operation(summary = "Criar pedido", description = "Cria um pedido real com base no carrinho e endereço do usuário")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Carrinho vazio ou endereço inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<PedidoDTO> criarPedido(Authentication authentication, PedidoCreateDTO dto);

    @Operation(summary = "Simular pedido", description = "Retorna um preview do pedido sem salvar no banco")
    @ApiResponse(responseCode = "200", description = "Preview do pedido retornado com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoPreviewDTO.class)))
    @ApiResponse(responseCode = "400", description = "Carrinho vazio ou endereço inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<PedidoPreviewDTO> simularPedido(Authentication authentication, PedidoCreateDTO dto);

    @Operation(summary = "Listar pedidos", description = "Lista todos os pedidos do usuário")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class)))
    ResponseEntity<List<PedidoDTO>> listarPedidos(Authentication authentication);

    @Operation(summary = "Buscar pedido por ID", description = "Retorna detalhes de um pedido específico")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id, Authentication authentication);

    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido se permitido pelo status")
    @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class)))
    @ApiResponse(responseCode = "409", description = "Tentativa de cancelar pedido em status não permitido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id, Authentication authentication);
}