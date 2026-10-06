# Testing and verification

## Commands

```powershell
.\gradlew.bat test
.\gradlew.bat test --tests example.roommate.ServicesTests.ReservationServiceTest
.\gradlew.bat test --tests example.roommate.web.FrontendFlowTest
```

Use `bash gradlew` instead of `.\gradlew.bat` on macOS/Linux. Gradle downloads
dependencies on the first run. Add `--offline` only after the required versions
have been cached.

## Test scopes

| Suite | Scope |
| --- | --- |
| `ReservationServiceTest` | Plain Java unit tests with a mock repository |
| `ServicesTests` | Spring Data JDBC integration tests using real H2 persistence |
| `WebControllerTest` | MVC controller slice with mocked service dependencies |
| `AdminControllerTest` | Admin routing, CSRF and role authorization |
| `FrontendFlowTest` | Real Thymeleaf rendering and browser-form request behavior |
| `RoomMateApplicationTests` | Spring startup against H2, with external synchronizers mocked |

The latest local suite passed **61 tests**: 5 reservation unit tests, 25 database
integration tests, 3 existing web tests, 12 admin tests, 15 frontend-flow tests
and 1 application-startup test.

## Covered behaviors

- Valid time ranges save; equal, reversed or missing times throw before saving.
- Adjacent reservations are allowed; partially overlapping reservations conflict.
- Search includes available desks without equipment and excludes booked intervals.
- Required/malformed booking fields return feedback without a null-pointer crash.
- Search times bind correctly and are preserved in the booking link/form.
- Successful booking redirects to confirmation; overlap errors do not save.
- A desk from another room is rejected by the booking form flow.
- Administration is denied to normal users and hidden from their navigation.
- Public sign-in and static frontend resources are reachable.
- Admin forms reject blank room/desk names, and cancellation uses the correct route.
- User/admin templates and empty states render with the shared layout.

## Visual previews

`FrontendFlowTest` writes HTML pages with sample data and their local assets to
`build/ui-preview`. These are static test artifacts. Forms and navigation there
are not connected to a live application.

The frontend was also reviewed in the browser at desktop and narrow mobile
widths. All generated page examples were checked for page-level horizontal
overflow. Admin tables scroll horizontally on narrow screens. The browser's
time-range constraint was checked with invalid and valid values.

## Limits

The flow tests mock services; the JDBC tests use H2, not PostgreSQL. A passing
suite does not verify live GitHub sign-in, external KeyMaster behavior,
PostgreSQL-specific SQL, concurrent requests or production deployment.
The configured GitHub workflow has not run until the public repository is created.

After changes, run the appropriate tests and then the full suite. Treat service
tests and database tests as complementary: mocks cannot detect incorrect SQL.
