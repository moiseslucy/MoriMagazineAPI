# MoriMagazineAPI

API para gerenciar clientes, produtos e transações financeiras.

## Requisitos
- Java 11 ou superior
- Maven
- MySQL (ou outro banco compatível)

## Como configurar o ambiente de desenvolvimento
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/MoriMagazineAPI.git
   ```
2. Acesse a pasta do projeto:
   ```bash
   cd MoriMagazineAPI
   ```
3. Configure as variáveis de ambiente (exemplo Linux/Mac):
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mori_db
   export SPRING_DATASOURCE_USERNAME=seu_usuario
   export SPRING_DATASOURCE_PASSWORD=sua_senha
   ```
   Ou ajuste `application.properties` conforme seu ambiente local.

## Como compilar e executar
- Com Maven:
  ```bash
  mvn clean install
  mvn spring-boot:run
  ```
- Ou abra o projeto no NetBeans/IntelliJ/Eclipse como um projeto Maven e execute a classe `MoriMagazineApiApplication`.

## Principais endpoints
- `GET /clientes` – Lista todos os clientes.
- `POST /clientes` – Cria um novo cliente.
- `GET /produtos` – Lista todos os produtos.
- `POST /produtos` – Cria um novo produto.
- `GET /transacoes` – Lista todas as transações.
- `POST /transacoes` – Registra nova transação.

## Dependências
- Spring Boot Starter Web
- Spring Data JPA
- MySQL Connector/J
- SLF4J / Logback (logging)
- Lombok (opcional)
- Jackson (JSON)

## Executar testes
```bash
mvn test
```

## Licença
Defina a licença (por exemplo, MIT, Apache 2.0) aqui.

## Contato
Em caso de dúvidas, abra uma issue ou envie e-mail para seu-email@dominio.com.
