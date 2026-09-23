package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.*

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val college: String,
    val branch: String,
    val year: String,
    val email: String,
    val password: String = "password123",
    val isEmailVerified: Boolean,
    val isBranchVerified: Boolean,
    val skillsRaw: String, // format: "Python:ADVANCED;ML:INTERMEDIATE"
    val interestsRaw: String, // format: "Hackathons;AI;Web"
    val availabilityWeekdays: Boolean,
    val availabilityWeekends: Boolean,
    val availabilityEvenings: Boolean,
    val availabilityFullTime: Boolean,
    val availableUntil: String,
    val preferredTypesRaw: String,
    val lookingFor: String,
    val contactPreference: String,
    val bio: String,
    val projectsRaw: String, // format: "title::desc::stack::role::url###..."
    val isPublic: Boolean
)

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val organizer: String,
    val category: String,
    val date: String,
    val deadline: String,
    val mode: String,
    val location: String,
    val teamSizeMin: Int,
    val teamSizeMax: Int,
    val requiredSkillsRaw: String,
    val preferredBranch: String,
    val eligibleYearsRaw: String,
    val description: String,
    val currentMembersCount: Int,
    val prizes: String,
    val isFeatured: Boolean,
    val isVerified: Boolean
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val opportunityId: String,
    val opportunityTitle: String,
    val organizer: String,
    val leaderId: String,
    val leaderName: String,
    val maxMembers: Int,
    val status: String,
    val createdAt: Long
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teamId: String,
    val studentId: String,
    val studentName: String,
    val role: String,
    val branch: String,
    val year: String,
    val keySkillsRaw: String
)

@Entity(tableName = "team_tasks")
data class TeamTaskEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val title: String,
    val assignedToName: String,
    val isCompleted: Boolean,
    val dueDate: String
)

@Entity(tableName = "team_chat_messages")
data class TeamChatMessageEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long,
    val isSystemMessage: Boolean
)

@Entity(tableName = "team_invitations")
data class TeamInvitationEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val teamName: String,
    val opportunityTitle: String,
    val fromStudentId: String,
    val fromStudentName: String,
    val toStudentId: String,
    val note: String,
    val status: String, // PENDING, ACCEPTED, DECLINED
    val timestamp: Long
)

@Entity(tableName = "team_resources")
data class TeamResourceEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val title: String,
    val url: String,
    val type: String
)

// Mapping extensions
fun StudentEntity.toDomain(): StudentProfile {
    val skills = if (skillsRaw.isBlank()) emptyList() else {
        skillsRaw.split(";").filter { it.isNotBlank() }.map { item ->
            val parts = item.split(":")
            val name = parts.getOrNull(0) ?: "Skill"
            val level = parts.getOrNull(1)?.let {
                try { SkillLevel.valueOf(it) } catch (e: Exception) { SkillLevel.INTERMEDIATE }
            } ?: SkillLevel.INTERMEDIATE
            SkillEntry(name, level)
        }
    }
    val interests = if (interestsRaw.isBlank()) emptyList() else interestsRaw.split(";").filter { it.isNotBlank() }
    val preferredTypes = if (preferredTypesRaw.isBlank()) emptyList() else preferredTypesRaw.split(";").filter { it.isNotBlank() }

    val projects = if (projectsRaw.isBlank()) emptyList() else {
        projectsRaw.split("###").filter { it.isNotBlank() }.map { projStr ->
            val parts = projStr.split("::")
            ProjectExperience(
                title = parts.getOrNull(0) ?: "Project",
                description = parts.getOrNull(1) ?: "",
                techStack = parts.getOrNull(2) ?: "",
                role = parts.getOrNull(3) ?: "",
                githubOrDemoUrl = parts.getOrNull(4) ?: ""
            )
        }
    }

    val lookingForEnum = try {
        LookingFor.valueOf(lookingFor)
    } catch (e: Exception) {
        LookingFor.BOTH
    }

    return StudentProfile(
        id = id,
        name = name,
        college = college,
        branch = branch,
        year = year,
        email = email,
        password = password,
        isEmailVerified = isEmailVerified,
        isBranchVerified = isBranchVerified,
        skills = skills,
        interests = interests,
        availability = Availability(
            weekdays = availabilityWeekdays,
            weekends = availabilityWeekends,
            evenings = availabilityEvenings,
            fullTimeEvent = availabilityFullTime,
            availableUntil = availableUntil
        ),
        preferredOpportunityTypes = preferredTypes,
        lookingFor = lookingForEnum,
        contactPreference = contactPreference,
        bio = bio,
        projects = projects,
        isPublic = isPublic
    )
}

