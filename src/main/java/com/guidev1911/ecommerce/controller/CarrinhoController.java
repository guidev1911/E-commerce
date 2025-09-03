package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.CarrinhoService;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;

    public CarrinhoController(CarrinhoService carrinhoService, UsuarioService usuarioService) {
        this.carrinhoService = carrinhoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/itens")
    public ResponseEntity<CarrinhoDTO> adicionarItem(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(carrinhoService.adicionarItem(usuario, produtoId, quantidade));
    }

    @DeleteMapping("/itens/{produtoId}")
    public ResponseEntity<CarrinhoDTO> removerItem(
            @PathVariable Long produtoId,
            Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(carrinhoService.removerItem(usuario, produtoId));
    }

    @GetMapping
    public ResponseEntity<CarrinhoDTO> listarCarrinho(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(carrinhoService.listarCarrinho(usuario));
    }
}