package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.service.CarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrinhos")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<CarrinhoDTO> criarCarrinho() {
        return ResponseEntity.ok(carrinhoService.criarCarrinho());
    }

    @PostMapping("/{carrinhoId}/itens")
    public ResponseEntity<CarrinhoDTO> adicionarItem(
            @PathVariable Long carrinhoId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {
        return ResponseEntity.ok(carrinhoService.adicionarItem(carrinhoId, produtoId, quantidade));
    }

    @DeleteMapping("/{carrinhoId}/itens/{produtoId}")
    public ResponseEntity<CarrinhoDTO> removerItem(
            @PathVariable Long carrinhoId,
            @PathVariable Long produtoId) {
        return ResponseEntity.ok(carrinhoService.removerItem(carrinhoId, produtoId));
    }

    @GetMapping("/{carrinhoId}")
    public ResponseEntity<CarrinhoDTO> listarCarrinho(@PathVariable Long carrinhoId) {
        return ResponseEntity.ok(carrinhoService.listarCarrinho(carrinhoId));
    }
}
