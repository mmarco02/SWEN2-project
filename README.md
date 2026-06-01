## GitHub Repository: 

- https://github.com/mmarco02/SWEN2-project

## Protocol:

- [Protocol Markdown File (Protocol.md)](Protocol.md)

## Running the Project

### For Development

Start the database:
```
docker-compose up db -d
```
Database is automatically initialized with test data from /resources/data.sql.

Start the backend:
```
Swen2TourPlannerApplication
```

Start the frontend:
```
cd /frontend
npm ci
npm run dev
```

### Production (Docker Compose)

```
docker-compose up
```
Runs database, backend and frontend together (but no hot-reload).
