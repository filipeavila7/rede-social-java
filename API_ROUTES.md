# Rotas da API

Este arquivo lista todas as rotas atuais dos controllers e descreve o que cada uma faz.

**Base URL (local)**  
`http://localhost:8080`

**Auth**  
Controller: `AuthController` (`/auth`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/auth/login` | Faz login e retorna token JWT. | Público |

**Users**  
Controller: `UserController` (`/users`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| GET | `/users` | Lista todos os usuários. | JWT |
| POST | `/users` | Cria usuário. | JWT |
| PUT | `/users/{id}` | Atualiza usuário por id. | JWT |
| DELETE | `/users/{id}` | Remove usuário por id. | JWT |

**Posts**  
Controller: `PostController` (`/posts`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| GET | `/posts` | Lista todos os posts. | JWT |
| GET | `/posts/user/me` | Lista posts do usuário logado. | JWT |
| GET | `/posts/user?email=...` | Lista posts de um usuário pelo email. | JWT |
| GET | `/posts/{postId}/stats` | Retorna contagem de likes e comentários do post. | JWT |
| POST | `/posts` | Cria post com usuário logado. | JWT |
| PUT | `/posts/{id}` | Edita post por id. | JWT |
| DELETE | `/posts/{id}` | Remove post por id. | JWT |

**Likes**  
Controller: `LikeController` (`/posts`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/posts/{postId}/likes` | Curte um post (usuário logado). | JWT |
| DELETE | `/posts/{postId}/likes` | Remove curtida do post (usuário logado). | JWT |

**Comentários**  
Controller: `CommenteController` (`/posts`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/posts/{postId}/comments` | Cria comentário em um post. | JWT |
| GET | `/posts/{postId}/comments` | Lista comentários de um post. | JWT |
| DELETE | `/posts/{commentId}/comments` | Remove comentário pelo id. | JWT |

**Perfis**  
Controller: `ProfileController` (`/profiles`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| GET | `/profiles/me` | Retorna perfil do usuário logado. | JWT |
| GET | `/profiles/user?email=...` | Retorna perfil de outro usuário pelo email. | JWT |
| PUT | `/profiles/me` | Atualiza perfil do usuário logado. | JWT |

**Follows / Seguidores**  
Controller: `FollowController` (`/users`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/users/{userId}/follow` | Seguir usuário. | JWT |
| DELETE | `/users/{userId}/follow` | Deixar de seguir usuário. | JWT |
| DELETE | `/users/followers/{followerId}` | Remover seguidor do usuário logado. | JWT |
| GET | `/users/{userId}/followers/count` | Contagem de seguidores de um usuário. | JWT |
| GET | `/users/{userId}/following/count` | Contagem de seguindo de um usuário. | JWT |
| GET | `/users/{userId}/followers` | Lista seguidores de um usuário. | JWT |
| GET | `/users/{userId}/following` | Lista usuários que ele segue. | JWT |
| GET | `/users/me/followers` | Lista seguidores do usuário logado. | JWT |
| GET | `/users/me/following` | Lista seguindo do usuário logado. | JWT |

**Conversas (Chat)**  
Controller: `ConversationController` (`/conversations`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| GET | `/conversations/me` | Lista todas as conversas do usuário logado. | JWT |

**Mensagens (Chat)**  
Controller: `MessageController` (`/messages`)

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| POST | `/messages/{receiverId}` | Envia mensagem para um usuário. | JWT |
| GET | `/messages/conversation/{conversationId}` | Lista mensagens de uma conversa. | JWT |

**Notas**
1. Todas as rotas acima exigem JWT, exceto `/auth/login` e as páginas estáticas liberadas no `SecurityConfig`.
2. Algumas rotas dependem do usuário logado (ex.: `/posts/user/me`, `/profiles/me`).
3. Se alguma rota estiver diferente no código, a referência sempre é o controller correspondente.
