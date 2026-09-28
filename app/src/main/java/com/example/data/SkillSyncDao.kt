package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillSyncDao {

    // Students
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentDirect(studentId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getStudentByEmail(email: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    // Opportunities
    @Query("SELECT * FROM opportunities")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE id = :oppId")
    fun getOpportunityById(oppId: String): Flow<OpportunityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opportunity: OpportunityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunities(opportunities: List<OpportunityEntity>)

    // Teams
    @Query("SELECT * FROM teams ORDER BY createdAt DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeamById(teamId: String): Flow<TeamEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    // Team Members
    @Query("SELECT * FROM team_members WHERE teamId = :teamId")
    fun getTeamMembers(teamId: String): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TeamMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMembers(members: List<TeamMemberEntity>)

    @Query("DELETE FROM team_members WHERE teamId = :teamId AND studentId = :studentId")
    suspend fun removeTeamMember(teamId: String, studentId: String)

    // Team Tasks
    @Query("SELECT * FROM team_tasks WHERE teamId = :teamId")
    fun getTeamTasks(teamId: String): Flow<List<TeamTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TeamTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TeamTaskEntity>)

    @Update
    suspend fun updateTask(task: TeamTaskEntity)

    @Query("DELETE FROM team_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    // Team Chat Messages
    @Query("SELECT * FROM team_chat_messages WHERE teamId = :teamId ORDER BY timestamp ASC")
    fun getTeamChatMessages(teamId: String): Flow<List<TeamChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: TeamChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<TeamChatMessageEntity>)

    // Team Invitations
    @Query("SELECT * FROM team_invitations WHERE toStudentId = :studentId OR fromStudentId = :studentId ORDER BY timestamp DESC")
    fun getInvitationsForStudent(studentId: String): Flow<List<TeamInvitationEntity>>

    @Query("SELECT * FROM team_invitations WHERE fromStudentId = :studentId ORDER BY timestamp DESC")
    fun getSentInvitationsByStudent(studentId: String): Flow<List<TeamInvitationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: TeamInvitationEntity)

    @Update
    suspend fun updateInvitation(invitation: TeamInvitationEntity)

    // Team Resources
    @Query("SELECT * FROM team_resources WHERE teamId = :teamId")
    fun getTeamResources(teamId: String): Flow<List<TeamResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: TeamResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<TeamResourceEntity>)
}