fun StudentProfile.toEntity(): StudentEntity {
    val skillsRaw = skills.joinToString(";") { "${it.name}:${it.level.name}" }
    val interestsRaw = interests.joinToString(";")
    val preferredTypesRaw = preferredOpportunityTypes.joinToString(";")
    val projectsRaw = projects.joinToString("###") {
        "${it.title}::${it.description}::${it.techStack}::${it.role}::${it.githubOrDemoUrl}"
    }

    return StudentEntity(
        id = id,
        name = name,
        college = college,
        branch = branch,
        year = year,
        email = email,
        password = password,
        isEmailVerified = isEmailVerified,
        isBranchVerified = isBranchVerified,
        skillsRaw = skillsRaw,
        interestsRaw = interestsRaw,
        availabilityWeekdays = availability.weekdays,
        availabilityWeekends = availability.weekends,
        availabilityEvenings = availability.evenings,
        availabilityFullTime = availability.fullTimeEvent,
        availableUntil = availability.availableUntil,
        preferredTypesRaw = preferredTypesRaw,
        lookingFor = lookingFor.name,
        contactPreference = contactPreference,
        bio = bio,
        projectsRaw = projectsRaw,
        isPublic = isPublic
    )
}

fun OpportunityEntity.toDomain(): Opportunity {
    val reqSkills = if (requiredSkillsRaw.isBlank()) emptyList() else requiredSkillsRaw.split(";").map { it.trim() }
    val years = if (eligibleYearsRaw.isBlank()) emptyList() else eligibleYearsRaw.split(";").map { it.trim() }
    val cat = try { OpportunityCategory.valueOf(category) } catch (e: Exception) { OpportunityCategory.HACKATHON }
    val modeEnum = try { OpportunityMode.valueOf(mode) } catch (e: Exception) { OpportunityMode.ONLINE }

    return Opportunity(
        id = id,
        title = title,
        organizer = organizer,
        category = cat,
        date = date,
        deadline = deadline,
        mode = modeEnum,
        location = location,
        teamSizeMin = teamSizeMin,
        teamSizeMax = teamSizeMax,
        requiredSkills = reqSkills,
        preferredBranch = preferredBranch,
        eligibleYears = years,
        description = description,
        currentMembersCount = currentMembersCount,
        prizes = prizes,
        isFeatured = isFeatured,
        isVerified = isVerified
    )
}

fun Opportunity.toEntity(): OpportunityEntity {
    return OpportunityEntity(
        id = id,
        title = title,
        organizer = organizer,
        category = category.name,
        date = date,
        deadline = deadline,
        mode = mode.name,
        location = location,
        teamSizeMin = teamSizeMin,
        teamSizeMax = teamSizeMax,
        requiredSkillsRaw = requiredSkills.joinToString(";"),
        preferredBranch = preferredBranch,
        eligibleYearsRaw = eligibleYears.joinToString(";"),
        description = description,
        currentMembersCount = currentMembersCount,
        prizes = prizes,
        isFeatured = isFeatured,
        isVerified = isVerified
    )
}

fun TeamEntity.toDomain(): Team {
    val statusEnum = try { ApplicationStatus.valueOf(status) } catch (e: Exception) { ApplicationStatus.TEAM_FORMING }
    return Team(
        id = id,
        name = name,
        opportunityId = opportunityId,
        opportunityTitle = opportunityTitle,
        organizer = organizer,
        leaderId = leaderId,
        leaderName = leaderName,
        maxMembers = maxMembers,
        status = statusEnum,
        createdAt = createdAt
    )
}

fun TeamMemberEntity.toDomain(): TeamMember {
    val skills = if (keySkillsRaw.isBlank()) emptyList() else keySkillsRaw.split(";").map { it.trim() }
    return TeamMember(
        id = id,
        teamId = teamId,
        studentId = studentId,
        studentName = studentName,
        role = role,
        branch = branch,
        year = year,
        keySkills = skills
    )
}

fun TeamTaskEntity.toDomain(): TeamTask {
    return TeamTask(
        id = id,
        teamId = teamId,
        title = title,
        assignedToName = assignedToName,
        isCompleted = isCompleted,
        dueDate = dueDate
    )
}

fun TeamChatMessageEntity.toDomain(): TeamChatMessage {
    return TeamChatMessage(
        id = id,
        teamId = teamId,
        senderId = senderId,
        senderName = senderName,
        message = message,
        timestamp = timestamp,
        isSystemMessage = isSystemMessage
    )
}

fun TeamInvitationEntity.toDomain(): TeamInvitation {
    val statusEnum = try { InvitationStatus.valueOf(status) } catch (e: Exception) { InvitationStatus.PENDING }
    return TeamInvitation(
        id = id,
        teamId = teamId,
        teamName = teamName,
        opportunityTitle = opportunityTitle,
        fromStudentId = fromStudentId,
        fromStudentName = fromStudentName,
        toStudentId = toStudentId,
        note = note,
        status = statusEnum,
        timestamp = timestamp
    )
}

fun TeamResourceEntity.toDomain(): TeamResource {
    return TeamResource(
        id = id,
        teamId = teamId,
        title = title,
        url = url,
        type = type
    )
}
