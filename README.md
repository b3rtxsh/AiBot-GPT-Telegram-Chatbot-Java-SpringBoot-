# AiBot (GPT)

**AiBot** is a simple and extensible Telegram bot that provides access to ChatGPT via the OpenAI API. It is built using **Java 21** and **Spring Boot 3.4.3**.

---

## 🚀 Features

- **ChatGPT Integration**  
  Chat with GPT through the OpenAI API directly in Telegram.

- **Spring Boot Framework**  
  Uses Spring Boot for streamlined development, testing, and deployment.

- **Database Support**  
  Stores user messages to maintain a unique chat history for each user.

- **Future Improvements (Planned)**  
  - Webhook-based architecture using TelegramBots API v6.8.0 (`WebhookBot` interface)  
  - Security layer integration  
  - Database schema optimization and refactoring  
  - Introduction of a `Usage` entity to enable commercial features

---

## 📦 Dependencies

This project uses the following libraries:

- Spring Boot:
  - `spring-boot-starter-web`
  - `spring-boot-starter-jdbc`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-tomcat`
  - `spring-boot-starter-test`
  - `spring-security-test`
  - `spring-boot-docker-compose`
- Flyway: `flyway-core`
- Java APIs: `jaxb-api`, `jaxb-runtime`, `jakarta.el`, `javax.activation`
- ORM & Validation: `hibernate-validator`
- Telegram Integration: `telegrambots`
- Database: `postgresql`
- JSON Processing: `json`
- Utilities: `lombok`

---

## 🛠 Prerequisites

Make sure you have the following installed and configured:

- [Docker Desktop](https://www.docker.com/products/docker-desktop) — must be running
- A Telegram Bot Token — [Get it from @BotFather](https://t.me/BotFather)
- An OpenAI API Key — [Get it from OpenAI](https://openai.com/)

---

## ⚙️ Setup Instructions

1. **Clone the repository**  
   ```bash
   git clone https://github.com/your-username/aibot.git
   cd aibot
   ```

2. **Configure Docker**  
   Modify `docker-compose.yml` as needed for your environment.

3. **Set up application properties**  
   Open `src/main/resources/application.properties` and configure:
   - Bot name
   - Bot token
   - OpenAI API key (optional: use environment variables or secrets manager)

4. **Run the Application**  
   - Open the project in your IDE (e.g., IntelliJ IDEA)  
   - Start the application  
   - Monitor the console for logs and bot interaction feedback

---

## 📝 License

This project is licensed under the [MIT License](LICENSE).

---

## 🔒 Note

Sensitive information like API keys is **not** included in the repository. Be sure to store them securely (e.g., environment variables, secret managers).

---

## 💡 Inspiration

Inspired by this [YouTube Guide](https://www.youtube.com/watch?v=2ZAI4cVjN6Y) on building a Telegram GPT bot.
