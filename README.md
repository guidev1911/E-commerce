# E-commerce 

### Autenticação JWT

Este projeto implementa autenticação baseada em **JWT (JSON Web Token)** com suporte a **refresh tokens rotativos** e expiração configurável.

---

## Endpoints

### Registrar usuário

**Método:** `POST`
**URL:** `/auth/register`

⚠️ **Importante:** endereço sendo associado ao usuário para posteriormente calcular o frete de acordo com o UF, a api identifica a região e calcula o valor do frete.

**Body (JSON):**

```json
{
  "email": "guilherme@example.com",
  "senha": "123456",
  "nome": "Guilherme",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 101",
    "bairro": "Centro",
    "cidade": "Aracaju",
    "estado": "SE",
    "cep": "49000-000",
    "pais": "Brasil",
    "principal": true
  }
}
```

**Resposta (201 Created):**

```json
{
  "id": 1,
  "email": "guilherme@example.com"
}
```

---

### Login

**Método:** `POST`
**URL:** `/auth/login`

**Body (JSON):**

```json
{
  "email": "guilherme@example.com",
  "senha": "123456"
}
```

**Resposta (200 OK):**

```json
{
  "accessToken": "<JWT_ACCESS_TOKEN>",
  "refreshToken": "<REFRESH_TOKEN>",
  "expiresIn": 3600000
}
```

* `accessToken`: válido por **1 hora** (3600000 ms).
* `refreshToken`: usado para renovar o access token.

---

### Acessar `/me` (usuário autenticado)

**Método:** `GET`
**URL:** `/auth/me`

**Headers:**

```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "Guilherme",
  "email": "guilherme@example.com"
}
```

---

### Refresh token

**Método:** `POST`
**URL:** `/auth/refresh`

**Body (JSON):**

```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

**Resposta (200 OK):**

```json
{
  "accessToken": "<NEW_JWT_ACCESS_TOKEN>",
  "refreshToken": "<NEW_REFRESH_TOKEN>",
  "expiresIn": 3600000
}
```

⚠️ **Importante:**

* O refresh token é **rotativo**: cada uso invalida o anterior.
* Se o refresh token expirar ou for reutilizado, o usuário deve **fazer login novamente**.
* Isso evita que refresh tokens comprometidos sejam reutilizados indefinidamente.


## ⚠️ Erros de Autenticação

Durante o processo de login, registro ou renovação de token, podem ser lançadas exceções específicas:

## Respostas de erro geradas pelo Auth

Todos os erros seguem a estrutura do `ApiError`:

```json
{
  "status": <http-status-code>,
  "error": "<HttpStatus reason>",
  "message": "<mensagem detalhada>",
  "path": "<endpoint que causou o erro>"
}
```

### 400 Bad Request

* **`EmailJaRegistradoException`**: email já existe

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email já registrado: guilherme@example.com",
  "path": "/auth/register"
}
```

* **`MethodArgumentNotValidException`**: campos inválidos no DTO (validação `@Valid`)

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "nome: O nome é obrigatório; senha: A senha é obrigatória",
  "path": "/auth/register"
}
```

### 401 Unauthorized

* **`CredenciaisInvalidasException`**: login falhou

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciais inválidas",
  "path": "/auth/login"
}
```

* **`RefreshTokenException`**: refresh token inválido, expirado ou reutilizado

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Refresh token inválido. Faça login novamente.",
  "path": "/auth/refresh"
}
```

### 404 Not Found

* **`UsuarioNaoEncontradoException`**: usuário não encontrado

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Usuário não encontrado: guilherme@example.com",
  "path": "/auth/me"
}
```

### 500 Internal Server Error

