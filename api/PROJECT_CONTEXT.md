# Contexto do Projeto: API - Achados e Perdidos

## 1. Visão Geral
- **Nome do Projeto**: API - Achados e Perdidos
- **Descrição**: API REST para um sistema de achados e perdidos acadêmico, com suporte a múltiplas instituições e campi.
- **Linguagem**: Java 21
- **Framework Principal**: Spring Boot 3.3.6
- **Build e Gerenciador de Dependências**: Maven
- **Bancos de Dados**:
  - PostgreSQL (via Spring Data JPA) para dados relacionais.
  - MongoDB (via Spring Data MongoDB) para mensagens de chat.

## 2. Tecnologias e Bibliotecas Principais
- **Módulos Spring Boot**: Web, Data JPA, Data MongoDB, Validation, Security, OAuth2 Client, WebSocket, Cache, Test.
- **Segurança e Autenticação**: Spring Security, JWT (`jjwt`), Integração OAuth2 (Google).
- **Armazenamento de Arquivos**: AWS SDK (S3) para fotos de usuários e itens.
- **Notificação**: Integração com OneSignal.
- **Documentação de API**: Springdoc OpenAPI (Swagger UI).
- **Utilitários e Outros**: Lombok, ModelMapper, Dotenv, Caffeine Cache.

## 3. Arquitetura do Projeto (Em camadas, inspirada em Clean Architecture / DDD)
O pacote raiz da aplicação está localizado em `src/main/java/com/AchadosPerdidos/API/`.

---

### 3.1. `Application/` (Camada de Aplicação)

#### `Config/`
Centraliza beans Spring: CORS, Cache (Caffeine), Variáveis de Ambiente, OAuth2 do Google, JWT, ModelMapper, OneSignal, S3 da AWS, Security, Swagger e WebSocket.

#### `Interfaces/`
Contratos (ports) do padrão Adapter — tudo que a aplicação depende está definido aqui como interface:

| Interface | Subpasta | Responsabilidade |
|---|---|---|
| `IStorageService` | `Storage/` | Upload, download, delete, signed URL (S3) |
| `IPushNotificationService` | `Notification/` | Envio de notificações push (OneSignal) |
| `ICacheService` | `Cache/` | Cache individual e em lote (Caffeine/Redis) |
| `IJWTService` | `Auth/` | Criar, validar e extrair claims de tokens JWT |
| `IOAuthProviderService` | `Auth/` | Contrato genérico para provedores OAuth2 (Google, Apple, etc.) |
| `IBaseService<T, ID>` | raiz | CRUD genérico base para todos os services |
| `IUsersService` | raiz | Estende `IBaseService`. Operações de usuários e autenticação |
| `IItemService` | raiz | Estende `IBaseService`. CRUD de itens, filtros, status e busca |
| `ICategoryService` | raiz | Estende `IBaseService`. Gestão de categorias |
| `ICampusService` | raiz | Estende `IBaseService`. Gestão de campi |
| `IInstitutionService` | raiz | Estende `IBaseService`. Gestão de instituições |
| `IRoleService` | raiz | Estende `IBaseService`. Leitura de roles |
| `IPhotoService` | raiz | Upload/remoção de fotos (orquestra S3 + repos de foto) |
| `IChatService` | raiz | Estende `IBaseService`. Abertura e encerramento de chats por item |
| `IChatMessageService` | raiz | Envio e leitura de mensagens (MongoDB) |

#### `DTOs/`
Organizado por domínio dentro de `Request/` e `Response/`:

```
DTOs/
├── Request/
│   ├── User/        LoginRequestDTO, CreateUserRequestDTO, UpdateUserRequestDTO, RedefinirSenhaRequestDTO
│   ├── Item/        CreateItemRequestDTO, UpdateItemRequestDTO, UpdateItemStatusRequestDTO
│   ├── Category/    CreateCategoryRequestDTO
│   ├── Campus/      CreateCampusRequestDTO
│   ├── Institution/ CreateInstitutionRequestDTO
│   └── Chat/        SendMessageRequestDTO
└── Response/
    ├── Auth/        OAuthUserDTO, TokenResponseDTO, TokenValidationDTO
    ├── User/        UsuariosDTO
    ├── Item/        ItemResponseDTO
    ├── Category/    CategoryResponseDTO
    ├── Campus/      CampusResponseDTO
    ├── Institution/ InstitutionResponseDTO
    └── Chat/        ChatResponseDTO, ChatMessageResponseDTO
```

#### `Services/`
Implementações concretas das interfaces acima:

| Service | Implementa | Responsabilidade |
|---|---|---|
| `Auth/JWTService` | `IJWTService` | Adapter para biblioteca `jjwt` |
| `Auth/GoogleAuthService` | `IOAuthProviderService` | Adapter para API OAuth2 do Google |
| `Photo/S3Service` | `IStorageService` | Adapter para AWS S3 SDK |
| `NotificationPush/OneSignalService` | `IPushNotificationService` | Adapter para REST API do OneSignal |
| `Cache/CacheService` | `ICacheService` | Adapter para Spring CacheManager (Caffeine) |
| `BaseService<T,ID,R>` | `IBaseService` | Classe abstrata que centraliza o CRUD base (`create`, `findById`, `findAll`). `update` e `deactivate` ficam abstratos. |
| `UsersService` | `IUsersService` | Herda `BaseService`. CRUD de usuários + gerência de Auth e soft-delete. |
| `ItemService` | `IItemService` | Herda `BaseService`. CRUD de itens, troca de status, busca textual. |
| `CategoryService` | `ICategoryService` | Herda `BaseService`. CRUD de categorias. |
| `CampusService` | `ICampusService` | Herda `BaseService`. CRUD de campi. |
| `InstitutionService` | `IInstitutionService`| Herda `BaseService`. CRUD de instituições. |
| `RoleService` | `IRoleService` | Herda `BaseService`. Leitura de roles. Impede updates/deletes de sistema. |
| `PhotoService` | `IPhotoService` | Orquestra S3 + metadados de foto no banco. |
| `ChatService` | `IChatService` | Herda `BaseService`. Usa `openChat`/`closeChat` para manuseamento idempotente de conversas. |
| `ChatMessageService` | `IChatMessageService` | Mensagens no MongoDB com `markAllAsRead` em lote |

