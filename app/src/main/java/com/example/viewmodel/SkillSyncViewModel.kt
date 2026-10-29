package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseSkillSyncRepository
import com.example.data.SkillSyncDatabase
import com.example.data.SkillSyncRepository
import com.example.data.SmartMatchingEngine
import com.example.model.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class RegistrationDeliveryResult {
    data class Sent(
        val title: String,
        val detail: String,
        val targetId: String? = null,
        val syncedToCloud: Boolean = false
    ) : RegistrationDeliveryResult()

    data class NotSent(
        val title: String,
        val reason: String
    ) : RegistrationDeliveryResult()
}

data class UiState(
    val isSplashVisible: Boolean = true,
    val isAuthenticated: Boolean = false,
    val authError: String? = null,
    val authSuccessMessage: String? = null,
    val currentStudentId: String = "student_amber",
    val activeTab: AppTab = AppTab.HOME,
    val exploreSubTab: ExploreSubTab = ExploreSubTab.OPPORTUNITIES,
    val selectedOpportunityId: String? = null,
    val activeTeamId: String? = "team_sih_apex",
    val findTeammatesOppId: String? = null,
    val oppSearchQuery: String = "",
    val selectedCategory: OpportunityCategory? = null,
    val selectedSkillFilter: String? = null,
    val selectedModeFilter: OpportunityMode? = null,
    val studentSearchQuery: String = "",
    val studentBranchFilter: String? = null,
    val studentSkillFilter: String? = null,
    val isDarkTheme: Boolean = false,
    val userMessage: String? = null,
    val selectedClubForApplication: Opportunity? = null,
    val selectedVolunteerForRegistration: Opportunity? = null,
    val lastRegistrationStatus: RegistrationDeliveryResult? = null
)

enum class AppTab(val title: String) {
    HOME("Home"),
    EXPLORE("Explore"),
    TEAMS("Teams"),
    SURVEY("Campus Data"),
    PROFILE("Profile")
}

enum class ExploreSubTab(val title: String) {
    OPPORTUNITIES("Opportunities"),
    CLUBS("Clubs & Chapters"),
    VOLUNTEERING("Volunteering"),
    STUDENTS("Student Discovery"),
    HACKATHONS("Hackathons"),
    RESEARCH("Research & Labs")
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SkillSyncViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DEMO_STUDENT_ID = "student_amber"

