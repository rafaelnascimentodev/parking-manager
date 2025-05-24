CREATE TABLE setores_tb
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome                   VARCHAR(100) NOT NULL,
    preco_base DOUBLE NOT NULL,
    capacidade_maxima      INT          NOT NULL,
    horario_abertura       TIME         NOT NULL,
    horario_fechamento     TIME         NOT NULL,
    limite_duracao_minutos INT          NOT NULL,
    ocupacao_atual         INT     DEFAULT 0,
    esta_fechado           BOOLEAN DEFAULT FALSE
);

CREATE TABLE veiculos_tb
(
    placa VARCHAR(20) PRIMARY KEY
);

CREATE TABLE vagas_tb
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    setor_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    ocupada  BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_vaga_setor FOREIGN KEY (setor_id) REFERENCES setores_tb (id)
);

CREATE TABLE sessao_estacionamento_tb
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    veiculo_placa       VARCHAR(20) NOT NULL,
    vaga_id             BIGINT      NOT NULL,
    setor_id            BIGINT      NOT NULL,
    horario_entrada     DATETIME    NOT NULL,
    horario_estacionado DATETIME,
    horario_saida       DATETIME,
    preco_cobrado DOUBLE,
    esta_ativa          BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_sessao_veiculo FOREIGN KEY (veiculo_placa) REFERENCES veiculos_tb (placa),
    CONSTRAINT fk_sessao_vaga FOREIGN KEY (vaga_id) REFERENCES vagas_tb (id),
    CONSTRAINT fk_sessao_setor FOREIGN KEY (setor_id) REFERENCES setores_tb (id)
);

CREATE TABLE faturamentos_tb
(
    id    VARCHAR(36) PRIMARY KEY,
    setor VARCHAR(100) NOT NULL,
    data  DATE         NOT NULL,
    valor DOUBLE NOT NULL
);
