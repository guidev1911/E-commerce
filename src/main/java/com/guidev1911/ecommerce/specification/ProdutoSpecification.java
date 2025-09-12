package com.guidev1911.ecommerce.specification;


import com.guidev1911.ecommerce.model.Produto;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProdutoSpecification {

    public static Specification<Produto> comCategoria(Long categoriaId) {
        return (root, query, cb) -> categoriaId == null ? null :
                cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Produto> comPrecoEntre(BigDecimal precoMin, BigDecimal precoMax) {
        return (root, query, cb) -> {
            if (precoMin != null && precoMax != null) {
                return cb.between(root.get("preco"), precoMin, precoMax);
            } else if (precoMin != null) {
                return cb.greaterThanOrEqualTo(root.get("preco"), precoMin);
            } else if (precoMax != null) {
                return cb.lessThanOrEqualTo(root.get("preco"), precoMax);
            }
            return null;
        };
    }

    public static Specification<Produto> comNome(String nome) {
        return (root, query, cb) -> nome == null ? null :
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
}