# SkillSync System Architecture

## 1. Architecture Overview

SkillSync follows a layered Android architecture organized around four primary areas:

- **Presentation:** Jetpack Compose screens and reusable UI components
- **ViewModel:** UI state, user actions, orchestration, and lifecycle-aware state
- **Data:** Repository, DAO, Room database/entities, and matching logic
- **Model:** Domain/application models used by the UI and data conversion layer

The repository currently contains a single :app Gradle module, so this should be described as a layered architecture rather than a strict multi-module Clean Architecture implementation.

## 2. High-Level Architecture

~~~text
                         ┌──────────────────┐
                         │     STUDENT      │
                         └────────┬─────────┘
                                  │
                                  ▼
              ┌───────────────────────────────────┐
              │        PRESENTATION LAYER         │
              │                                   │
              │ Jetpack Compose                   │
              │ • AuthScreen                       │
              │ • HomeScreen                       │
              │ • ExploreScreen                    │
              │ • TeamsScreen                      │
              │ • ProfileScreen                    │
              │ • SurveyScreen                     │
              │ • Dialogs / Sheets / Components    │
              └──────────────────┬────────────────┘
                                 │
                                 ▼
              ┌───────────────────────────────────┐
              │          VIEWMODEL LAYER           │
              │                                   │
              │ SkillSyncViewModel                │
              │ • UI state                         │
              │ • Authentication flow              │
              │ • Profile updates                  │
              │ • Opportunity actions              │
              │ • Team actions                     │
              │ • Application / registration flow  │
              └──────────────────┬────────────────┘
                                 │
                                 ▼
              ┌───────────────────────────────────┐
              │             DATA LAYER             │
              │                                   │
              │ SkillSyncRepository               │
              │ SkillSyncDao                      │
              │ Entities / conversions             │
              │ SmartMatchingEngine               │
              └───────────────┬───────────┬───────┘
                              │           │
                              ▼           ▼
                    ┌──────────────┐  ┌──────────────┐
                    │ Room / SQLite│  │ Matching     │
                    │ Local data   │  │ calculations │
                    └──────────────┘  └──────────────┘

       Platform / external integrations
       ┌─────────────┬─────────────┬──────────────┐
       │ Firebase AI │ App Check   │ Network APIs │
       │             │             │ Retrofit/    │
       │             │             │ OkHttp/Moshi │
       └─────────────┴─────────────┴──────────────┘
~~~

## 3. Presentation Layer

The application entry point is MainActivity.kt.

It:

1. Creates the SkillSyncViewModel.
2. Collects UI state using Compose state collection.
3. Applies the application theme.
4. Shows authentication when the user is not authenticated.
5. Shows the main Scaffold and bottom navigation after authentication.
6. Routes the active tab to Home, Explore, Teams, Survey, or Profile.
7. Opens opportunity details and teammate matching sheets from UI state.

### UI organization

~~~text
ui/
├── components/    Reusable UI components
├── screens/       Feature screens
└── theme/         Colors, typography and application theme
~~~

## 4. ViewModel Layer

SkillSyncViewModel.kt is the main presentation/application-state coordinator.

Responsibilities include:

- Authentication and sign-up flow
- Current student state
- Profile editing
- Skills and interests
- Availability preferences
- Project information
- Opportunity selection
- Team creation and management
- Invitations
- Team chat
- Team tasks
- Team resources
- Club applications
- Volunteer registrations
- Survey submissions
- Theme state
- UI messages and errors

The ViewModel uses Kotlin Coroutines and exposes state to Compose.

## 5. Data Layer

### Repository

SkillSyncRepository.kt provides a higher-level API between the ViewModel and Room DAO.

It:

- Reads students, opportunities, teams, messages, tasks, resources, and invitations.
- Converts database entities to application/domain models.
- Converts application/domain models back to database entities.
- Performs create/update operations.
- Creates teams and their initial leader/member/system data.
- Sends invitations.
- Adds team members.
- Creates chat messages.
- Creates and updates tasks.
- Adds team resources.

### DAO

SkillSyncDao.kt is the Room data-access interface.

It provides the database queries and mutations used by the repository.

### Database

SkillSyncDatabase.kt configures the Room database and its entities/DAOs.

