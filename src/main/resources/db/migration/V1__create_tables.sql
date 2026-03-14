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