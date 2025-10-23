# Architecture Documentation

## System Architecture

### Overview
The Music Streaming Platform follows a modern three-tier architecture with containerized microservices.

```
┌─────────────────────────────────────────────────────────────┐
│                         Client Layer                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │   React Frontend (Port 3000)                         │  │
│  │   - Single Page Application                          │  │
│  │   - React Router for navigation                      │  │
│  │   - Axios for HTTP requests                          │  │
│  │   - JWT token management                             │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │   Spring Boot Backend (Port 8080)                    │  │
│  │   ┌──────────────────────────────────────────────┐  │  │
│  │   │   Controller Layer                            │  │  │
│  │   │   - REST API endpoints                        │  │  │
│  │   │   - Request validation                        │  │  │
│  │   │   - Exception handling                        │  │  │
│  │   └──────────────────────────────────────────────┘  │  │
│  │   ┌──────────────────────────────────────────────┐  │  │
│  │   │   Service Layer                               │  │  │
│  │   │   - Business logic                            │  │  │
│  │   │   - Transaction management                    │  │  │
│  │   │   - Recommendation engine                     │  │  │
│  │   └──────────────────────────────────────────────┘  │  │
│  │   ┌──────────────────────────────────────────────┐  │  │
│  │   │   Security Layer                              │  │  │
│  │   │   - JWT authentication                        │  │  │
│  │   │   - OAuth2 support                            │  │  │
│  │   │   - Role-based access control                 │  │  │
│  │   └──────────────────────────────────────────────┘  │  │
│  │   ┌──────────────────────────────────────────────┐  │  │
│  │   │   Repository Layer                            │  │  │
│  │   │   - Spring Data JPA                           │  │  │
│  │   │   - Database queries                          │  │  │
│  │   └──────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │   MySQL Database (Port 3306)                         │  │
│  │   - Users, Songs, Artists, Playlists                 │  │
│  │   - Play History, Analytics                          │  │
│  │   - Relational data model                            │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Frontend
- **React 18.2.0**: UI library
- **React Router DOM 6.20.0**: Client-side routing
- **Axios 1.6.2**: HTTP client
- **CSS3**: Styling with Spotify-inspired design

### Backend
- **Spring Boot 3.1.5**: Application framework
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: Database access
- **Hibernate**: ORM
- **JWT (JJWT 0.11.5)**: Token-based authentication
- **MySQL Connector**: Database driver
- **Lombok**: Boilerplate code reduction

### Database
- **MySQL 8.0**: Relational database
- **JPA/Hibernate**: ORM layer

### Infrastructure
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Nginx**: Reverse proxy for frontend
- **Maven**: Backend build tool
- **npm**: Frontend package manager

### CI/CD
- **GitHub Actions**: Automated testing and deployment

## Database Schema

### Entity Relationship Diagram

```
┌─────────────┐         ┌─────────────┐
│    User     │────────▶│   Artist    │
│─────────────│  follows│─────────────│
│ id          │         │ id          │
│ username    │         │ name        │
│ email       │         │ bio         │
│ password    │         │ country     │
│ role        │         │ followerCount│
└──────┬──────┘         └──────┬──────┘
       │                       │
       │ owns                  │ creates
       │                       │
       ▼                       ▼
┌─────────────┐         ┌─────────────┐
│  Playlist   │────────▶│    Song     │
│─────────────│ contains│─────────────│
│ id          │         │ id          │
│ name        │         │ title       │
│ description │         │ album       │
│ isPublic    │         │ duration    │
└──────┬──────┘         │ genre       │
       │                │ playCount   │
       │                └──────┬──────┘
       │                       │
       └───────────┬───────────┘
                   │
                   ▼
            ┌─────────────┐
            │ PlayHistory │
            │─────────────│
            │ id          │
            │ userId      │
            │ songId      │
            │ playedAt    │
            │ duration    │
            └─────────────┘
```

### Tables

#### users
- Primary entity for user accounts
- Stores authentication credentials
- Many-to-many with playlists and artists

#### songs
- Stores song metadata
- Many-to-one with artists
- Many-to-many with playlists

#### artists
- Stores artist information
- One-to-many with songs
- Many-to-many with users (followers)

#### playlists
- User-created song collections
- Many-to-many with songs and users

#### play_history
- Analytics and tracking
- Records user listening behavior
- Used for recommendations

## Security Architecture

### Authentication Flow

```
1. User Login
   ┌──────┐                   ┌──────────┐
   │Client│                   │ Backend  │
   └───┬──┘                   └────┬─────┘
       │                           │
       │ POST /auth/signin         │
       │ {username, password}      │
       │──────────────────────────▶│
       │                           │
       │                      Validate
       │                      credentials
       │                           │
       │           JWT Token       │
       │◀──────────────────────────│
       │                           │
       │ Store token locally       │
       │                           │

2. Authenticated Request
   ┌──────┐                   ┌──────────┐
   │Client│                   │ Backend  │
   └───┬──┘                   └────┬─────┘
       │                           │
       │ GET /api/songs            │
       │ Authorization: Bearer JWT │
       │──────────────────────────▶│
       │                           │
       │                      Validate
       │                      JWT token
       │                           │
       │           Response        │
       │◀──────────────────────────│
       │                           │
