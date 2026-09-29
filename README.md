# Item Notes Search

A full-text search app built on PostgreSQL. Search a product catalog by name and notes,
look up purchases by the item bought, and browse Pokémon and Digimon datasets. Everything is served by a
Spring Boot REST API and a React single-page app.

## Tech stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2 (Web, Data JPA, Validation, Actuator), virtual threads enabled |
| **Database** | PostgreSQL 15 with `tsvector` full-text search, GIN indexes, `pg_trgm` |
| **Migrations** | Flyway (schema + seed data run automatically on startup) |
| **API docs** | springdoc-openapi (Swagger UI) |
| **Frontend** | React 19, TypeScript, Vite, React Router, TanStack Query, Axios, Tailwind CSS 4 |
| **Frontend extras** | jsPDF + jspdf-autotable (PDF export of purchases), DOMPurify |
| **Testing** | JUnit 5, Spring Boot Test, Testcontainers (Postgres) · Vitest, Testing Library, jsdom |
| **Runtime** | Docker Compose: `postgres`, `backend` (JRE 21 Alpine), `frontend` (nginx) |

## Architecture

```
Browser ──► frontend (nginx :3000)
              ├─ /            → React SPA (static build)
              ├─ /api/*       → proxied to backend:8080
              └─ /actuator/*  → proxied to backend:8080

backend (Spring Boot :8080)
  controller → service → repository (JPA + native full-text SQL)
              │
              ▼
postgres (:5433 on host, :5432 in the compose network)
```

The backend follows a layered layout under `backend/src/main/java/com/noteslookup/`:

- `controller/api` – REST controllers; `controller/request` – request bodies
- `dto` – response DTOs (entities are never returned directly)
- `service` – business logic (interface + `*Impl`)
- `repository` – Spring Data JPA repositories with native full-text queries
- `model` – JPA entities
- `config` – exceptions and the global exception handler

### How search works

Each searchable table has a generated `tsvector` column with a GIN index
(see `backend/src/main/resources/db/migration/`). Queries use
`plainto_tsquery('english', …)` and rank with `ts_rank`, so matching is stemmed and word-based:
"cables" also matches "cable", but partial words like "cab" do not.

| Dataset | Searched fields |
|---|---|
| Items | `item_name` (weight A) + `notes` (weight B) |
| Purchases | the purchased item's name and notes |
| Pokémon | `name` (A) + `type1`/`type2` (B) |
| Digimon | separate `name` and `description` vectors, plus `ILIKE` substring fallback, combined with `mode=and\|or` |

## Quick start (Docker)

Prerequisites: Docker with Docker Compose.

```bash
cp .env.example .env        # optional – defaults already match
make up                     # or: docker compose up -d
```

| URL | What |
|---|---|
| http://localhost:3000 | Web app |
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/v3/api-docs | OpenAPI JSON |
| http://localhost:8080/actuator/health | Health check |

On first start, Flyway creates the tables and loads the sample items, purchases, Pokémon and Digimon.

### Make targets

| Command | Action |
|---|---|
| `make up` / `make down` | Start / stop all containers |
| `make build` / `make rebuild` | Build images (`rebuild` = `--no-cache`) |
| `make logs`, `make logs-backend`, `make logs-frontend` | Tail logs |
| `make psql` | Open `psql` in the database container |
| `make shell-backend` | Shell into the backend container |
| `make test` | Run backend and frontend tests |
| `make clean` | Stop and **delete the database volume** |
| `make reset` | `clean` + `up` (fresh database) |

## Local development (without Docker for the apps)

Prerequisites: Java 21, Node 20+, Docker (for Postgres and for backend tests).

**1. Database**

```bash
docker compose up -d postgres
```

Postgres is published on host port **5433**. For an external Postgres 15 instead, create the database and
run `docker/postgres/init.sql` (enables `pg_trgm` and `uuid-ossp`) before starting the backend.

**2. Backend** (port 8080)

`application.yml` defaults to `localhost:5432`, so point it at 5433:

