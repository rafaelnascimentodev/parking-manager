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
- **Banco de Dados**: MySQL
- **Migrações**: Flyway
- **Testes**: JUnit 5 + Mockito
- **Documentação**: SpringDoc OpenAPI
- **Containerização**: Docker + Docker Compose

---

## ▶️ Como executar o projeto localmente

### ✅ Pré-requisitos

- [Java 21+](https://adoptium.net/)
- [Docker e Docker Compose](https://docs.docker.com/get-docker/)
- Git
- (Opcional) [HTTPie](https://httpie.io/) ou Postman para testar os endpoints

### 🚀 Passo a passo

1. **Clone o repositório:**

```bash
git clone https://github.com/rafaelnascimentodev/parking-manager.git
cd parking-manager
```

2. **Suba o banco de dados (MySQL) com Docker:**

```bash
docker-compose up -d
```

> Isso iniciará um container com MySQL na porta `3306`, com banco `parkingdb`, usuário `root`, senha `root`.

3. **Execute a aplicação localmente:**

```bash
./gradlew bootRun
```

A aplicação estará disponível em:  
📍 `http://localhost:8080`

4. **A documentação da API estará disponível em:**

📘 `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testes

Para rodar os testes unitários e de integração:

```bash
./gradlew test
```

---

## 🧰 Variáveis de Ambiente (application.properties ou .env)

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=parkingdb
DB_USER=root
DB_PASSWORD=root

spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
```

---

## 🐘 Docker Compose (exemplo)

Crie um arquivo `docker-compose.yml` na raiz do projeto com:

```yaml
version: '3.8'

services:
  db:
    image: mysql:8
    container_name: parking-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: parkingdb
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## ❗ Problemas comuns

- ❌ **Erro de schema ou coluna ausente**: verifique se o banco está limpo (`docker-compose down -v` para resetar volumes) e se o Flyway está aplicando corretamente o `V1__create_tables.sql`.
- ❌ **Porta em uso**: certifique-se de que as portas `3306` e `8080` não estão em uso por outros serviços.

---

## 📬 Contato

Caso queira entrar em contato para dúvidas ou sugestões, envie um e-mail para: [seuemail@exemplo.com](mailto:seuemail@exemplo.com)