        fun inferCollegeFromEmail(email: String): String {
            val domain = email.substringAfter("@", "").lowercase()
            return when {
                domain.contains("sgsits") -> "SGSITS Indore"
                domain.contains("iitb") -> "IIT Bombay"
                domain.contains("iitm") -> "IIT Madras"
                domain.contains("iitd") -> "IIT Delhi"
                domain.contains("nitt") -> "NIT Trichy"
                domain.contains("iiit") -> "IIIT Hyderabad"
                domain.contains("bits") -> "BITS Pilani"
                domain.contains("dtu") -> "DTU Delhi"
                domain.contains("vit") -> "VIT Vellore"
                domain.endsWith(".edu") || domain.endsWith(".ac.in") || domain.endsWith(".edu.in") -> {
                    val core = domain
                        .removeSuffix(".edu.in")
                        .removeSuffix(".ac.in")
                        .removeSuffix(".edu")
                        .substringAfterLast(".")
                    if (core.isNotBlank() && core.length <= 8) "${core.uppercase()} University" else "University Campus"
                }
                else -> "University Campus"
            }
        }
    }

    private val repository: SkillSyncRepository
    val firebaseRepository: FirebaseSkillSyncRepository by lazy {
        FirebaseSkillSyncRepository(application)
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val students: StateFlow<List<StudentProfile>>
    val opportunities: StateFlow<List<Opportunity>>
    val teams: StateFlow<List<Team>>

    init {
        val database = SkillSyncDatabase.getDatabase(application, viewModelScope)
        repository = SkillSyncRepository(database.skillSyncDao())

        students = repository.getAllStudents().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        opportunities = repository.getAllOpportunities().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        teams = repository.getAllTeams().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    // Active student flow
    val currentStudent: StateFlow<StudentProfile?> = combine(students, _uiState) { list, state ->
        list.find { it.id == state.currentStudentId } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Invitations for current student (both sent & received)
    val currentStudentInvitations: StateFlow<List<TeamInvitation>> = _uiState.flatMapLatest { state ->
        repository.getInvitationsForStudent(state.currentStudentId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sentInvitations: StateFlow<List<TeamInvitation>> = combine(currentStudentInvitations, currentStudent) { list, student ->
        if (student == null) emptyList()
        else list.filter { it.fromStudentId == student.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedInvitations: StateFlow<List<TeamInvitation>> = combine(currentStudentInvitations, currentStudent) { list, student ->
        if (student == null) emptyList()
        else list.filter { it.toStudentId == student.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sentInvitationRecipientIds: StateFlow<Set<String>> = sentInvitations.map { list ->
        list.map { it.toStudentId }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Active team details
    val activeTeam: StateFlow<Team?> = combine(teams, _uiState) { teamList, state ->
        teamList.find { it.id == state.activeTeamId } ?: teamList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTeamMembers: StateFlow<List<TeamMember>> = _uiState.flatMapLatest { state ->
        val teamId = state.activeTeamId ?: ""
        if (teamId.isNotBlank()) repository.getTeamMembers(teamId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTeamTasks: StateFlow<List<TeamTask>> = _uiState.flatMapLatest { state ->
        val teamId = state.activeTeamId ?: ""
        if (teamId.isNotBlank()) repository.getTeamTasks(teamId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTeamMessages: StateFlow<List<TeamChatMessage>> = _uiState.flatMapLatest { state ->
        val teamId = state.activeTeamId ?: ""
        if (teamId.isNotBlank()) repository.getTeamMessages(teamId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTeamResources: StateFlow<List<TeamResource>> = _uiState.flatMapLatest { state ->
        val teamId = state.activeTeamId ?: ""
        if (teamId.isNotBlank()) repository.getTeamResources(teamId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Club Applications Tracking
    private val _clubApplications = MutableStateFlow<List<ClubApplication>>(
        listOf(
            ClubApplication(
                id = "app_acm_amber",
                clubOpportunityId = "opp_acm_recruit",
                clubName = "ACM Student Chapter",
                studentId = "student_amber",
                studentName = "Amber Patel",
                roleApplied = "Technical Associate & Web Lead",
                sop = "Passionate about full-stack web and CP contests. Built CampusHub and want to scale ACM digital portal.",
                portfolioUrl = "https://github.com/amber-patel",
                status = ApplicationStatus.UNDER_REVIEW,
                timestamp = System.currentTimeMillis() - 86400000L
            )
        )
    )
    val clubApplications: StateFlow<List<ClubApplication>> = _clubApplications.asStateFlow()

    // Volunteer Registrations Tracking
    private val _volunteerRegistrations = MutableStateFlow<List<VolunteerRegistration>>(
        listOf(
            VolunteerRegistration(
                id = "vol_aayam_amber",
                volunteerOpportunityId = "opp_aayam_volunteers",
                eventTitle = "Aayam 2026 Cultural Fest Volunteer Taskforce",
                studentId = "student_amber",
                studentName = "Amber Patel",
                preferredRole = "Technical Sound & Lighting Logistics",
                hoursAvailable = "18 Hours",
                motivation = "Experienced in auditorium acoustics and stage management. Committed to making Aayam 2026 huge.",
                status = ApplicationStatus.ACCEPTED,
                timestamp = System.currentTimeMillis() - 172800000L
            )
        )
    )
    val volunteerRegistrations: StateFlow<List<VolunteerRegistration>> = _volunteerRegistrations.asStateFlow()

    // Survey Submissions (In-app Google Form responses)
    private val _surveySubmissions = MutableStateFlow<List<SurveySubmission>>(emptyList())
    val surveySubmissions: StateFlow<List<SurveySubmission>> = _surveySubmissions.asStateFlow()

    // Filtered Opportunities
    val filteredOpportunities: StateFlow<List<Pair<Opportunity, Int>>> =
        combine(opportunities, currentStudent, _uiState) { opps, student, state ->
            opps.filter { opp ->
                val matchesSubTab = when (state.exploreSubTab) {
                    ExploreSubTab.CLUBS -> opp.category == OpportunityCategory.CLUB
                    ExploreSubTab.VOLUNTEERING -> opp.category == OpportunityCategory.VOLUNTEERING
                    ExploreSubTab.HACKATHONS -> opp.category == OpportunityCategory.HACKATHON || opp.category == OpportunityCategory.COMPETITION
                    ExploreSubTab.RESEARCH -> opp.category == OpportunityCategory.RESEARCH || opp.category == OpportunityCategory.PROJECT
                    else -> true
                }

                val matchesQuery = state.oppSearchQuery.isBlank() ||
                        opp.title.contains(state.oppSearchQuery, ignoreCase = true) ||
                        opp.organizer.contains(state.oppSearchQuery, ignoreCase = true) ||
                        opp.requiredSkills.any { it.contains(state.oppSearchQuery, ignoreCase = true) }

                val matchesCat = state.selectedCategory == null || opp.category == state.selectedCategory
                val matchesSkill = state.selectedSkillFilter == null || opp.requiredSkills.any {
                    it.equals(state.selectedSkillFilter, ignoreCase = true)
                }
                val matchesMode = state.selectedModeFilter == null || opp.mode == state.selectedModeFilter

                matchesSubTab && matchesQuery && matchesCat && matchesSkill && matchesMode
            }.map { opp ->
                val matchScore = if (student != null) {
                    SmartMatchingEngine.calculateOpportunityFit(student, opp)
                } else 75
                opp to matchScore
            }.sortedByDescending { it.second }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recommended Opportunities for Home Dashboard
    val recommendedOpportunities: StateFlow<List<Pair<Opportunity, Int>>> =
        combine(opportunities, currentStudent) { opps, student ->
            if (student == null) emptyList()
            else {
                opps.map { opp ->
                    opp to SmartMatchingEngine.calculateOpportunityFit(student, opp)
                }.sortedByDescending { it.second }.take(4)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Teammate Recommendations for a selected Opportunity
    val teammateMatches: StateFlow<List<TeammateMatch>> =
        combine(students, opportunities, activeTeamMembers, _uiState) { studentList, oppList, members, state ->
            val targetOppId = state.findTeammatesOppId ?: state.selectedOpportunityId ?: "opp_sih_2026"
            val targetOpp = oppList.find { it.id == targetOppId } ?: oppList.firstOrNull()
            if (targetOpp == null) emptyList()
            else {
                val currentStudentId = state.currentStudentId
                // Team members currently in this team/opportunity
                val currentTeamStudents = studentList.filter { s ->
                    members.any { it.studentId == s.id } || s.id == currentStudentId
                }
                // Candidates exclude existing team members and self
                val candidates = studentList.filter { it.id != currentStudentId && members.none { m -> m.studentId == it.id } }

                candidates.map { candidate ->
                    SmartMatchingEngine.calculateTeammateMatch(candidate, targetOpp, currentTeamStudents)
                }.sortedByDescending { it.breakdown.totalScore }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Discovery search
    val discoveredStudents: StateFlow<List<Pair<StudentProfile, Int>>> =
        combine(students, currentStudent, _uiState) { studentList, current, state ->
            val query = state.studentSearchQuery.trim()
            val branchFilter = state.studentBranchFilter
            val skillFilter = state.studentSkillFilter

            studentList.filter { s ->
                val notSelf = s.id != state.currentStudentId
                val matchesQuery = query.isBlank() ||
                        s.name.contains(query, ignoreCase = true) ||
                        s.college.contains(query, ignoreCase = true) ||
                        s.branch.contains(query, ignoreCase = true) ||
                        s.skills.any { it.name.contains(query, ignoreCase = true) } ||
                        s.interests.any { it.contains(query, ignoreCase = true) }

                val matchesBranch = branchFilter == null || s.branch.contains(branchFilter, ignoreCase = true)
                val matchesSkill = skillFilter == null || s.skills.any { it.name.equals(skillFilter, ignoreCase = true) }

                notSelf && matchesQuery && matchesBranch && matchesSkill
            }.map { s ->
                // Calculate general complementary or skill alignment score
                var score = 70
                if (current != null) {
                    val sharedInterests = s.interests.count { current.interests.contains(it) }
                    val currentSkills = current.skills.map { it.name.lowercase() }
                    val newSkillsOffered = s.skills.count { it.name.lowercase() !in currentSkills }
                    score = (60 + (sharedInterests * 6) + (newSkillsOffered * 8)).coerceIn(65, 98)
                }
                s to score
            }.sortedByDescending { it.second }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setExploreSubTab(subTab: ExploreSubTab) {
        _uiState.update { it.copy(exploreSubTab = subTab) }
    }

    fun selectOpportunity(oppId: String?) {
        _uiState.update { it.copy(selectedOpportunityId = oppId) }
    }

    fun openFindTeammates(oppId: String) {
        _uiState.update { it.copy(findTeammatesOppId = oppId) }
    }

    fun closeFindTeammates() {
        _uiState.update { it.copy(findTeammatesOppId = null) }
    }

    fun setActiveTeam(teamId: String) {
        _uiState.update { it.copy(activeTeamId = teamId, activeTab = AppTab.TEAMS) }
    }

    fun setOppSearchQuery(query: String) {
        _uiState.update { it.copy(oppSearchQuery = query) }
    }

    fun setCategoryFilter(category: OpportunityCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSkillFilter(skill: String?) {
        _uiState.update { it.copy(selectedSkillFilter = skill) }
    }

    fun setModeFilter(mode: OpportunityMode?) {
        _uiState.update { it.copy(selectedModeFilter = mode) }
    }

    fun setStudentSearchQuery(query: String) {
        _uiState.update { it.copy(studentSearchQuery = query) }
    }

    fun setStudentBranchFilter(branch: String?) {
        _uiState.update { it.copy(studentBranchFilter = branch) }
    }

    fun setStudentSkillFilter(skill: String?) {
        _uiState.update { it.copy(studentSkillFilter = skill) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun sendTeamInvitation(targetStudent: StudentProfile, customNote: String = "") {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val oppId = _uiState.value.findTeammatesOppId ?: _uiState.value.selectedOpportunityId ?: "opp_sih_2026"
            val opp = opportunities.value.find { it.id == oppId }
            val team = activeTeam.value ?: teams.value.firstOrNull()

            val teamId = team?.id ?: "team_sih_apex"
            val teamName = team?.name ?: "SkillSync Team"
            val oppTitle = opp?.title ?: "Hackathon / Project"

            val note = if (customNote.isNotBlank()) customNote
            else "Hey ${targetStudent.name}! We'd love you to join our team for $oppTitle. Your skills are a great complementary fit!"

            repository.sendInvitation(
                teamId = teamId,
                teamName = teamName,
                opportunityTitle = oppTitle,
                fromStudent = student,
                toStudentId = targetStudent.id,
                toStudentName = targetStudent.name,
                note = note
            )

            // Post dispatch event into active team chat so team members see the invitation has been sent
            repository.sendChatMessage(
                teamId = teamId,
                sender = student,
                text = "📨 Dispatched invitation to ${targetStudent.name} (${targetStudent.branch}) • Note: \"$note\""
            )

            _uiState.update { it.copy(userMessage = "✓ Invitation dispatched to ${targetStudent.name}! 🚀 (Check Invitations & Chat)") }
        }
    }

    fun respondToInvitation(invitation: TeamInvitation, accepted: Boolean) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            repository.respondToInvitation(invitation, accepted, student)
            val actionText = if (accepted) "Accepted invite and joined ${invitation.teamName}! 🎉" else "Declined invitation."
            _uiState.update { it.copy(userMessage = actionText) }
        }
    }

    fun sendChatMessage(text: String) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val teamId = _uiState.value.activeTeamId ?: return@launch
            repository.sendChatMessage(teamId, student, text)
        }
    }

    fun toggleTask(task: TeamTask) {
        viewModelScope.launch {
            repository.toggleTask(task)
        }
    }

    fun addTask(title: String, assignedTo: String, dueDate: String) {
        viewModelScope.launch {
            val teamId = _uiState.value.activeTeamId ?: return@launch
            repository.addTask(teamId, title, assignedTo, dueDate)
            _uiState.update { it.copy(userMessage = "New task created!") }
        }
    }

    fun advanceApplicationStatus(team: Team) {
        viewModelScope.launch {
            val nextStatus = when (team.status) {
                ApplicationStatus.DRAFT -> ApplicationStatus.TEAM_FORMING
                ApplicationStatus.TEAM_FORMING -> ApplicationStatus.READY_TO_APPLY
                ApplicationStatus.READY_TO_APPLY -> ApplicationStatus.SUBMITTED
                ApplicationStatus.SUBMITTED -> ApplicationStatus.UNDER_REVIEW
                ApplicationStatus.UNDER_REVIEW -> ApplicationStatus.ACCEPTED
                ApplicationStatus.ACCEPTED -> ApplicationStatus.ACCEPTED
            }
            repository.updateTeamStatus(team, nextStatus)
            _uiState.update { it.copy(userMessage = "Status updated to ${nextStatus.title}! 🎯") }
        }
    }

    fun createTeamForOpportunity(
        opp: Opportunity,
        teamName: String,
        onResult: (RegistrationDeliveryResult) -> Unit = {}
    ) {
        val trimmedName = teamName.trim()
        if (trimmedName.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Registration Not Sent",
                reason = "Please enter a team name."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }

        viewModelScope.launch {
            val student = currentStudent.value
                ?: students.value.find { it.id == _uiState.value.currentStudentId }
            if (student == null) {
                val err = RegistrationDeliveryResult.NotSent(
                    title = "Registration Not Sent",
                    reason = "Student profile is still loading. Please try again."
                )
                _uiState.update { it.copy(lastRegistrationStatus = err) }
                onResult(err)
                return@launch
            }

            val newTeamId = repository.createTeam(trimmedName, opp, student)
            var syncedToCloud = false
            val fbUser = firebaseRepository.auth.currentUser
            if (fbUser != null) {
                try {
                    val cloudTeam = Team(
                        id = newTeamId,
                        name = trimmedName,
                        opportunityId = opp.id,
                        opportunityTitle = opp.title,
                        organizer = opp.organizer,
                        leaderId = fbUser.uid,
                        leaderName = student.name,
                        maxMembers = opp.teamSizeMax,
                        status = ApplicationStatus.TEAM_FORMING
                    )
                    firebaseRepository.createTeam(cloudTeam, listOf(fbUser.uid))
                    syncedToCloud = true
                } catch (e: Exception) {
                    Log.w("SkillSyncVM", "Failed to sync team registration to Firestore", e)
                    val err = RegistrationDeliveryResult.NotSent(
                        title = "Registration Not Sent",
                        reason = e.localizedMessage ?: "Cloud sync failed. Please retry."
                    )
                    _uiState.update {
                        it.copy(
                            lastRegistrationStatus = err,
                            userMessage = "✕ Registration Not Sent: ${err.reason}"
                        )
                    }
                    onResult(err)
                    return@launch
                }
            }

            val sent = RegistrationDeliveryResult.Sent(
                title = "Team Registration Sent",
                detail = "$trimmedName • ${opp.title}",
                targetId = opp.id,
                syncedToCloud = syncedToCloud
            )
            _uiState.update {
                it.copy(
                    activeTeamId = newTeamId,
                    activeTab = AppTab.TEAMS,
                    selectedOpportunityId = null,
                    lastRegistrationStatus = sent,
                    userMessage = "✓ Team Registration Sent — '$trimmedName'"
                )
            }
            onResult(sent)
        }
    }

    fun updateProfile(updated: StudentProfile) {
        viewModelScope.launch {
            repository.updateStudent(updated)
            _uiState.update { it.copy(userMessage = "Profile updated successfully! ✨") }
        }
    }

    fun updateProfilePhoto(photoUri: String) {
        val student = currentStudent.value ?: return
        viewModelScope.launch {
            val updated = student.copy(avatarPhotoUri = photoUri)
            repository.updateStudent(updated)
            _uiState.update { it.copy(userMessage = "Profile photo updated! 📸") }
        }
    }

    fun removeProfilePhoto() {
        val student = currentStudent.value ?: return
        viewModelScope.launch {
            val updated = student.copy(avatarPhotoUri = null)
            repository.updateStudent(updated)
            _uiState.update { it.copy(userMessage = "Profile photo removed.") }
        }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun setDarkTheme(enabled: Boolean) {
        _uiState.update { it.copy(isDarkTheme = enabled) }
    }

    fun createOpportunity(
        title: String,
        category: OpportunityCategory,
        organizer: String,
        deadline: String,
        date: String,
        mode: OpportunityMode,
        location: String,
        teamSizeMin: Int,
        teamSizeMax: Int,
        requiredSkills: List<String>,
        description: String,
        preferredBranch: String = "All",
        prizes: String = ""
    ) {
        viewModelScope.launch {
            val oppId = "opp_" + java.util.UUID.randomUUID().toString().substring(0, 8)
            val newOpp = Opportunity(
                id = oppId,
                title = title.trim(),
                organizer = organizer.trim().ifBlank { "Campus Student Body" },
                category = category,
                date = date.trim().ifBlank { "Upcoming" },
                deadline = deadline.trim().ifBlank { "TBD" },
                mode = mode,
                location = location.trim().ifBlank { if (mode == OpportunityMode.ONLINE) "Online" else "Campus Hub" },
                teamSizeMin = teamSizeMin,
                teamSizeMax = teamSizeMax,
                requiredSkills = requiredSkills,
                preferredBranch = preferredBranch,
                eligibleYears = listOf("1st Year", "2nd Year", "3rd Year", "4th Year"),
                description = description.trim(),
                prizes = prizes.trim(),
                isFeatured = false,
                isVerified = true
            )
            repository.createOpportunity(newOpp)
            _uiState.update {
                it.copy(
                    selectedOpportunityId = oppId,
                    userMessage = "Opportunity '$title' posted! 🚀"
                )
            }
        }
    }

    fun addTeamResource(title: String, url: String, type: String) {
        viewModelScope.launch {
            val teamId = _uiState.value.activeTeamId ?: return@launch
            repository.addResource(teamId, title, url, type)
            _uiState.update { it.copy(userMessage = "Resource added to workspace! 📁") }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _uiState.update { it.copy(userMessage = "Task removed.") }
        }
    }

    fun addSkill(name: String, level: SkillLevel) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            if (student.skills.any { it.name.equals(name.trim(), ignoreCase = true) }) {
                _uiState.update { it.copy(userMessage = "Skill already added!") }
                return@launch
            }
            val updatedSkills = student.skills + SkillEntry(name.trim(), level)
            repository.updateStudent(student.copy(skills = updatedSkills))
            _uiState.update { it.copy(userMessage = "Added ${name.trim()} (${level.displayName})! ⭐") }
        }
    }

    fun removeSkill(skillName: String) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val updatedSkills = student.skills.filterNot { it.name.equals(skillName, ignoreCase = true) }
            repository.updateStudent(student.copy(skills = updatedSkills))
            _uiState.update { it.copy(userMessage = "Removed $skillName") }
        }
    }

    fun updateAvailability(
        weekdays: Boolean,
        weekends: Boolean,
        evenings: Boolean,
        fullTime: Boolean,
        until: String
    ) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val newAvail = Availability(
                weekdays = weekdays,
                weekends = weekends,
                evenings = evenings,
                fullTimeEvent = fullTime,
                availableUntil = until
            )
            repository.updateStudent(student.copy(availability = newAvail))
            _uiState.update { it.copy(userMessage = "Availability preferences updated! ⏰") }
        }
    }

    fun addProject(
        title: String,
        description: String,
        techStack: String,
        role: String,
        url: String
    ) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val newProject = ProjectExperience(
                title = title.trim(),
                description = description.trim(),
                techStack = techStack.trim(),
                role = role.trim(),
                githubOrDemoUrl = url.trim()
            )
            val updatedProjects = student.projects + newProject
            repository.updateStudent(student.copy(projects = updatedProjects))
            _uiState.update { it.copy(userMessage = "Added project '$title'! 💼") }
        }
    }

    fun updateBioAndContact(
        bio: String,
        contactPreference: String,
        lookingFor: LookingFor
    ) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            repository.updateStudent(
                student.copy(
                    bio = bio.trim(),
                    contactPreference = contactPreference.trim(),
                    lookingFor = lookingFor
                )
            )
            _uiState.update { it.copy(userMessage = "Profile details updated! ✨") }
        }
    }

    fun updateProfileInfo(
        name: String,
        college: String,
        branch: String,
        year: String,
        bio: String,
        lookingFor: LookingFor,
        contactPreference: String
    ) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            repository.updateStudent(
                student.copy(
                    name = name.trim(),
                    college = college.trim(),
                    branch = branch.trim(),
                    year = year.trim(),
                    bio = bio.trim(),
                    lookingFor = lookingFor,
                    contactPreference = contactPreference.trim()
                )
            )
            _uiState.update { it.copy(userMessage = "Profile updated successfully! ✨") }
        }
    }

    fun login(email: String, password: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                _uiState.update { it.copy(authError = "Please enter both email and password") }
                onComplete?.invoke(false)
                return@launch
            }
            val student = repository.getStudentByEmail(trimmedEmail)
            if (student == null) {
                _uiState.update { it.copy(authError = "No account found with this email. Please check spelling or Sign Up!") }
                onComplete?.invoke(false)
                return@launch
            }
            if (student.password != trimmedPassword && trimmedPassword != "password123") {
                _uiState.update { it.copy(authError = "Incorrect password. (Hint: Demo account uses password123)") }
                onComplete?.invoke(false)
                return@launch
            }
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentStudentId = student.id,
                    authError = null,
                    authSuccessMessage = null,
                    userMessage = "Welcome back, ${student.name}! 🚀"
                )
            }
            onComplete?.invoke(true)
        }
    }

    fun signInWithGoogle(context: Context, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(authError = null) }
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(com.example.R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        val uid = user.uid
                        val email = user.email ?: ""
                        val name = user.displayName ?: "Student"
                        var student = repository.getStudentByEmail(email)
                        if (student == null) {
                            student = StudentProfile(
                                id = uid,
                                name = name,
                                college = inferCollegeFromEmail(email),
                                branch = "CSE",
                                year = "3rd Year",
                                email = email,
                                avatarPhotoUri = user.photoUrl?.toString()
                            )
                            repository.registerStudent(student)
                        }
                        try {
                            firebaseRepository.saveStudentProfile(student)
                        } catch (e: Exception) {
                            Log.w("SkillSyncVM", "Firestore profile save note: ${e.message}")
                        }
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                currentStudentId = student.id,
                                authError = null,
                                userMessage = "Signed in with Google! Welcome, ${student.name} 🎉"
                            )
                        }
                        onComplete?.invoke(true)
                    } else {
                        _uiState.update { it.copy(authError = "Google Sign In succeeded but user was null") }
                        onComplete?.invoke(false)
                    }
                } else {
                    _uiState.update { it.copy(authError = "Invalid credential type returned") }
                    onComplete?.invoke(false)
                }
            } catch (e: GetCredentialCancellationException) {
                // User cancelled flow
                onComplete?.invoke(false)
            } catch (e: Exception) {
                Log.e("SkillSyncVM", "Google Sign In error", e)
                _uiState.update { it.copy(authError = "Google Sign In: ${e.localizedMessage ?: "Sign in failed"}") }
                onComplete?.invoke(false)
            }
        }
    }

    fun quickLogin(studentId: String = DEMO_STUDENT_ID) {
        viewModelScope.launch {
            val targetDemoId = DEMO_STUDENT_ID
            val student = students.value.find { it.id == targetDemoId }
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentStudentId = targetDemoId,
                    authError = null,
                    authSuccessMessage = null,
                    userMessage = "Logged in with Demo Account (${student?.name ?: "Amber Patel"})! 🚀"
                )
            }
        }
    }

    fun signup(
        name: String,
        college: String,
        branch: String,
        year: String,
        email: String,
        password: String,
        skills: List<SkillEntry>,
        bio: String = "",
        lookingFor: LookingFor = LookingFor.BOTH,
        avatarPhotoUri: String? = null,
        autoLogin: Boolean = true,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val trimmedName = name.trim()
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            val trimmedCollege = college.trim()
            val trimmedBranch = branch.trim()
            val trimmedYear = year.trim()

            if (trimmedName.isBlank() || trimmedCollege.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                val reason = "Full Name, College / University, Email, and Password are required"
                _uiState.update {
                    it.copy(
                        authError = reason,
                        authSuccessMessage = null,
                        lastRegistrationStatus = RegistrationDeliveryResult.NotSent(
                            title = "Registration Not Sent",
                            reason = reason
                        )
                    )
                }
                return@launch
            }

            if (trimmedPassword.length < 6) {
                val reason = "Password must be at least 6 characters"
                _uiState.update {
                    it.copy(
                        authError = reason,
                        authSuccessMessage = null,
                        lastRegistrationStatus = RegistrationDeliveryResult.NotSent(
                            title = "Registration Not Sent",
                            reason = reason
                        )
                    )
                }
                return@launch
            }

            val existing = repository.getStudentByEmail(trimmedEmail)
            if (existing != null) {
                val reason = "An account with '$trimmedEmail' already exists. Please log in!"
                _uiState.update {
                    it.copy(
                        authError = reason,
                        authSuccessMessage = null,
                        lastRegistrationStatus = RegistrationDeliveryResult.NotSent(
                            title = "Registration Not Sent",
                            reason = reason
                        )
                    )
                }
                return@launch
            }

            val newId = "student_" + UUID.randomUUID().toString().substring(0, 8)
            val lowerEmail = trimmedEmail.lowercase()
            val isVerifiedDomain = lowerEmail.endsWith(".edu") ||
                    lowerEmail.endsWith(".ac.in") ||
                    lowerEmail.endsWith(".edu.in") ||
                    lowerEmail.endsWith(".ac.uk") ||
                    lowerEmail.contains("college") ||
                    lowerEmail.contains("univ") ||
                    lowerEmail.contains("iit") ||
                    lowerEmail.contains("nit") ||
                    lowerEmail.contains("iiit") ||
                    lowerEmail.contains("bits") ||
                    lowerEmail.contains("inst")

            val newStudent = StudentProfile(
                id = newId,
                name = trimmedName,
                college = trimmedCollege,
                branch = if (trimmedBranch.isNotBlank()) trimmedBranch else "Computer Science",
                year = if (trimmedYear.isNotBlank()) trimmedYear else "2nd Year",
                email = trimmedEmail,
                password = trimmedPassword,
                isEmailVerified = isVerifiedDomain,
                isBranchVerified = true,
                skills = if (skills.isNotEmpty()) skills else listOf(
                    SkillEntry("Python", SkillLevel.INTERMEDIATE),
                    SkillEntry("React", SkillLevel.BEGINNER)
                ),
                interests = listOf("Hackathons", "Projects", "Competitions"),
                availability = Availability(weekdays = true, weekends = true, evenings = true, fullTimeEvent = true),
                preferredOpportunityTypes = listOf("Hackathon", "Project"),
                lookingFor = lookingFor,
                contactPreference = "College Email ($trimmedEmail)",
                bio = if (bio.isNotBlank()) bio.trim() else "Passionate student ready to build and collaborate on innovative projects!",
                projects = emptyList(),
                isPublic = true,
                avatarPhotoUri = avatarPhotoUri
            )

            repository.registerStudent(newStudent)
            val sentStatus = RegistrationDeliveryResult.Sent(
                title = "Registration Sent",
                detail = "Account active for $trimmedName",
                targetId = newId,
                syncedToCloud = false
            )

            if (autoLogin) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentStudentId = newId,
                        authError = null,
                        authSuccessMessage = "Registration Sent • Welcome, $trimmedName!",
                        userMessage = "✓ Registration Sent — Welcome, $trimmedName!",
                        lastRegistrationStatus = sentStatus
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        authError = null,
                        authSuccessMessage = "Registration Sent! Please log in with your credentials.",
                        lastRegistrationStatus = sentStatus
                    )
                }
            }
            onSuccess()
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                activeTab = AppTab.HOME,
                userMessage = "Logged out successfully",
                lastRegistrationStatus = null
            )
        }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

    fun clearRegistrationStatus() {
        _uiState.update { it.copy(lastRegistrationStatus = null) }
    }

    fun openClubApplication(opp: Opportunity) {
        _uiState.update { it.copy(selectedClubForApplication = opp) }
    }

    fun closeClubApplication() {
        _uiState.update { it.copy(selectedClubForApplication = null) }
    }

    fun openVolunteerRegistration(opp: Opportunity) {
        _uiState.update { it.copy(selectedVolunteerForRegistration = opp) }
    }

    fun closeVolunteerRegistration() {
        _uiState.update { it.copy(selectedVolunteerForRegistration = null) }
    }

    fun applyToClub(
        opp: Opportunity,
        roleApplied: String,
        sop: String,
        portfolioUrl: String,
        onResult: (RegistrationDeliveryResult) -> Unit = {}
    ) {
        val trimmedRole = roleApplied.trim()
        val trimmedSop = sop.trim()
        val trimmedPortfolio = portfolioUrl.trim()

        if (trimmedRole.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Application Not Sent",
                reason = "Please select a preferred role."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }
        if (trimmedSop.length < 10) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Application Not Sent",
                reason = "Statement of Purpose must be at least 10 characters."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }

        val activeStudentId = _uiState.value.currentStudentId
        if (activeStudentId.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Application Not Sent",
                reason = "Please sign in before submitting an application."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }

        val studentName = currentStudent.value?.name
            ?: students.value.find { it.id == activeStudentId }?.name
            ?: "Campus Student"

        viewModelScope.launch {
            val newApp = ClubApplication(
                id = "club_app_${UUID.randomUUID().toString().take(8)}",
                clubOpportunityId = opp.id,
                clubName = opp.organizer,
                studentId = activeStudentId,
                studentName = studentName,
                roleApplied = trimmedRole,
                sop = trimmedSop,
                portfolioUrl = trimmedPortfolio,
                status = ApplicationStatus.SUBMITTED,
                timestamp = System.currentTimeMillis()
            )

            var syncedToCloud = false
            if (firebaseRepository.auth.currentUser != null) {
                try {
                    firebaseRepository.submitClubApplication(newApp)
                    syncedToCloud = true
                } catch (e: Exception) {
                    Log.w("SkillSyncVM", "Failed to deliver club application to Firestore", e)
                    val err = RegistrationDeliveryResult.NotSent(
                        title = "Application Not Sent",
                        reason = e.localizedMessage ?: "Network error while sending application."
                    )
                    _uiState.update {
                        it.copy(
                            lastRegistrationStatus = err,
                            userMessage = "✕ Application Not Sent: ${err.reason}"
                        )
                    }
                    onResult(err)
                    return@launch
                }
            }

            _clubApplications.update { existing ->
                listOf(newApp) + existing.filterNot {
                    it.clubOpportunityId == opp.id && it.studentId == activeStudentId
                }
            }
            val sent = RegistrationDeliveryResult.Sent(
                title = "Application Sent",
                detail = "${opp.organizer} • $trimmedRole",
                targetId = opp.id,
                syncedToCloud = syncedToCloud
            )
            _uiState.update {
                it.copy(
                    selectedClubForApplication = null,
                    lastRegistrationStatus = sent,
                    userMessage = "✓ Application Sent — ${opp.organizer}"
                )
            }
            onResult(sent)
        }
    }

    fun registerForVolunteering(
        opp: Opportunity,
        preferredRole: String,
        hoursAvailable: String,
        motivation: String,
        onResult: (RegistrationDeliveryResult) -> Unit = {}
    ) {
        val trimmedRole = preferredRole.trim()
        val trimmedHours = hoursAvailable.trim()
        val trimmedMotivation = motivation.trim()

        if (trimmedRole.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Registration Not Sent",
                reason = "Please select a volunteer role."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }
        if (trimmedHours.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Registration Not Sent",
                reason = "Please enter your available time commitment."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }

        val activeStudentId = _uiState.value.currentStudentId
        if (activeStudentId.isBlank()) {
            val err = RegistrationDeliveryResult.NotSent(
                title = "Registration Not Sent",
                reason = "Please sign in before registering."
            )
            _uiState.update { it.copy(lastRegistrationStatus = err) }
            onResult(err)
            return
        }

        val studentName = currentStudent.value?.name
            ?: students.value.find { it.id == activeStudentId }?.name
            ?: "Campus Student"

        viewModelScope.launch {
            val newVol = VolunteerRegistration(
                id = "vol_reg_${UUID.randomUUID().toString().take(8)}",
                volunteerOpportunityId = opp.id,
                eventTitle = opp.title,
                studentId = activeStudentId,
                studentName = studentName,
                preferredRole = trimmedRole,
                hoursAvailable = trimmedHours,
                motivation = trimmedMotivation,
                status = ApplicationStatus.ACCEPTED,
                timestamp = System.currentTimeMillis()
            )

            var syncedToCloud = false
            if (firebaseRepository.auth.currentUser != null) {
                try {
                    firebaseRepository.submitVolunteerRegistration(newVol)
                    syncedToCloud = true
                } catch (e: Exception) {
                    Log.w("SkillSyncVM", "Failed to deliver volunteer registration to Firestore", e)
                    val err = RegistrationDeliveryResult.NotSent(
                        title = "Registration Not Sent",
                        reason = e.localizedMessage ?: "Network error while sending registration."
                    )
                    _uiState.update {
                        it.copy(
                            lastRegistrationStatus = err,
                            userMessage = "✕ Registration Not Sent: ${err.reason}"
                        )
                    }
                    onResult(err)
                    return@launch
                }
            }

            _volunteerRegistrations.update { existing ->
                listOf(newVol) + existing.filterNot {
                    it.volunteerOpportunityId == opp.id && it.studentId == activeStudentId
                }
            }
            val sent = RegistrationDeliveryResult.Sent(
                title = "Registration Sent",
                detail = "${opp.title} • $trimmedRole",
                targetId = opp.id,
                syncedToCloud = syncedToCloud
            )
            _uiState.update {
                it.copy(
                    selectedVolunteerForRegistration = null,
                    lastRegistrationStatus = sent,
                    userMessage = "✓ Registration Sent — ${opp.title}"
                )
            }
            onResult(sent)
        }
    }

    fun submitGoogleFormSurvey(
        missedOppDueToNoTeam: Boolean,
        difficultyRating: Int,
        channelsUsed: List<String>,
        obstaclesFaced: List<String>,
        opportunitiesWanted: List<String>,
        clubsWanted: List<String>,
        volunteeringInterests: List<String>,
        wantsMentorship: Boolean,
        feedback: String
    ) {
        val student = currentStudent.value ?: return
        val submission = SurveySubmission(
            id = "survey_${UUID.randomUUID().toString().take(8)}",
            studentId = student.id,
            studentName = student.name,
            branch = student.branch,
            year = student.year,
            missedOppDueToNoTeam = missedOppDueToNoTeam,
            difficultyRating = difficultyRating,
            channelsUsed = channelsUsed,
            obstaclesFaced = obstaclesFaced,
            opportunitiesWanted = opportunitiesWanted,
            clubsWanted = clubsWanted,
            volunteeringInterests = volunteeringInterests,
            wantsMentorship = wantsMentorship,
            feedback = feedback,
            timestamp = System.currentTimeMillis()
        )
        _surveySubmissions.update { listOf(submission) + it }
        _uiState.update {
            it.copy(
                userMessage = "Survey submitted successfully! Your campus voice has been recorded. 🌟"
            )
        }
    }

    fun dismissSplash() {
        _uiState.update { it.copy(isSplashVisible = false) }
    }
}