```bash
cd backend
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/itemsearch ./mvnw spring-boot:run
```

**3. Frontend** (Vite dev server, http://localhost:5173)

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` to `http://localhost:8080`. It does not proxy `/actuator`, so the
nav-bar health badge may not show the backend's status in dev mode. Add an `/actuator` entry to
`server.proxy` in `vite.config.ts` if you need it (the backend has no CORS config, so calling
`localhost:8080` directly from the dev server's origin won't work).

## Testing

```bash
make test-backend     # cd backend && ./mvnw test
make test-frontend    # cd frontend && npm test
```

- Backend integration tests (`ItemCrudIntegrationTest`, `ItemRepositoryTest`,
  `ItemNotesSearchApplicationTest`) start a `postgres:15-alpine` container through Testcontainers,
  so **Docker must be running**. Controller and service tests are plain unit/slice tests.
- Frontend tests use Vitest + Testing Library in jsdom (`frontend/src/test/`).
- Other frontend scripts: `npm run lint`, `npm run build`, `npm run test:watch`.

## Using the app

| Page | Path | What you can do |
|---|---|---|
| Items | `/` | Search items by name/notes; open an item to view and edit its notes |
| Item detail | `/items/:id` | View, update, or delete an item |
| Purchases | `/purchases` | Search purchases by item; export results to PDF |
| Pokémon | `/pokemon` | Search by name or type; open a stats modal |
| Digimon | `/digimon` | Search by name and/or description (AND / OR); images load via the backend image proxy |

## REST API

All list and search endpoints are paginated with `page` (default `0`) and `size` (default `20`).
Full schemas are in Swagger UI.

**Items** – `/api/items`

| Method | Path | Notes |
|---|---|---|
| `GET` | `/api/items` | List |
| `GET` | `/api/items/search?q=` | Full-text search |
| `GET` | `/api/items/{id}` | Get by UUID |
| `POST` | `/api/items` | Create |
| `PATCH` | `/api/items/{id}` | Partial update |
| `DELETE` | `/api/items/{id}` | Delete |

**Purchases** – `/api/purchases`

| Method | Path | Notes |
|---|---|---|
| `GET` | `/api/purchases?buyer=&itemId=` | List, optional filters |
| `GET` | `/api/purchases/search?q=` | Search by purchased item |
| `GET` | `/api/purchases/{id}` | Get by UUID |
| `POST` | `/api/purchases` | Create |
| `DELETE` | `/api/purchases/{id}` | Delete |

**Pokémon** – `/api/pokemon`: `GET /`, `GET /search?q=`, `GET /{id}`

**Digimon** – `/api/digimon`: `GET /`, `GET /search?name=&description=&mode=and|or`, `GET /{id}`

**Other**

- `GET /api/image-proxy?file=` – resolves and streams a Digimon image from wikimon.net
- `GET /actuator/health`, `/actuator/info`, `/actuator/metrics`

Example:

```bash
curl "http://localhost:8080/api/items/search?q=wireless&size=5"
```

## Configuration

| Variable | Default | Used by |
|---|---|---|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | `itemsearch` / `itemsearch` / `localdev` | Docker Compose (`.env`) |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/itemsearch` | Backend |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `itemsearch` / `localdev` | Backend |
| `SPRING_PROFILES_ACTIVE` | – | `docker` (set by Compose) or `prod` (Hikari pool size 20); both require the datasource env vars |
| `VITE_API_BASE_URL` | `""` (same origin) | Frontend build-time API base URL |

The default credentials are for local development only. Don't reuse them in any shared or deployed
environment.

## Project layout

```
.
├── backend/              Spring Boot API (Maven wrapper, Dockerfile)
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/ Flyway V1–V8 (schema + seed data)
├── frontend/             React + Vite SPA (nginx Dockerfile, nginx.conf)
│   └── src/{api,hooks,components,pages,test}
├── docker/postgres/      init.sql (Postgres extensions)
├── docker-compose.yml
├── Makefile
└── .env.example
```
