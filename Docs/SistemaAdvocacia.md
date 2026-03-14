# ⚖ SISTEMA DE GESTÃO JURÍDICA

**Documento de Requisitos, Arquitetura e Prompt de Desenvolvimento**

> Stack: Spring Boot + Vaadin + PostgreSQL
> Contexto: Escritório de Advocacia Real (Cível, Trabalhista, Previdenciário)
> Versão: 1.0 | Março 2026

---

## 1. CONTEXTO E LEVANTAMENTO DE REQUISITOS

### 1.1 Sobre o Escritório

Com base na entrevista realizada, o escritório possui o seguinte perfil:

| Característica | Detalhe |
|---|---|
| Advogados fixos | 2 advogados |
| Advogados parceiros | 2 advogados parceiros |
| Administrativo | 1 pessoa (Kecia — recepção e atendimento) |
| Áreas de atuação | Cível, Trabalhista, Previdenciário |
| Ferramentas atuais | ADVBOX (processos), Pipedrive (CRM), MegaZap (WhatsApp) |
| Armazenamento docs | Pastas locais no computador + armário físico |
| Canal principal de leads | WhatsApp, Indicação, Facebook/Instagram, Tráfego pago |

### 1.2 Dores e Problemas Identificados

- Informações espalhadas em múltiplas ferramentas (ADVBOX + Pipedrive + MegaZap + pastas locais)
- Controle de prazos feito manualmente pelo advogado, sem alertas automáticos
- Documentos físicos e digitais desorganizados, sem versionamento
- Dificuldade em ter uma visão consolidada dos processos ativos
- Atendimento inicial e cadastro de clientes consomem muito tempo da Kecia
- Sem histórico estruturado de comunicações por cliente
- Funil de vendas no Pipedrive separado do restante do sistema

### 1.3 Objetivos do Sistema

O sistema deve substituir todas as ferramentas atuais com uma plataforma única que:

1. Centralize clientes, processos, tarefas e documentos em um só lugar
2. Ofereça controle de prazos com alertas automáticos
3. Permita registro do histórico de atendimentos e comunicações
4. Tenha níveis de acesso diferenciados (Gestor, Advogado, Administrativo)
5. Apresente um dashboard executivo para visão rápida do escritório
6. Esteja em conformidade com a LGPD

---

## 2. MINI MUNDO — BANCO DE DADOS

### 2.1 Descrição das Entidades

**USUÁRIO (users)**
Representa qualquer pessoa que acessa o sistema. Possui papel (role) que define suas permissões: GESTOR (acesso total), ADVOGADO (processos e casos), ADMINISTRATIVO (cadastros e atendimento). Um usuário pode ser responsável por vários processos e tarefas.

**CLIENTE (clients)**
Pessoa física ou jurídica que contrata os serviços do escritório. Possui dados pessoais completos (CPF/CNPJ criptografado, RG, endereço, telefone). Um cliente pode ter vários processos jurídicos e um histórico de comunicações. O campo `registration_source` registra de onde veio o lead.

**PROCESSO JURÍDICO (legal_processes)**
Representa um processo jurídico vinculado a um cliente. Contém número do processo, vara, tribunal, tipo de ação, status (INICIAL, EM_ANDAMENTO, CONCLUÍDO, ARQUIVADO) e advogado responsável.

**TAREFA / PRAZO (tasks)**
Representa uma atividade ou prazo associado a um processo ou cliente. Possui prioridade (BAIXA, MÉDIA, ALTA, URGENTE) e status de andamento. O sistema gera alertas automáticos quando o prazo se aproxima.

**DOCUMENTO (documents)**
Arquivo digital vinculado a um cliente e/ou processo. Possui versionamento, tipo de documento, localização de armazenamento. O caminho do arquivo é criptografado por segurança.

**COMUNICAÇÃO (communications)**
Registro de um contato realizado com um cliente, seja via WhatsApp, ligação ou presencial. Registra quem atendeu, quando, o conteúdo e observações.

**LOG DE AUDITORIA (audit_logs)**
Registra todas as ações realizadas no sistema por qualquer usuário, armazenando o estado anterior e posterior dos dados (JSON). Essencial para conformidade com a LGPD.

