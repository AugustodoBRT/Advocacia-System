# Advocacia System

Sistema web para gestão de escritório de advocacia, desenvolvido em **Java (Spring Boot)** no backend e **Vaadin** no frontend.  
O objetivo é substituir o uso combinado de ADVBOX, Pipedrive e pastas locais, centralizando clientes, processos, tarefas, documentos e histórico de atendimentos em um único sistema.

## 🎯 Objetivo

- Centralizar cadastro de **clientes** e **processos jurídicos**
- Controlar **tarefas** e **prazos** dos advogados
- Registrar **histórico de atendimentos** (WhatsApp, ligações, etc.)
- Organizar **documentos** vinculados a clientes e processos
- Fornecer **dashboard executivo** com Vaadin Charts (processos ativos, prazos da semana, relatórios básicos)
- Atender requisitos de **LGPD** (logs de auditoria, controle de acesso por role, criptografia de dados sensíveis)

## 🧩 Principais Módulos (MVP)

- Autenticação e autorização (Gestor, Advogado, Administrativo)
- Clientes (CRUD completo + histórico de contatos)
- Processos jurídicos (CRUD, workflow de status, vinculado ao cliente e advogado)
- Tarefas e prazos (responsável, prioridade, status, alertas automáticos)
- Documentos (upload/download, vinculado a cliente/processo)
- Comunicações (histórico de atendimentos por cliente)
- Dashboard executivo (prazos da semana, processos ativos, relatórios, logs de auditoria LGPD)

## 🏛 Arquitetura

- **Backend:** Spring Boot 4.0.3, Spring Web, Spring Data JPA, Spring Security
- **Frontend:** Vaadin Flow 25.x
- **Banco de dados:** PostgreSQL 
- **Segurança:**
  - Interface Vaadin usa **Spring Security com sessão server-side** — sem token no cliente
  - `@RolesAllowed` nas views Vaadin para controle de acesso por role
  - **JWT reservado para a API REST** (integrações externas: mobile, webhooks, MegaZap)
  - Criptografia de campos sensíveis com Jasypt (CPF/CNPJ, RG, telefone, caminhos de arquivos)
- **Padrão:** divisão em camadas (controller, service, repository, domain, dto)
- **Infraestrutura:** Docker Compose para PostgreSQL local

> O Vaadin acessa os Services Java diretamente — sem chamadas HTTP internas entre frontend e backend.

## ⚙️ Tecnologias

**Backend**
- Java 25
- Spring Boot 4.0.3 (Web, Security, Data JPA, Validation)
- PostgreSQL
- Maven
- Jasypt (criptografia de campos sensíveis)

**Frontend**
- Vaadin Flow 25.x

**Infraestrutura**
- Docker Compose (PostgreSQL local)

## 📁 Documentação

Toda a arquitetura detalhada (entidades, endpoints, modelo ER, script SQL, sprints, checklist de desenvolvimento) está descrita em:

- `docs/SistemaAdvocacia.md`

## 🗂 Sprints

| Sprint | Duração | Entregáveis |
|---|---|---|
| Sprint 1 | 2 semanas | Setup do projeto, Spring Boot + Vaadin integrado, MainLayout com sidebar, LoginView, autenticação Spring Security com 3 roles |
| Sprint 2 | 2 semanas | CRUD completo de Clientes — ClientListView (Grid), ClientFormView (Binder), ClientDetailView (abas) |
| Sprint 3 | 2 semanas | CRUD de Processos Jurídicos, vínculo Cliente-Processo, workflow de status, timeline visual |
| Sprint 4 | 2 semanas | Upload e gestão de Documentos (Vaadin Upload), histórico de Comunicações por cliente |
| Sprint 5 | 2 semanas | Tarefas e Prazos com alertas, visão Kanban, notificações de deadline via Vaadin Notification |
| Sprint 6 | 2 semanas | Dashboard executivo com Vaadin Charts, relatórios básicos, logs de auditoria LGPD |
| Sprint 7+ | Contínuo | Feedback dos usuários, ajustes, correções |
