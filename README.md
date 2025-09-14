# E-commerce 

### Autenticação JWT

Este projeto implementa autenticação baseada em **JWT (JSON Web Token)** com suporte a **refresh tokens rotativos** e expiração configurável.

---

## Endpoints

### Registrar usuário

**Método:** `POST`
**URL:** `/auth/register`

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



