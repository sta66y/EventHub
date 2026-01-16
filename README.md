# EventHub

EventHub — backend-приложение для управления мероприятиями, билетами и заказами с JWT-аутентификацией, ролевой моделью доступа и поддержкой бизнес-процессов бронирования и оплаты.

Проект реализован на **Spring Boot Java + Kotlin**, с использованием **Spring Security (JWT)**, **Spring Data JPA**, **PostgreSQL**.

---

## 🚀 Основные возможности

### Пользователи

- Регистрация и аутентификация (JWT)
- Обновление профиля
- Ролевой доступ (USER / ORGANIZER / ADMIN)
- Авторизация по **permissions**
### Мероприятия

- Создание, обновление, удаление мероприятий
- Поиск и фильтрация
- Ограничение количества билето
- Optimistic Lock для защиты от oversell

### Заказы и билеты

- Создание заказа на несколько мероприятий
- Резервирование билетов
- Оплата заказа
- Автоотмена просроченных бронирований (scheduler)
- Контроль доступа к заказам

### Безопасность

- JWT (stateless)
- Custom `UserDetailsService`
- Гранулярные permissions (`@PreAuthorize`)
- Централизованная обработка ошибок

---

## 🧱 Технологический стек

- **Kotlin**
- **Java**
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL**
- **Jackson**
- **Maven**
- **Docker**
- **Gitlab CI/CD**

---

## 🔐 Аутентификация и авторизация

### JWT

- Stateless-подход
- Токен передаётся в заголовке:

`Authorization: Bearer <jwt-token>`

### Flow

1. `/api/v1/auth/register` — регистрация
2. `/api/v1/auth/login` — логин, получение JWT
3. JWT проверяется **на каждом запросе** через `JwtAuthenticationFilter`
4. `SecurityContext` заполняется `Authentication`
5. Доступ к эндпоинтам ограничен через `@PreAuthorize`    

---

## 🧩 Роли и permissions

### Permissions

`USER_READ, USER_UPDATE, USER_DELETE EVENT_READ, EVENT_CREATE, EVENT_UPDATE, EVENT_DELETE ORDER_CREATE, ORDER_READ, ORDER_CANCEL, ORDER_PAY`

### Роли

- **USER** — обычный пользователь
- **ORGANIZER** — управление мероприятиями
- **ADMIN** — полный доступ

Каждая роль содержит набор permissions.

---

## 📡 API (основные эндпоинты)

### Auth

`POST /api/v1/auth/register POST /api/v1/auth/login`

### Users

`GET    /api/v1/users GET    /api/v1/users/search PATCH  /api/v1/users DELETE /api/v1/users`

### Events

`GET    /api/v1/events GET    /api/v1/events/search POST   /api/v1/events PATCH  /api/v1/events/{id} DELETE /api/v1/events/{id}`

### Orders

`POST   /api/v1/orders GET    /api/v1/orders/my GET    /api/v1/orders/search POST   /api/v1/orders/{id}/pay POST   /api/v1/orders/{id}/cancel`

---

## ⚙️ Конфигурация

### application.yml

`spring:   datasource:     url: jdbc:postgresql://localhost:5432/eventhub_db     username: postgres     password: postgres    jpa:     hibernate:       ddl-auto: update     show-sql: true  jwt:   secret: super-long-random-256-bit-key   validity: 3600000`

---

## 🛑 Обработка ошибок

- `@RestControllerAdvice`
- Кастомные exception'ы
- Валидация через `@Valid`
- Security ошибки обрабатываются отдельно (`AuthenticationEntryPoint`)

Пример ответа:

`{   "message": "Заказ не найден",   "status": 404 }`

---

## 🧠 Архитектура

- Controller — только HTTP
- Service — бизнес-логика
- Mapper — преобразование DTO ↔ Entity
- Repository — доступ к данным
- Security — полностью изолирован

---

## 🧪 Статус проекта

- [x]  Security (JWT)
- [x] Роли и permissions
- [x] Orders / Events / Users
- [x] Scheduler
- [ ] Kafka (в процессе)
- [ ] Swagger
- [ ] Тесты (в процессе)
- [ ] Redis

---

## 📌 Планы развития

- Интеграция Kafka (event-driven)
- DLQ и retry-механизмы
- OpenAPI / Swagger UI
- Контрактные тесты
