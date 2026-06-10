# Leave Management System

A backend REST API for managing employee leave applications — built with Spring Boot. Handles everything from submitting leave requests to manager approvals, with a full notification log and validation rules that keep the data clean.

---

## What It Does

Employees submit leave requests, managers approve or reject them, and the system keeps a record of everything including notification history. It enforces sensible rules like blocking past-date applications, catching overlapping requests, and requiring a reason when a manager rejects.

Three roles exist — **EMPLOYEE**, **MANAGER**, and **ADMIN** — though role-based access control is intentionally left open for now (all endpoints are publicly accessible), making it easy to test the API without authentication overhead during development.

---

## Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| Language    | Java 17                 |
| Framework   | Spring Boot 4.0.6       |
| Persistence | Spring Data JPA + H2    |
| Security    | Spring Security 6       |
| Build Tool  | Maven                   |
| Testing     | JUnit 5 + Mockito       |

The database is H2 in-memory — it resets on every restart, which keeps the setup dead simple. If you need persistence across restarts, swapping to PostgreSQL or MySQL is just a few lines in `application.properties`.

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included `./mvnw` wrapper — no install needed)

### Run the App

```bash
git clone https://github.com/nqobile-x/leave-management-system.git
cd leave-management-system
./mvnw spring-boot:run
```

The server starts on **port 8081**.

### Run Tests

```bash
./mvnw test
```

---

## API Reference

Base URL: `http://localhost:8081`

### Leave Endpoints

| Method | Endpoint                              | Description                          |
|--------|---------------------------------------|--------------------------------------|
| POST   | `/api/leave/submit`                   | Submit a new leave application       |
| GET    | `/api/leave/employee/{employeeId}`    | Get all applications for an employee |
| GET    | `/api/leave/pending`                  | Get all pending applications         |
| GET    | `/api/leave/all`                      | Get every application in the system  |
| PUT    | `/api/leave/cancel/{applicationId}`   | Cancel a pending application         |
| GET    | `/api/leave/notifications`            | View all notification logs           |

### Approval Endpoints

| Method | Endpoint                                    | Description                           |
|--------|---------------------------------------------|---------------------------------------|
| PUT    | `/api/approval/approve/{applicationId}`     | Approve a leave application           |
| PUT    | `/api/approval/reject/{applicationId}`      | Reject a leave application (comment required) |

---

## Request & Response Examples

### Submit a Leave Application

```http
POST /api/leave/submit
Content-Type: application/json

{
  "employeeId": 1,
  "leaveType": "Annual",
  "startDate": "2026-06-20",
  "endDate": "2026-06-25",
  "reason": "Family vacation"
}
```

### Approve a Leave Application

```http
PUT /api/approval/approve/1?comment=Approved, enjoy your leave
```

### Reject a Leave Application

```http
PUT /api/approval/reject/1?comment=Clashes with project deadline
```

---

## Business Rules

- Start date cannot be in the past
- End date must be after or equal to start date
- An employee cannot have two overlapping **PENDING** applications at the same time
- Only **PENDING** applications can be cancelled
- Rejection always requires a comment
- Approval comment is optional

---

## Data Models

### Employee

| Field        | Type   | Notes                           |
|--------------|--------|---------------------------------|
| `id`         | Long   | Auto-generated                  |
| `name`       | String | Required                        |
| `email`      | String | Unique, required                |
| `role`       | Enum   | EMPLOYEE / MANAGER / ADMIN      |
| `department` | String | Optional                        |

### Leave Application

| Field             | Type      | Notes                                        |
|-------------------|-----------|----------------------------------------------|
| `id`              | Long      | Auto-generated                               |
| `employee`        | Employee  | Foreign key                                  |
| `leaveType`       | String    | e.g. Annual, Sick                            |
| `startDate`       | LocalDate | Must be today or future                      |
| `endDate`         | LocalDate | Must be after or equal to start              |
| `reason`          | String    | Required                                     |
| `status`          | Enum      | PENDING / APPROVED / REJECTED / CANCELLED    |
| `managerComment`  | String    | Optional on approve, required on reject      |
| `appliedDate`     | LocalDate | Auto-set on submission                       |

### Notification Log

| Field            | Type      | Notes                                         |
|------------------|-----------|-----------------------------------------------|
| `id`             | Long      | Auto-generated                                |
| `recipientEmail` | String    |                                               |
| `recipientName`  | String    |                                               |
| `subject`        | String    |                                               |
| `message`        | String    |                                               |
| `type`           | Enum      | LEAVE_SUBMITTED / APPROVED / REJECTED / CANCELLED |
| `sentAt`         | Timestamp | Auto-set                                      |

> **Note:** Notifications are logged to the database only — no actual emails are sent. The notification log is there as an audit trail and as a foundation if you want to wire up real email delivery later.

---

## Sample Data

On startup, the `DataSeeder` component pre-loads three employees so you can start hitting the API immediately without manual setup:

| ID | Name             | Role     | Department          |
|----|------------------|----------|---------------------|
| 1  | Nqobile Sibiya   | EMPLOYEE | Software Development |
| 2  | Thabo Dlamini    | MANAGER  | Software Development |
| 3  | Lerato Mokoena   | EMPLOYEE | HR                  |

---

## H2 Console

If you want to poke around the database directly:

- URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:lmsdb`
- Username: `sa`
- Password: *(leave blank)*

---

## Project Structure

```
src/
├── main/java/com/lms/leave_management_system/
│   ├── LeaveManagementSystemApplication.java   # Entry point
│   ├── DataSeeder.java                          # Sample data on startup
│   ├── config/
│   │   └── SecurityConfig.java                 # Security setup
│   ├── controller/
│   │   ├── LeaveController.java
│   │   └── ApprovalController.java
│   ├── service/
│   │   ├── LeaveApplicationService.java
│   │   ├── ApprovalService.java
│   │   └── NotificationService.java
│   ├── model/
│   │   ├── Employee.java
│   │   ├── LeaveApplication.java
│   │   └── NotificationLog.java
│   └── repository/
│       ├── EmployeeRepository.java
│       ├── LeaveApplicationRepository.java
│       └── NotificationLogRepository.java
└── test/java/com/lms/leave_management_system/
    ├── LeaveApplicationServiceTest.java
    ├── ApprovalServiceTest.java
    ├── LeaveIntegrationTest.java
    └── LeaveManagementSystemApplicationTests.java
```

---

## What's Next

A few things that would make sense to add down the line:

- Swap H2 for PostgreSQL for a production-ready setup
- Wire in actual email delivery via JavaMailSender
- Add JWT-based authentication and enforce role-based access
- Build a leave balance tracker (remaining days per employee per leave type)
- Frontend UI — the API is clean enough that dropping React or Angular on top would be straightforward

---

## Author

Built by **Nqobile Sibiya**
