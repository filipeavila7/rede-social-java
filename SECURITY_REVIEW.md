# Segurança — Revisão do Projeto

Este documento resume o que já está implementado e o que ainda falta para endurecer a segurança da API.

## ✅ O que você já tem
1. **JWT com expiração**  
   - Tokens expiram em 1h (`JwtService.gerarToken`).
2. **Hash de senha com BCrypt**  
   - Senhas nunca são salvas em texto puro (`UserService`).
3. **JWT stateless**  
   - Sessão desativada (`SessionCreationPolicy.STATELESS`).
4. **Filtro de autenticação JWT**  
   - Extração e validação do token no `JwtAuthFilter`.
5. **CORS restrito**  
   - Permitido apenas `http://localhost:5173`.
6. **Rotas públicas mínimas**  
   - `/auth/login`, `POST /users`, páginas estáticas e `/uploads/**`.
7. **Upload básico de arquivos**  
   - Arquivos são salvos localmente e servidos por `/uploads/**`.
8. **Validação básica de campos (User)**  
   - `@NotBlank` e `@Email` no `User`, com `@Valid` no controller.

## ⚠️ Pontos com risco ou falha potencial
1. **JWT secret fixo no código**  
   - A chave está hardcoded em `JwtService`.  
   - Risco: exposição do segredo se o código vazar.
2. **Uploads sem validação de tipo/tamanho**  
   - Qualquer arquivo pode ser enviado.  
   - Risco: armazenamento de conteúdo malicioso.
3. **Uploads acessíveis publicamente**  
   - `/uploads/**` é público.  
   - Risco: exposição de arquivos sensíveis se forem enviados por engano.
4. **Sem rate limit**  
   - Login pode sofrer brute-force.
5. **Sem DTOs de saída para tudo**  
   - Algumas respostas ainda retornam entidades completas (ex.: User em alguns endpoints).  
   - Risco: vazamento de campos não desejados.
6. **Sem validação de entrada em várias entidades**  
   - Post, Comment, Message, Profile não têm validações (`@NotBlank`, `@Size`).
7. **`spring.jpa.hibernate.ddl-auto=update` em produção**  
   - Pode alterar schema inesperadamente.
8. **E-mail único só no JPA**  
   - Se o índice UNIQUE não existir no banco, pode duplicar.

## ✅ Melhorias recomendadas (prioridade alta)
1. **Mover JWT secret para variável de ambiente**  
   - Ex.: `JWT_SECRET` no `.env`/config.
2. **Validar uploads**  
   - Limitar tamanho (ex.: 2MB).  
   - Permitir apenas `png`, `jpg`, `jpeg`.
3. **Criar DTOs de saída**  
   - Ex.: `UserPublicResponse` para evitar retorno de campos indesejados.
4. **Adicionar rate limit no login**  
   - Biblioteca: `bucket4j`.
5. **Validar entradas principais**  
   - `Post.content`, `Comment.content`, `Message.content`.

## ✅ Melhorias recomendadas (prioridade média)
1. **Logs de segurança**  
   - Logar tentativas de login inválidas.
2. **Mensagens de erro padronizadas**  
   - Evita vazamento de detalhes internos.
3. **Auditoria básica**  
   - `createdAt` e `updatedAt` nas entidades principais.

## ✅ Melhorias recomendadas (prioridade baixa)
1. **Paginação**  
   - Evita retorno gigante de dados.
2. **Desativar open-in-view**  
   - Evita queries inesperadas durante serialização.

---

Se quiser, posso aplicar as melhorias mais urgentes agora (JWT secret por env, validação de upload e DTOs de saída).