### 2.2 Relacionamentos

| Relacionamento | Cardinalidade e Regra |
|---|---|
| USUÁRIO → PROCESSO | 1 usuário (advogado) pode ser responsável por N processos |
| CLIENTE → PROCESSO | 1 cliente pode ter N processos (1:N) |
| PROCESSO → TAREFA | 1 processo pode ter N tarefas (1:N, opcional) |
| CLIENTE → TAREFA | 1 cliente pode ter N tarefas avulsas (1:N, opcional) |
| CLIENTE → COMUNICAÇÃO | 1 cliente pode ter N comunicações (1:N) |
| PROCESSO → COMUNICAÇÃO | 1 processo pode estar vinculado a N comunicações (opcional) |
| CLIENTE → DOCUMENTO | 1 cliente pode ter N documentos (1:N) |
| PROCESSO → DOCUMENTO | 1 processo pode ter N documentos (1:N) |
| USUÁRIO → AUDIT_LOG | 1 usuário gera N logs de auditoria |

### 2.3 Script SQL — PostgreSQL

```sql
-- TABELA DE USUÁRIOS
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('GESTOR','ADVOGADO','ADMINISTRATIVO')),
  full_name VARCHAR(150),
  department VARCHAR(50),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE CLIENTES
CREATE TABLE clients (
  id SERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  cpf_cnpj VARCHAR(20) UNIQUE,  -- armazenar criptografado
  rg VARCHAR(20),               -- armazenar criptografado
  birthdate DATE,
  email VARCHAR(100),
  phone VARCHAR(20),            -- armazenar criptografado
  address TEXT,
  zip_code VARCHAR(10),
  city VARCHAR(80),
  state VARCHAR(2),
  profession VARCHAR(100),
  marital_status VARCHAR(30),
  registration_source VARCHAR(50), -- WHATSAPP|INDICACAO|SITE|FACEBOOK|INSTAGRAM
  notes TEXT,
  contact_history_count INTEGER DEFAULT 0,
  last_contact_date TIMESTAMP,
  created_by INTEGER REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE PROCESSOS JURÍDICOS
CREATE TABLE legal_processes (
  id SERIAL PRIMARY KEY,
  process_number VARCHAR(30) UNIQUE NOT NULL,
  client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
  vara VARCHAR(100),
  tribunal VARCHAR(150),
  action_type VARCHAR(100),
  status VARCHAR(20) NOT NULL CHECK (status IN ('INICIAL','EM_ANDAMENTO','CONCLUIDO','ARQUIVADO')) DEFAULT 'INICIAL',
  opening_date DATE,
  opposing_party VARCHAR(150),
  observations TEXT,
  assigned_lawyer_id INTEGER REFERENCES users(id),
  created_by INTEGER REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE TAREFAS E PRAZOS
CREATE TABLE tasks (
  id SERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  process_id INTEGER REFERENCES legal_processes(id) ON DELETE SET NULL,
  client_id INTEGER REFERENCES clients(id) ON DELETE SET NULL,
  assigned_to_id INTEGER NOT NULL REFERENCES users(id),
  deadline DATE,
  priority VARCHAR(20) CHECK (priority IN ('BAIXA','MEDIA','ALTA','URGENTE')),
  status VARCHAR(20) CHECK (status IN ('A_FAZER','EM_ANDAMENTO','CONCLUIDA','CANCELADA')) DEFAULT 'A_FAZER',
  completed_at TIMESTAMP,
  completion_notes TEXT,
  created_by INTEGER REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE DOCUMENTOS
CREATE TABLE documents (
  id SERIAL PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,  -- armazenar criptografado
  file_size BIGINT,
  mime_type VARCHAR(80),
  client_id INTEGER REFERENCES clients(id) ON DELETE CASCADE,
  process_id INTEGER REFERENCES legal_processes(id) ON DELETE CASCADE,
  document_type VARCHAR(80),
  version INTEGER DEFAULT 1,
  uploaded_by_id INTEGER REFERENCES users(id),
  is_active BOOLEAN DEFAULT TRUE,
  storage_location VARCHAR(50) DEFAULT 'local',
  upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE COMUNICAÇÕES
CREATE TABLE communications (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
  process_id INTEGER REFERENCES legal_processes(id) ON DELETE SET NULL,
  type VARCHAR(50),  -- WHATSAPP, LIGACAO, PRESENCIAL, EMAIL
  channel VARCHAR(50),
  message TEXT,
  sender_user_id INTEGER REFERENCES users(id),
  communication_date TIMESTAMP,
  duration_minutes INTEGER,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABELA DE LOGS DE AUDITORIA (LGPD)
CREATE TABLE audit_logs (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
  action VARCHAR(50) NOT NULL,       -- CREATE, UPDATE, DELETE, VIEW
  entity_type VARCHAR(50) NOT NULL,  -- clients, processes, documents...
  entity_id INTEGER,
  old_value JSON,
  new_value JSON,
  ip_address VARCHAR(45),
  user_agent VARCHAR(500),
  reason VARCHAR(255),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ÍNDICES PARA PERFORMANCE
CREATE INDEX idx_clients_cpf      ON clients(cpf_cnpj);
CREATE INDEX idx_processes_client ON legal_processes(client_id);
CREATE INDEX idx_processes_status ON legal_processes(status);
CREATE INDEX idx_tasks_deadline   ON tasks(deadline);
CREATE INDEX idx_tasks_assigned   ON tasks(assigned_to_id);
CREATE INDEX idx_comms_client     ON communications(client_id);
CREATE INDEX idx_audit_timestamp  ON audit_logs(timestamp);
```

