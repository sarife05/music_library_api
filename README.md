# Music Library RESTful API - End-Term Project

**Complete Spring Boot REST API with Design Patterns & Component Principles**

---

## Project Overview

This project is a complete transformation of the Music Library system into a professional **Spring Boot RESTful API**, integrating:
- **Design Patterns**: Singleton, Factory, Builder
- **Component Principles**: REP, CCP, CRP
- **SOLID Architecture**: Maintained from previous assignments
- **RESTful API**: Full CRUD operations with Spring Boot
- **Database Integration**: JDBC with Spring JdbcTemplate
- **Global Exception Handling**: Professional error responses
- **Complete Documentation**: API docs, UML diagrams, screenshots

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Postman/Browser)                  │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP Requests (JSON)
┌────────────────────────▼────────────────────────────────────┐
│               REST CONTROLLERS (@RestController)             │
│  MediaController, PlaylistController, HealthController      │
└────────────────────────┬────────────────────────────────────┘
                         │ Uses Design Patterns
                         │ (Factory, Builder)
┌────────────────────────▼────────────────────────────────────┐
│               SERVICE LAYER (@Service)                       │
│  MediaServiceImpl, PlaylistServiceImpl                       │
│  - Business Logic                                            │
│  - Validation                                                │
│  - Exception Handling                                        │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│            REPOSITORY LAYER (@Repository)                    │
│  MediaRepositoryImpl, PlaylistRepositoryImpl                 │
│  - JDBC Operations                                           │
│  - Spring JdbcTemplate                                       │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                    DATABASE (PostreSQL                       │
│  Tables: media, playlists, playlist_items                    │
└──────────────────────────────────────────────────────────────┘

CROSS-CUTTING CONCERNS:
┌──────────────────────────────────────────────────────────────┐
│  Singleton Patterns: AppConfig, DatabaseConfig, LoggingService│
│  Global Exception Handler: @RestControllerAdvice             │
│  Component Principles: Package organization (REP, CCP, CRP)  │
└──────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Implementation

### 1. Singleton Pattern (25% of grade)

#### A. AppConfig Singleton
**Purpose**: Application-wide configuration management
**Location**: `config/AppConfig.java`

```java
@Component
public class AppConfig {
    private static AppConfig instance;
    
    private AppConfig() {
        // Private constructor
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }
}
```

**Why Singleton?**
- Single source of configuration across application
- Prevents multiple instances loading properties
- Thread-safe lazy initialization
- Spring `@Component` ensures singleton scope

#### B. DatabaseConfig Singleton
**Purpose**: Database connection management
**Location**: `config/DatabaseConfig.java`

```java
@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() {
        // Single DataSource instance for entire application
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        // Single JdbcTemplate instance
    }
}
```

**Why Singleton?**
- Connection pooling requires single instance
- Configuration consistency
- Resource management

#### C. LoggingService Singleton
**Purpose**: Centralized logging
**Location**: `config/LoggingService.java`

```java
@Service
public class LoggingService {
    private static LoggingService instance;
    
    public void logApiRequest(String method, String endpoint) {
        // Centralized logging
    }
}
```

**Why Singleton?**
- Consistent log formatting
- Single point of control
- Resource efficiency

---

### 2. Factory Pattern (25% of grade)

#### MediaFactory
**Purpose**: Create different Media subtypes (Song, Podcast)
**Location**: `patterns/MediaFactory.java`

```java
@Component
public class MediaFactory {
    public Media createMedia(MediaType type, String name, int duration, String creator) {
        return switch (type) {
            case SONG -> new Song(name, duration, creator);
            case PODCAST -> new Podcast(name, duration, creator);
        };
    }
    
    public Song createSong(String name, int duration, String creator, 
                          String album, String genre, double price) {
        // Creates Song with all parameters
    }
    
    public Podcast createPodcast(String name, int duration, String creator,
                                String host, int episodeNumber, String category) {
        // Creates Podcast with all parameters
    }
}
```

**Usage in REST Controller**:
```java
@PostMapping
public ResponseEntity<ApiResponse<Media>> createMedia(@RequestBody MediaRequest request) {
    Media media = mediaFactory.createMedia(
        request.getType(),
        request.getName(),
        request.getDuration(),
        request.getCreator()
    );
    return mediaService.createMedia(media);
}
```

**Benefits**:
- Client doesn't know concrete classes
- Easy to add new media types
- Follows Open-Closed Principle
- Returns base type for polymorphism

---

### 3. Builder Pattern (25% of grade)

#### PlaylistBuilder
**Purpose**: Construct complex Playlist objects with optional parameters
**Location**: `patterns/PlaylistBuilder.java`

```java
@Component
public class PlaylistBuilder {
    private String name;
    private String description;
    private List<Media> items;
    
    public static PlaylistBuilder builder() {
        return new PlaylistBuilder();
    }
    
    public PlaylistBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public PlaylistBuilder description(String description) {
        this.description = description;
        return this;
    }
    
    public PlaylistBuilder addMedia(Media media) {
        items.add(media);
        return this;
    }
    
    public Playlist build() {
        if (name == null) {
            throw new IllegalStateException("Name required");
        }
        return new Playlist(name, description, items);
    }
}
```

**Usage in REST Controller**:
```java
@PostMapping
public ResponseEntity<ApiResponse<Playlist>> createPlaylist(@RequestBody PlaylistRequest request) {
    Playlist playlist = playlistBuilder
            .reset()
            .name(request.getName())
            .description(request.getDescription())
            .build();
    
    return playlistService.createPlaylist(playlist);
}
```

**Benefits**:
- ✅ Avoids telescoping constructors
- ✅ Fluent, readable API
- ✅ Optional parameters
- ✅ Immutable object construction

---

## Component Principles

### REP - Reuse/Release Equivalence Principle
*"The granule of reuse is the granule of release"*

**Package Structure**:
```
com.musiclibrary/
├── patterns/          ← Reusable design patterns module
│   ├── MediaFactory
│   ├── PlaylistBuilder
│   └── [Future patterns]
│
├── config/            ← Reusable configuration module
│   ├── AppConfig
│   ├── DatabaseConfig
│   └── LoggingService
│
├── utils/             ← Reusable utility module
│   ├── ReflectionUtils
│   └── SortingUtils
│
└── [Other packages]
```

**Benefits**:
- Each package can be released independently
- Patterns package is reusable across projects
- Config package can be extracted as library
- Clear versioning boundaries

---

### CCP - Common Closure Principle
*"Classes that change together should be packaged together"*

**Package Grouping**:

```
controller/            ← All REST endpoints change together
├── MediaController
├── PlaylistController
└── HealthController

service/               ← Business logic changes together
├── interfaces/
│   ├── MediaService
│   └── PlaylistService
├── MediaServiceImpl
└── PlaylistServiceImpl

repository/            ← Data access changes together
├── interfaces/
│   ├── MediaRepository
│   └── PlaylistRepository
├── MediaRepositoryImpl
└── PlaylistRepositoryImpl

dto/                   ← Request/Response objects change together
├── MediaRequest
├── PlaylistRequest
└── ApiResponse

exception/             ← Exception types change together
├── InvalidInputException
├── DuplicateResourceException
├── ResourceNotFoundException
└── DatabaseOperationException
```

**Reasoning**:
- API contract changes affect all DTOs together
- Business rule changes affect all services together
- Database schema changes affect all repositories together
- Reduces shotgun surgery

---

### CRP - Common Reuse Principle
*"Don't force users of a component to depend on things they don't need"*

**Package Dependencies**:

```
controller/  → depends on → service/, dto/, exception/
service/     → depends on → repository/, model/, exception/
repository/  → depends on → model/, exception/
patterns/    → depends on → model/ only
utils/       → no dependencies (pure utilities)
```

**Benefits**:
- Controllers don't depend on repository implementation
- Patterns don't depend on Spring components
- Utils are completely independent
- Minimal coupling

**Example - Patterns Independence**:
```java
// MediaFactory doesn't depend on Spring, JDBC, or services
@Component  // Optional - can work without Spring
public class MediaFactory {
    public Media createMedia(...) {
        // Only depends on model package
        return new Song(...);
    }
}
```

---

## RESTful API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Media Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/media` | Get all media | - | `List<Media>` |
| GET | `/media/{id}` | Get media by ID | - | `Media` |
| POST | `/media` | Create new media | `MediaRequest` | `Media` |
| PUT | `/media/{id}` | Update media | `MediaRequest` | `Media` |
| DELETE | `/media/{id}` | Delete media | - | Success message |
| GET | `/media/type/{type}` | Get media by type | - | `List<Media>` |
| GET | `/media/search?keyword={keyword}` | Search media | - | `List<Media>` |

#### Playlist Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/playlists` | Get all playlists | - | `List<Playlist>` |
| GET | `/playlists/{id}` | Get playlist by ID | - | `Playlist` |
| POST | `/playlists` | Create playlist | `PlaylistRequest` | `Playlist` |
| PUT | `/playlists/{id}` | Update playlist | `PlaylistRequest` | `Playlist` |
| DELETE | `/playlists/{id}` | Delete playlist | - | Success message |
| POST | `/playlists/{playlistId}/media/{mediaId}` | Add media to playlist | - | Success message |
| DELETE | `/playlists/{playlistId}/media/{mediaId}` | Remove media | - | Success message |

---

### Sample API Requests & Responses

#### 1. Create Song (POST /api/media)

**Request**:
```json
{
    "name": "Bohemian Rhapsody",
    "duration": 354,
    "creator": "Queen",
    "type": "SONG",
    "album": "A Night at the Opera",
    "genre": "Rock",
    "price": 1.29
}
```

**Response (201 Created)**:
```json
{
    "success": true,
    "message": "Media created successfully",
    "data": {
        "id": 1,
        "name": "Bohemian Rhapsody",
        "duration": 354,
        "creator": "Queen",
        "type": "SONG",
        "mediaType": "SONG",
        "album": "A Night at the Opera",
        "genre": "Rock",
        "price": 1.29
    },
    "timestamp": "2026-02-10T13:45:30"
}
```

#### 2. Get All Media (GET /api/media)

**Response (200 OK)**:
```json
{
    "success": true,
    "message": "Retrieved all media",
    "data": [
        {
            "id": 1,
            "name": "Bohemian Rhapsody",
            "duration": 354,
            "creator": "Queen",
            "type": "SONG",
            ...
        },
        {
            "id": 2,
            "name": "The Joe Rogan Experience",
            "duration": 7200,
            "creator": "Joe Rogan",
            "type": "PODCAST",
            ...
        }
    ],
    "timestamp": "2026-02-10T13:46:15"
}
```

#### 3. Create Playlist (POST /api/playlists)

**Request**:
```json
{
    "name": "My Favorites",
    "description": "Best songs of all time"
}
```

**Response (201 Created)**:
```json
{
    "success": true,
    "message": "Playlist created successfully",
    "data": {
        "id": 1,
        "name": "My Favorites",
        "description": "Best songs of all time",
        "items": []
    },
    "timestamp": "2026-02-10T13:47:00"
}
```

#### 4. Error Response Example

**Request**: GET /api/media/999 (non-existent ID)

**Response (404 Not Found)**:
```json
{
    "success": false,
    "message": "Media with ID 999 not found",
    "data": null,
    "timestamp": "2026-02-10T13:48:22"
}
```

---

## Database Schema

Same as previous assignments with Spring Boot integration:

```sql
CREATE TABLE media (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    duration INTEGER NOT NULL CHECK(duration > 0),
    type TEXT NOT NULL CHECK(type IN ('SONG', 'PODCAST')),
    creator TEXT NOT NULL,
    album TEXT,
    genre TEXT,
    price REAL DEFAULT 0.99 CHECK(price >= 0),
    host TEXT,
    episode_number INTEGER DEFAULT 0,
    category TEXT,
    UNIQUE(name, type, creator)
);

CREATE TABLE playlists (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE playlist_items (
    playlist_id INTEGER NOT NULL,
    media_id INTEGER NOT NULL,
    position INTEGER DEFAULT 0,
    PRIMARY KEY (playlist_id, media_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);
```

---

## Setup & Running

### Prerequisites
- Java JDK 17+
- Maven 3.8+
- Postman or similar API testing tool

### Application Properties

Create `src/main/resources/application.properties`:
```properties
# Server Configuration
server.port=8080
spring.application.name=music-library-api

# Database Configuration
spring.datasource.url=jdbc:postgres:musiclibrary
spring.datasource.driver-class-name=org.postgres.JDBC

# Logging
logging.level.com.musiclibrary=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Maven Dependencies (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.musiclibrary</groupId>
    <artifactId>music-library-api</artifactId>
    <version>2.0.0</version>
    <name>Music Library REST API</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web (REST API) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot JDBC -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        
        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

       <!-- PostgreSQL JDBC Driver -->
       <dependency>
          <groupId>org.postgresql</groupId>
          <artifactId>postgresql</artifactId>
          <version>42.7.8</version>
          <scope>runtime</scope>
       </dependency>
        
        <!-- Jackson for JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <!-- Lombok (Optional) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Spring Boot DevTools (Development) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/music-library-api-2.0.0.jar
```

### Verify it's Running

```bash
# Health check
curl http://localhost:8080/api/health

# API documentation
curl http://localhost:8080/api/docs
```

---

## Postman Testing Guide

### Import Postman Collection

Create a collection with these requests:

1. **GET All Media**
    - URL: `http://localhost:8080/api/media`
    - Method: GET
    - Expected: 200 OK with media list

2. **POST Create Song**
    - URL: `http://localhost:8080/api/media`
    - Method: POST
    - Body (JSON):
   ```json
   {
       "name": "Test Song",
       "duration": 180,
       "creator": "Test Artist",
       "type": "SONG",
       "album": "Test Album",
       "genre": "Pop",
       "price": 0.99
   }
   ```
    - Expected: 201 Created

3. **GET Media by ID**
    - URL: `http://localhost:8080/api/media/1`
    - Method: GET
    - Expected: 200 OK with single media

4. **PUT Update Media**
    - URL: `http://localhost:8080/api/media/1`
    - Method: PUT
    - Body: Updated media JSON
    - Expected: 200 OK

5. **DELETE Media**
    - URL: `http://localhost:8080/api/media/1`
    - Method: DELETE
    - Expected: 200 OK

---

## SOLID Principles in REST API

All SOLID principles from previous assignments are maintained:

- **SRP**: Each controller handles one resource type
- **OCP**: Factory pattern allows adding new media types
- **LSP**: Song/Podcast can substitute Media everywhere
- **ISP**: Narrow REST endpoints for specific operations
- **DIP**: Controllers depend on service interfaces

---

## UML Diagrams

See `docs/` folder for:
- Class diagram with design patterns
- Component diagram showing package structure
- Sequence diagrams for API flows

---

## Deployment

### Docker (Optional)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/music-library-api-2.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
docker build -t music-library-api .
docker run -p 8080:8080 music-library-api
```

---

## Reflection

### What I Learned
1. **Design Patterns in Practice**: Real-world application of Singleton, Factory, Builder
2. **Spring Boot**: Building production-ready REST APIs
3. **Component Principles**: Organizing code for reusability and maintainability
4. **REST Architecture**: Designing clean, intuitive APIs
5. **Integration**: Combining multiple patterns and principles cohesively

### Challenges
1. Integrating design patterns with Spring's DI
2. Balancing pattern usage vs. over-engineering
3. Maintaining SOLID while adding new features
4. JSON serialization with polymorphism

### Future Improvements
1. Add Spring Data JPA
2. Implement authentication/authorization
3. Add API versioning
4. Implement caching
5. Add comprehensive test suite
6. Deploy to cloud platform