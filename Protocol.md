# SWEN2 Tour Planner

Tour Planner is a full-stack app for creating, managing and searching tours.
Users can create, edit and delete tours with start/end locations, upload images, see tour images in the list view, get automatic distance and time estimates (via the OpenRouteService API), and create, edit and delete tour logs with ratings, difficulty, distance, time, etc.

**Git Repository:** https://github.com/mmarco02/SWEN2-project

## Documents

- [Wireframe Mockup (PDF)](SWEN2-Project%20Mockup.pdf)
- [Project Specification](semester-project-specification.pdf)

### Mockup

![Wireframe Mockup](SWEN2-Project%20Mockup.png)

## Tech Stack

| Layer      | Technology                                                     |
|------------|----------------------------------------------------------------|
| Backend    | Java 21, Spring Boot 4.0.6, Spring Data JPA, Hibernate Search 8 |
| Frontend   | Vue 3 (Composition API), Pinia, Vue Router, Vite               |
| Database   | PostgreSQL (Docker)                                            |
| Maps       | Leaflet (map rendering), OpenRouteService API (routing/geocoding) |
| Build      | Maven (backend), npm/Vite (frontend), Docker Compose           |
| Logging    | Log4j2 with SLF4J                                              |

## Application Architecture

## Backend Structure

The backend uses Spring Boot and is seperated into the following packages:

### Controllers
We have a separate Controller for each Entity, each Controller handles the HTTP requests for its entity and then is called in the corresponding Service.

TourController
Handles all tour related endpoints: CRUD operations, image upload and download, fetching tours by user or by from/to location.

TourLogController
Handles tour log CRUD, with the endpoint `/tours/{tourId}/logs` so logs are always in the context of a specific tour.

UserController
Handles user registration, login and username to ID lookup.

SearchController
Has a single endpoint for full-text search that searches across tours and tour logs.

### Services
All business logic is in the Service classes. Controllers are kept minimal, they just validate input, call services and map the response.

TourService
Contains all tour CRUD operations. When uploading an image it calls ImageStorageService to save it and only stores the resulting filename on the tour entity. When deleting a tour it also deletes the file from the disk.

TourLogService
Contains tour log CRUD operations. Validates that the referenced tour exists before creating a log.

UserService
Handles user registration with password hashing (via PasswordHash) and login verification. Also has a method to map a username to its user ID.

SearchService
Uses Hibernate Search to do full-text search across Tour fields (name, description, fromLocation, toLocation, transportType) and TourLog fields (comment, difficulty).

ImageStorageService
Handles image storage in the filesystem. When storing an image it generates a UUID filename. It validates that only image files are uploaded (.jpg, .jpeg, .png, .gif, .webp). The upload directory is configurable in application.ies and defaults to uploads/images. The directory is created on application startup via @PostConstruct.

PasswordHash
Uses BCrypt from Spring Security for hashing and verifying passwords. We only use the hashing utility.

### Domain (Entities)
JPA entities that map to database tables.

Tour
Fields: id, name, description, fromLocation, toLocation, transportType, distanceKm, estimatedTime, route, imagePath. 
Has a @ManyToOne relation to User and @OneToMany to TourLog with cascade delete so when a tour is deleted all its logs are deleted.
Its also annotated with @Indexed so its indexed by the Hibernate search.

TourLog
Fields: id, dateTime, comment, difficulty, totalDistance, totalTime, rating. 
@ManyToOne to Tour and @ManyToOne to User.
Is also Indexed for hibernate search.

User
Fields: id, username (unique), password. 
@OneToMany to Tour and TourLog with cascade delete.

SearchResult
A wrapper class that holds the matched tours and the matched tour logs from a search query.

Enums
TransportType (CAR, BICYCLE, WALKING, BUS) and Difficulty (EASY, MEDIUM, HARD, EXPERT) are in domain/enums.

### DTOs
DTOs are used to only send necessary data to the API and separate the Controllers from the Domain model . We use records for our DTOs, which means they are immutable and need no getters, setters, etc.

TourDTO
Includes userId and userUsername for display purposes but excludes the actual User entity and the TourLog list.

TourLogDTO
Representation with tourId and userId as references instead of the full entity objects.