---

## 3. STACK TECNOLÓGICA RECOMENDADA

> **Decisão: Vaadin no frontend em vez de React** — integração nativa com Spring Boot, desenvolvimento full-stack em Java puro (sem JavaScript), componentes ricos prontos, ideal para quem vem de Java e quer produtividade máxima sem trocar de linguagem.

### 3.1 Backend — Spring Boot

| Tecnologia | Uso e Justificativa |
|---|---|
| Java 25 + Spring Boot 5.x | Base sólida, compatível com seu background em Java |
| Spring Security | Autenticação com 3 papéis de acesso, integração nativa com Vaadin |
| Spring Data JPA + Hibernate | ORM para acesso ao banco com menos boilerplate |
| PostgreSQL | Banco relacional robusto, gratuito e amplamente usado |
| Jasypt | Criptografia de dados sensíveis (CPF, RG, telefone) em repouso |
| Lombok | Reduz código repetitivo (getters, setters, construtores) |
| Springdoc OpenAPI (Swagger) | Documentação automática da API REST |
| Maven | Gerenciador de dependências (você já conhece de Java) |
| Docker Compose | Subir backend + banco localmente com 1 comando |

### 3.2 Frontend — Vaadin

| Tecnologia | Uso e Justificativa |
|---|---|
| Vaadin 25 (Flow) | Framework full-stack Java — UI definida 100% em Java, sem HTML/JS/CSS manual |
| Vaadin Components | Biblioteca de componentes ricos: Grid, Form, Dialog, Notification |
| Spring Boot + Vaadin | Integração nativa via vaadin-spring — zero configuração de CORS ou API separada |
| Vaadin Security | Integração direta com Spring Security — @RolesAllowed nas views |
| Vaadin Router (Flow) | Navegação automática entre views Java com proteção de rotas por role |
| Vaadin Charts | Gráficos para dashboard — add-on oficial Vaadin |
| Vaadin Upload | Componente de upload de arquivos nativo |
| Vaadin Grid | Tabela com paginação lazy, filtros e ordenação — sem bibliotecas extras |

### 3.3 Por que Vaadin em vez de React?

- Vaadin permite escrever o frontend em Java puro — aproveitando 100% do seu background em Java
- Zero configuração de CORS, zero Axios, zero gerenciamento de estado JS — tudo no mesmo processo Spring Boot
- Componentes prontos e ricos (Grid com lazy loading, formulários, modais) sem instalar dezenas de libs
- Integração nativa com Spring Security — `@RolesAllowed` por view é suficiente para controle de acesso
- Sem necessidade de npm, Vite ou bundlers — o Maven cuida de tudo
- Vaadin Flow compila as views Java em HTML/JS automaticamente — o dev nunca vê JavaScript
- Ideal para sistemas internos e ERPs, exatamente o caso de um sistema de gestão jurídica

