package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class SkillSyncRepository(private val dao: SkillSyncDao) {

    fun getAllStudents(): Flow<List<StudentProfile>> {
        return dao.getAllStudents().map { entities -> entities.map { it.toDomain() } }
    }

    fun getStudent(id: String): Flow<StudentProfile?> {
        return dao.getStudentById(id).map { it?.toDomain() }
    }

    suspend fun updateStudent(student: StudentProfile) {
        dao.updateStudent(student.toEntity())
    }

    suspend fun getStudentByEmail(email: String): StudentProfile? {
        return dao.getStudentByEmail(email)?.toDomain()
    }

    suspend fun registerStudent(student: StudentProfile) {
        dao.insertStudent(student.toEntity())
    }

    fun getAllOpportunities(): Flow<List<Opportunity>> {
        return dao.getAllOpportunities().map { entities -> entities.map { it.toDomain() } }
    }

    fun getOpportunity(id: String): Flow<Opportunity?> {
        return dao.getOpportunityById(id).map { it?.toDomain() }
    }

    fun getAllTeams(): Flow<List<Team>> {
        return dao.getAllTeams().map { entities -> entities.map { it.toDomain() } }
    }

    fun getTeam(id: String): Flow<Team?> {
        return dao.getTeamById(id).map { it?.toDomain() }
    }

    fun getTeamMembers(teamId: String): Flow<List<TeamMember>> {
        return dao.getTeamMembers(teamId).map { entities -> entities.map { it.toDomain() } }
    }

    fun getTeamTasks(teamId: String): Flow<List<TeamTask>> {
        return dao.getTeamTasks(teamId).map { entities -> entities.map { it.toDomain() } }
    }

    fun getTeamMessages(teamId: String): Flow<List<TeamChatMessage>> {
        return dao.getTeamChatMessages(teamId).map { entities -> entities.map { it.toDomain() } }
    }

    fun getInvitationsForStudent(studentId: String): Flow<List<TeamInvitation>> {
        return dao.getInvitationsForStudent(studentId).map { entities -> entities.map { it.toDomain() } }
    }

    fun getTeamResources(teamId: String): Flow<List<TeamResource>> {
        return dao.getTeamResources(teamId).map { entities -> entities.map { it.toDomain() } }
    }

    suspend fun createTeam(
        name: String,
        opportunity: Opportunity,
        leader: StudentProfile
    ): String {
        val teamId = "team_" + UUID.randomUUID().toString().substring(0, 8)
        val teamEntity = TeamEntity(
            id = teamId,
            name = name,
            opportunityId = opportunity.id,
            opportunityTitle = opportunity.title,
            organizer = opportunity.organizer,
            leaderId = leader.id,
            leaderName = leader.name,
            maxMembers = opportunity.teamSizeMax,
            status = ApplicationStatus.TEAM_FORMING.name,
            createdAt = System.currentTimeMillis()
        )
        dao.insertTeam(teamEntity)

        // Add leader as first member
        val leaderMember = TeamMemberEntity(
            teamId = teamId,
            studentId = leader.id,
            studentName = leader.name,
            role = "Team Leader",
            branch = leader.branch,
            year = leader.year,
            keySkillsRaw = leader.skills.take(3).joinToString(";") { it.name }
        )
        dao.insertTeamMember(leaderMember)

        // Add initial system message
        dao.insertChatMessage(
            TeamChatMessageEntity(
                id = "msg_" + UUID.randomUUID().toString().substring(0, 8),
                teamId = teamId,
                senderId = "system",
                senderName = "SkillSync System",
                message = "Welcome to $name! Start by inviting complementary teammates and discussing your project plan.",
                timestamp = System.currentTimeMillis(),
                isSystemMessage = true
            )
        )

        // Add initial default task
        dao.insertTask(
            TeamTaskEntity(
                id = "task_" + UUID.randomUUID().toString().substring(0, 8),
                teamId = teamId,
                title = "Complete team roster (${opportunity.teamSizeMin}-${opportunity.teamSizeMax} members)",
                assignedToName = leader.name,
                isCompleted = false,
                dueDate = opportunity.deadline
            )
        )

        return teamId
    }

    suspend fun addMemberToTeam(teamId: String, student: StudentProfile, role: String) {
        val member = TeamMemberEntity(
            teamId = teamId,
            studentId = student.id,
            studentName = student.name,
            role = role,
            branch = student.branch,
            year = student.year,
            keySkillsRaw = student.skills.take(3).joinToString(";") { it.name }
        )
        dao.insertTeamMember(member)

        dao.insertChatMessage(
            TeamChatMessageEntity(
                id = "msg_" + UUID.randomUUID().toString().substring(0, 8),
                teamId = teamId,
                senderId = "system",
                senderName = "SkillSync System",
                message = "${student.name} joined the team as $role! 🎉",
                timestamp = System.currentTimeMillis(),
                isSystemMessage = true
            )
        )
    }

    suspend fun sendInvitation(
        teamId: String,
        teamName: String,
        opportunityTitle: String,
        fromStudent: StudentProfile,
        toStudentId: String,
        toStudentName: String = "",
        note: String
    ) {
        val inv = TeamInvitationEntity(
            id = "inv_" + UUID.randomUUID().toString().substring(0, 8),
            teamId = teamId,
            teamName = teamName,
            opportunityTitle = opportunityTitle,
            fromStudentId = fromStudent.id,
            fromStudentName = fromStudent.name,
            toStudentId = toStudentId,
            toStudentName = toStudentName,
            note = note,
            status = InvitationStatus.PENDING.name,
            timestamp = System.currentTimeMillis()
        )
        dao.insertInvitation(inv)
    }

    suspend fun respondToInvitation(
        invitation: TeamInvitation,
        accepted: Boolean,
        currentStudent: StudentProfile
    ) {
        val newStatus = if (accepted) InvitationStatus.ACCEPTED else InvitationStatus.DECLINED
        val updated = TeamInvitationEntity(
            id = invitation.id,
            teamId = invitation.teamId,
            teamName = invitation.teamName,
            opportunityTitle = invitation.opportunityTitle,
            fromStudentId = invitation.fromStudentId,
            fromStudentName = invitation.fromStudentName,
            toStudentId = invitation.toStudentId,
            toStudentName = invitation.toStudentName,
            note = invitation.note,
            status = newStatus.name,
            timestamp = invitation.timestamp
        )
        dao.updateInvitation(updated)

        if (accepted) {
            addMemberToTeam(invitation.teamId, currentStudent, "Member")
        }
    }

    suspend fun sendChatMessage(teamId: String, sender: StudentProfile, text: String) {
        val msg = TeamChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().substring(0, 8),
            teamId = teamId,
            senderId = sender.id,
            senderName = sender.name,
            message = text.trim(),
            timestamp = System.currentTimeMillis(),
            isSystemMessage = false
        )
        dao.insertChatMessage(msg)
    }

    suspend fun addTask(teamId: String, title: String, assignedToName: String, dueDate: String) {
        val task = TeamTaskEntity(
            id = "task_" + UUID.randomUUID().toString().substring(0, 8),
            teamId = teamId,
            title = title.trim(),
            assignedToName = assignedToName.ifBlank { "Unassigned" },
            isCompleted = false,
            dueDate = dueDate.ifBlank { "Pending" }
        )
        dao.insertTask(task)
    }

    suspend fun toggleTask(task: TeamTask) {
        dao.updateTask(
            TeamTaskEntity(
                id = task.id,
                teamId = task.teamId,
                title = task.title,
                assignedToName = task.assignedToName,
                isCompleted = !task.isCompleted,
                dueDate = task.dueDate
            )
        )
    }

    suspend fun updateTeamStatus(team: Team, newStatus: ApplicationStatus) {
        dao.updateTeam(
            TeamEntity(
                id = team.id,
                name = team.name,
                opportunityId = team.opportunityId,
                opportunityTitle = team.opportunityTitle,
                organizer = team.organizer,
                leaderId = team.leaderId,
                leaderName = team.leaderName,
                maxMembers = team.maxMembers,
                status = newStatus.name,
                createdAt = team.createdAt
            )
        )
    }

    suspend fun createOpportunity(opportunity: Opportunity) {
        dao.insertOpportunity(opportunity.toEntity())
    }

    suspend fun addResource(teamId: String, title: String, url: String, type: String) {
        val res = TeamResourceEntity(
            id = "res_" + UUID.randomUUID().toString().substring(0, 8),
            teamId = teamId,
            title = title.trim(),
            url = url.trim(),
            type = type
        )
        dao.insertResource(res)
    }

    suspend fun deleteTask(taskId: String) {
        dao.deleteTask(taskId)
    }
}