---

### 3.2. `Domain/` (Camada de Domínio)

#### `Entity/`
Todas as entidades são completamente anotadas com JPA (`@Entity`, `@Table`, `@Id`, `@Column`, `@Enumerated`, `@PrePersist`).
- `BaseEntity`: superclasse com `active`, `createdAt`, `deletedAt` e `@PrePersist` (preenche `createdAt` e `active=true` automaticamente).
- **PostgreSQL**: `Users`, `Role`, `Institution`, `Campus`, `Category`, `Item`, `Photo`, `Item_Photo`, `User_Photo`, `Chat`.
- **MongoDB**: `ChatMessage` (`@Document(collection = "chat_messages")`).

#### `Enum/`
- `Status_Item`, `Type_Item`: estados e tipo do item achado/perdido.
- `Status_Message`, `Typing_Status`: controle de mensagens de chat.

#### `Repository/`
Organizado em dois níveis:

- **`BaseRepository<T, ID>`** (`@NoRepositoryBean`): estende `JpaRepository` limpo e enxuto apenas com `findByActiveTrue` e `findByActiveFalse` para isolar consultas de soft-delete.
- **Repositories concretas** (implementam `BaseRepository` + `I{Entity}Repository`):
  - PostgreSQL: `UsersRepository`, `RoleRepository`, `InstitutionRepository`, `CampusRepository`, `CategoryRepository`, `ItemRepository`, `PhotoRepository`, `ItemPhotoRepository`, `UserPhotoRepository`, `ChatRepository`.
  - MongoDB: `ChatMessageRepository` (estende `MongoRepository`, não `BaseRepository`).
- **`Interfaces/`**: contratos de domínio puros (`IUsersRepository`, `IItemRepository`, `IChatMessageRepository`, etc.) alocados na pasta *Domain/Interfaces* (ao em vez na subpasta orginal do Repository) para manter os serviços isolados de dependências do Spring Data para **mocks limpos em testes unitários**.
- **Convenção de queries**: `static final String QUERY_NOME = "JPQL..."` no topo de cada repository concreto (equivalente ao `const string` do C#), referenciadas em `@Query(NOME_DA_CONSTANTE)`.

---

### 3.3. `Infrastructure/` (Camada de Infraestrutura)
- **DataBase/**: consultas customizadas para PostgreSQL.
- **MongoDB/**: operações para MongoDB.
- **Security/**: filtro de autenticação JWT e proteção de rotas.

### 3.4. `Presentation/` (Camada de Apresentação)
- **Controller/**: endpoints REST HTTP.
  - `AuthController` — login e autenticação.
  - `UserController` — operações de usuário.
  - `ItemController` — operações de achados e perdidos.
  - `CategoryController` — gestão de categorias de itens.
  - `CampusController` — gestão de campi.
  - `InstitutionController` — gestão de instituições.
  - `ChatController` — gerenciamento de conversas/chats.
  - `PhotoController` — manipulação e upload de fotos.

### 3.5. `Exeptions/`
Tratamentos globais e exceções de negócio personalizadas.

---

## Convenções Aplicadas no Projeto

| Convenção | Regra |
|---|---|
| **Campos das Entities** | `camelCase` no Java; `@Column(name="snake_case")` mapeia para o banco |
| **Soft-delete** | `setActive(false)` + `setDeletedAt(now())` — nunca `DELETE` físico |
| **Injeção de dependência** | Services via **interface** (`@Autowired IXxxService`); Repositories via interface Spring Data (`@Autowired XxxRepository`) para acessar métodos JPA. |
| **Transações leitura** | `@Transactional(readOnly = true)` em todos os `findAll`, `findBy...` |
| **Queries custom** | `static final String` no topo do repository, referenciada em `@Query(CONSTANTE)` |
| **DTOs Request** | Em `DTOs/Request/{Dominio}/` — dados que **entram** na API |
| **DTOs Response** | Em `DTOs/Response/{Dominio}/` — dados que **saem** da API |
| **Interfaces de service** | Em `Application/Interfaces/` (junto com as outras interfaces). Todas que são transacionais com DB relacional estendem `IBaseService`. |
| **Interfaces de repository** | Em `Domain/Interfaces/` e representam as abstrações limpas conectadas aos Serviços. |
| **Padrão Adapter (GoF)** | Toda integração externa tem uma interface Target + implementação concreta |

## Atualização de Contexto
Sempre que novos Controladores, Serviços, Entidades, Configurações ou Dependências do Pom.xml forem criados, ou quando arquivos forem removidos, este documento deverá ser atualizado para refletir o cenário atual do back-end.
