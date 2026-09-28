package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SkillSyncDatabase
import com.example.data.SkillSyncRepository
import com.example.data.SmartMatchingEngine
import com.example.model.*
import java.util.UUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
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
    val isSurveyFormOpen: Boolean = false,
    val selectedClubForApplication: Opportunity? = null,
    val selectedVolunteerForRegistration: Opportunity? = null
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
    CLUBS("Club Recruitment"),
    VOLUNTEERING("Volunteering"),
    STUDENTS("Student Discovery")
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SkillSyncViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SkillSyncRepository

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
                clubName = "ACM SGSITS Student Chapter",
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

    fun switchStudent(studentId: String) {
        _uiState.update { it.copy(currentStudentId = studentId, userMessage = "Switched active student profile") }
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

    fun createTeamForOpportunity(opp: Opportunity, teamName: String) {
        viewModelScope.launch {
            val student = currentStudent.value ?: return@launch
            val newTeamId = repository.createTeam(teamName, opp, student)
            _uiState.update {
                it.copy(
                    activeTeamId = newTeamId,
                    activeTab = AppTab.TEAMS,
                    selectedOpportunityId = null,
                    userMessage = "Team '$teamName' created successfully! 🎉"
                )
            }
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
                _uiState.update { it.copy(authError = "Incorrect password. (Hint: Demo accounts use password123)") }
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

    fun quickLogin(studentId: String) {
        viewModelScope.launch {
            val student = students.value.find { it.id == studentId }
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentStudentId = studentId,
                    authError = null,
                    authSuccessMessage = null,
                    userMessage = "Logged in as ${student?.name ?: "Student"}! 🚀"
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

            if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                _uiState.update { it.copy(authError = "Full Name, College Email, and Password are required") }
                return@launch
            }

            if (trimmedPassword.length < 6) {
                _uiState.update { it.copy(authError = "Password must be at least 6 characters") }
                return@launch
            }

            val existing = repository.getStudentByEmail(trimmedEmail)
            if (existing != null) {
                _uiState.update { it.copy(authError = "An account with '$trimmedEmail' already exists. Please log in!") }
                return@launch
            }

            val newId = "student_" + UUID.randomUUID().toString().substring(0, 8)
            val isVerifiedDomain = trimmedEmail.endsWith(".edu") || trimmedEmail.endsWith(".ac.in") ||
                    trimmedEmail.contains("college") || trimmedEmail.contains("univ")

            val newStudent = StudentProfile(
                id = newId,
                name = trimmedName,
                college = if (trimmedCollege.isNotBlank()) trimmedCollege else "Campus University",
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

            if (autoLogin) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentStudentId = newId,
                        authError = null,
                        authSuccessMessage = "Account created successfully! Welcome, $trimmedName! ✨",
                        userMessage = "Account created successfully! Welcome, $trimmedName! ✨"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        authError = null,
                        authSuccessMessage = "Account created successfully! Please log in with your credentials."
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
                userMessage = "Logged out successfully"
            )
        }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

    fun openSurveyForm() {
        _uiState.update { it.copy(isSurveyFormOpen = true) }
    }

    fun closeSurveyForm() {
        _uiState.update { it.copy(isSurveyFormOpen = false) }
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
        portfolioUrl: String
    ) {
        val student = currentStudent.value ?: return
        val newApp = ClubApplication(
            id = "club_app_${UUID.randomUUID().toString().take(8)}",
            clubOpportunityId = opp.id,
            clubName = opp.organizer,
            studentId = student.id,
            studentName = student.name,
            roleApplied = roleApplied,
            sop = sop,
            portfolioUrl = portfolioUrl,
            status = ApplicationStatus.SUBMITTED,
            timestamp = System.currentTimeMillis()
        )
        _clubApplications.update { listOf(newApp) + it }
        _uiState.update {
            it.copy(
                selectedClubForApplication = null,
                userMessage = "Application submitted to ${opp.organizer} for $roleApplied! 📋"
            )
        }
    }

    fun registerForVolunteering(
        opp: Opportunity,
        preferredRole: String,
        hoursAvailable: String,
        motivation: String
    ) {
        val student = currentStudent.value ?: return
        val newVol = VolunteerRegistration(
            id = "vol_reg_${UUID.randomUUID().toString().take(8)}",
            volunteerOpportunityId = opp.id,
            eventTitle = opp.title,
            studentId = student.id,
            studentName = student.name,
            preferredRole = preferredRole,
            hoursAvailable = hoursAvailable,
            motivation = motivation,
            status = ApplicationStatus.ACCEPTED,
            timestamp = System.currentTimeMillis()
        )
        _volunteerRegistrations.update { listOf(newVol) + it }
        _uiState.update {
            it.copy(
                selectedVolunteerForRegistration = null,
                userMessage = "Successfully registered for ${opp.title} as $preferredRole! 🤝"
            )
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
                isSurveyFormOpen = false,
                userMessage = "Survey submitted successfully! Your campus voice has been recorded. 🌟"
            )
        }
    }
}
