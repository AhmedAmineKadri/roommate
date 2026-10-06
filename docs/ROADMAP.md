# Status and roadmap

RoomMate is unfinished educational software. The current version supports the
main browser booking/search flow and administration, with a refreshed frontend.

## Next priorities

1. Verify persistence against PostgreSQL, ideally through Testcontainers.
2. Make overlap checking and saving atomic; consider a database exclusion constraint.
3. Consolidate reservation validation so callers cannot bypass date/availability rules.
4. Strengthen foreign keys, required columns and equipment uniqueness through new migrations.
5. Link reservations to users and add a personal booking list with ownership checks.
6. Fix entity equality/hash-code behavior for unsaved and subsequently persisted objects.
7. Audit the public `/api/access` response and its authorization and joins.
8. Review KeyMaster UUID lookups, retries, error handling and configurable endpoint URLs.
9. Update framework/dependencies after compatibility testing and reduce OAuth scope.
10. Normalize package naming, German/English identifiers and redundant code.

## UI improvements worth exploring

- Confirmation details tied to the actual saved reservation.
- Room/desk information richer than the generic illustrations.
- Pagination and sorting for larger administration lists.
- More accessible search and error announcements and a full accessibility audit.
- Localization, if the interface needs both German and English.

## Deployment work

No hosted deployment or Java Dockerfile is supplied. Production configuration,
HTTPS, secrets handling, database backups and runtime monitoring need separate
work. The Compose database credentials and local bind mount are development
configuration, not production defaults.
