# gerador de senhas
O projeto é criar um geranciador simples e completo de senhas como uma fila.

## Estrutura do Projeto 🗂️
```
src/
└── main/
    └── java/
        └── com.myproject.queueSystem/
            ├── config/
            │   └── ConfigCors.java
            │
            ├── controller/
            │   └── QueueController.java
            │
            ├── domain
            │   ├── queue/
            │   │   └── Queue.java
            │   │   ├── QueueRepository.java
            │   │   ├── STATUS.java
            │   │   └── TYPE.java
            │   │       └── dto/
            │   │           └── QueueDTO.java
            │   ├── service/
            │       └── QueueService.java

```
## 1 🗂️ Entidades do Sistemas

### 👤 Queue
```json
{
  "id": "long",
  "code": "string (ex: A001, P002)",
  "type": "NORMAL | PREFERENCIAL",
  "status": "PENDING | CALLED | CANCELLED",
  "timestamp": "datetime"
}
```
## 2 🃏 Funcionalidades

### 2.1 gerar uma senha
- **Endpoint**: `POST /queue`
- **Corpo da requisição (JSON)**
```json
{
  "type": "NORMAL" // ou PREFERENCIAL
}
```
- **Retorno**:
 ```json
{
  "id": 1,
  "code": "N001",
  "type": "NORMAL",
  "status": "PENDING",
  "timestamp": "2025-05-26T20:33:41.2358325"
}
```
### 2.2 Chamar próxima senha
- **Endpoint**: `POST /queue/call`
- **Irá chamar a ultima senha e remover-la da fila**
- **Retorno**:
 ```json
{
  "id": 2,
  "code": "N002",
  "type": "NORMAL",
  "status": "CALLED",
  "timestamp": "2025-05-26T20:33:41.235833"
}
```
### 2.3 cancelar senha
- **Endpoint**: `POST /queue/cancel/:code`
- **Irá cancelar uma senha recebida no endpoint**

### 2.4 Listar senhas em fila
- **Endpoint**: `GET /queue/list`
- **Retorno**:
 ```json
[
  {
    "id": 5,
    "code": "P001",
    "type": "PREFERENCIAL",
    "status": "PENDING",
    "timestamp": "2025-05-28T19:45:48.166887"
  },
  {
    "id": 6,
    "code": "P002",
    "type": "PREFERENCIAL",
    "status": "PENDING",
    "timestamp": "2025-05-28T19:45:48.987983"
  }
]
```
### 2.5 Resetar senhas do dia
- **Endpoint**: `POST /queue/reset`
- **Irá limpar os códigos (code = null) de todas elas**

## 🛠️ Ferramentas
- Intellij
- Java/SpringBoot
- Mysql
- Insomnia(postman)