```

### Security Features

1. **Password Encryption**
   - BCrypt hashing algorithm
   - Salt rounds: 10 (default)

2. **JWT Token**
   - HS512 signature algorithm
   - Expiration: 24 hours
   - Stateless authentication

3. **CORS Configuration**
   - Allowed origins: localhost:3000, frontend container
   - Allowed methods: GET, POST, PUT, DELETE, OPTIONS

4. **Role-Based Access Control**
   - USER: Basic operations
   - ARTIST: Upload songs
   - ADMIN: Full access

## API Architecture

### RESTful Design
- Resource-based URLs
- HTTP methods (GET, POST, PUT, DELETE)
- JSON request/response format
- Stateless communication

### Endpoint Structure
```
/api
├── /auth
│   ├── /signin
│   └── /signup
├── /songs
│   ├── /
│   ├── /{id}
│   ├── /search
│   ├── /top
│   └── /{id}/play
├── /playlists
│   ├── /my
│   ├── /public
│   ├── /{id}
│   └── /{playlistId}/songs/{songId}
├── /artists
│   ├── /
│   ├── /{id}
│   ├── /search
│   ├── /top
│   ├── /following
│   └── /{id}/follow
└── /recommendations
```

## Recommendation Engine

### Algorithm
```
1. Analyze user's play history
2. Identify top played songs
3. Extract preferred genres
4. Find similar songs in same genres
5. Exclude already played songs
6. Return top 20 recommendations
```

### Factors Considered
- Play count frequency
- Genre preferences
- Artist affinity
- Recency of plays

## Deployment Architecture

### Docker Compose Services

```
┌─────────────────────────────────────┐
│        Docker Network               │
│                                     │
│  ┌─────────────┐                   │
│  │  Frontend   │                   │
│  │  (Nginx)    │                   │
│  │  Port: 3000 │                   │
│  └──────┬──────┘                   │
│         │                           │
│  ┌──────▼──────┐                   │
│  │  Backend    │                   │
│  │  (Spring)   │                   │
│  │  Port: 8080 │                   │
│  └──────┬──────┘                   │
│         │                           │
│  ┌──────▼──────┐                   │
│  │   MySQL     │                   │
│  │  Port: 3306 │                   │
│  │  Volume:    │                   │
│  │  mysql-data │                   │
│  └─────────────┘                   │
│                                     │
└─────────────────────────────────────┘
```

### Container Details

**Frontend Container**
- Base: node:18-alpine (build), nginx:alpine (runtime)
- Build process: npm ci, npm run build
- Serves static files via Nginx
- Proxies API requests to backend

**Backend Container**
- Base: maven:3.9-eclipse-temurin-17 (build), eclipse-temurin:17-jre-alpine (runtime)
- Build process: mvn clean package
- Multi-stage build for optimization
- Health check: MySQL availability

**Database Container**
- Base: mysql:8.0
- Persistent volume for data
- Health check: mysqladmin ping

## Scalability Considerations

### Horizontal Scaling
- Backend: Stateless design allows multiple instances
- Load balancer for request distribution
- Session management via JWT (no server-side sessions)

### Performance Optimization
- Database indexing on frequently queried fields
- Connection pooling
- Lazy loading for JPA entities
- Frontend code splitting
- CDN for static assets (future)

### Future Enhancements
- Redis for caching
- Elasticsearch for advanced search
- Message queue for async operations
- Microservices architecture
- Kubernetes orchestration

## Monitoring and Logging

### Application Logs
- Spring Boot logging framework
- Log levels: ERROR, WARN, INFO, DEBUG
- Rotating file logs

### Health Checks
- Database connectivity
- Service availability
- Docker health checks

## Development Workflow

```
Developer
    │
    ├─▶ Feature Branch
    │       │
    │       ├─▶ Local Development
    │       │       │
    │       │       ├─▶ Code Changes
    │       │       ├─▶ Local Testing
    │       │       └─▶ Commit
    │       │
    │       └─▶ Push to GitHub
    │               │
    │               ▼
    │         GitHub Actions
    │               │
    │               ├─▶ Build Backend
    │               ├─▶ Test Backend
    │               ├─▶ Build Frontend
    │               ├─▶ Test Frontend
    │               ├─▶ Build Docker Images
    │               └─▶ Test Docker Compose
    │               │
    │               ▼
    │         Pull Request
    │               │
    │               ▼
    │         Code Review
    │               │
    │               ▼
    │         Merge to Main
    │               │
    │               ▼
    │         Production Deployment
    │
    └─▶ Monitoring & Maintenance
```

## Best Practices

### Backend
- RESTful API design
- Transaction management
- Exception handling
- Input validation
- Security best practices

### Frontend
- Component-based architecture
- State management
- Error boundaries
- Responsive design
- Accessibility

### Database
- Normalized schema
- Proper indexing
- Foreign key constraints
- Transaction isolation

### DevOps
- Infrastructure as Code
- Automated testing
- Continuous Integration
- Container best practices
