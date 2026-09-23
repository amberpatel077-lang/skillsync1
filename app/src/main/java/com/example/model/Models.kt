package com.example.model

enum class SkillLevel(val displayName: String, val weight: Double) {
    BEGINNER("Beginner", 0.5),
    INTERMEDIATE("Intermediate", 0.8),
    ADVANCED("Advanced", 1.0)
}

data class SkillEntry(
    val name: String,
    val level: SkillLevel = SkillLevel.INTERMEDIATE
) {
    val iconEmoji: String
        get() = when (name.lowercase()) {
            "python" -> "🐍"
            "machine learning", "ml", "ai/ml", "ai" -> "🤖"
            "react", "web development", "javascript", "frontend" -> "🌐"
            "ui/ux", "figma", "design" -> "🎨"
            "java" -> "☕"
            "c++", "cpp" -> "⚡"
            "cloud/devops", "cloud", "devops", "docker" -> "☁️"
            "iot/electronics", "iot", "robotics", "cad" -> "🔌"
            "presentation", "presentation/pitch", "pitching" -> "🎤"
            "android", "kotlin", "flutter" -> "📱"
            "data science", "analytics" -> "📊"
            else -> "🛠️"
        }
}

data class Availability(
    val weekdays: Boolean = true,
    val weekends: Boolean = true,
    val evenings: Boolean = true,
    val fullTimeEvent: Boolean = true,
    val availableUntil: String = "30 Nov 2026"
) {
    fun summaryText(): String {
        val parts = mutableListOf<String>()
        if (weekdays) parts.add("Weekdays")
        if (weekends) parts.add("Weekends")
        if (evenings) parts.add("Evenings")
        if (fullTimeEvent) parts.add("Full-time events")
        return parts.joinToString(" • ")
    }
}

enum class LookingFor(val label: String) {
    TEAM("Looking for Team"),
    OPPORTUNITY("Looking for Opportunity"),
    BOTH("Looking for Both")
}

data class ProjectExperience(
    val title: String,
    val description: String,
    val techStack: String,
    val role: String,
    val githubOrDemoUrl: String = ""
)

data class StudentProfile(
    val id: String,
    val name: String,
    val college: String,
    val branch: String,
    val year: String,
    val email: String,
    val password: String = "password123",
    val isEmailVerified: Boolean = true,
    val isBranchVerified: Boolean = true,
    val skills: List<SkillEntry> = emptyList(),
    val interests: List<String> = emptyList(),
    val availability: Availability = Availability(),
    val preferredOpportunityTypes: List<String> = emptyList(),
    val lookingFor: LookingFor = LookingFor.BOTH,
    val contactPreference: String = "College Email & Discord",
    val bio: String = "",
    val projects: List<ProjectExperience> = emptyList(),
    val isPublic: Boolean = true
)

enum class OpportunityCategory(val displayName: String, val iconEmoji: String) {
    HACKATHON("Hackathon", "🏆"),
    PROJECT("Project", "🚀"),
    COMPETITION("Competition", "🥇"),
    CLUB("Club", "👥"),
    INTERNSHIP("Internship", "💼"),
    RESEARCH("Research", "🔬"),
    VOLUNTEERING("Volunteering", "🤝")
}

enum class OpportunityMode(val label: String) {
    ONLINE("Online"),
    OFFLINE("In-Person"),
    HYBRID("Hybrid")
}

data class Opportunity(
    val id: String,
    val title: String,
    val organizer: String,
    val category: OpportunityCategory,
    val date: String,
    val deadline: String,
    val mode: OpportunityMode,
    val location: String,
    val teamSizeMin: Int,
    val teamSizeMax: Int,
    val requiredSkills: List<String>,
    val preferredBranch: String,
    val eligibleYears: List<String>,
    val description: String,
    val currentMembersCount: Int = 1,
    val prizes: String = "",
    val isFeatured: Boolean = false,
    val isVerified: Boolean = true
)

data class MatchBreakdown(
    val totalScore: Int,
    val skillScore: Int, // Max 50
    val interestScore: Int, // Max 20
    val availabilityScore: Int, // Max 15
    val requirementScore: Int, // Max 15
    val complementarySkillsCovered: List<String>,
    val candidateSkillsMatched: List<SkillEntry>,
    val missingSkillsStillNeeded: List<String>,
    val highlights: String
)

data class TeammateMatch(
    val candidate: StudentProfile,
    val breakdown: MatchBreakdown
)

enum class ApplicationStatus(val title: String, val stepNumber: Int) {
    DRAFT("Drafting", 1),
    TEAM_FORMING("Team Forming", 2),
    READY_TO_APPLY("Ready to Submit", 3),
    SUBMITTED("Application Submitted", 4),
    UNDER_REVIEW("Under Review", 5),
    ACCEPTED("Accepted / Selected", 6)
}

data class Team(
    val id: String,
    val name: String,
    val opportunityId: String,
    val opportunityTitle: String,
    val organizer: String,
    val leaderId: String,
    val leaderName: String,
    val maxMembers: Int = 4,
    val status: ApplicationStatus = ApplicationStatus.TEAM_FORMING,
    val createdAt: Long = System.currentTimeMillis()
)

data class TeamMember(
    val id: Long = 0,
    val teamId: String,
    val studentId: String,
    val studentName: String,
    val role: String,
    val branch: String,
    val year: String,
    val keySkills: List<String>
)

data class TeamTask(
    val id: String,
    val teamId: String,
    val title: String,
    val assignedToName: String,
    val isCompleted: Boolean = false,
    val dueDate: String = ""
)

data class TeamChatMessage(
    val id: String,
    val teamId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystemMessage: Boolean = false
)

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}

data class TeamInvitation(
    val id: String,
    val teamId: String,
    val teamName: String,
    val opportunityTitle: String,
    val fromStudentId: String,
    val fromStudentName: String,
    val toStudentId: String,
    val note: String,
    val status: InvitationStatus = InvitationStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

data class TeamResource(
    val id: String,
    val teamId: String,
    val title: String,
    val url: String,
    val type: String // GitHub, Figma, Drive, Docs
)
