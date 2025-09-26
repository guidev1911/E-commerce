package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.controller.swagger.EnderecoControllerDoc;
import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EnderecoController implements EnderecoControllerDoc {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @Override
    @PostMapping
    public ResponseEntity<EnderecoDTO> adicionar(@PathVariable Long usuarioId,
                                                 @RequestBody EnderecoDTO dto) {
        return ResponseEntity.ok(enderecoService.adicionarEndereco(usuarioId, dto));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EnderecoDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(enderecoService.listarEnderecos(usuarioId));
    }

    @Override
    @PutMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizar(@PathVariable Long usuarioId,
                                                 @PathVariable Long enderecoId,
                                                 @RequestBody EnderecoDTO dto) {
        return ResponseEntity.ok(enderecoService.atualizarEndereco(usuarioId, enderecoId, dto));
    }

    @Override
    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long usuarioId,
                                        @PathVariable Long enderecoId) {
        enderecoService.deletarEndereco(usuarioId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}