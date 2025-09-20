package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.mapper.CategoriaMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaDTO dto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");

        dto = new CategoriaDTO();
        dto.setId(1L);
        dto.setNome("Eletrônicos");
    }

    @Test
    void deveCriarCategoriaUnica() {
        when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toDTO(categoria)).thenReturn(dto);

        CategoriaDTO resultado = categoriaService.criar(dto);

        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveCriarVariasCategorias() {
        List<CategoriaDTO> dtos = List.of(dto);

        when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        when(categoriaRepository.saveAll(List.of(categoria))).thenReturn(List.of(categoria));
        when(categoriaMapper.toDTO(categoria)).thenReturn(dto);

        List<CategoriaDTO> resultado = categoriaService.criarVarias(dtos);

        assertEquals(1, resultado.size());
        assertEquals("Eletrônicos", resultado.get(0).getNome());
        verify(categoriaRepository).saveAll(anyList());
    }

    @Test
    void deveCriarVariasCategoriasMesmoQueAlgumaSejaInvalidaNoDTO() {
        CategoriaDTO dtoInvalido = new CategoriaDTO();
        List<CategoriaDTO> dtos = List.of(dto, dtoInvalido);

        when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        when(categoriaMapper.toEntity(dtoInvalido)).thenReturn(new Categoria());
        when(categoriaRepository.saveAll(anyList())).thenReturn(List.of(categoria, new Categoria()));
        when(categoriaMapper.toDTO(any())).thenReturn(dto);

        List<CategoriaDTO> resultado = categoriaService.criarVarias(dtos);

        assertEquals(2, resultado.size());
        verify(categoriaRepository).saveAll(anyList());
    }

    @Test
    void deveListarTodasCategorias() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Categoria> page = new PageImpl<>(List.of(categoria));

        when(categoriaRepository.findAll(pageable)).thenReturn(page);
        when(categoriaMapper.toDTO(categoria)).thenReturn(dto);

        Page<CategoriaDTO> resultado = categoriaService.listarTodos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Eletrônicos", resultado.getContent().get(0).getNome());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toDTO(categoria)).thenReturn(dto);

        CategoriaDTO resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> categoriaService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarCategoria() {
        CategoriaDTO dtoAtualizado = new CategoriaDTO();
        dtoAtualizado.setId(1L);
        dtoAtualizado.setNome("Informática");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        doAnswer(invocation -> {
            CategoriaDTO dtoArg = invocation.getArgument(0);
            Categoria catArg = invocation.getArgument(1);
            catArg.setNome(dtoArg.getNome());
            return null;
        }).when(categoriaMapper).updateFromDto(dtoAtualizado, categoria);

        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toDTO(categoria)).thenReturn(dtoAtualizado);

        CategoriaDTO resultado = categoriaService.atualizar(1L, dtoAtualizado);

        assertEquals("Informática", resultado.getNome());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveDeletarCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.deletar(1L);

        verify(categoriaRepository).delete(categoria);
    }
}