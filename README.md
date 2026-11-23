# sms-backend

Base URL: `http://localhost:8080`

## Students

| Method | URL | What it does |
| --- | --- | --- |
| `POST` | `/api/students` | Make a new student. Send JSON in the body. |
| `GET` | `/api/students` | Get all students. |
| `GET` | `/api/students/{id}` | Get one student by id. |
| `GET` | `/api/students/search` | Get students page by page. Can filter and sort. |
| `PUT` | `/api/students/{id}` | Change one student. Send JSON in the body. |
| `DELETE` | `/api/students/{id}` | Remove one student. |
| `POST` | `/api/students/{id}/credits` | Add or remove credits. |
| `POST` | `/api/students/{id}/rollback-demo` | Always fails on purpose. Shows how a rollback works. |

Body for `POST` and `PUT`:

```json
{ "firstName": "Momo", "lastName": "Cat", "email": "momo.cat@aku.com" }
```

## Search options

`GET /api/students/search?q=cat&size=2&sortBy=lastName`

| Name | Default | What it does |
| --- | --- | --- |
| `q` | empty | Keep only students whose name or email has this text. |
| `minCredits` | `0` | Keep only students with at least this many credits. |
| `page` | `0` | Which page to show. Starts at 0. |
| `size` | `20` | How many students per page. Max 100. |
| `sortBy` | `id` | Sort by `id`, `firstName`, `lastName`, `email`, or `credits`. |
| `direction` | `asc` | `asc` for small to big, `desc` for big to small. |

## Credits options

`POST /api/students/1/credits?delta=1&mode=read-committed&thinkTimeMillis=1500`

| Name | Default | What it does |
| --- | --- | --- |
| `delta` | needed | How much to add. Use a minus sign to remove. |
| `mode` | `read-committed` | `read-committed`, `repeatable-read`, or `pessimistic-lock`. |
| `thinkTimeMillis` | `0` | Wait this long before saving. Use it to test two requests at once. |

What each mode does when two requests run at the same time:

| Mode | Result |
| --- | --- |
| `read-committed` | Both work, but one change is lost. |
| `repeatable-read` | One works, the other gets an error. Nothing is lost. |
| `pessimistic-lock` | Both work, one after the other. Nothing is lost. |

## Actuator

| Method | URL | What it does |
| --- | --- | --- |
| `GET` | `/actuator/health` | Say if the app and the database are OK. |
| `GET` | `/actuator/info` | Show the app name and Java version. |
| `GET` | `/actuator/metrics` | List all the numbers the app keeps. |
| `GET` | `/actuator/metrics/{name}` | Show one number, like `sms.students.total`. |
| `GET` | `/actuator/loggers` | Show the log level of every logger. |
| `POST` | `/actuator/loggers/{name}` | Change a log level while the app runs. |
| `GET` | `/actuator/env` | Show the app settings. |
| `GET` | `/actuator/beans` | List all Spring beans. |
| `GET` | `/actuator/mappings` | List all URLs the app answers. |

## Status codes

| Code | When |
| --- | --- |
| `200` | It worked. |
| `201` | A new student was made. |
| `204` | It worked. No body is sent back. |
| `400` | Something you sent is wrong. |
| `404` | That student id does not exist. |
| `409` | The email is already used, or two requests clashed. |
| `500` | The app broke. |
