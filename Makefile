.PHONY: up down build rebuild logs logs-backend logs-frontend test-backend test-frontend test clean reset psql shell-backend

up:
	docker compose up -d

down:
	docker compose down

build:
	docker compose build

rebuild:
	docker compose build --no-cache

logs:
	docker compose logs -f

logs-backend:
	docker compose logs -f backend

logs-frontend:
	docker compose logs -f frontend

test-backend:
	cd backend && ./mvnw test

test-frontend:
	cd frontend && npm test

test: test-backend test-frontend

clean:
	docker compose down -v

reset: clean up

psql:
	docker compose exec postgres psql -U itemsearch -d itemsearch

shell-backend:
	docker compose exec backend sh
