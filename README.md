# SkillSync 🚀

SkillSync is an Android application for students to discover opportunities, manage skills and profiles, form complementary project teams, collaborate with teammates, and participate in clubs and volunteering activities.

## Problem Statement

Students often have skills and interests but have difficulty finding suitable teammates, relevant opportunities, clubs, and volunteering activities. SkillSync brings these activities into one student-focused platform.

## Core Features

- Student registration and local login flow
- Student profile management
- Skills with proficiency levels
- Interests and availability preferences
- Project and portfolio information
- Opportunity discovery
- Opportunity-fit recommendations
- Smart teammate matching based on complementary skills
- Team creation and team-member management
- Team invitations and responses
- Team chat
- Team tasks and task completion tracking
- Team resources
- Club applications
- Volunteering registration
- Student survey submission
- Light/dark theme support
- Local Room persistence
- Firebase App Check configuration
- Firebase AI dependency for AI-powered functionality

## Smart Matching

SkillSync contains a dedicated SmartMatchingEngine.

For teammate matching, the engine evaluates:

1. **Skill compatibility** — up to 50 points
2. **Interest compatibility** — up to 20 points
3. **Availability** — up to 15 points
4. **Opportunity requirements / eligibility** — up to 15 points

The engine also considers skills already covered by the current team and prioritizes candidates who fill missing required skills.

For opportunity recommendations, the engine evaluates required skills, interests, availability, and branch compatibility.

## System Architecture

~~~text
┌─────────────────────────────────────────────┐
│                   STUDENT                   │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│              PRESENTATION LAYER             │
│                                             │
│ Jetpack Compose                             │
│ • Auth Screen                               │
│ • Home                                      │
│ • Explore                                   │
│ • Teams                                     │
│ • Profile                                   │
│ • Survey                                    │
│ • Opportunity / teammate sheets & dialogs   │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│               VIEWMODEL LAYER               │
│          SkillSyncViewModel                 │
│                                             │
│ UI state • authentication • profile state   │
│ navigation • team actions • opportunities   │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                 DATA LAYER                  │
│                                             │
│ SkillSyncRepository                         │
│ SkillSyncDao                                │
│ Room entities / domain conversion           │
│ SmartMatchingEngine                         │
└───────────────┬──────────────┬──────────────┘
                │              │
                ▼              ▼
       ┌────────────────┐  ┌─────────────────┐
       │ Room Database  │  │ Matching Engine │
       │ Local storage  │  │ Rule-based      │
       │ + Flow         │  │ scoring         │
       └────────────────┘  └─────────────────┘

External / platform integrations:
• Firebase AI
• Firebase App Check
• Retrofit + OkHttp
• Moshi
• Coil
~~~

See [System Architecture](docs/SYSTEM_ARCHITECTURE.md) for the detailed architecture and data flow.

## Technology Stack

| Area | Technology |
|---|---|
| Platform | Android |
| Language | Kotlin |
| UI | Jetpack Compose |
| Design system | Material 3 |
| State / presentation | Android ViewModel + Kotlin StateFlow/Flow |
| Local persistence | Room / SQLite |
| Matching | Kotlin SmartMatchingEngine |
| AI integration | Firebase AI dependency |
| App protection | Firebase App Check |
| Networking | Retrofit + OkHttp |
| JSON | Moshi |
| Image loading | Coil |
| Async | Kotlin Coroutines |
| Code generation | KSP |
| Build | Gradle Kotlin DSL |
| Testing | JUnit, Robolectric, Compose UI testing, Roborazzi |

See [Tech Stack](docs/TECH_STACK.md) for implementation details.

## Project Structure

~~~text
SkillSync/
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/example/
│       │       ├── MainActivity.kt
│       │       ├── data/
│       │       │   ├── Entities.kt
│       │       │   ├── SkillSyncDao.kt
│       │       │   ├── SkillSyncDatabase.kt
│       │       │   ├── SkillSyncRepository.kt
│       │       │   └── SmartMatchingEngine.kt
│       │       ├── model/
│       │       │   └── Models.kt
│       │       ├── ui/
│       │       │   ├── components/
│       │       │   ├── screens/
│       │       │   └── theme/
│       │       └── viewmodel/
│       │           └── SkillSyncViewModel.kt
│       └── test/
├── docs/
│   ├── SYSTEM_ARCHITECTURE.md
│   └── TECH_STACK.md
├── .env.example
├── build.gradle.kts
└── settings.gradle.kts
~~~

## Data Flow

~~~text
User action
    ↓
Compose screen
    ↓
SkillSyncViewModel
    ↓
SkillSyncRepository
    ↓
SkillSyncDao
    ↓
Room Database
    ↓
Flow / domain models
    ↓
ViewModel state
    ↓
Compose UI
~~~

For matching:

~~~text
Student profile + Opportunity + Current team
                    ↓
          SmartMatchingEngine
                    ↓
      MatchBreakdown / fit score
                    ↓
             UI recommendation
~~~

## Security and Configuration

The project uses the Secrets Gradle Plugin convention with:

- .env for local secrets
- .env.example as the template
- Firebase App Check debug/reCAPTCHA dependencies

Do not commit real API keys, passwords, signing credentials, or other secrets.

The repository currently keeps Firebase Firestore and Firebase Authentication dependencies commented out in app/build.gradle.kts; they should not be described as active services until they are enabled and used by the application.

## Requirements

- Android Studio
- JDK 11+
- Android SDK with API 36
- Android device or emulator

## Build

Windows:

~~~bash
gradlew.bat build
~~~

macOS/Linux:

~~~bash
./gradlew build
~~~

## Test

~~~bash
gradlew.bat test
~~~

## Current Architecture Note

The codebase uses a practical layered Android architecture with dedicated data, model, UI, and ViewModel packages. It is **not** documented here as a strict multi-module Clean Architecture implementation because the repository currently contains a single :app module.

## Future Scope

Potential extensions include:

- Cloud-backed synchronization
- Firebase Authentication
- Firestore-based real-time collaboration
- Push notifications
- Real-time team chat
- Admin dashboard
- More advanced recommendation models
- Club and volunteering management workflows
- Analytics and achievement tracking

## Repository

[SkillSync on GitHub](https://github.com/amberpatel077-lang/skillsync1)
