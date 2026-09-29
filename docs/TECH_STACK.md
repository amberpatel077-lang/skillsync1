# SkillSync Tech Stack

## 1. Platform

**Android**

SkillSync is an Android application built as a single :app Gradle module.

- Application ID: com.aistudio.skillsync.vwtqrx
- Minimum SDK: 24
- Target SDK: 36
- Compile SDK: 36.1

## 2. Programming Language

**Kotlin**

Kotlin is used throughout the application, including:

- UI
- ViewModel
- repository
- Room entities/DAO/database
- matching engine
- domain models

## 3. UI

### Jetpack Compose

The application uses Jetpack Compose for declarative UI.

### Material 3

Material 3 provides the application UI components and design system.

The project also includes Material icons, including the extended icon set.

## 4. Architecture / State

### Android ViewModel

SkillSyncViewModel coordinates UI state and application actions.

### Kotlin Flow / StateFlow

Reactive state and Room query streams are handled using Kotlin Flow APIs.

### Kotlin Coroutines

Coroutines are used for asynchronous and suspendable operations.

## 5. Local Database

### Room

The project includes:

- Room runtime
- Room KTX
- Room compiler through KSP

Room provides local persistence through:

~~~text
Room Database
     ↓
DAO
     ↓
Repository
     ↓
ViewModel
     ↓
Compose UI
~~~

SQLite is the underlying local database technology used by Room.

## 6. Matching Engine

### Custom Kotlin SmartMatchingEngine

The project contains a dedicated SmartMatchingEngine.kt.

It provides deterministic scoring for:

- Teammate matching
- Opportunity fit

Teammate matching uses skill compatibility, interest compatibility, availability, and requirements/eligibility.

The maximum component weights implemented by the engine are:

- Skill compatibility: 50
- Interest compatibility: 20
- Availability: 15
- Requirements/eligibility: 15

## 7. AI

### Firebase AI

The project includes the Firebase AI dependency.

The repository is configured to support Gemini/Firebase AI integration, and .env.example contains a placeholder for a Gemini API key.

**Documentation note:** Firebase AI is included in the build configuration, but a dependency alone should not be interpreted as proof that every AI feature is currently active.

## 8. Firebase App Check

The project includes:

- Firebase App Check reCAPTCHA
- Firebase App Check debug support

This is intended to help protect Firebase-backed resources from unauthorized clients.

## 9. Networking

### Retrofit

Retrofit is included for HTTP API communication.

### OkHttp

OkHttp is used as the HTTP client layer.

### Logging Interceptor

The project includes the OkHttp logging interceptor dependency.

### Moshi

Moshi is used for JSON serialization/deserialization, with Kotlin code generation configured through KSP.

## 10. Image Loading

### Coil Compose

Coil is included for image loading in Jetpack Compose.

## 11. Code Generation

### Kotlin Symbol Processing (KSP)

KSP is configured for:

- Room compiler
- Moshi Kotlin code generation

## 12. Build System

### Gradle Kotlin DSL

The project uses:

- build.gradle.kts
- settings.gradle.kts
- Version catalog aliases
- Gradle dependency management

The root project is named **SkillSync**.

## 13. Secrets

The Secrets Gradle Plugin is configured to use:

- .env
- .env.example

The repository's example environment file documents the Gemini API key placeholder.

Real secrets must not be committed.

## 14. Testing

The project is configured with:

### Unit / JVM testing

- JUnit
- AndroidX JUnit
- Kotlin Coroutines Test
- Robolectric

### Compose / Android testing

- Compose UI test JUnit4
- AndroidX Runner
- Espresso

### Screenshot testing

- Roborazzi
- Roborazzi Compose
- Roborazzi JUnit Rule

## 15. Optional / Currently Disabled Dependencies

The Gradle file contains several commented-out dependencies.

Notably:

- Firebase Firestore
- Firebase Authentication
- Credential Manager
- Google Sign-In
- Navigation Compose
- DataStore
- Camera
- Location

These should be described as optional/future integrations rather than current production dependencies.

## 16. Technology Stack Summary

| Category | Technology |
|---|---|
| Platform | Android |
| Language | Kotlin |
| UI | Jetpack Compose |
| UI Design | Material 3 |
| State | ViewModel + Flow/StateFlow |
| Async | Kotlin Coroutines |
| Database | Room / SQLite |
| Matching | Custom Kotlin SmartMatchingEngine |
| AI | Firebase AI / Gemini integration |
| Security | Firebase App Check |
| HTTP | Retrofit + OkHttp |
| JSON | Moshi |
| Images | Coil Compose |
| Code generation | KSP |
| Build | Gradle Kotlin DSL |
| Unit testing | JUnit + Robolectric |
| UI testing | Compose UI Test + Espresso |
| Screenshot testing | Roborazzi |
