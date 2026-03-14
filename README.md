# Advocacia System

Sistema web para gestão de escritório de advocacia, desenvolvido em **Java (Spring Boot)** no backend e **Vaadin** no frontend.  
O objetivo é substituir o uso combinado de ADVBOX, Pipedrive e pastas locais, centralizando clientes, processos, tarefas, documentos e histórico de atendimentos em um único sistema.

## 🎯 Objetivo

- Centralizar cadastro de **clientes** e **processos jurídicos**
- Controlar **tarefas** e **prazos** dos advogados
- Registrar **histórico de atendimentos** (WhatsApp, ligações, etc.)
- Organizar **documentos** vinculados a clientes e processos
- Fornecer **dashboard** com visão rápida do escritório (processos ativos, prazos da semana, etc.)
- Atender requisitos básicos de **LGPD** (logs, controle de acesso, dados sensíveis)

## 🧩 Principais Módulos (MVP)

- Autenticação e autorização (Admin, Advogado, Administrativo)
- Clientes (CRUD completo + histórico de contatos)
- Processos jurídicos (CRUD, status, vinculado ao cliente e advogado)
- Tarefas e prazos (responsável, prioridade, status)
- Documentos (upload/download, vinculado a cliente/processo)
- Dashboard inicial (prazos da semana, processos ativos)

## 🏛 Arquitetura

- **Backend:** Spring Boot 3, Spring Web, Spring Data JPA, Spring Security (JWT)
- **Frontend:** Vaadin
- **Banco de dados:** PostgreSQL
- **Segurança:** autenticação JWT, controle de roles, preparação para LGPD
- **Padrão:** API REST, divisão em camadas (controller, service, repository, domain, dto)

Toda a arquitetura detalhada (pastas, entidades, endpoints, modelo ER) está descrita no documento:

- `docs/arquitetura_projeto_advocacia.md`

## ⚙️ Tecnologias

**Backend**

- Java 17+
- Spring Boot (Web, Security, Data JPA, Validation)
- PostgreSQL
- Maven

**Frontend**

- Vaadin

