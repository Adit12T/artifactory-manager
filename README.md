# Artifactory Manager

[![CI](https://github.com/Adit12T/artifactory-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/Adit12T/artifactory-manager/actions/workflows/ci.yml)
[![CodeQL](https://github.com/Adit12T/artifactory-manager/actions/workflows/codeql.yml/badge.svg)](https://github.com/Adit12T/artifactory-manager/actions/workflows/codeql.yml)

A secure REST gateway in front of **JFrog Artifactory**. It lets teams list repositories, inspect builds,
and create or delete local repositories through one small, audited API, without handing out
Artifactory admin credentials. Built with **Java 21, Spring Boot 3.5, Spring Security and Caffeine**.

I started this in 2018 as a learning project calling Artifactory with `RestTemplate`. Version 2 is a
rewrite with typed responses, proper authentication and tests.

## Features

- **Repositories:** list (filter by local, remote, virtual or federated), create a local repository
  for Maven, Gradle, npm, PyPI, Docker, Helm, NuGet, Go or generic packages, and delete one.
- **Builds:** list builds newest first, list the runs of a build, and read full build info: modules,
  artifacts with SHA-256, dependencies and the VCS revision.
- **Two API keys:** a read key for listing, and an admin key that can also create and delete.
  Keys are compared in constant time.
- **Credentials stay server-side:** the Artifactory access token is read from the environment, sent
  only as a Bearer header, and masked if the configuration is ever printed.
- **Fast and gentle on Artifactory:** repository and build lists are cached for 60 seconds, and the
  cache is cleared on every create or delete.
- **Clear failures:** timeouts (3 s connect, 10 s read) and Artifactory errors map to `404`, `502` or
  `504` problem details. Artifactory's raw error text is never passed on.
- **Input validation:** repository keys and build names are checked against strict patterns before
  they reach an Artifactory URL.

## API

| Method | Path | Key | Purpose |
|---|---|---|---|
| `GET` | `/api/v1/repositories?type=local` | read | List repositories |
| `POST` | `/api/v1/repositories` | admin | Create `{ "key": "team-npm", "packageType": "NPM" }` |
| `DELETE` | `/api/v1/repositories/{key}` | admin | Delete a repository |
| `GET` | `/api/v1/builds` | read | List builds |
| `GET` | `/api/v1/builds/{name}` | read | Run numbers of a build |
| `GET` | `/api/v1/builds/{name}/{number}` | read | Build info |

Send the key in the `X-API-Key` header. Interactive docs: `http://localhost:8080/swagger-ui.html`.

## Run it

```bash
cp .env.example .env      # then fill in the four values
docker compose up --build
curl -H "X-API-Key: $APP_READ_API_KEY" localhost:8080/api/v1/repositories
```

## Configuration

| Variable | Required | Meaning |
|---|---|---|
| `ARTIFACTORY_URL` | yes | Base URL, e.g. `https://acme.jfrog.io/artifactory` |
| `ARTIFACTORY_ACCESS_TOKEN` | yes | Artifactory access token used by this service |
| `APP_READ_API_KEY` | yes, 32+ characters | Key for read-only calls |
| `APP_ADMIN_API_KEY` | yes, 32+ characters | Key that can also create and delete |

## Tests

```bash
mvn verify
```

No Artifactory needed. The client is tested against a scripted Artifactory (request URLs, the auth
header, JSON parsing and error mapping), and the API is tested for key handling, roles and input
validation.

## License

Copyright © 2026 Aditya Tripathi. All rights reserved. The code is published for viewing only; see
[LICENSE](LICENSE). Security reports: see [SECURITY.md](SECURITY.md).
