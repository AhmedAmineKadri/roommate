# Database and reservation rules

PostgreSQL runs in the `database` Compose service; Java connects over JDBC.
Flyway applies files in `src/main/resources/db/migration` in version order.

## Tables

| Migration | Table | Relevant columns |
| --- | --- | --- |
| V1 | `room_dto` | `room_id` primary key, name, unique external UUID `id` |
| V1 | `key` | `key_id` primary key, owner, unique external UUID `id` |
| V2 | `arbeitsplatz` | `arbeitsplatz_id` primary key, name, `room_id` |
| V3 | `ausstatung` | name, `arbeitsplatz` foreign key to workspace |
| V4 | `person` | `person_id` primary key, `github_id`, username |
| V5 | `reservation` | `reservation_id` primary key, `tag`, times, `arbeitsplatz_id` |

The workspace-to-room and reservation-to-workspace foreign-key declarations
are commented out in the current migrations. The model expresses those logical
relationships, but the database does not enforce all of them.

`ReservationFormular` explicitly maps to `reservation` using `@Table`. Without
that annotation Spring would derive a different table name from the class name.
`RoomDto` is the persistence representation of a room.

## Time ranges

Required booking fields are date, start time, end time and workspace ID.
The end must be strictly after the start. Same-day starts in the past are
rejected in form validation; `@FutureOrPresent` rejects past dates.

Two valid ranges overlap when:

```text
existing.start < requested.end
AND existing.end > requested.start
```

An existing 10:00–11:00 reservation conflicts with 10:30–11:30, but not with
11:00–12:00. The current queries express equivalent interval cases through
multiple OR conditions; simplifying them is a future refactoring.

The workspace and day must also match for a booking conflict. Search includes
desks without equipment when no equipment filter is requested.

## Current limits

The controller checks overlap and later calls save. Concurrent requests can
both pass that check before either inserts. There is no PostgreSQL exclusion
constraint or other atomic conflict prevention yet.

Reservations are not linked to a `Person` in the schema. Ownership-aware
booking lists and cancellation are therefore not implemented.

ID-based equality on new objects and mutable hash codes need further review.
Two unsaved reservation objects currently compare equal when both IDs are null.

## Migration practice

For a database already using these migrations, add a new numbered migration
rather than rewriting an applied file. Schema constraint changes need a plan
for existing data. Database files and dumps must not be committed.

H2 integration tests use case-insensitive identifiers to accommodate the current
mapping and migrations. That setting is test-specific; PostgreSQL verification
and stricter schema checks are still necessary.
