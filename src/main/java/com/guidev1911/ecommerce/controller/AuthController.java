package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.*;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        Usuario saved = usuarioService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", saved.getId(), "email", saved.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        AuthResponse resp = usuarioService.authenticate(req.getEmail(), req.getSenha());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        AuthResponse newTokens = usuarioService.refreshToken(refreshToken);
        return ResponseEntity.ok(newTokens);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarMe(@Valid @RequestBody UsuarioUpdateDTO dto,
                                                  Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        Usuario atualizado = usuarioService.atualizarUsuario(usuario.getId(), dto);

        UsuarioDTO response = new UsuarioDTO();
        response.setId(atualizado.getId());
        response.setNome(atualizado.getNome());
        response.setEmail(atualizado.getEmail());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> me(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        UsuarioDTO dto = new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
        return ResponseEntity.ok(dto);
    }

}