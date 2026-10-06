# Routes and forms

These routes implement the current browser application. The POST routes return
views or redirects, not a JSON reservation API. POST forms require CSRF tokens.

## User routes

| Method | Path | Purpose / parameters |
| --- | --- | --- |
| GET | `/` | Public welcome page; records signed-in identity when present |
| GET | `/login` | Public custom GitHub sign-in page |
| GET | `/oauth2/authorization/github` | Spring Security OAuth authorization entry |
| GET | `/login/oauth2/code/github` | Spring Security OAuth callback |
| POST | `/logout` | Spring Security session logout |
| GET | `/chooseroom` | Browse rooms |
| POST | `/chooseroom` | Select `selectedRoom`; redirect to that room |
| GET | `/chooseplatz/{roomId}` | Booking form; optional `arbeitsplatzId`, `tag`, `startTime`, `endTime` |
| POST | `/chooseplatz` | Book `arbeitsplatzId`, `tag`, `startTime`, `endTime`, and `roomId` |
| GET | `/confirm` | Generic confirmation view; refresh does not resubmit the booking |
| GET | `/search` | Search form with suggested date and time |
| POST | `/search` | Search using `date`, `timeFrom`, `timeTo`, optional repeated `equipment` |

Application pages other than the public routes require authentication. Form date
values use `yyyy-MM-dd`; times use an ISO-compatible value such as `10:30`.
The confirmation route is a generic message, not a lookup of a specific booking.

## Administration (`ROLE_ADMIN`)

| Method | Path | Purpose / parameters |
| --- | --- | --- |
| GET/POST | `/addroom` | Add a room using `roomName` |
| GET/POST | `/addarbeitsplatz/{roomId}` | Add a desk using `arbeitsplatzName` |
| GET/POST | `/addaustattung/{arbeitsplatzId}` | Add equipment using `austattungName` |
| GET | `/roomoverview` | Room administration |
| GET | `/modifyroom/{roomId}` | Desks in a room |
| POST | `/room/{roomId}/platz/{arbeitsplatzId}` | Delete a workspace |
| GET | `/modifyarbeitsplatz/{arbeitsplatzId}` | Workspace equipment administration |
| POST | `/platz/{arbeitsplatzId}/ausstatung/{ausstatungName}` | Remove equipment |
| GET | `/allreservation` | All reservations |
| POST | `/deleteReservation/{reservationId}` | Delete a reservation |

Some route names preserve the original German spelling, including
`addaustattung`. There is no active room-deletion route in this version.

## Integration endpoint

`GET /api/access` returns JSON from `RoomService.getAllRoomsWithKeys()`:

```json
[
  {
    "key": "00000000-0000-0000-0000-000000000001",
    "owner": "example-owner",
    "raum": "Example room",
    "room": "00000000-0000-0000-0000-000000000002"
  }
]
```

These are illustrative UUIDs, not live records. The endpoint currently permits
anonymous access. Review access control and the actual join semantics before
using it beyond local development.
