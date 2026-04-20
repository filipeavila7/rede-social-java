# API Routes

Documentacao atualizada a partir dos `controllers`, `services`, DTOs e entidades em `src/main/java/com/example/demo`.

Base URL local: `http://localhost:8080`

## Regras gerais

- Auth publica: `/auth/**`, `POST /users` e `/files/**`
- Demais rotas: exigem JWT no header `Authorization: Bearer <token>`
- Quando a rota retorna `204 No Content`, nao existe JSON no corpo
- Algumas rotas retornam numero puro no corpo. Isso continua sendo JSON valido, por exemplo: `3`
- `GET /files/{filename}` nao retorna JSON; retorna o arquivo da imagem

## Auth

Controller: `AuthController`

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/auth/login` | Publico | `{ "email": "user@email.com", "senha": "123456" }` | `200 OK` com `{ "token": "jwt...", "type": "Bearer" }` |

## Users

Controller: `UserController`

JSON de `User` retornado hoje:

```json
{
  "id": 1,
  "nome": "Ana",
  "email": "ana@email.com"
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| GET | `/users` | JWT | sem body | `200 OK` com array de `User` |
| GET | `/users/me` | JWT | sem body | `200 OK` com um `User` do usuario logado |
| POST | `/users` | Publico | `{ "nome": "Ana", "email": "ana@email.com", "senha": "123456" }` | `201 Created` com `User` criado. `senha` nao volta no JSON |
| PUT | `/users/{id}` | JWT | qualquer subset de `{ "nome", "email", "senha" }` | `200 OK` com `User` atualizado. `senha` nao volta no JSON |
| DELETE | `/users/{id}` | JWT | sem body | `204 No Content` |

## Posts

Controller: `PostController`

JSON de `Post` retornado hoje:

```json
{
  "id": 10,
  "content": "Meu post",
  "imageUrl": "http://localhost:8080/files/abc123.jpg",
  "user": {
    "id": 1,
    "nome": "Ana",
    "email": "ana@email.com"
  },
  "createdAt": "2026-04-18T10:00:00",
  "likesCount": 2,
  "commentsCount": 1
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| GET | `/posts` | JWT | sem body | `200 OK` com array de `Post`, ordenado por `createdAt desc` |
| GET | `/posts/user/me` | JWT | sem body | `200 OK` com array de `Post` do usuario logado |
| GET | `/posts/user?email=...` | JWT | sem body | `200 OK` com array de `Post` do email informado |
| GET | `/posts/user/{userId}/count` | JWT | sem body | `200 OK` com numero JSON, ex.: `5` |
| GET | `/posts/{postId}/stats` | JWT | sem body | `200 OK` com `{ "likes": 2, "comments": 1 }` |
| POST | `/posts` | JWT | `{ "content": "Texto", "imageUrl": "/files/abc123.jpg" }` | `201 Created` com `Post` criado. `user` e `createdAt` sao preenchidos no backend |
| PUT | `/posts/{id}` | JWT | `{ "content": "Novo texto", "imageUrl": "/files/novo.jpg" }` | `200 OK` com `Post` atualizado |
| DELETE | `/posts/{id}` | JWT | sem body | `204 No Content` |

## Likes

Controller: `LikeController`

JSON de `Like` retornado hoje:

```json
{
  "id": 7,
  "post": {
    "id": 10,
    "content": "Meu post",
    "imageUrl": "http://localhost:8080/files/abc123.jpg",
    "user": {
      "id": 1,
      "nome": "Ana",
      "email": "ana@email.com"
    },
    "createdAt": "2026-04-18T10:00:00",
    "likesCount": 2,
    "commentsCount": 1
  },
  "user": {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com"
  }
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/posts/{postId}/likes` | JWT | sem body | `201 Created` com objeto `Like` |
| DELETE | `/posts/{postId}/likes` | JWT | sem body | `204 No Content` |

## Comments

Controller: `CommenteController`

JSON de `Commente` retornado hoje:

```json
{
  "id": 3,
  "content": "Comentario",
  "post": {
    "id": 10,
    "content": "Meu post",
    "imageUrl": "http://localhost:8080/files/abc123.jpg",
    "user": {
      "id": 1,
      "nome": "Ana",
      "email": "ana@email.com"
    },
    "createdAt": "2026-04-18T10:00:00",
    "likesCount": 2,
    "commentsCount": 1
  },
  "user": {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com"
  }
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/posts/{postId}/comments` | JWT | `{ "content": "Comentario" }` | `201 Created` com objeto `Commente` |
| GET | `/posts/{postId}/comments` | JWT | sem body | `200 OK` com array de `Commente` |
| DELETE | `/posts/{commentId}/comments` | JWT | sem body | `204 No Content` |

## Profiles

Controller: `ProfileController`

JSON de `ProfileResponse`:

```json
{
  "nome": "Ana",
  "bio": "Minha bio",
  "imageUrlProfile": "http://localhost:8080/files/profile.jpg",
  "messageStatus": "Online"
}
```

JSON de `Profile` retornado no update:

```json
{
  "id": 4,
  "bio": "Minha bio",
  "imageUrlProfile": "http://localhost:8080/files/profile.jpg",
  "messageStatus": "Online",
  "messageStatusCreatedAt": "2026-04-18T10:00:00"
}
```

JSON de `FollowingProfileResponse`:

```json
{
  "userId": 2,
  "nome": "Bruno",
  "imageUrlProfile": "http://localhost:8080/files/bruno.jpg",
  "messageStatus": "Ola"
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| GET | `/profiles/me` | JWT | sem body | `200 OK` com `ProfileResponse` do usuario logado. Se nao existir perfil, o backend cria um vazio |
| GET | `/profiles/user?email=...` | JWT | sem body | `200 OK` com `ProfileResponse` do email informado |
| GET | `/profiles/following` | JWT | sem body | `200 OK` com array de `FollowingProfileResponse` |
| GET | `/profiles/followers` | JWT | sem body | `200 OK` com array de `FollowingProfileResponse` dos usuarios que seguem o logado |
| PUT | `/profiles/me` | JWT | `{ "bio": "Nova bio", "imageUrlProfile": "/files/foto.jpg", "messageStatus": "Ola" }` | `200 OK` com objeto `Profile` salvo |

## Follows

Controller: `FollowController`

JSON de `Follow` retornado hoje:

```json
{
  "id": 8,
  "follower": {
    "id": 1,
    "nome": "Ana",
    "email": "ana@email.com"
  },
  "followed": {
    "id": 2,
    "nome": "Bruno",
    "email": "bruno@email.com"
  },
  "createdAt": "2026-04-18T10:00:00"
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/users/{userId}/follow` | JWT | sem body | `201 Created` com objeto `Follow` |
| DELETE | `/users/{userId}/follow` | JWT | sem body | `204 No Content` |
| DELETE | `/users/followers/{followerId}` | JWT | sem body | `204 No Content` |
| GET | `/users/{userId}/followers/count` | JWT | sem body | `200 OK` com numero JSON, ex.: `12` |
| GET | `/users/{userId}/following/count` | JWT | sem body | `200 OK` com numero JSON, ex.: `7` |
| GET | `/users/{userId}/followers` | JWT | sem body | `200 OK` com array de `User` |
| GET | `/users/{userId}/following` | JWT | sem body | `200 OK` com array de `User` |
| GET | `/users/me/followers` | JWT | sem body | `200 OK` com array de `User` |
| GET | `/users/me/following` | JWT | sem body | `200 OK` com array de `User` |

## Conversations

Controller: `ConversationController`

JSON de `ConversationResponse`:

```json
{
  "conversationId": 5,
  "otherUserId": 2,
  "otherUserName": "Bruno",
  "otherUserPhoto": "http://localhost:8080/files/bruno.jpg",
  "lastMessage": "Oi",
  "lastMessageAt": "2026-04-18T10:00:00"
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| GET | `/conversations/me` | JWT | sem body | `200 OK` com array de `ConversationResponse` |
| GET | `/conversations/contacts` | JWT | sem body | `200 OK` com o mesmo array de `ConversationResponse` |

## Messages

Controller: `MessageController`

JSON de `MessageResponse`:

```json
{
  "id": 11,
  "conversationId": 5,
  "senderId": 1,
  "senderName": "Ana",
  "senderPhoto": "http://localhost:8080/files/ana.jpg",
  "content": "Oi",
  "createdAt": "2026-04-18T10:00:00"
}
```

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/messages/{receiverId}` | JWT | `{ "content": "Oi" }` | `201 Created` com `MessageResponse`. Se nao existir conversa entre os usuarios, ela e criada automaticamente |
| GET | `/messages/conversation/{conversationId}` | JWT | sem body | `200 OK` com array de `MessageResponse` ordenado por data ascendente |

## Files

Controllers: `UploadController` e `FileViewController`

| Metodo | Rota | Auth | Body | Retorno de sucesso |
| --- | --- | --- | --- | --- |
| POST | `/files/upload` | Publico | `multipart/form-data` com campo `file` | `200 OK` com `{ "url": "/files/arquivo-gerado.jpg" }` |
| GET | `/files/{filename}` | Publico | sem body | `200 OK` com o arquivo da imagem. Nao retorna JSON |

Validacoes de `POST /files/upload`:

- arquivo vazio: `400` com `{ "error": "Arquivo vazio" }`
- maior que 2MB: `400` com `{ "error": "Arquivo maior que 2MB" }`
- tipo diferente de PNG/JPG: `400` com `{ "error": "Somente PNG ou JPG" }`

## Observacoes

- `PUT /profiles/me` esta mapeado como `/profiles/me` mesmo sem a barra inicial no controller, porque o Spring concatena corretamente com o path base
- `GET /files/{filename}` aceita apenas `.png`, `.jpg` e `.jpeg`
- Os JSONs acima refletem o shape atual do codigo. Se voce mudar entidade/DTO/getters, esta documentacao precisa ser atualizada junto
