package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/{usuarioId}/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO> adicionar(@PathVariable Long usuarioId,
                                                 @Valid @RequestBody EnderecoDTO dto) {
        return ResponseEntity.ok(enderecoService.adicionarEndereco(usuarioId, dto));
    }

    @GetMapping
    public ResponseEntity<List<EnderecoDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(enderecoService.listarEnderecos(usuarioId));
    }

    @PutMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizar(@PathVariable Long usuarioId,
                                                 @PathVariable Long enderecoId,
                                                 @Valid @RequestBody EnderecoDTO dto) {
        return ResponseEntity.ok(enderecoService.atualizarEndereco(usuarioId, enderecoId, dto));
    }

    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long usuarioId,
                                        @PathVariable Long enderecoId) {
        enderecoService.deletarEndereco(usuarioId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}