UserDTO
Only contains username and password, for registration and login requests.

### Mappers
Mapper classes convert between entities and DTOs. We have an AbstractMapper<E, D> which is a generic abstract base class with mapToDTO(), mapToEntity(), mapToDTOList() and mapToEntityList().

TourMapper and TourLogMapper extend AbstractMapper with the specific field mappings for their entity/DTO pairs.

### Persistence (Repositories)
We use the Spring JPA Repos.

the respositories: 
- `TourRepository`

- `TourLogRepository`

- `UserRepository`

have the standard methods, and we extend them with any needed queries that are auto generated by JPA.

## Frontend Structure

The frontend is a Vue 3 Single Page Application using the Composition API and is in frontend/src/.

### Views
Page-level (...View) components, one per route. These are the Pages of the app.

MainView
The main dashboard. Has the tour creation form with location autocomplete (using geocode/autocomplete), image upload and the tour list.

TourDetailView
Shows all tour info (name, route, distance, estimated time, image etc.) in the sidebar and a list of tour logs. Users can edit the selected tour, replace its image, add new tour logs, edit existing tour logs by clicking them, and delete logs. The right panel shows a Leaflet map with the route on the map.

SearchView
Full-text search interface. Has a search bar and displays matching tours and tour logs.

LoginView / RegisterView
Authentication pages for login and registration.

### Components
Reusable UI components that are used across multiple views.

Map.vue
A Leaflet map component. Takes start/end coordinates and transport type as props, renders the map with OpenStreetMap tiles, places markers for start and end locations, and draws the route as overlay.
, uses OpenRoure Composable

TourListTile.vue
A reusable tour card/tile component used in the tour list on the main page. It displays the tour image thumbnail, route, description, transport type, distance, estimated time and delete action.

TourLogTile.vue
A reusable tour log entry component used in the tour detail page. It displays date, difficulty, comment, distance, time and rating. Clicking a log entry opens the edit form; the delete button removes the log without opening edit mode.

### Composables
Reusable JavaScript functions using Vue 3's Composition API.

useOpenRoute.js
Contains all OpenRouteService API calls: geocoding (location name to coordinates), reverse geocoding, autocomplete (real-time location suggestions), route calculation and distance/time estimation. Supports multiple transport profiles (driving-car, cycling-regular, foot-walking). The API key is loaded from the VITE_OPEN_ROUTE_API_KEY environment variable.

useMapping.js
Utility functions for time conversion. minutesToHrsAndMins() converts total minutes into hours and minutes, hrsAndMinsToMinutes() does the reverse.

### Stores
We use Pinia for state management.

auth.js
Manages the user session (username, userId). Persists the login state in sessionStorage so it survives page refreshes. Has login(), logout() and a computed isLoggedIn.

### Router
Vue Router configuration in router/index.js. Defines the routes:
- / maps to MainView
- /login maps to LoginView
- /register maps to RegisterView
- /tour/:id maps to TourDetailView
- /search maps to SearchView

### Vite Proxy
During development the frontend runs on port 5173 and the backend on port 8080. To avoid CORS issues, vite.config.js configures a dev proxy that forwards /tours, /users and /search requests to the backend. 
This way the frontend can just call fetch('/tours/...') without worrying about the backend URL. 
In production (Docker), nginx handles the proxying instead.

## UX / UI Description

The UI is a sidebar and main panel layout.

Main Page
The sidebar has the tour creation form with inputs for name, from, to (with autocomplete from OpenRouteService), description, transport type and image upload. 
From and To inputs are validated so numbers are rejected with a browser alert.
Below the form is a route estimation that calculates distance and time when both locations are filled in. 
The main panel shows the user's tours as tiles with image thumbnails.

Tour Detail Page
The sidebar shows the tour info (name, transport type, route, author, description, distance, estimated time, image) and tour logs.
The selected tour can be edited directly in the sidebar, including changing from/to locations, transport type and image.
Tour logs can be created, edited by clicking an existing log, and deleted.
The main panel shows a Leaflet map with the route drawn between start and end markers.

## Intermediate Hand-In Features