---

## 4. ARQUITETURA DO PROJETO

### 4.1 Estrutura de Pastas

Com Vaadin, o frontend fica **dentro do mesmo projeto Spring Boot** — não há pasta separada.

```
advocacia-system/
└── src/main/java/com/advocacialegal/
    ├── config/
    │   ├── SecurityConfig.java       # Spring Security + roles
    │   └── SwaggerConfig.java        # Documentação API REST
    │
    ├── domain/                       # Entidades JPA
    │   ├── User.java
    │   ├── Client.java
    │   ├── LegalProcess.java
    │   ├── Task.java
    │   ├── Document.java
    │   ├── Communication.java
    │   └── AuditLog.java
    │
    ├── repository/                   # Interfaces Spring Data
    │   ├── UserRepository.java
    │   ├── ClientRepository.java
    │   ├── ProcessRepository.java
    │   ├── TaskRepository.java
    │   ├── DocumentRepository.java
    │   └── CommunicationRepository.java
    │
    ├── service/                      # Regras de negócio
    │   ├── AuthService.java
    │   ├── ClientService.java
    │   ├── ProcessService.java
    │   ├── TaskService.java
    │   ├── DocumentService.java
    │   ├── DashboardService.java
    │   └── AuditLogService.java
    │
    ├── controller/                   # Endpoints REST (Swagger/API externa)
    │   ├── AuthController.java
    │   ├── ClientController.java
    │   ├── ProcessController.java
    │   ├── TaskController.java
    │   ├── DocumentController.java
    │   └── DashboardController.java
    │
    ├── views/                        # *** VAADIN — Views Java ***
    │   ├── MainLayout.java           # AppLayout: sidebar + header
    │   ├── LoginView.java            # Tela de login
    │   ├── DashboardView.java        # Dashboard executivo
    │   ├── clients/
    │   │   ├── ClientListView.java   # Grid de clientes com filtros
    │   │   ├── ClientFormView.java   # Formulário criar/editar
    │   │   └── ClientDetailView.java # Detalhe com abas
    │   ├── processes/
    │   │   ├── ProcessListView.java
    │   │   └── ProcessDetailView.java
    │   ├── tasks/
    │   │   └── TasksView.java        # Lista + Kanban
    │   └── documents/
    │       └── DocumentsView.java    # Upload + lista
    │
    ├── dto/                          # Objetos de transferência (API REST)
    │   ├── auth/   LoginRequest, AuthResponse
    │   ├── client/ ClientDTO, ClientCreateDTO
    │   ├── process/ ProcessDTO
    │   └── task/   TaskDTO
    │
    ├── security/
    │   ├── JwtTokenProvider.java
    │   └── JwtAuthenticationFilter.java
    │
    ├── exception/
    │   └── GlobalExceptionHandler.java
    │
    └── util/
        ├── CPFValidator.java
        └── LGPDEncryptionUtil.java

└── src/main/resources/
    ├── application.properties
    └── application-prod.properties

pom.xml
```

### 4.2 Exemplo de View Vaadin — ClientListView

```java
@Route(value = "clientes", layout = MainLayout.class)
@RolesAllowed({"GESTOR", "ADVOGADO", "ADMINISTRATIVO"})
public class ClientListView extends VerticalLayout {

    private final ClientService clientService;
    private Grid<ClientDTO> grid = new Grid<>(ClientDTO.class, false);

    public ClientListView(ClientService clientService) {
        this.clientService = clientService;
        setSizeFull();
        configureGrid();
        add(createToolbar(), grid);
        updateGrid("");
    }

    private void configureGrid() {
        grid.addColumn(ClientDTO::getName).setHeader("Nome").setSortable(true);
        grid.addColumn(ClientDTO::getEmail).setHeader("E-mail");
        grid.addColumn(ClientDTO::getPhone).setHeader("Telefone");
        grid.addColumn(ClientDTO::getRegistrationSource).setHeader("Origem");
        grid.addComponentColumn(c -> {
            Button btn = new Button("Ver", e -> navigate(c.getId()));
            return btn;
        }).setHeader("Ações");
        grid.setSizeFull();
    }

    private HorizontalLayout createToolbar() {
        TextField filter = new TextField("Buscar por nome");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateGrid(e.getValue()));
        Button novoCliente = new Button("+ Novo Cliente",
            e -> UI.getCurrent().navigate(ClientFormView.class));
        return new HorizontalLayout(filter, novoCliente);
    }

    private void updateGrid(String filter) {
        grid.setItems(clientService.findAll(filter));
    }
}
```