* **Exceção genérica**

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "<mensagem da exceção>",
  "path": "/algum-endpoint"
}
```

---

## Fluxo resumido

1. **Registrar usuário** → cria conta (`/auth/register`).
2. **Login** → retorna `accessToken` + `refreshToken`.
3. **Acessar recursos protegidos** → enviar `Authorization: Bearer <accessToken>`.
4. **Refresh** → troca refresh token válido por novo par (`/auth/refresh`).
5. **Expiração / rotatividade** → se o refresh token expira ou é reutilizado, o usuário precisa logar novamente.

# Categorias de produtos

Permite gerenciar categorias de produtos em um e-commerce. É possível **adicionar uma única categoria ou uma lista de categorias de uma vez**.

---

## Endpoints

### Criar categoria(s)

**Método:** `POST`
**URL:** `/api/v1/categorias`

**Body (JSON) — exemplo de 1 categoria:**

```json
{
  "nome": "hardware",
  "descricao": "peças de computador e periféricos"
}
```

**Body (JSON) — exemplo de lista de categorias:**

```json
[
  {"nome": "hardware", "descricao": "peças de computador e periféricos"},
  {"nome": "software", "descricao": "programas e aplicativos"},
  {"nome": "acessórios", "descricao": "periféricos e cabos"}
]
```

**Resposta (201 Created):**

* Retorna a categoria criada ou a lista de categorias.

```json
{
  "id": 1,
  "nome": "hardware",
  "descricao": "peças de computador e periféricos"
}
```

```json
[
  {"id": 1, "nome": "hardware", "descricao": "peças de computador e periféricos"},
  {"id": 2, "nome": "software", "descricao": "programas e aplicativos"}
]
```

---

### Listar categorias (paginado)

**Método:** `GET`
**URL:** `/api/v1/categorias`

**Parâmetros (opcional):** `page`, `size`, `sort`

**Resposta (200 OK):**

```json
{
  "content": [
    {"id": 1, "nome": "hardware", "descricao": "peças de computador e periféricos"},
    {"id": 2, "nome": "software", "descricao": "programas e aplicativos"}
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "empty": true,
      "unsorted": true,
      "sorted": false
    },
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "unsorted": true,
    "sorted": false
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}

```

---

### Buscar categoria por ID

**Método:** `GET`
**URL:** `/api/v1/categorias/{id}`

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "hardware",
  "descricao": "peças de computador e periféricos"
}
```

### Atualizar categoria

**Método:** `PUT`
**URL:** `/api/v1/categorias/{id}`

**Body (JSON):**

```json
{
  "nome": "hardware atualizado",
  "descricao": "novas descrições de hardware"
}
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "hardware atualizado",
  "descricao": "novas descrições de hardware"
}
```

### Deletar categoria

**Método:** `DELETE`
**URL:** `/api/v1/categorias/{id}`

**Resposta (204 No Content)** — sem corpo

---

## ⚠️Erros possíveis

Todos os erros retornam `ApiError` com a estrutura:

```json
{
  "status": <http-status-code>,
  "error": "<HttpStatus reason>",
  "message": "<mensagem detalhada>",
  "path": "<endpoint que causou o erro>"
}
```

### 404 Not Found — Categoria não encontrada

**Exemplo:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Categoria com ID 99 não encontrada.",
  "path": "/api/v1/categorias/99"
}
```

### 400 Bad Request — Validação de campos obrigatórios (usando `@Valid`)

**Exemplo:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "nome: O nome da categoria é obrigatório",
  "path": "/api/v1/categorias"
}
```

---

## Observações importantes

* O endpoint `POST /api/v1/categorias` aceita tanto um **objeto único** quanto uma **lista de objetos**. O sistema detecta automaticamente e cria corretamente todos os registros.
* Paginação em `GET /api/v1/categorias` permite controlar quantas categorias são retornadas por página e ordenação.
* Atualizações e exclusões só são possíveis se a categoria existir; caso contrário, será lançado um erro 404.
* O `CategoriaService` encapsula toda a lógica de criação, atualização, busca e exclusão.

# Produtos

Permite gerenciar produtos em um e-commerce. Suporta **criação de um produto ou múltiplos produtos de uma vez**, além de listagem paginada com filtros opcionais.

---

## Endpoints

### Criar produto(s)

