# CampusCare authentication and login update

## Changes
- Enforced `@strathmore.edu` server-side for login and student registration.
- Normalized emails to lowercase before lookup/storage.
- Login continues to authenticate against the existing users table, check BCrypt passwords,
  respect account activation, and issue the existing JWT access/refresh tokens.
- The role returned by authentication remains the role assigned in the database.
- Public registration continues to create STUDENT accounts only; it cannot assign STAFF/ADMIN.
- Updated the login page to a responsive dark-navy design while preserving the existing API
  endpoint and token/localStorage contract.
- Updated demo account addresses in the development seed and visible dashboard placeholders.

## Important limitation
The current schema has a `users` account table, but no separate authoritative Strathmore
student/staff directory and no email-verification integration. Therefore, login verifies that
an account already exists and is active in `users`; the domain check alone does not prove
institutional affiliation. Public student registration remains self-service, as it was before.
For production, connect registration to an official roster/admission-number verification
service or add university email verification, and replace the development seed credentials.

## Development demo accounts
- `student@strathmore.edu`
- `staff@strathmore.edu`
- `admin@strathmore.edu`

The existing seeded development password is `password123`. Do not deploy these credentials
or the H2 development configuration to production.
