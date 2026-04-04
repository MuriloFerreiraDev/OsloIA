# Oslo IA 🤖

> API REST de Inteligência Artificial integrada ao Google Gemini, com autenticação JWT, histórico de conversas e infraestrutura completa na AWS.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-blue?style=flat-square&logo=docker)
![AWS](https://img.shields.io/badge/AWS-EKS%20%7C%20RDS%20%7C%20ECR-orange?style=flat-square&logo=amazonaws)
![Terraform](https://img.shields.io/badge/Terraform-IaC-purple?style=flat-square&logo=terraform)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-black?style=flat-square&logo=githubactions)

---

## 📋 Sobre o Projeto

O **Oslo IA** é uma API REST que permite aos usuários conversarem com a Inteligência Artificial do Google Gemini de forma segura e autenticada. Cada usuário possui seu próprio histórico de conversas armazenado em banco de dados.

O projeto foi desenvolvido com foco em boas práticas de desenvolvimento, segurança e infraestrutura em nuvem, sendo um projeto completo de portfólio que cobre desde o desenvolvimento até o deploy em produção.

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                     Cliente / Swagger                    │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP
┌─────────────────────────▼───────────────────────────────┐
│              AWS Load Balancer (ELB)                     │
│         muriloferrieradev.ddns.net                       │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                  AWS EKS (Kubernetes)                    │
│                                                          │
│   ┌──────────────────────────────────────────────────┐  │
│   │              Oslo IA (Spring Boot)               │  │
│   │                                                  │  │
│   │  /auth/register  →  Cadastro de usuário          │  │
│   │  /auth/login     →  Autenticação JWT             │  │
│   │  /ai/chat        →  Conversa com Gemini          │  │
│   │  /ai/history     →  Histórico de conversas       │  │
│   └──────────────┬───────────────────────────────────┘  │
└──────────────────┼──────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼──────┐    ┌─────────▼────────┐
│  AWS RDS     │    │  Google Gemini   │
│  PostgreSQL  │    │  API             │
└──────────────┘    └──────────────────┘
```

---

## 🚀 Tecnologias

### Backend
| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web |
| Spring Security | 6.x | Segurança e autenticação |
| JWT (JJWT) | 0.11.5 | Tokens de autenticação |
| Spring Data JPA | 3.x | Persistência de dados |
| Flyway | 9.x | Migrations de banco |
| SpringDoc OpenAPI | 2.3.0 | Documentação Swagger |
| WebFlux | 6.x | Cliente HTTP reativo |

### Banco de Dados
| Tecnologia | Uso |
|---|---|
| PostgreSQL 15 | Banco de dados principal |
| Docker Compose | Ambiente de desenvolvimento local |
| AWS RDS | Banco de dados em produção |

### Infraestrutura & DevOps
| Tecnologia | Uso |
|---|---|
| Docker | Containerização da aplicação |
| AWS ECR | Repositório de imagens Docker |
| AWS EKS | Orquestração de containers (Kubernetes) |
| AWS RDS | Banco de dados gerenciado |
| Terraform | Infraestrutura como código (IaC) |
| GitHub Actions | CI/CD automatizado |
| Kubernetes | Deployment, Service, Secrets |

### IA
| Tecnologia | Uso |
|---|---|
| Google Gemini 2.5 Flash | Modelo de linguagem |

---

## 📁 Estrutura do Projeto

```
oslo-ia/
├── src/
│   └── main/
│       ├── java/com/muriloDev/osloIA/
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   └── SwaggerConfig.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   └── ChatController.java
│       │   ├── domain/
│       │   │   ├── enums/Role.java
│       │   │   └── model/
│       │   │       ├── User.java
│       │   │       └── ChatHistory.java
│       │   ├── dto/
│       │   │   ├── request/
│       │   │   └── response/
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   └── ChatHistoryRepository.java
│       │   ├── security/
│       │   │   ├── JwtService.java
│       │   │   ├── JwtFilter.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       └── GeminiService.java
│       └── resources/
│           ├── application.properties
│           └── db/migration/
│               ├── V1__create_users_table.sql
│               └── V2__create_chat_history_table.sql
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── terraform/
│   └── main.tf
├── .github/
│   └── workflows/
│       └── deploy.yml
├── docker-compose.yml
└── Dockerfile
```

---

## 🔌 Endpoints

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/register` | Cadastro de usuário | ❌ |
| POST | `/auth/login` | Login e retorno do JWT | ❌ |




### IA

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/ai/chat` | Envia mensagem para o Gemini | ✅ JWT |
| GET | `/ai/history` | Histórico de conversas do usuário | ✅ JWT |

---

## ⚠️ Códigos de Resposta HTTP

| Código | Descrição |
|---|---|
| **200** | Requisição bem sucedida |
| **201** | Recurso criado com sucesso |
| **400** | Dados inválidos na requisição |
| **401** | Credenciais inválidas ou token expirado |
| **403** | Sem permissão de acesso |
| **404** | Recurso não encontrado |
| **409** | Conflito — email já cadastrado |
| **429** | Limite de requisições do Gemini atingido |
| **503** | Serviço do Gemini indisponível |
| **500** | Erro interno no servidor |

### Exemplo de resposta de erro
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists",
  "timestamp": "2026-04-04T10:30:00"
}
```

## 📖 Como Usar

### 1. Cadastro
```bash
curl -X POST http://muriloferrieradev.ddns.net/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Seu Nome",
    "email": "seu@email.com",
    "password": "123456"
  }'
```

### 2. Login
```bash
curl -X POST http://muriloferrieradev.ddns.net/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seu@email.com",
    "password": "123456"
  }'
```

### 3. Chat com IA
```bash
curl -X POST http://muriloferrieradev.ddns.net/ai/chat \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{"message": "Olá! Quem é você?"}'
```

### 4. Histórico
```bash
curl -X GET http://muriloferrieradev.ddns.net/ai/history \
  -H "Authorization: Bearer {seu-token}"
```

---

## 📚 Documentação

Acesse o Swagger para testar todos os endpoints interativamente:

```
http://muriloferrieradev.ddns.net/swagger-ui.html

> ⚠️ **Acesso externo disponível até 10/04/2026.** 
Após essa data o ambiente AWS será desligado para evitar custos. 
Para testar localmente siga as instruções da seção **Rodando Localmente**.
```

---

## 🛠️ Rodando Localmente

### Pré-requisitos
- Java 21
- Maven
- Docker Desktop

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/MuriloFerreiraDev/OsloIA.git
cd OsloIA

# 2. Suba o banco de dados
docker-compose up -d

# 3. Configure a variável de ambiente
export GEMINI_API_KEY=sua-chave-aqui

# 4. Rode a aplicação
./mvnw spring-boot:run
```

Acesse: `http://localhost:8080/swagger-ui.html`

---

## ☁️ Infraestrutura AWS

A infraestrutura é provisionada com **Terraform** na região `sa-east-1` (São Paulo):

```bash
cd terraform
terraform init
terraform apply
```

### Recursos criados:
- **ECR** — Repositório de imagens Docker
- **EKS** — Cluster Kubernetes com Node Group t3.small
- **RDS** — PostgreSQL 15 (db.t3.micro)
- **Security Groups** — Configurados para cada serviço
- **IAM Roles** — Permissões para EKS e Node Group

---

## 🔄 CI/CD

O pipeline de CI/CD é executado automaticamente a cada push na branch `main`:

```
Push na branch main
        ↓
Build da imagem Docker
        ↓
Push para o AWS ECR
        ↓
Atualização dos Kubernetes Secrets
        ↓
Deploy no AWS EKS
        ↓
Rolling Update (zero downtime)
```

---

## 🔐 Segurança

- Autenticação via **JWT** com expiração de 24 horas
- Senhas criptografadas com **BCrypt**
- Credenciais armazenadas em **Kubernetes Secrets**
- Secrets do pipeline armazenados no **GitHub Secrets**
- Banco de dados acessível apenas dentro da VPC

---

## 🧪 Testes

O projeto possui testes unitários implementados com **JUnit 5** e **Mockito**.

### Executar os testes
```bash
./mvnw test
```

### Cobertura de testes

| Classe | Testes | Cenários cobertos |
|---|---|---|
| `AuthService` | 5 | Register com sucesso, email duplicado, login com sucesso, senha incorreta, usuário não encontrado |

### Tecnologias utilizadas nos testes

| Tecnologia | Uso |
|---|---|
| **JUnit 5** | Framework de testes |
| **Mockito** | Mock de dependências |
| **AssertJ** | Verificações dos testes |
| **H2** | Banco em memória para testes de integração |

---

## 👨‍💻 Autor

**Murilo Ferreira**

[![GitHub](https://img.shields.io/badge/GitHub-MuriloFerreiraDev-black?style=flat-square&logo=github)](https://github.com/MuriloFerreiraDev)

---

## 📄 Licença

Este projeto está sob a licença MIT.