**Método:** `POST`
**URL:** `/api/v1/produtos`

**Body (JSON) — exemplo de 1 produto:**

⚠️ **Importante:**: O porte do produto (peso, tamanho, fragilidade) dado pelo vendedor irá influenciar diretamente no cálculo de frete para envio, junto ao UF cadastrado no endereço do usuário.

```json
{
  "nome": "Gabinete AIGO c285",
  "descricao": "gabinete aquário em vidro",
  "preco": 400.00,
  "estoque": 50,
  "categoriaId": 8,
  "peso": "MEDIO",
  "tamanho": "MEDIO",
  "fragilidade": "ALTA"
}
```

**Body (JSON) — exemplo de lista de produtos:**

```json
[
  {
    "nome": "Gabinete AIGO c285",
    "descricao": "gabinete aquário em vidro",
    "preco": 400.00,
    "estoque": 50,
    "categoriaId": 8,
    "peso": "MEDIO",
    "tamanho": "MEDIO",
    "fragilidade": "ALTA"
  },
  {
    "nome": "Mouse Gamer XYZ",
    "descricao": "mouse com DPI ajustável",
    "preco": 120.00,
    "estoque": 100,
    "categoriaId": 8,
    "peso": "LEVE",
    "tamanho": "PEQUENO",
    "fragilidade": "BAIXA"
  }
]
```

**Resposta (201 Created):**

* Retorna o produto criado ou a lista de produtos.

```json
{
  "id": 1,
  "nome": "Gabinete AIGO c285",
  "descricao": "gabinete aquário em vidro",
  "preco": 400.00,
  "estoque": 50,
  "categoriaId": 8,
  "peso": "MEDIO",
  "tamanho": "MEDIO",
  "fragilidade": "ALTA"
}
```

```json
[
  {"id": 1, "nome": "Gabinete AIGO c285", "descricao": "gabinete aquário em vidro", "preco": 400.00, "estoque": 50, "categoriaId": 8, "peso": "MEDIO", "tamanho": "MEDIO", "fragilidade": "ALTA"},
  {"id": 2, "nome": "Mouse Gamer XYZ", "descricao": "mouse com DPI ajustável", "preco": 120.00, "estoque": 100, "categoriaId": 8, "peso": "LEVE", "tamanho": "PEQUENO", "fragilidade": "BAIXA"}
]
```

---

### Listar produtos com filtros (paginado)

**Método:** `GET`
**URL:** `/api/v1/produtos`

**Parâmetros todos opcionais:**

* `categoriaId` → filtrar por categoria
* `precoMin` → preço mínimo
* `precoMax` → preço máximo
* `nome` → busca parcial por nome
* `page`, `size`, `sort` → controle de paginação

**Ex: GET http://localhost:8080/api/v1/produtos?categoriaId=3&precoMin=1000&precoMax=3000&nome=Smartphone (sendo possivel passar 1 ou vários)**

**Resposta (200 OK):**

```json
{
  "content": [
    {"id": 1, "nome": "Gabinete AIGO c285", "descricao": "gabinete aquário em vidro", "preco": 400.00, "estoque": 50, "categoriaId": 8},
    {"id": 2, "nome": "Mouse Gamer XYZ", "descricao": "mouse com DPI ajustável", "preco": 120.00, "estoque": 100, "categoriaId": 8}
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "sort": {
            "empty": true,
            "unsorted": true,
            "sorted": false
        },
        "offset": 0,
        "unpaged": false,
        "paged": true
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 2,
    "size": 20,
    "number": 0,
    "sort": {
        "empty": true,
        "unsorted": true,
        "sorted": false
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
}
```

---

### Buscar produto por ID

