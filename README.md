# PicPay Simplificado – Desafio Back-End

Este projeto é uma implementação do desafio técnico **PicPay Simplificado**, que simula uma plataforma de pagamentos entre usuários e lojistas.  
👉 [Link oficial do desafio](https://github.com/PicPay/picpay-desafio-backend)

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **H2 Database** 
- **JUnit 5 e Mockito** (para testes unitários)
- **Maven**

---

## ⚙️ Funcionalidades

- Cadastro de usuários 
- Transferência de valores entre usuários
- Validação de saldo e tipo de usuário
- Consulta a serviço externo de autorização
- Envio de notificações (simulado)
- Transações atômicas com `@Transactional`

---

## 🔒 Regras de Negócio

- **Usuários comuns** podem **enviar e receber** dinheiro.  
- **Lojistas** podem **apenas receber**.  
- **E-mail e CPF/CNPJ** são únicos.  
- **Saldo insuficiente** impede transações.  
- **Valores não positivos** são inválidos.  
- Antes de confirmar uma transação, o sistema consulta um **serviço autorizador externo** (`https://util.devi.tools/api/v2/authorize`).  
- Após a transação, o sistema tenta enviar uma **notificação** para o pagador e o recebedor (`https://util.devi.tools/api/v1/notify`).  
- Caso a notificação falhe, o processo **não é revertido** — apenas logado.

---

## 🧪 Testes

Os testes unitários foram implementados com **JUnit 5 e Mockito**, cobrindo:

- Criação e atualização de usuários  
- Verificação de regras de negócio (documento, e-mail duplicado etc.)  
- Transações válidas e inválidas  
- Testes com mocks para dependências
