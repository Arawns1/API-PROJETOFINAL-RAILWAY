
-- Apaga a public antes
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;


-- Criação das Tabelas

CREATE TABLE Orgao (
    org_cd_id INTEGER PRIMARY KEY,
    org_tx_nome VARCHAR
);

CREATE TABLE Servico (
    serv_cd_id INTEGER PRIMARY KEY,
    serv_tx_nome VARCHAR,
    fk_orgao_id INTEGER
);

CREATE TABLE Pessoa (
    pes_cd_id INTEGER PRIMARY KEY,
    pes_tx_nome_razao_social VARCHAR,
    pes_tx_CPF_CNPJ VARCHAR (14),
    pes_tx_Identidade_InscricaoMunicipal VARCHAR,
    pes_tx_telefone VARCHAR,
    pes_tx_email VARCHAR,
    pes_dt_data_registro DATE,
    pes_bl_preferencial BOOLEAN,
    fk_endereco_id INTEGER
);

CREATE TABLE Endereco (
    end_cd_id INTEGER PRIMARY KEY,
    end_tx_cep VARCHAR,
    end_tx_logradouro VARCHAR,
    end_tx_bairro VARCHAR,
    end_tx_cidade VARCHAR,
    end_tx_estado VARCHAR,
    end_tx_pais VARCHAR,
    end_tx_numero VARCHAR,
    end_tx_complemento VARCHAR,
    UNIQUE (end_tx_cep, end_cd_id)
);

CREATE TABLE Agendamento (
    age_cd_id INTEGER PRIMARY KEY,
    age_tx_descricao VARCHAR,
    age_dt_data_hora_inicial DATE,
    age_dt_data_hora_final DATE,
    age_dt_data_hora_agendamento TIMESTAMP,
    fk_pessoa_id INTEGER,
    fk_servico_id INTEGER
);

CREATE TABLE Anexo (
    anex_cd_id INTEGER PRIMARY KEY,
    anex_blob_dados OID,
    anex_tx_tipo VARCHAR,
    anex_tx_nome VARCHAR,
    fk_agendamento_id INTEGER
);

CREATE TABLE Usuario (
    usu_cd_id INTEGER PRIMARY KEY,
    usu_tx_nome_usuario VARCHAR UNIQUE,
    usu_tx_senha VARCHAR,
    fk_orgao_id INTEGER,
    fk_cargo_id INTEGER
);

CREATE TABLE Cargo (
    carg_cd_id INTEGER PRIMARY KEY,
    carg_tx_nome VARCHAR
);
 
ALTER TABLE Servico ADD CONSTRAINT FK_Servico_2
    FOREIGN KEY (fk_orgao_id)
    REFERENCES Orgao (org_cd_id)
    ON DELETE RESTRICT;
 
ALTER TABLE Pessoa ADD CONSTRAINT FK_Pessoa_2
    FOREIGN KEY (fk_endereco_id)
    REFERENCES Endereco (end_cd_id)
    ON DELETE RESTRICT;
 
ALTER TABLE Agendamento ADD CONSTRAINT FK_Agendamento_2
    FOREIGN KEY (fk_pessoa_id)
    REFERENCES Pessoa (pes_cd_id)
    ON DELETE RESTRICT;
 
ALTER TABLE Agendamento ADD CONSTRAINT FK_Agendamento_3
    FOREIGN KEY (fk_servico_id)
    REFERENCES Servico (serv_cd_id)
    ON DELETE CASCADE;
 
ALTER TABLE Anexo ADD CONSTRAINT FK_Anexo_2
    FOREIGN KEY (fk_agendamento_id)
    REFERENCES Agendamento (age_cd_id);
 
ALTER TABLE Usuario ADD CONSTRAINT FK_Usuario_3
    FOREIGN KEY (fk_orgao_id)
    REFERENCES Orgao (org_cd_id);
 
ALTER TABLE Usuario ADD CONSTRAINT FK_Usuario_4
    FOREIGN KEY (fk_cargo_id)
    REFERENCES Cargo (carg_cd_id);