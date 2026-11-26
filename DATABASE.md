# CampusCare Database Design (Module 5)

## Overview
The database schema is defined in `src/main/resources/db/migration/V1__schema.sql` (PostgreSQL, managed by Flyway). The Java entities in `com.campuscare.campuscare.model` map to these tables.

## Tables and entities
| Table | Entity | Purpose |
|---|---|---|
| `roles` | `Role` | Access levels: STUDENT, STAFF, ADMIN |
| `departments` | `Department` | University service units (e.g. ICT Services, Finance) |
| `users` | `User` | Student, staff and admin accounts |
| `categories` | `Category` | Request types, each linked to a department |
| `requests` | `Request` | Service requests and complaints |
| `request_status_history` | `RequestStatusHistory` | Audit trail of status changes |
| `responses` | `Response` | Replies and internal staff notes |
| `notifications` | `Notification` | Alerts sent to users |

`RequestStatus` is an enum holding the request workflow statuses.

## Key relationships
- A user belongs to one role and optionally one department.
- A request belongs to a student (user) and a category, and may be assigned to a staff user.
- Status history, responses and notifications each link back to a request.

## Data integrity
- Email is unique ignoring upper/lower case (`ux_users_email_lower`).
- `CHECK` constraints limit role names, request types, priorities and statuses to valid values.
- Indexes exist on request student, assignee, category, status and creation time.

## Running with different databases
- Default (dev profile): in-memory H2 database, Flyway disabled, for quick local runs.
- PostgreSQL: use a local profile that points to a PostgreSQL database and enables Flyway so `V1__schema.sql` is applied.

## Rules for changing the schema
Never edit `V1__schema.sql` once it is shared. Add a new numbered migration (e.g. `V2__description.sql`) for any change.
