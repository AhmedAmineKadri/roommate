# Security and development boundaries

This is a local educational application, not a reviewed production service.

## Configuration and data

- Keep OAuth secrets and real credentials in environment variables or ignored
  local configuration. `.env.example` contains placeholders only.
- The Compose username/password are development examples.
- Never publish the `data/` directory, database dumps, team records or personal
  feedback files. Ignoring a file does not remove it from existing Git history.
- The public export uses a new snapshot without the private repository history.

## Application behavior

Administration uses method-level `ROLE_ADMIN` checks; navigation visibility is
not the authorization mechanism. POST forms retain CSRF protection.

`/api/access` currently permits anonymous access to room/key ownership data.
Review and restrict that integration endpoint before exposing a live instance.
The code also lacks atomic conflict prevention and reservation ownership, as
documented in the roadmap.

## Reporting an issue

For ordinary bugs, open an issue with steps to reproduce and synthetic data.
Do not post secrets, real database contents or exploitable details publicly.
For sensitive findings, use GitHub's private vulnerability reporting if the
repository owner has enabled it. Otherwise contact the owner privately before
sharing those details.

## Supported versions

There is no stable release or formal support policy yet. Dependency maintenance
and production hardening are future work.