**Método:** `GET`
**URL:** `/api/v1/produtos/{id}`

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "Gabinete AIGO c285",
  "descricao": "gabinete aquário em vidro",
  "preco": 400.00,
  "estoque": 50,
  "categoriaId": 8,
  "peso": "MEDIO",
  "tamanho": "MEDIO",
  "fragilidade": "ALTA"
}
```

### Atualizar produto

**Método:** `PUT`
**URL:** `/api/v1/produtos/{id}`

**Body (JSON):**

```json
{
  "nome": "Gabinete AIGO c285 Atualizado",
  "descricao": "Gabinete atualizado",
  "preco": 450.00,
  "estoque": 40,
  "categoriaId": 8,
  "peso": "MEDIO",
  "tamanho": "MEDIO",
  "fragilidade": "ALTA"
}
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "Gabinete AIGO c285 Atualizado",
  "descricao": "Gabinete atualizado",
  "preco": 450.00,
  "estoque": 40,
  "categoriaId": 8,
  "peso": "MEDIO",
  "tamanho": "MEDIO",
  "fragilidade": "ALTA"
}
```

### Deletar produto

**Método:** `DELETE`
**URL:** `/api/v1/produtos/{id}`

**Resposta (204 No Content)** — sem corpo

---

## Erros possíveis

* **404 Not Found** — Produto não encontrado (`ProdutoNaoEncontradoException`) ou Categoria inexistente (`CategoriaNaoEncontradaException`)
* **400 Bad Request** — Validação de campos obrigatórios usando `@Valid`

**Exemplo 404:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Produto com ID 99 não encontrado.",
  "path": "/api/v1/produtos/99"
}
```

**Exemplo 400:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "nome: O nome do produto é obrigatório; preco: O preço deve ser maior que zero",
  "path": "/api/v1/produtos"
}
```

---

## Observações importantes

* O endpoint `POST /api/v1/produtos` aceita **objeto único** ou **lista de objetos**. O sistema detecta e cria corretamente todos os registros.
* A listagem `GET /api/v1/produtos` permite filtros opcionais (`categoriaId`, `precoMin`, `precoMax`, `nome`) e paginação com `Pageable`.
* Para criar um produto, obrigatoriamente é necessario ter uma categoria para associar.

# Carrinho de Compras

Este serviço gerencia o carrinho de compras do usuário, permitindo **adicionar, atualizar, remover e listar itens**. Cada operação respeita a quantidade em estoque e valida a existência de produtos.

---

## Endpoints

### Adicionar item ao carrinho

**Método:** `POST`
**URL:** `/api/v1/carrinho/itens`

**Body (JSON):**

```json
{
  "produtoId": 1,
  "quantidade": 2
}
```
* Se o cliente fizer a mesma requisição a quantidade do produto é **somada**.

**Resposta (200 OK):**

```json
{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "nomeProduto": "Gabinete AIGO c285",
      "quantidade": 2,
      "precoUnitario": 400.0,
      "subtotal": 800.0
    }
  ],
  "total": 800.0
}
```

### Atualizar quantidade de um item

**Método:** `PUT`
**URL:** `/api/v1/carrinho/itens/{produtoId}`

**Body (JSON):**

```json
{
  "quantidade": 3
}
```

* A quantidade é **sobrescrita** pelo valor enviado.

**Resposta (200 OK):** JSON atualizado do carrinho (mesma estrutura do endpoint de adicionar item).

### Remover item do carrinho

**Método:** `DELETE`
**URL:** `/api/v1/carrinho/itens/{produtoId}`

**Resposta (200 OK):** JSON atualizado do carrinho após remoção.

### Listar itens do carrinho

**Método:** `GET`
**URL:** `/api/v1/carrinho`

**Resposta (200 OK):** JSON completo do carrinho, incluindo todos os itens e o total.

---

## Respostas de erro

### 400 Bad Request — Estoque insuficiente

* **`EstoqueInsuficienteException`** → quando a quantidade desejada é maior que o estoque disponível.

**Exemplo:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Estoque insuficiente para o produto ID 1. Pedido: 5, Disponível: 3",
  "path": "/api/v1/carrinho/itens"
}
```

### 404 Not Found — Produto ou carrinho não encontrado

