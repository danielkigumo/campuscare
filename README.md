# Campus Care

Student Service Request & Complaint Management System, Group 7.
Stack: Java + Spring Boot, PostgreSQL (pgAdmin), HTML/CSS/JavaScript + Bootstrap.

## 1. First-time setup (everyone)

1. Install Git, JDK 17+, VS Code or IntelliJ IDEA, and PostgreSQL (includes pgAdmin).
2. Clone the repo:
   ```
   git clone https://github.com/danielkigumo/campuscare.git
   cd campuscare
   ```
3. In pgAdmin, create an empty database named `campuscare`.
4. Create `src/main/resources/application-local.properties` (this file is git-ignored):
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/campuscare
   spring.datasource.username=postgres
   spring.datasource.password=YOUR_LOCAL_PASSWORD
   ```
   Never commit your password.
5. Run the app with the `local` profile. Flyway builds all tables automatically
   from `src/main/resources/db/migration`.
6. Check in pgAdmin that `campuscare` now has 8 tables.

## 2. Branches

- `main` is always working. Nobody commits to it directly.
- One branch per piece of work, named `module-feature`:
  `auth-login`, `requests-submit`, `tracking-status`, `admin-dashboard`, `db-schema`.

## 3. Daily workflow

```
git checkout main
git pull                          # always start the day with this
git checkout -b requests-submit   # new branch for your task
# ... write code ...
git add .
git commit -m "Add request submission form and endpoint"
git push -u origin requests-submit
```

Then on GitHub: open a Pull Request into `main`.
- The other person in your module pair reviews and merges it.
- Any PR that touches `db/migration`, entities or repositories also needs a review
  from Adrian or Samantha (Module 5).

After a PR is merged, everyone runs `git checkout main && git pull`.

## 4. Database changes

- Never edit an existing migration (for example `V1__schema.sql`).
- To change the database, add a new numbered script:
  `V2__add_phone_to_requests.sql`, `V3__...`
- Ask Module 5 (Adrian, Samantha) through the task board. They write the script.
- Post in the WhatsApp group when a database change is merged, so everyone pulls.

## 5. Who owns what

| Module | Pair | Main area |
| --- | --- | --- |
| 1. Authentication & User Management | Arisa, Bett | auth and user code |
| 2. Request / Complaint Management | Richard, Brown | request code |
| 3. Tracking & Responses | Daniel, Nyukuri | tracking and response code |
| 4. Admin Dashboard & Reports | Chiaji, Arisa | dashboard and reports |
| 5. Database & Integration | Bett, Samantha | entities, repositories, migrations |

## 6. Rules

- No passwords, tokens or `.env` files in Git.
- Small commits with clear messages.
- If `git pull` shows a conflict, ask your module partner before resolving it.