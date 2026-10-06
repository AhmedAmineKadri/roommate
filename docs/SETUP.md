# Local setup

## Requirements

- JDK 21 available to Gradle (`java -version` should show 21).
- Docker and Docker Compose for the PostgreSQL service.
- A GitHub account and a GitHub OAuth application for browser sign-in.
- An internet connection for the first Gradle dependency download.

The Gradle wrapper is included; a separate Gradle installation is unnecessary.

## GitHub OAuth

Follow [GitHub's OAuth application instructions](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app).
Use these local values:

| Setting | Value |
| --- | --- |
| Homepage URL | `http://localhost:8080` |
| Authorization callback URL | `http://localhost:8080/login/oauth2/code/github` |

Copy the client ID and client secret into your shell environment or IDE run
configuration. Never commit the real client secret. The configuration currently
requests `read:public_repo`; reviewing and reducing this scope is a roadmap item.

## Environment variables

| Variable | Purpose |
| --- | --- |
| `CLIENT_ID` | GitHub OAuth client ID; required for application sign-in |
| `CLIENT_SECRET` | GitHub OAuth client secret |
| `ROOMMATE_ADMINS` | Comma-separated GitHub logins granted `ROLE_ADMIN` |
| `DB_URL` | JDBC URL; defaults to `jdbc:postgresql://localhost:5432/RoomMate` |
| `DB_USERNAME` | Database username; defaults to `user` |
| `DB_PASSWORD` | Database password; defaults to the local example `iamuser` |
| `KEYMASTER_ENABLED` | Set to `false` for standalone use; `true` enables polling |

`.env.example` lists example values. Docker Compose can read a `.env` file,
but Spring does not automatically import it. Export variables in the terminal
used to launch Gradle, or enter them in your IDE's environment configuration.

For the original private checkout, existing admin defaults and enabled
KeyMaster polling are preserved unless overridden. The prepared public copy
has no default admin accounts and disables polling by default. Setting the
variables explicitly, as in the README, works with either checkout.

## Database and application

```bash
docker compose up -d
docker compose ps
bash gradlew bootRun
```

Use `.\gradlew.bat bootRun` in PowerShell. Flyway creates the tables on application
startup. Open `http://localhost:8080`, sign in, then add a room and workspace
through administration. A fresh database has no seeded rooms or bookings.

Compose exposes computer port 5432 to container port 5432. Database files are
stored in the local `data/` bind mount. Stopping the container preserves data;
deleting that folder destroys the database files. The folder is excluded from
the public publication copy.

The Java application currently runs on the host, not in Docker. If moved into
the same Compose network, its database hostname would be `database`, not
`localhost`. A Java Dockerfile is not included in this version.

`DB_USERNAME` and `DB_PASSWORD` configure Spring. The supplied Compose file
defines its own example PostgreSQL credentials; change both sides together
when using different credentials. Changing container environment variables
does not reset credentials in an already initialized PostgreSQL data directory.

## Optional KeyMaster integration

KeyMaster is a separate service expected at `http://localhost:3000` with
`GET /room` and `GET /key`. It is not distributed in the public project.
To use it, start your own compatible service and set `KEYMASTER_ENABLED=true`.
The two synchronizers poll every 10 seconds and use UUIDs to identify records.
With synchronization disabled, manually added rooms and desks still work.

## Troubleshooting

| Symptom | Check |
| --- | --- |
| Wrong Java version | IDE Gradle JVM, `JAVA_HOME`, and `java -version` |
| PostgreSQL connection refused | Start Docker and inspect `docker compose ps`; check port 5432 |
| Port 5432 already in use | Adjust the host mapping and matching `DB_URL` |
| GitHub callback mismatch | Match hostname, port and callback path in the OAuth registration |
| No administration menu | Set your exact GitHub login in `ROOMMATE_ADMINS`, then sign out/in |
| Repeated connection errors to port 3000 | Disable KeyMaster polling or start the external service |
| Missing client registration values | Export `CLIENT_ID` and `CLIENT_SECRET` in the process that starts Java |
| No rooms or search results | Add a room and desk; use a valid date/time and appropriate equipment |

Database integration tests use H2. Passing them does not replace a manual check
against PostgreSQL and the real OAuth application.