* **`ProdutoNaoEncontradoException`** → produto não existe.
* **`CarrinhoNotFoundException`** → carrinho do usuário não encontrado.

**Exemplo:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Produto com ID 10 não encontrado.",
  "path": "/api/v1/carrinho/itens/10"
}
```

* **`CarrinhoVazioException`** (quando aplicável) → caso o carrinho esteja vazio e seja necessário uma validação.

**Exemplo:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Carrinho vazio, não é possível criar pedido.",
  "path": "/api/v1/pedidos"
}
```

---

## Observações importantes

* Os endpoints aceitam **alterações de quantidade incrementais** ou sobrescritas.
* O carrinho é **criado automaticamente** para o usuário, se ainda não existir.

# Pedidos

Este módulo gerencia a criação, simulação, listagem e cancelamento de pedidos.
Possui dois tipos principais de criação de pedido: **simulação (preview)** e **pedido real**.

---

## Endpoints

### Criar pedido real

**Método:** `POST`
**URL:** `/api/v1/pedidos`

**Body (JSON):**

```json
{
  "enderecoId": 1
}
```

> O pedido real:
>
> * Salva no banco de dados.
> * Limpa automaticamente o carrinho do usuário.
> * Calcula frete com base no endereço e nas categorias de peso/tamanho/fragilidade do produto.
> * Define status `PENDENTE` e expiração em 24h.

**Exemplo de resposta (201 Created):**

```json
{
    "id": 4,
    "usuarioId": 1,
    "itens": [
        {
            "produtoId": 10,
            "nomeProduto": "Processador AMD Ryzen 5 5600X",
            "quantidade": 3,
            "precoUnitario": 1400.00,
            "subtotal": 4200.00,
            "peso": "LEVE",
            "tamanho": "PEQUENO",
            "fragilidade": "ALTA"
        }
    ],
    "total": 4231.50,
    "status": "PENDENTE",
    "criadoEm": "2025-09-15T02:20:11.7173444",
    "expiraEm": "2025-09-16T02:20:11.7173444",
    "frete": 31.50,
    "enderecoId": 1
}
```

---

### Simular pedido (preview)

**Método:** `POST`
**URL:** `/api/v1/pedidos/preview`

**Body (JSON):**

```json
{
  "enderecoId": 1
}
```

> Retorna um **preview** do pedido sem salvar no banco, útil para calcular frete e conferir itens.

**Exemplo de resposta (200 OK):**

```json
{
    "itens": [
        {
            "produtoId": 10,
            "nomeProduto": "Processador AMD Ryzen 5 5600X",
            "quantidade": 3,
            "precoUnitario": 1400.00,
            "subtotal": 4200.00,
            "peso": "LEVE",
            "tamanho": "PEQUENO",
            "fragilidade": "ALTA"
        }
    ],
    "subtotal": 4200.00,
    "frete": 31.50,
    "total": 4231.50,
    "enderecoId": 1
}
```

---

### Listar pedidos do usuário

**Método:** `GET`
**URL:** `/api/v1/pedidos`

**Resposta (200 OK) — lista de pedidos do usuário:**

```json
[
  {
    "id": 4,
    "usuarioId": 1,
    "subtotal": 4200.00,
    "frete": 31.50,
    "total": 4231.50,
    "status": "PENDENTE",
    "criadoEm": "2025-09-15T02:20:11.7173444"
  },
  {
    "id": 3,
    "usuarioId": 1,
    "subtotal": 2500.00,
    "frete": 15.00,
    "total": 2515.00,
    "status": "CONCLUIDO",
    "criadoEm": "2025-09-12T15:30:00"
  }
]
```

---

### Buscar pedido por ID

**Método:** `GET`
**URL:** `/api/v1/pedidos/{id}`

**Resposta (200 OK):**

