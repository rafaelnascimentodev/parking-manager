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
- **Banco de Dados**: PostgreSQL (ou MySQL)
- **Testes**: JUnit 5 + Mockito
- **Documentação**: SpringDoc OpenAPI
- **Containerização**: Docker

---

## ▶️ Como executar o projeto localmente

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
   cd seu-repositorio
