package com.guidev1911.ecommerce.controller.swagger;

import com.guidev1911.ecommerce.dto.*;
import com.guidev1911.ecommerce.exception.global.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.validation.Valid;

@Tag(name = "Autenticação", description = "Endpoints para registro, login, refresh token e dados do usuário autenticado")
@RequestMapping("/auth")
public interface AuthControllerDoc {

    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
            content = @Content(schema = @Schema(example = """
                    {
                      "id": 1,
                      "email": "guilherme@example.com"
                    }
                    """)))
    @ApiResponse(responseCode = "400", description = "Email já existe ou DTO inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO dto);

    @Operation(summary = "Login", description = "Autentica o usuário e retorna tokens JWT")
    @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
            content = @Content(schema = @Schema(example = """
                    {
                      "accessToken": "<JWT_ACCESS_TOKEN>",
                      "refreshToken": "<REFRESH_TOKEN>",
                      "expiresIn": 3600000
                    }
                    """)))
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req);

    @Operation(summary = "Atualizar token", description = "Gera um novo par de tokens a partir de um refresh token válido")
    @ApiResponse(responseCode = "200", description = "Tokens renovados",
            content = @Content(schema = @Schema(example = """
                    {
                      "accessToken": "<NEW_JWT_ACCESS_TOKEN>",
                      "refreshToken": "<NEW_REFRESH_TOKEN>",
                      "expiresIn": 3600000
                    }
                    """)))
    @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body);

    @Operation(summary = "Atualizar meus dados", description = "Atualiza informações do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<UsuarioDTO> atualizarMe(@Valid @RequestBody UsuarioUpdateDTO dto,
                                           Authentication authentication);

    @Operation(summary = "Obter meus dados", description = "Retorna informações do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<UsuarioDTO> me(Authentication authentication);
}