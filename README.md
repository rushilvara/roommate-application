# Roommate Application (Spring Boot + JSP + MySQL)

A full roommate matching MVP built with Spring Boot MVC, JSP, HTML/CSS/JS, MySQL, and Spring Data JPA.

## Features

- User registration and login (BCrypt password hashing)
- Session-based authentication and protected routes
- Profile management
- Room CRUD (create/list/search/view/edit/delete) with ownership checks
- Roommate preferences
- Ranked room matches with score summaries (city, budget, gender, lifestyle)

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring MVC + JSP
- Spring Data JPA + Hibernate
- MySQL Connector/J
- Maven

## Windows Setup (MySQL Server 8.0 + MySQL Workbench 8.0)

> MySQL Workbench is only a GUI client. **MySQL Server 8.0 must be installed and running** (Windows service usually named `MySQL80`).

### 1) Install prerequisites

- JDK 17+
- Maven 3.9+
- MySQL Server 8.0
- MySQL Workbench 8.0
- Git

### 2) Start MySQL Server

- Open **Services** on Windows
- Find `MySQL80`
- Start/Restart if not running

### 3) Create database

Open MySQL Workbench, connect to your local server, run:

```sql
CREATE DATABASE IF NOT EXISTS roommate_db;
```

(Optional app user)

```sql
CREATE USER IF NOT EXISTS 'roommate_user'@'localhost' IDENTIFIED BY 'roommate_password';
GRANT ALL PRIVILEGES ON roommate_db.* TO 'roommate_user'@'localhost';
FLUSH PRIVILEGES;
```

### 4) Configure environment variables (recommended)

PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-mysql-password"
# Optional custom JDBC URL:
# $env:DB_URL="jdbc:mysql://localhost:3306/roommate_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

The app uses defaults from `application.properties`:

- `DB_URL` default: `jdbc:mysql://localhost:3306/roommate_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
- `DB_USERNAME` default: `root`
- `DB_PASSWORD` default: empty

No credentials are hardcoded in source.

### 5) Run the app

From repository root:

```bash
mvn clean test
mvn spring-boot:run
```

Open:

- `http://localhost:8080/`
- `http://localhost:8080/register`
- `http://localhost:8080/login`

## Route Map

- Public: `/`, `/register`, `/login`
- Authenticated: `/logout`, `/dashboard`, `/profile`
- Rooms: `/rooms`, `/rooms/new`, `/rooms/{id}`, `/rooms/{id}/edit`, `/rooms/{id}/delete`
- Preferences: `/preferences`
- Matches: `/matches`

## Database Behavior

- `spring.jpa.hibernate.ddl-auto=update` for local development
- Hibernate creates/updates schema from JPA entities
- `schema.sql` is not required for this MVP

## Testing

Tests run with an H2 test profile (no live MySQL required):

- matching logic unit test
- registration/password hashing unit test
- auth guard controller flow test

Run:

```bash
mvn test
```

## Troubleshooting

1. **Access denied for user**
    - Check `DB_USERNAME`/`DB_PASSWORD`
    - Validate MySQL account in Workbench
2. **Connection refused**
    - Ensure `MySQL80` service is running
    - Confirm port `3306`
3. **JSP not rendering**
    - Ensure app starts with Maven (`spring-boot:run`) and Jasper dependency remains in `pom.xml`
4. **No tables created**
    - Confirm DB name is `roommate_db`
    - Verify `spring.jpa.hibernate.ddl-auto=update`

## Merge and Run on Main Branch

If working via PR branch, merge the PR into `main` in GitHub, then pull latest and run:

```bash
git checkout main
git pull
mvn clean test
mvn spring-boot:run
```
