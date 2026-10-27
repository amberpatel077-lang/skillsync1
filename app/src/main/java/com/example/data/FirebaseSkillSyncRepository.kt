package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseSkillSyncRepository(
    val db: FirebaseFirestore,
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Secondary constructor to safely resolve the named database ID
    constructor(context: Context) : this(resolveFirestore(context))

    companion object {
        private const val TAG = "FirebaseRepository"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_TEAMS = "teams"
        const val COLLECTION_OPPORTUNITIES = "opportunities"
        const val COLLECTION_CLUB_APPLICATIONS = "club_applications"
        const val COLLECTION_VOLUNTEER_REGISTRATIONS = "volunteer_registrations"
        const val COLLECTION_INVITATIONS = "invitations"
        const val SUB_COLLECTION_MESSAGES = "messages"
        const val SUB_COLLECTION_TASKS = "tasks"

        private fun resolveFirestore(context: Context): FirebaseFirestore {
            val appContext = context.applicationContext ?: context
            if (com.google.firebase.FirebaseApp.getApps(appContext).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(appContext)
            }
            return FirebaseFirestore.getInstance(
                appContext.getString(R.string.firestore_database_id)
            )
        }
    }

    // ----------------------------------------------------
    // User / Student Profiles
    // ----------------------------------------------------
    fun observeStudents(): Flow<List<StudentProfile>> {
        return db.collection(COLLECTION_USERS)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("userId") ?: doc.id
                        val name = doc.getString("name") ?: ""
                        val email = doc.getString("email") ?: ""
                        val branch = doc.getString("branch") ?: "CSE"
                        val year = doc.getString("year") ?: "3rd Year"
                        val bio = doc.getString("bio") ?: ""
                        val college = doc.getString("college") ?: "SGSITS Indore"
                        val skillsRaw = doc.get("skills") as? List<Map<String, Any>> ?: emptyList()
                        val skills = skillsRaw.map {
                            SkillEntry(
                                name = it["name"] as? String ?: "",
                                level = try {
                                    SkillLevel.valueOf(it["level"] as? String ?: "INTERMEDIATE")
                                } catch (e: Exception) {
                                    SkillLevel.INTERMEDIATE
                                }
                            )
                        }
                        StudentProfile(
                            id = id,
                            name = name,
                            college = college,
                            branch = branch,
                            year = year,
                            email = email,
                            bio = bio,
                            skills = skills
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Error mapping user document ${doc.id}", e)
                        null
                    }
                }
            }
            .catch { error ->
                Log.w(TAG, "Error observing students from Firestore", error)
                emit(emptyList())
            }
    }

    suspend fun saveStudentProfile(student: StudentProfile) {
        val payload = mapOf(
            "userId" to student.id,
            "name" to student.name,
            "college" to student.college,
            "branch" to student.branch,
            "year" to student.year,
            "email" to student.email,
            "bio" to student.bio,
            "skills" to student.skills.map {
                mapOf("name" to it.name, "level" to it.level.name)
            },
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection(COLLECTION_USERS).document(student.id).set(payload).await()
    }

    // ----------------------------------------------------
    // Teams
    // ----------------------------------------------------
    fun observeTeams(): Flow<List<Team>> {
        return db.collection(COLLECTION_TEAMS)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("id") ?: doc.id
                        val name = doc.getString("name") ?: ""
                        val oppId = doc.getString("opportunityId") ?: ""
                        val oppTitle = doc.getString("opportunityTitle") ?: ""
                        val organizer = doc.getString("organizer") ?: ""
                        val leaderId = doc.getString("leaderId") ?: ""
                        val leaderName = doc.getString("leaderName") ?: ""
                        val maxMembers = (doc.getLong("maxMembers") ?: 4L).toInt()
                        Team(
                            id = id,
                            name = name,
                            opportunityId = oppId,
                            opportunityTitle = oppTitle,
                            organizer = organizer,
                            leaderId = leaderId,
                            leaderName = leaderName,
                            maxMembers = maxMembers
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Error mapping team document ${doc.id}", e)
                        null
                    }
                }
            }
            .catch { error ->
                Log.w(TAG, "Error observing teams from Firestore", error)
                emit(emptyList())
            }
    }

    suspend fun createTeam(team: Team, memberIds: List<String>) {
        val payload = mapOf(
            "id" to team.id,
            "name" to team.name,
            "opportunityId" to team.opportunityId,
            "opportunityTitle" to team.opportunityTitle,
            "organizer" to team.organizer,
            "leaderId" to team.leaderId,
            "leaderName" to team.leaderName,
            "memberIds" to memberIds,
            "maxMembers" to team.maxMembers,
            "status" to team.status.name,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection(COLLECTION_TEAMS).document(team.id).set(payload).await()
    }

    // ----------------------------------------------------
    // Team Chat Messages
    // ----------------------------------------------------
    fun observeTeamMessages(teamId: String): Flow<List<TeamChatMessage>> {
        return db.collection(COLLECTION_TEAMS).document(teamId)
            .collection(SUB_COLLECTION_MESSAGES)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        TeamChatMessage(
                            id = doc.getString("id") ?: doc.id,
                            teamId = teamId,
                            senderId = doc.getString("senderId") ?: "",
                            senderName = doc.getString("senderName") ?: "",
                            message = doc.getString("content") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                Log.w(TAG, "Error observing team messages", error)
                emit(emptyList())
            }
    }

    suspend fun sendTeamMessage(teamId: String, message: TeamChatMessage) {
        val payload = mapOf(
            "id" to message.id,
            "teamId" to teamId,
            "senderId" to message.senderId,
            "senderName" to message.senderName,
            "content" to message.message,
            "timestamp" to message.timestamp
        )
        db.collection(COLLECTION_TEAMS).document(teamId)
            .collection(SUB_COLLECTION_MESSAGES).document(message.id)
            .set(payload).await()
    }

    // ----------------------------------------------------
    // Team Tasks
    // ----------------------------------------------------
    fun observeTeamTasks(teamId: String): Flow<List<TeamTask>> {
        return db.collection(COLLECTION_TEAMS).document(teamId)
            .collection(SUB_COLLECTION_TASKS)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        TeamTask(
                            id = doc.getString("id") ?: doc.id,
                            teamId = teamId,
                            title = doc.getString("title") ?: "",
                            assignedToName = doc.getString("assignedToName") ?: "",
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            dueDate = doc.getString("dueDate") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                Log.w(TAG, "Error observing tasks", error)
                emit(emptyList())
            }
    }

    suspend fun saveTeamTask(teamId: String, task: TeamTask) {
        val payload = mapOf(
            "id" to task.id,
            "teamId" to teamId,
            "title" to task.title,
            "assignedToName" to task.assignedToName,
            "isCompleted" to task.isCompleted,
            "dueDate" to task.dueDate
        )
        db.collection(COLLECTION_TEAMS).document(teamId)
            .collection(SUB_COLLECTION_TASKS).document(task.id)
            .set(payload).await()
    }

    // ----------------------------------------------------
    // Opportunities
    // ----------------------------------------------------
    fun observeOpportunities(): Flow<List<Opportunity>> {
        return db.collection(COLLECTION_OPPORTUNITIES)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("id") ?: doc.id
                        val title = doc.getString("title") ?: ""
                        val organizer = doc.getString("organizer") ?: ""
                        val categoryStr = doc.getString("category") ?: "HACKATHON"
                        val category = try { OpportunityCategory.valueOf(categoryStr) } catch(e: Exception) { OpportunityCategory.HACKATHON }
                        val date = doc.getString("date") ?: ""
                        val deadline = doc.getString("deadline") ?: ""
                        val modeStr = doc.getString("mode") ?: "ONLINE"
                        val mode = try { OpportunityMode.valueOf(modeStr) } catch(e: Exception) { OpportunityMode.ONLINE }
                        val location = doc.getString("location") ?: ""
                        val teamSizeMin = (doc.getLong("teamSizeMin") ?: 2L).toInt()
                        val teamSizeMax = (doc.getLong("teamSizeMax") ?: 4L).toInt()
                        @Suppress("UNCHECKED_CAST")
                        val requiredSkills = (doc.get("requiredSkills") as? List<String>) ?: emptyList()
                        val description = doc.getString("description") ?: ""
                        val prizes = doc.getString("prizes") ?: ""

                        Opportunity(
                            id = id,
                            title = title,
                            organizer = organizer,
                            category = category,
                            date = date,
                            deadline = deadline,
                            mode = mode,
                            location = location,
                            teamSizeMin = teamSizeMin,
                            teamSizeMax = teamSizeMax,
                            requiredSkills = requiredSkills,
                            preferredBranch = "All",
                            eligibleYears = listOf("1st", "2nd", "3rd", "4th"),
                            description = description,
                            prizes = prizes
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { error ->
                Log.w(TAG, "Error observing opportunities", error)
                emit(emptyList())
            }
    }

    suspend fun saveOpportunity(opp: Opportunity) {
        val payload = mapOf(
            "id" to opp.id,
            "title" to opp.title,
            "organizer" to opp.organizer,
            "category" to opp.category.name,
            "date" to opp.date,
            "deadline" to opp.deadline,
            "mode" to opp.mode.name,
            "location" to opp.location,
            "teamSizeMin" to opp.teamSizeMin,
            "teamSizeMax" to opp.teamSizeMax,
            "requiredSkills" to opp.requiredSkills,
            "description" to opp.description,
            "prizes" to opp.prizes,
            "creatorId" to (auth.currentUser?.uid ?: "")
        )
        db.collection(COLLECTION_OPPORTUNITIES).document(opp.id).set(payload).await()
    }

    // ----------------------------------------------------
    // Club Applications & Volunteering Registrations
    // ----------------------------------------------------
    suspend fun submitClubApplication(app: ClubApplication) {
        val payload = mapOf(
            "id" to app.id,
            "applicantId" to (auth.currentUser?.uid ?: app.studentId),
            "clubName" to app.clubName,
            "roleApplied" to app.roleApplied,
            "sop" to app.sop,
            "status" to app.status.name,
            "appliedAt" to FieldValue.serverTimestamp()
        )
        db.collection(COLLECTION_CLUB_APPLICATIONS).document(app.id).set(payload).await()
    }

    suspend fun submitVolunteerRegistration(reg: VolunteerRegistration) {
        val payload = mapOf(
            "id" to reg.id,
            "volunteerId" to (auth.currentUser?.uid ?: reg.studentId),
            "opportunityId" to reg.volunteerOpportunityId,
            "eventTitle" to reg.eventTitle,
            "preferredRole" to reg.preferredRole,
            "hoursAvailable" to reg.hoursAvailable,
            "motivation" to reg.motivation,
            "status" to reg.status.name,
            "registeredAt" to FieldValue.serverTimestamp()
        )
        db.collection(COLLECTION_VOLUNTEER_REGISTRATIONS).document(reg.id).set(payload).await()
    }

    suspend fun sendInvitation(inv: TeamInvitation) {
        val payload = mapOf(
            "id" to inv.id,
            "teamId" to inv.teamId,
            "senderId" to (auth.currentUser?.uid ?: inv.fromStudentId),
            "recipientId" to inv.toStudentId,
            "note" to inv.note,
            "status" to inv.status.name,
            "sentAt" to FieldValue.serverTimestamp()
        )
        db.collection(COLLECTION_INVITATIONS).document(inv.id).set(payload).await()
    }
}
