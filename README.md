
# 🚗 Estapar Parking Management System

Este projeto foi desenvolvido como parte do **teste técnico para Desenvolvedor Backend Kotlin** da **Estapar**.

O sistema é responsável por gerenciar a operação de estacionamentos, desde o controle de entrada e saída de veículos até o cálculo dinâmico de preços e faturamento por setor, com base em regras específicas fornecidas pela empresa.

---

## 🧠 Funcionalidades

- Importação automática da configuração de garagem via simulador externo
- Processamento de eventos de entrada, estacionamento e saída de veículos via webhook
- Cálculo de ocupação em tempo real por setor
- Precificação dinâmica baseada na lotação do setor
- Regras de cobrança com carência de 15 minutos e tarifa pró-rata a cada 15 minutos
- Controle de vagas e fechamento automático de setor com 100% de ocupação
- API REST para:
  - Consulta do status de uma placa
  - Consulta da ocupação de uma vaga
  - Faturamento por setor e data

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Framework**: Spring Boot `3.5.0`
- **Build Tool**: Gradle Kotlin DSL
- **JDK**: Java 21
- **Banco de Dados**: MySQL 8
- **Testes**: JUnit 5 + Mockito
- **Documentação**: SpringDoc OpenAPI
- **Containerização**: Docker + Docker Compose
- **Migrations**: Flyway

---

## ▶️ Como executar o projeto localmente

### 1. Clone o repositório

```bash
git clone https://github.com/rafaelnascimentodev/parking-manager.git
cd parking-manager
```

### 2. Suba os serviços necessários (MySQL + Simulador)

```bash
docker-compose up -d
```

Isso irá iniciar:
- Um container MySQL na porta `3306`
- O simulador de garagem na porta `8080`

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=parkingdb
DB_USER=root
DB_PASSWORD=root
```

Ou exporte essas variáveis diretamente no terminal, se preferir.

### 4. Rode a aplicação

Utilizando Gradle:

```bash
./gradlew bootRun
```

Ou execute a classe `ParkingManangerApplication.kt` pela sua IDE.

### 5. Acesse a documentação da API

Após a aplicação subir, acesse:

```
http://localhost:8081/swagger-ui.html
```

> Verifique se a porta da aplicação está configurada como `8081` no `application.yml` para evitar conflito com o simulador.

---

## 🐘 Flyway Migrations

As migrations do banco de dados são executadas automaticamente ao iniciar a aplicação. O script principal está localizado em:

```
src/main/resources/db/migration/V1__criar_tabelas_setor_vaga_veiculo_sessaoestacionamento_faturamento.sql
src/main/resources/db/migration/V2__adiciona_campo_preco_base_na_tabela_sessaoestacionamento.sql
```

---

## 🐳 Docker Compose

O arquivo `docker-compose.yaml` configura dois serviços essenciais:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: estapar-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: parkingdb
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    restart: unless-stopped

  garage-simulador:
    image: cfontes0estapar/garage-sim:1.0.0
    container_name: estapar-garage-simulator
    ports:
      - "8080:3000"
    restart: unless-stopped

volumes:
  mysql-data:
```

---

## ✅ Status

✔️ Pronto para rodar localmente e consumir eventos do simulador.

---

## 📬 Contato

Em caso de dúvidas, entre em contato via [GitHub Issues](https://github.com/rafaelnascimentodev/parking-manager/issues).
