# Contributing

Start with [setup](docs/SETUP.md) and [architecture](docs/ARCHITECTURE.md).
The project is being maintained as a learning exercise; small, explainable
changes with clear behavior are especially useful.

## Development workflow

1. Create a focused branch and describe the problem you are solving.
2. Keep controller, service and database responsibilities clear.
3. Run relevant tests, then the full suite before proposing a change.
4. Update route/configuration documentation when behavior changes.
5. Explain what changed, why, and how it was verified in the pull request.

Preserve existing contribution attribution. Do not commit credentials, private
course records, database files or another service's binary distribution.

## Frontend changes

Reuse `fragments/layout.html` and the shared stylesheet. Keep POST forms using
Thymeleaf actions so Spring Security supplies CSRF tokens. Use semantic labels,
keyboard-visible focus, useful empty states and narrow-screen layouts.

The model key used in a template must match the controller's model attribute
and binding-result name. Test error states, not just successful rendering.

## Database changes

Add a new Flyway migration rather than editing already-applied migrations.
Verify constraints against PostgreSQL as well as H2 where relevant.

## License

The application currently has no selected open-source license. Do not assume
permission to redistribute or relicense all original coursework contributions.
Resolve contributor permissions before adding a license.