### 4.3 Exemplo de MainLayout — Sidebar Vaadin

```java
@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(new DrawerToggle(), new H1("⚖ Gestão Jurídica"));

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard",  DashboardView.class,  VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Clientes",   ClientListView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Processos",  ProcessListView.class,VaadinIcon.BRIEFCASE.create()));
        nav.addItem(new SideNavItem("Tarefas",    TasksView.class,      VaadinIcon.TASKS.create()));
        nav.addItem(new SideNavItem("Documentos", DocumentsView.class,  VaadinIcon.FOLDER.create()));

        addToDrawer(nav);
    }
}
```

---

## 5. ENDPOINTS REST — API

Os endpoints REST são mantidos para integração futura (mobile, MegaZap, etc). O Vaadin acessa os services Java diretamente — sem Axios ou chamadas HTTP internas.

### 5.1 Autenticação
```
POST /api/auth/login    # Login → retorna JWT
POST /api/auth/logout   # Logout (invalida token)
POST /api/auth/refresh  # Renovar token
```

### 5.2 Clientes
```
GET    /api/clients                      # Listar (paginado, com filtros)
GET    /api/clients/{id}                 # Buscar por ID
POST   /api/clients                      # Criar novo cliente
PUT    /api/clients/{id}                 # Atualizar dados
DELETE /api/clients/{id}                 # Deletar (soft delete)
GET    /api/clients/{id}/processes       # Processos do cliente
GET    /api/clients/{id}/communications  # Histórico de contatos
GET    /api/clients/{id}/documents       # Documentos
POST   /api/clients/{id}/communication   # Registrar novo contato
```

### 5.3 Processos Jurídicos
```
GET    /api/processes              # Listar (filtros: status, advogado)
GET    /api/processes/{id}         # Detalhe
POST   /api/processes              # Criar
PUT    /api/processes/{id}         # Atualizar
PATCH  /api/processes/{id}/status  # Mudar status (workflow)
DELETE /api/processes/{id}         # Arquivar
```

### 5.4 Tarefas e Prazos
```
GET    /api/tasks                  # Listar (filtros: status, prazo, responsável)
POST   /api/tasks                  # Criar tarefa
PUT    /api/tasks/{id}             # Atualizar
PATCH  /api/tasks/{id}/status      # Mudar status
DELETE /api/tasks/{id}             # Deletar
GET    /api/tasks/alerts           # Prazos vencendo nos próximos 7 dias
```

### 5.5 Documentos
```
GET    /api/documents       # Listar
GET    /api/documents/{id}  # Download
POST   /api/documents       # Upload (multipart/form-data)
DELETE /api/documents/{id}  # Deletar
```

### 5.6 Dashboard
```
GET /api/dashboard/summary            # Resumo executivo
GET /api/dashboard/processes-active   # Processos ativos
GET /api/dashboard/deadlines-week     # Prazos da semana
GET /api/dashboard/unreached-clients  # Clientes sem retorno
```

---

## 6. SEGURANÇA E LGPD

### 6.1 Dados Sensíveis — O que criptografar

| Campo | Motivo / Lei |
|---|---|
| cpf_cnpj | Dado pessoal sensível — LGPD Art. 5, I |
| rg | Documento de identificação — LGPD |
| phone | Dado de contato pessoal — LGPD |
| file_path (documentos) | Evitar acesso direto via URL |
| password | Hash BCrypt — nunca armazenar em texto plano |

### 6.2 Controle de Acesso por Role — Vaadin (@RolesAllowed)