```json
{
    "id": 4,
    "usuarioId": 1,
    "itens": [
        {
            "produtoId": 10,
            "nomeProduto": "Processador AMD Ryzen 5 5600X",
            "quantidade": 3,
            "precoUnitario": 1400.00,
            "subtotal": 4200.00,
            "peso": "LEVE",
            "tamanho": "PEQUENO",
            "fragilidade": "ALTA"
        }
    ],
    "total": 4231.50,
    "status": "PENDENTE",
    "criadoEm": "2025-09-15T02:20:11.7173444",
    "expiraEm": "2025-09-16T02:20:11.7173444",
    "frete": 31.50,
    "enderecoId": 1
}
```

---

### Cancelar pedido

**Método:** `PUT`
**URL:** `/api/v1/pedidos/{id}/cancelar`

> Só é possível cancelar pedidos com status `PENDENTE`.
> Status `ENVIADO`, `CANCELADO`, `EXPIRADO` ou `CONCLUIDO` não podem ser cancelados.

**Resposta (200 OK):**

```json
{
  "id": 4,
  "usuarioId": 1,
  "status": "CANCELADO",
  "subtotal": 4200.00,
  "frete": 31.50,
  "total": 4231.50
}
```

---

## Cálculo de frete

O frete é calculado com base em:

1. **Peso do produto**: LEVE, MEDIO, PESADO.
2. **Tamanho do produto**: PEQUENO, MEDIO, GRANDE, ENORME.
3. **Fragilidade do produto**: BAIXA, MEDIA, ALTA.
4. **Região do endereço** (estado de entrega):

| Região       | Multiplicador |
| ------------ | ------------- |
| SE           | 1.0           |
| Nordeste     | 1.3           |
| Sudeste      | 2.5           |
| Sul          | 3.0           |
| Centro-Oeste | 3.5           |
| Norte        | 4.5           |
| Outros       | 3.0           |

> O frete é multiplicado pela quantidade de itens e pelo fator da região.

---

## Erros possíveis

* **400 Bad Request**

  * Carrinho vazio: `"Carrinho vazio, não é possível criar pedido."`
  * Endereço inválido ou não encontrado

* **404 Not Found**

  * Produto do carrinho não encontrado: `"Produto com ID X não encontrado."`

* **409 Conflict**

  * Tentativa de cancelar pedido em status não permitido

**Exemplo 400:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Carrinho vazio, não é possível criar pedido.",
  "path": "/api/v1/pedidos"
}
```

**Exemplo 404:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Produto com ID 10 não encontrado.",
  "path": "/api/v1/pedidos"
}
```