### Entities

Entities.kt contains the persistence representations used by Room.

## 6. Domain / Application Models

Models.kt contains application-level models and enums such as:

- StudentProfile
- SkillEntry
- SkillLevel
- Opportunity
- Team
- TeamMember
- TeamTask
- TeamChatMessage
- TeamInvitation
- TeamResource
- Availability information
- Application and opportunity status types
- Opportunity categories and modes

These models are used by the UI, ViewModel, repository and matching logic.

## 7. Smart Matching Engine

SmartMatchingEngine.kt contains deterministic Kotlin matching logic.

### Teammate matching

The teammate score is composed of:

| Component | Maximum |
|---|---:|
| Skill compatibility | 50 |
| Interest compatibility | 20 |
| Availability | 15 |
| Requirements / eligibility | 15 |
| **Total** | **100** |

The implementation constrains the returned total to its configured range and returns a MatchBreakdown containing:

- Total score
- Skill score
- Interest score
- Availability score
- Requirement score
- Complementary skills covered
- Candidate skills matched
- Remaining missing skills
- Human-readable highlights

The important design characteristic is **complementarity**: skills already covered by the current team are identified first, then candidates who cover missing required skills receive additional weight.

### Opportunity matching

calculateOpportunityFit() evaluates:

- Required skills
- Student interests
- Availability
- Branch compatibility

The result is an integer fit score used for opportunity recommendations.

## 8. Authentication Flow

The current authentication flow is local/application-level rather than Firebase Authentication.

~~~text
Login / Signup
      ↓
SkillSyncViewModel
      ↓
SkillSyncRepository
      ↓
Room
      ↓
StudentProfile
      ↓
Authenticated UI state
~~~

The Gradle file contains Firebase Authentication dependencies as commented-out optional dependencies. Therefore, Firebase Authentication should be treated as a future integration unless it is enabled and wired into the application.

## 9. Team Collaboration Flow

~~~text
Opportunity
    ↓
Create Team
    ↓
Team Leader Added
    ↓
Initial System Message
    ↓
Initial Team Task
    ↓
Find Complementary Teammates
    ↓
Invitation
    ↓
Accept / Decline
    ↓
Member Added
    ↓
Chat + Tasks + Resources
~~~

## 10. Club and Volunteering Flow

~~~text
Opportunity
    ├── Club opportunity
    │       ↓
    │   Application form
    │       ↓
    │   ClubApplication
    │
    └── Volunteering opportunity
            ↓
       Registration form
            ↓
       VolunteerRegistration
~~~

## 11. Persistence and Reactive Data

Room queries are exposed through Kotlin Flow where appropriate.

~~~text
Room DAO
   ↓
Flow<Entity>
   ↓
Repository
   ↓
Flow<Domain Model>
   ↓
ViewModel
   ↓
Compose
~~~

This allows UI state to react to local database changes.

## 12. External / Platform Integrations

The project is configured with:

- Firebase AI
- Firebase App Check
- Retrofit
- OkHttp
- Moshi
- Coil

Important implementation distinction: the presence of a dependency does not by itself mean that a service is actively used in every feature. Firebase Firestore and Firebase Authentication are currently commented out in the Gradle configuration and should therefore be documented as optional/future integrations unless enabled in code.

## 13. Security Configuration

The project uses the Secrets Gradle Plugin with:

~~~text
.env
.env.example
~~~

Firebase App Check dependencies are included for debug and reCAPTCHA-based protection.

Real secrets should remain outside version control.

## 14. Testing Architecture

The project is configured for:

- JUnit
- Robolectric
- Compose UI testing
- Espresso
- Roborazzi screenshot testing

The test dependencies are configured in app/build.gradle.kts.

## 15. Architectural Summary

~~~text
Compose UI
    ↓
SkillSyncViewModel
    ↓
SkillSyncRepository
    ↓
SkillSyncDao
    ↓
Room Database

              ↘ SmartMatchingEngine
                    ↓
              Match Results

Platform integrations:
Firebase AI / App Check / Retrofit / OkHttp / Moshi / Coil
~~~

This structure keeps UI rendering separate from state management and database access while keeping the matching algorithm isolated from the presentation layer.