- Tour CRUD: users can create, edit and delete tours.
- Tour attributes: tours include name, description, from, to, transport type, distance, estimated time, route information and image.
- Tour list view: tours are managed in a list and uploaded images are shown as thumbnails.
- Tour details: selected tours show all required attributes and a Leaflet map.
- Tour log CRUD: users can create, edit and delete tour logs for a selected tour.
- Tour log attributes: logs include date/time, comment, difficulty, total distance, total time and rating.
- Validation: From and To inputs reject numbers with a browser alert to prevent invalid location input.
- Responsive UI: the sidebar collapses on smaller screens.

Search Page
Search bar at the top, results split into matched tours and matched tour logs. 
Clicking a tour navigates to its detail page.

Responsive Design
On mobile the sidebar collapses and can be toggled with a button. 
An overlay appears behind it when open.

## Design Patterns

DTO Pattern
Controllers communicate through DTOs (records) instead of exposing entities directly. 
prevents exposing internal relations.

Mapper Pattern
AbstractMapper<E, D> base class with generic mapping between entities and DTOs. 
TourMapper and TourLogMapper extend it. 
The abstract class provides list mapping via Streams so the concrete mappers only implement single-object conversion.

Builder Pattern
Tour and TourLog use Lomboks @Builder for object construction. Used in the mappers and services.

Repository Pattern
Spring Data JPA repositories. We define interfaces with query methods and Spring generates the implementations at runtime.

Service Layer Pattern
Business logic in @Service classes. Controllers just validate input, call services and map the response.

Composable Pattern (Frontend)
Reusable logic extracted into composable functions (useOpenRoute, useMapping). 
Components import only what they need.

Stores (Frontend)
Pinia store (auth.js) for centralized state management. 
Session state synced with sessionStorage.

## Full-Text Search with Hibernate Search

We use Hibernate Search with a Lucene backend for full-text search across tours and tour logs. Tour fields (name, description, fromLocation, toLocation) and TourLog fields (comment, difficulty) are annotated with @FullTextField / @KeywordField, 
which indexes them into a local Lucene index at data/index/.

The SearchService uses simpleQueryString to search both entities and returns the results in a SearchResult object. 
Since data.sql inserts test data before Hibernate Search can index it, we rebuild the index on every startup with `@EventListener(ApplicationReadyEvent.class)` that calls `massIndexer().startAndWait()`.

## Library Decisions and Lessons Learned

Hibernate Search over SQL LIKE queries
We wanted full-text search (fuzzy matching, multi-field) without Elasticsearch. Hibernate Search with Lucene runs embedded so no extra infrastructure needed. 
The index lives on the filesystem which is good enough for this project.

Leaflet
Free, open-source, no API key needed for map tiles (OpenStreetMap). 
Combined with OpenRouteService for routing and geocoding.

OpenRouteService API
Free with good limits for geocoding, autocomplete and route calculation. 
Supports multiple transport profiles. 
We use it for converting location names to coordinates, autocomplete suggestions and calculating routes.

Pinia over Vuex
Officially recommended for Vue 3, simpler API, works natively with the Composition API.

Java Records for DTOs
Immutable, no boilerplate, makes it clear these are data carriers not mutable entities.

BCrypt for passwords
Easy to integrate from Spring Security.

Vite dev proxy
We had CORS issues when the frontend called the backend directly. 
The Vite proxy solved this without needing CORS headers in the backend.

## Unit Testing

We use JUnit 5 with Spring Boot Test.

## Database

We use PostgreSQL, which runs in a Docker container created by docker compose.

docker-compose config
Image: postgres:16-alpine
Port: 5432
Database credentials loaded from env variables and application.properties

Database Initialization
The schema is auto-generated by Hibernate JPA (ddl-auto is set to create-drop). 
On startup data.sql in src/main/resources/ inserts test data.
Datasource initialization is deferred so JPA creates the tables first, then data.sql runs.

The Lucene index is stored at data/index/ and rebuilt on every startup.

## Version Control (Git)

We use GitHub for hosting the Repo. 
We have 2 branches: main and dev. 
dev is the working branch where we develop.
When a bigger feature is done we merge into main.

Repository: https://github.com/mmarco02/SWEN2-project