**Exemplo 409:**

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Não é possível cancelar este pedido no status atual: CONCLUIDO",
  "path": "/api/v1/pedidos/4/cancelar"
}
```

---

## Fluxo resumido

1. **Preview** (`/preview`) → simula o pedido, calcula frete e subtotal.
2. **Criar pedido real** (`POST /pedidos`) → salva no banco e limpa o carrinho.
3. **Listar pedidos** (`GET /pedidos`) → retorna histórico do usuário.
4. **Buscar por ID** (`GET /pedidos/{id}`) → retorna detalhes do pedido.
5. **Cancelar pedido** (`PUT /pedidos/{id}/cancelar`) → muda status para CANCELADO se permitido.

> Pedidos com status `PENDENTE` expiram automaticamente após 24h.
> Só é possível cancelar um pedido se ainda não foi pago.


# Simulação de Pagamento

Este serviço implementa uma **simulação de pagamento** para pedidos do e-commerce. Ele cobre o fluxo de início de pagamento, processamento assíncrono via callback e atualização automática de status do pedido e do pagamento.

---

## Endpoints

### Iniciar pagamento

**POST** `/pagamentos/iniciar/{pedidoId}`

**Parâmetros:**

* `pedidoId` (path) — ID do pedido a ser pago.
* `metodo` (query) — Método de pagamento (ex.: `CARTAO_CREDITO`, `PIX`, `BOLETO`).

**Exemplo de request:**

```http
POST /pagamentos/iniciar/4?metodo=CARTAO_CREDITO
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "status": "PENDENTE",
  "metodo": "CARTAO_CREDITO",
  "valor": 4231.50,
  "confirmadoEm": 2025-09-14 00:59:13.666932
}
```

⚠️ Observações:

* Apenas pedidos com status `PENDENTE` podem iniciar pagamento.
* O valor do pedido é recalculado automaticamente antes de criar o pagamento.
* O pagamento inicia como `PENDENTE`.

---

### Callback de pagamento

**POST** `/pagamentos/callback/{pagamentoId}`

**Parâmetros:**

* `pagamentoId` (path) — ID do pagamento.
* `aprovado` (query) — `true` se o pagamento foi aprovado, `false` caso contrário.

**Exemplo de request:**

```http
POST /pagamentos/callback/1?aprovado=true
```

**Descrição:**

* Este endpoint **simula a notificação do processador de pagamento**.
* Na simulação automática (`simularCallbackAsync`):

  * Um delay aleatório de 3 a 7 segundos é aplicado.
  * 80% de chance de aprovação do pagamento.

---

## Fluxo do pagamento

1. **Iniciar pagamento**:

   * Cria um objeto `Pagamento` vinculado ao pedido.
   * Status inicial: `PENDENTE`.

2. **Simulação assíncrona de callback**:

   * Chamado internamente via `@Async simularCallbackAsync`.
   * Aplica atraso aleatório e decide se o pagamento é aprovado (80% de chance) ou recusado.
   * Chama `processarCallback`.

3. **Processamento do callback** (`processarCallback`):

   * Se aprovado:

     * Verifica estoque de cada produto do pedido.

       * Se algum produto estiver com estoque insuficiente:

         * Pagamento: `RECUSADO`
         * Pedido: `CANCELADO`
     * Se estoque suficiente:

       * Deduz a quantidade dos produtos no estoque.
       * Atualiza status do pagamento: `APROVADO`
       * Atualiza status do pedido: `PAGO` e registra `pagoEm`.
   * Se recusado:

     * Pagamento: `RECUSADO`
     * Pedido: `CANCELADO`

---

## Status

### Pagamento (`StatusPagamento`)

* `PENDENTE` — criado, aguardando processamento.
* `APROVADO` — pagamento confirmado com sucesso.
* `RECUSADO` — pagamento recusado ou falha de estoque.

### Pedido (`StatusPedido`)

* `PENDENTE` — aguardando pagamento.
* `PAGO` — pagamento aprovado e estoque atualizado.
* `CANCELADO` — pagamento recusado ou pedido cancelado.

---

## Regras importantes

* Apenas pedidos com status `PENDENTE` podem ser pagos.
* O sistema verifica **estoque antes de aprovar o pagamento**.
* Pagamentos são simulados com delay e chance de aprovação aleatória.
* Toda atualização de status é **transacional** para garantir consistência.

---

## Exemplo de fluxo real

1. Pedido criado:

```json
{
  "id": 4,
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 10,
      "nomeProduto": "Processador AMD Ryzen 5 5600X",
      "quantidade": 3,
      "precoUnitario": 1400.00,
      "subtotal": 4200.00,
      "peso": "LEVE",
      "tamanho": "PEQUENO",
      "fragilidade": "ALTA"
    }
  ],
  "total": 4231.50,
  "status": "PENDENTE",
  "criadoEm": "2025-09-15T02:20:11.7173444",
  "expiraEm": "2025-09-16T02:20:11.7173444",
  "frete": 31.50,
  "enderecoId": 1
}
```

2. Início de pagamento:

```json
{
  "id": 1,
  "status": "PENDENTE",
  "metodo": "CARTAO_CREDITO",
  "valor": 4231.50,
  "confirmadoEm": null
}
```

3. Callback aprovado:

```json
{
  "id": 1,
  "status": "APROVADO",
  "metodo": "CARTAO_CREDITO",
  "valor": 4231.50,
  "confirmadoEm": "2025-09-15T02:25:30.123456"
}
```

* Pedido atualizado para `PAGO`.

---










