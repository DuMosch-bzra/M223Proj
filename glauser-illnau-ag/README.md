# Glauser Illnau AG – Auftragsverwaltung

Modul M223 – Full-Stack Webanwendung  
**Stack:** Spring Boot 3.3 · React 18 · TypeScript · Supabase (PostgreSQL)

---

## Schnellstart

### Schritt 1 – Supabase Datenbank einrichten

1. Account erstellen auf [supabase.com](https://supabase.com) und neues Projekt anlegen
2. Im Supabase Dashboard → **SQL Editor** → Inhalt von `backend/src/main/resources/schema.sql` einfügen und ausführen
3. Projektref und DB-Passwort notieren (Settings → Database)

### Schritt 2 – Backend konfigurieren

Datei `backend/src/main/resources/application.properties` öffnen und anpassen:

```properties
spring.datasource.url=jdbc:postgresql://db.DEIN-PROJEKT-REF.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=DEIN-PASSWORT
```

### Schritt 3 – Backend starten

```powershell
# Windows PowerShell
cd backend
mvn spring-boot:run

# Oder in IntelliJ: AuftragsverwaltungApplication.java → Rechtsklick → Run
```

API läuft auf: http://localhost:8080

### Schritt 4 – Frontend starten

```powershell
cd frontend
npm install
npm run dev
```

App läuft auf: http://localhost:5173

---

## Test-Benutzer (Passwort: admin123)

| E-Mail | Rolle | Kann |
|--------|-------|------|
| admin@glauser-illnau.ch | ADMIN | Alles |
| manager@glauser-illnau.ch | MANAGER | Disponieren, Rapport freigeben |
| peter.keller@glauser-illnau.ch | EMPLOYEE | Starten, Rapport erfassen |

---

## Workflow

```
1. CREATED     ← GL/Admin erfasst Auftrag
2. SCHEDULED   ← BL disponiert (weist MA zu, setzt Termin)
3. IN_PROGRESS ← MA startet Ausführung
4. COMPLETED   ← MA erfasst Rapport (Status wechselt automatisch)
5. APPROVED    ← BL gibt Rapport zur Verrechnung frei
6. INVOICED    ← Admin verrechnet
```

---

## API Endpoints

| Methode | URL | Beschreibung |
|---------|-----|-------------|
| POST | /api/auth/login | Login → JWT |
| GET | /api/work-orders | Alle Aufträge (optional ?status=CREATED) |
| GET | /api/work-orders/{id} | Einzelauftrag |
| POST | /api/work-orders | Auftrag erfassen |
| PATCH | /api/work-orders/{id}/dispatch | Disponieren |
| PATCH | /api/work-orders/{id}/start | Starten |
| POST | /api/work-orders/{id}/report | Rapport erfassen |
| PATCH | /api/work-orders/{id}/approve-report | Rapport freigeben |
| PATCH | /api/work-orders/{id}/invoice | Verrechnen |
| GET | /api/customers | Kundenliste |
| GET | /api/employees | Mitarbeiterliste |

---

## Projektstruktur

```
glauser-illnau-ag/
├── backend/
│   ├── src/main/java/ch/glauserillnau/auftragsverwaltung/
│   │   ├── config/       SecurityConfig, GlobalExceptionHandler
│   │   ├── controller/   Auth, WorkOrder, Customer, Employee, Report
│   │   ├── dto/          Request/Response DTOs
│   │   ├── entity/       JPA Entities
│   │   ├── enums/        OrderStatus, Role, DocumentType
│   │   ├── repository/   Spring Data JPA Repositories
│   │   ├── security/     JwtUtil, JwtAuthFilter
│   │   └── service/      Business Logic
│   └── src/main/resources/
│       ├── application.properties
│       └── schema.sql
└── frontend/
    └── src/
        ├── api/           Axios client + API services
        ├── components/    Layout, StatusBadge
        ├── context/       AuthContext (JWT)
        ├── pages/         Login, Dashboard, WorkOrders, Detail, Customers
        └── types/         TypeScript Interfaces
```
