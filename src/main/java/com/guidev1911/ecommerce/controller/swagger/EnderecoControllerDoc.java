package com.guidev1911.ecommerce.controller.swagger;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.exception.global.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Endereço", description = "Gerenciamento de endereços do usuário")
@RequestMapping("/usuarios/{usuarioId}/enderecos")
public interface EnderecoControllerDoc {

    @Operation(summary = "Adicionar endereço", description = "Registra um novo endereço para o usuário")
    @ApiResponse(responseCode = "200", description = "Endereço adicionado com sucesso",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<EnderecoDTO> adicionar(@PathVariable Long usuarioId, EnderecoDTO dto);

    @Operation(summary = "Listar endereços", description = "Retorna todos os endereços do usuário")
    @ApiResponse(responseCode = "200", description = "Endereços listados com sucesso",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<List<EnderecoDTO>> listar(@PathVariable Long usuarioId);

    @Operation(summary = "Atualizar endereço", description = "Atualiza um endereço existente do usuário")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "403", description = "Endereço não pertence ao usuário",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Usuário ou endereço não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<EnderecoDTO> atualizar(@PathVariable Long usuarioId,
                                          @PathVariable Long enderecoId,
                                          EnderecoDTO dto);

    @Operation(summary = "Deletar endereço", description = "Remove um endereço do usuário")
    @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso")
    @ApiResponse(responseCode = "403", description = "Endereço não pertence ao usuário",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Usuário ou endereço não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<Void> deletar(@PathVariable Long usuarioId, @PathVariable Long enderecoId);
}