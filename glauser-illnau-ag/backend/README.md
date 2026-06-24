# Glauser Illnau AG – Auftragsverwaltung Backend

Spring Boot REST API for the Sanitärunternehmen Glauser Illnau AG order management system.

## Tech Stack

- **Java 21** + **Spring Boot 3.3**
- **Spring Security** + **JWT** (jjwt 0.12.5)
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** via **Supabase**
- **Lombok**

## Project Structure

```
src/main/java/ch/glauserillnau/auftragsverwaltung/
├── config/
│   ├── GlobalExceptionHandler.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── CustomerController.java
│   ├── EmployeeController.java
│   ├── ReportController.java
│   └── WorkOrderController.java
├── dto/
│   ├── AuthDto.java
│   ├── CustomerDto.java
│   ├── ReportDto.java
│   └── WorkOrderDto.java
├── entity/
│   ├── Appointment.java
│   ├── Customer.java
│   ├── Document.java
│   ├── Employee.java   ← mapped to "user" table
│   ├── Invoice.java
│   ├── Report.java
│   └── WorkOrder.java  ← mapped to "work_order" table
├── enums/
│   ├── DocumentType.java
│   ├── OrderStatus.java
│   └── Role.java
├── repository/         (one per entity)
├── security/
│   ├── JwtAuthFilter.java
│   └── JwtUtil.java
└── service/
    ├── AuthService.java
    ├── CustomerService.java
    ├── ReportService.java
    └── WorkOrderService.java
```

## Setup

### 1. Supabase Database

1. Create a new project at [supabase.com](https://supabase.com)
2. Open the SQL editor and run `src/main/resources/schema.sql`
3. Note your project ref and database password

### 2. Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://db.<YOUR-PROJECT-REF>.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<YOUR-PASSWORD>
app.jwt.secret=<MIN-32-CHARS-SECRET>
```

### 3. Run

```bash
./mvnw spring-boot:run
```

API available at `http://localhost:8080`

## API Endpoints

### Auth
| Method | URL | Description | Auth |
|--------|-----|-------------|------|
| POST | `/api/auth/login` | Login → JWT | Public |
| POST | `/api/auth/register` | Register employee | Public |

### Work Orders
| Method | URL | Description | Role |
|--------|-----|-------------|------|
| GET | `/api/work-orders` | List all (optional `?status=CREATED`) | Any |
| GET | `/api/work-orders/{id}` | Get single order | Any |
| POST | `/api/work-orders` | Create order | ADMIN/MANAGER |
| PUT | `/api/work-orders/{id}` | Update order | Any |
| PATCH | `/api/work-orders/{id}/dispatch` | Assign employee + schedule | ADMIN/MANAGER |
| PATCH | `/api/work-orders/{id}/start` | Mark IN_PROGRESS | Any |
| PATCH | `/api/work-orders/{id}/complete` | Mark COMPLETED | Any |
| PATCH | `/api/work-orders/{id}/approve-report` | Approve report (BL) | ADMIN/MANAGER |
| PATCH | `/api/work-orders/{id}/invoice` | Mark INVOICED | ADMIN |

### Customers
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/customers` | List all |
| GET | `/api/customers/{id}` | Get single |
| POST | `/api/customers` | Create |
| PUT | `/api/customers/{id}` | Update |

### Reports (nested under work-order)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/work-orders/{id}/report` | Get report |
| POST | `/api/work-orders/{id}/report` | Create report (MA) |
| PATCH | `/api/work-orders/{id}/report/approve` | Approve report (BL) |

## Order Status Flow

```
CREATED → SCHEDULED → IN_PROGRESS → COMPLETED → APPROVED → INVOICED
  (GL)       (BL)         (MA)          (MA)        (BL)      (Admin)
```

## Default Test Users (Password: admin123)

| Email | Role |
|-------|------|
| admin@glauser-illnau.ch | ADMIN |
| manager@glauser-illnau.ch | MANAGER |
| peter.keller@glauser-illnau.ch | EMPLOYEE |
