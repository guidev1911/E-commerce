package com.guidev1911.ecommerce.controller.swagger;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoRequest;
import com.guidev1911.ecommerce.exception.global.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras do usuário autenticado")
@RequestMapping("/api/v1/carrinho")
public interface CarrinhoControllerDoc {

    @Operation(summary = "Adicionar item ao carrinho",
            description = "Adiciona um item ao carrinho. Caso o produto já esteja presente, a quantidade será somada.")
    @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso",
            content = @Content(schema = @Schema(implementation = CarrinhoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou carrinho inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CarrinhoDTO> adicionarItem(@Valid ItemCarrinhoRequest request,
                                              Authentication authentication);

    @Operation(summary = "Atualizar quantidade de um item",
            description = "Atualiza a quantidade de um item já existente no carrinho (sobrescreve o valor).")
    @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = CarrinhoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Estoque insuficiente",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Produto ou carrinho não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CarrinhoDTO> atualizarQuantidade(Long produtoId,
                                                    @Valid ItemCarrinhoRequest request,
                                                    Authentication authentication);

    @Operation(summary = "Remover item do carrinho",
            description = "Remove um item específico do carrinho.")
    @ApiResponse(responseCode = "200", description = "Item removido com sucesso",
            content = @Content(schema = @Schema(implementation = CarrinhoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Produto ou carrinho não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CarrinhoDTO> removerItem(Long produtoId,
                                            Authentication authentication);

    @Operation(summary = "Listar carrinho",
            description = "Retorna todos os itens do carrinho do usuário, incluindo o total.")
    @ApiResponse(responseCode = "200", description = "Carrinho retornado com sucesso",
            content = @Content(schema = @Schema(implementation = CarrinhoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Carrinho não encontrado ou vazio",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CarrinhoDTO> listarCarrinho(Authentication authentication);
}