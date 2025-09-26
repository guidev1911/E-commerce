package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.controller.swagger.CarrinhoControllerDoc;
import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoRequest;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.CarrinhoService;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class CarrinhoController implements CarrinhoControllerDoc {

    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;

    public CarrinhoController(CarrinhoService carrinhoService, UsuarioService usuarioService) {
        this.carrinhoService = carrinhoService;
        this.usuarioService = usuarioService;
    }

    @Override
    @PostMapping("/itens")
    public ResponseEntity<CarrinhoDTO> adicionarItem(@Valid @RequestBody ItemCarrinhoRequest request,
                                                     Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(
                carrinhoService.alterarItem(usuario, request.getProdutoId(), request.getQuantidade(), true)
        );
    }

    @Override
    @PutMapping("/itens/{produtoId}")
    public ResponseEntity<CarrinhoDTO> atualizarQuantidade(@PathVariable Long produtoId,
                                                           @Valid @RequestBody ItemCarrinhoRequest request,
                                                           Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(
                carrinhoService.alterarItem(usuario, produtoId, request.getQuantidade(), false)
        );
    }

    @Override
    @DeleteMapping("/itens/{produtoId}")
    public ResponseEntity<CarrinhoDTO> removerItem(@PathVariable Long produtoId,
                                                   Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(carrinhoService.removerItem(usuario, produtoId));
    }

    @Override
    @GetMapping
    public ResponseEntity<CarrinhoDTO> listarCarrinho(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(carrinhoService.listarCarrinho(usuario));
    }
}