| Funcionalidade | GESTOR | ADVOGADO | ADMINISTRATIVO |
|---|---|---|---|
| Dashboard completo | ✅ | ✅ | — |
| Criar/editar clientes | ✅ | ✅ | ✅ |
| Criar/editar processos | ✅ | ✅ | ✅ |
| Ver todos os processos | ✅ | Só os seus | ✅ |
| Gerenciar tarefas | ✅ | ✅ | ✅ |
| Upload de documentos | ✅ | ✅ | ✅ |
| Logs de auditoria | ✅ | — | — |
| Gerenciar usuários | ✅ | — | — |

### 6.3 Autenticação no Vaadin

- Vaadin Flow usa Spring Security diretamente — `@RolesAllowed` na classe da view é suficiente
- Login via `LoginView.java` personalizada com Vaadin (sem formulário HTML manual)
- API REST mantém JWT para integrações externas (mobile, webhooks)
- Sessão Vaadin gerenciada pelo servidor — sem token no lado do cliente para a UI

---

## 7. PLANO DE DESENVOLVIMENTO — SPRINTS

| Sprint | Duração | Entregáveis |
|---|---|---|
| Sprint 1 | 2 semanas | Setup do projeto, Spring Boot + Vaadin integrado, MainLayout com sidebar, LoginView, autenticação Spring Security com 3 roles |
| Sprint 2 | 2 semanas | CRUD completo de Clientes — ClientListView (Grid), ClientFormView (Binder), ClientDetailView (abas) |
| Sprint 3 | 2 semanas | CRUD de Processos Jurídicos, vínculo Cliente-Processo, workflow de status, timeline visual |
| Sprint 4 | 2 semanas | Upload e gestão de Documentos (Vaadin Upload), histórico de Comunicações por cliente |
| Sprint 5 | 2 semanas | Tarefas e Prazos com alertas, visão Kanban, notificações de deadline via Vaadin Notification |
| Sprint 6 | 2 semanas | Dashboard executivo com Vaadin Charts, relatórios básicos, logs de auditoria LGPD |
| Sprint 7+ | Contínuo | Feedback dos usuários, ajustes, correções, API REST para preparação de app mobile |

> **CRÍTICO (Sprints 1-3):** Autenticação + Clientes + Processos. Isso sozinho já substitui o ADVBOX.

> **IMPORTANTE (Sprints 4-5):** Documentos + Comunicações + Tarefas. Substitui pastas locais e parte do MegaZap.

> **NICE TO HAVE (Sprint 6+):** Dashboard completo + relatórios + auditoria LGPD.

---

## 8. CHECKLIST DE DESENVOLVIMENTO

### Setup inicial

1. Criar projeto Spring Boot no Spring Initializr (Java 21, Maven, Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Lombok, Validation)
2. Adicionar dependência Vaadin no pom.xml: `com.vaadin:vaadin-spring-boot-starter`
3. Adicionar dependência Jasypt: `com.github.ulisesbocchio:jasypt-spring-boot-starter`
4. Configurar Docker Compose com PostgreSQL
5. Criar e executar os scripts SQL das 7 tabelas
6. Configurar `SecurityConfig.java` com Vaadin + Spring Security

### Backend — ordem de implementação

7. Entidade User + UserRepository + AuthService + Spring Security
8. SecurityConfig com rotas públicas e views Vaadin protegidas por role
9. GlobalExceptionHandler para erros padronizados (API REST)
10. Entidade Client + CRUD completo + validação CPF
11. Entidade LegalProcess + workflow de status
12. Entidade Task + alertas de prazo
13. Entidade Document + upload de arquivos
14. Entidade Communication + histórico
15. DashboardService com queries agregadas
16. AuditLogService integrado a todos os services

### Frontend (Vaadin) — ordem de implementação

17. MainLayout.java com AppLayout, SideNav e links de navegação
18. LoginView.java com formulário Vaadin + integração Spring Security
19. ClientListView, ClientFormView, ClientDetailView
20. ProcessListView, ProcessDetailView com timeline de status
21. TasksView com lista e visão Kanban
22. DocumentsView com Vaadin Upload, lista e download
23. DashboardView com Vaadin Charts e cards de métricas

---

*Documento gerado para uso em projeto de desenvolvimento de software.*
