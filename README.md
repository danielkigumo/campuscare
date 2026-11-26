# Campus Care - Student Service Request & Complaint Management System

A complete Spring Boot 4.1.2 application for managing student service requests and complaints in a university setting.

## Tech Stack

- **Backend**: Java 17, Spring Boot 4.1.2, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (production), H2 (testing)
- **Authentication**: JWT (JSON Web Tokens)
- **Migration**: Flyway
- **Frontend**: HTML5, Bootstrap 5, Vanilla JavaScript, Chart.js
- **Build**: Maven

## Project Structure

```
src/
├── main/
│   ├── java/com/campuscare/campuscare/
│   │   ├── CampuscareApplication.java
│   │   ├── controller/          # REST Controllers
│   │   │   ├── AuthController.java        # Registration, Login, Token Refresh
│   │   │   ├── RequestController.java     # Student request CRUD
│   │   │   ├── AdminController.java       # Admin dashboard, assignment, users
│   │   │   ├── TrackingController.java    # Request tracking, timeline, notifications
│   │   │   └── CategoryController.java    # Category listing
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   ├── Department.java
│   │   │   ├── Category.java
│   │   │   ├── Request.java
│   │   │   ├── RequestStatus.java
│   │   │   ├── RequestStatusHistory.java
│   │   │   ├── Response.java
│   │   │   └── Notification.java
│   │   ├── repository/          # Spring Data JPA Repositories
│   │   ├── security/            # JWT & Security Config
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── UserPrincipal.java
│   │   └── service/             # Business Logic
│   │       ├── AuthService.java
│   │       ├── RequestService.java
│   │       ├── AdminService.java
│   │       └── TrackingService.java
│   └── resources/
│       ├── application.properties
│       ├── db/migration/V1__schema.sql
│       └── static/              # Frontend pages
│           ├── login.html
│           ├── register.html
│           ├── dashboard.html       # Student dashboard
│           ├── admin-dashboard.html # Admin/Staff dashboard
│           └── tracking.html        # Request tracking & responses
└── test/
    ├── java/com/campuscare/campuscare/
    │   └── CampuscareApplicationTests.java
    └── resources/
        └── application-test.properties  # H2 test config
```

## Features

### Authentication Module
- Student registration with validation
- JWT-based login with access & refresh tokens
- Role-based access control (STUDENT, STAFF, ADMIN)
- Password encryption with BCrypt
- Token refresh endpoint

### Request Management (Student)
- Create service requests & complaints
- View own requests with filtering
- Update requests (only in SUBMITTED status)
- Cancel requests (only in SUBMITTED status)
- Real-time status tracking

### Tracking & Responses
- Chronological timeline (status changes + responses)
- Staff/Admin can update request status with audit trail
- Public responses visible to students
- Internal staff notes (hidden from students)
- Email-like notifications for students

### Admin Dashboard
- Statistics cards (total, submitted, in-progress, resolved, rejected)
- Requests by status chart (Chart.js)
- Paginated, searchable, filterable request table
- Assign requests to staff
- User management (view all users, staff list)

## Quick Start

### Prerequisites
- Java 17+ (JDK)
- Maven (or use included `mvnw`)
- PostgreSQL 16+ (or Docker)

### 1. Start PostgreSQL (Docker)
```bash
docker-compose up -d
```

### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campuscare
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Build & Run
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or with compiled JAR
./mvnw clean package
java -jar target/campuscare-0.0.1-SNAPSHOT.jar
```

### 4. Access Application
- **Student Portal**: http://localhost:8080/login.html
- **Admin Portal**: http://localhost:8080/login.html (login as admin/staff)
- **Request Tracking**: http://localhost:8080/tracking.html?requestId=1

### 5. Run Tests
```bash
./mvnw test
```
Tests use H2 in-memory database automatically.

## Default Test Accounts (from Flyway migration)
| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@strathmore.edu | (register via app) |
| STAFF | staff@strathmore.edu | (register via app) |
| STUDENT | student@strathmore.edu | (register via app) |

> **Note**: The seed data in `V1__schema.sql` has placeholder password hashes. Register new users through the application.

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new student |
| POST | `/api/auth/login` | Login, get JWT tokens |
| POST | `/api/auth/refresh` | Refresh access token |

### Student Requests
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/requests` | Create new request |
| GET | `/api/requests/my` | Get my requests |
| GET | `/api/requests/{id}` | Get request details |
| PUT | `/api/requests/{id}` | Update request (SUBMITTED only) |
| DELETE | `/api/requests/{id}` | Cancel request (SUBMITTED only) |

### Admin/Staff
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Dashboard statistics |
| GET | `/api/admin/requests` | All requests (paginated) |
| GET | `/api/admin/requests/status/{status}` | Filter by status |
| PUT | `/api/admin/requests/{id}/assign` | Assign to staff |
| GET | `/api/admin/users` | All users (paginated) |
| GET | `/api/admin/users/staff` | Staff members |

### Tracking & Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/api/requests/{id}/status` | Update status (audit trail) |
| POST | `/api/requests/{id}/responses` | Add response/note |
| GET | `/api/requests/{id}/timeline` | Get timeline |
| GET | `/api/users/{id}/notifications` | Get user notifications |
| PATCH | `/api/notifications/{id}/read` | Mark as read |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | All categories |
| GET | `/api/categories/active` | Active categories only |

## Request Status Flow
```
SUBMITTED → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
                ↓           ↓
             REJECTED    REJECTED
                ↓
            CANCELLED (from SUBMITTED only)
```

## Development

### Database Migration
Flyway migrations in `src/main/resources/db/migration/`:
- `V1__schema.sql` - Initial schema with seed data

### Adding New Migrations
```sql
-- V2__description.sql
ALTER TABLE users ADD COLUMN new_field VARCHAR(100);
```

### Frontend Development
Static files in `src/main/resources/static/` are served directly. No build step required.

## Security Notes
- Change `app.jwt.secret` in production (use 256-bit key)
- Use HTTPS in production
- Configure CORS for production domains
- Enable `spring.jpa.hibernate.ddl-auto=validate` in production

## Team (Group 7)
- Daniel Kigumo (220852)
- Lionel Arisa (220842)
- Patrick Chiaji (223611)
- Brown Mitsanze (223608)
- Richard Luduba (222763)
- Adrian Bett (221411)
- Samantha Mobisa (220935)
- Nicholas Nyukuri (222205)

## License
Academic Project - Group 7 Software Engineering

## University email sign-in

CampusCare accepts login and student self-registration addresses ending in
`@strathmore.edu`. The backend enforces this domain; the browser check is only
for quicker feedback. Login still requires an existing active account, a
matching BCrypt password, and the role already assigned to that account in
the database. Public registration creates STUDENT accounts only; it cannot
create STAFF or ADMIN accounts.

The H2 development seed accounts use:
- `student@strathmore.edu`
- `staff@strathmore.edu`
- `admin@strathmore.edu`

The seeded development password remains `password123`. Do not use these
seed credentials or the development H2 configuration in production.
