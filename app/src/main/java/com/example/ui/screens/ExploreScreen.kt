package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.ExploreSubTab
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val filteredOpps by viewModel.filteredOpportunities.collectAsState()
    val discoveredStudents by viewModel.discoveredStudents.collectAsState()

    var inviteCandidateDialog by remember { mutableStateOf<StudentProfile?>(null) }
    var inviteNoteText by remember { mutableStateOf("") }
    var showPostOppDialog by remember { mutableStateOf(false) }
    var clubOppToApply by remember { mutableStateOf<Opportunity?>(null) }
    var volOppToRegister by remember { mutableStateOf<Opportunity?>(null) }

    // Intercept back gesture to return to Home screen
    BackHandler {
        viewModel.setTab(AppTab.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen_root")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Dedicated Top Header with Back Button to return to Home
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setTab(AppTab.HOME) },
                        modifier = Modifier.testTag("btn_explore_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        val headerInfo = when (uiState.exploreSubTab) {
                            ExploreSubTab.CLUBS -> Triple("Clubs & Chapters", "SGSITS Inductions & Core Teams", "👥")
                            ExploreSubTab.VOLUNTEERING -> Triple("Campus Volunteering", "NSS, Aayam & Activity Credits", "🤝")
                            ExploreSubTab.STUDENTS -> Triple("Find Teammates", "Skill-Based Peer Discovery", "🔍")
                            ExploreSubTab.HACKATHONS -> Triple("Hackathons & Challenges", "Live Tech Contests & SIH 2026", "🏆")
                            ExploreSubTab.RESEARCH -> Triple("Research & Labs", "Faculty Projects & Core IoT Labs", "🔬")
                            ExploreSubTab.OPPORTUNITIES -> Triple("Campus Opportunities", "All SGSITS Hub Opportunities", "🌐")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = headerInfo.third,
                                fontSize = 16.sp
                            )
                            Text(
                                text = headerInfo.first,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = headerInfo.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Reset search button if query is typed
                    if (uiState.oppSearchQuery.isNotBlank() || uiState.studentSearchQuery.isNotBlank()) {
                        TextButton(
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setStudentSearchQuery("")
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Top Category Tabs Row for Explore Sub-Categories
            val allSubTabs = listOf(
                ExploreSubTab.CLUBS,
                ExploreSubTab.VOLUNTEERING,
                ExploreSubTab.STUDENTS,
                ExploreSubTab.HACKATHONS,
                ExploreSubTab.RESEARCH,
                ExploreSubTab.OPPORTUNITIES
            )
            val selectedTabIndex = allSubTabs.indexOf(uiState.exploreSubTab).coerceAtLeast(0)

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SkyBluePrimary,
                edgePadding = 12.dp
            ) {
                allSubTabs.forEach { subTab ->
                    val isSelected = uiState.exploreSubTab == subTab
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setExploreSubTab(subTab) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    when (subTab) {
                                        ExploreSubTab.CLUBS -> "👥"
                                        ExploreSubTab.VOLUNTEERING -> "🤝"
                                        ExploreSubTab.STUDENTS -> "🔍"
                                        ExploreSubTab.HACKATHONS -> "🏆"
                                        ExploreSubTab.RESEARCH -> "🔬"
                                        ExploreSubTab.OPPORTUNITIES -> "🌐"
                                    },
                                    fontSize = 14.sp
                                )
                                Text(
                                    subTab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_${subTab.name.lowercase()}")
                    )
                }
            }

            when (uiState.exploreSubTab) {
                ExploreSubTab.CLUBS -> {
                    // CLUB RECRUITMENT VIEW
                    ClubRecruitmentView(
                        viewModel = viewModel,
                        filteredOpps = filteredOpps,
                        currentStudent = currentStudent,
                        onApplyClub = { clubOppToApply = it }
                    )
                }
                ExploreSubTab.VOLUNTEERING -> {
                    // VOLUNTEERING DRIVES VIEW
                    VolunteeringDrivesView(
                        viewModel = viewModel,
                        filteredOpps = filteredOpps,
                        currentStudent = currentStudent,
                        onRegisterVolunteer = { volOppToRegister = it }
                    )
                }
                ExploreSubTab.STUDENTS -> {
                    // STUDENT DISCOVERY VIEW
                    StudentDiscoveryView(
                        viewModel = viewModel,
                        discoveredStudents = discoveredStudents,
                        uiState = uiState,
                        onInviteStudent = { student ->
                            inviteCandidateDialog = student
                            inviteNoteText = "Hey ${student.name}! We saw your impressive skills in ${student.skills.take(2).joinToString(", ") { it.name }}. Would you like to team up for our upcoming project/hackathon?"
                        }
                    )
                }
                ExploreSubTab.HACKATHONS, ExploreSubTab.RESEARCH, ExploreSubTab.OPPORTUNITIES -> {
                    // OPPORTUNITY HUB VIEW (Hackathons, Research, or All Opportunities)
                    OpportunityHubView(
                        viewModel = viewModel,
                        filteredOpps = filteredOpps,
                        currentStudent = currentStudent,
                        uiState = uiState
                    )
                }
            }
        }

        // Floating Action Button to post an opportunity
        if (uiState.exploreSubTab == ExploreSubTab.OPPORTUNITIES || uiState.exploreSubTab == ExploreSubTab.HACKATHONS || uiState.exploreSubTab == ExploreSubTab.RESEARCH) {
            ExtendedFloatingActionButton(
                onClick = { showPostOppDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Post Opportunity", fontWeight = FontWeight.Bold) },
                containerColor = SkyBluePrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp)
                    .testTag("fab_post_opportunity")
            )
        }
    }

    // Club Application Dialog
    clubOppToApply?.let { opp ->
        com.example.ui.components.ClubApplicationDialog(
            opportunity = opp,
            viewModel = viewModel,
            onDismiss = { clubOppToApply = null }
        )
    }

    // Volunteering Registration Dialog
    volOppToRegister?.let { opp ->
        com.example.ui.components.VolunteerRegistrationDialog(
            opportunity = opp,
            viewModel = viewModel,
            onDismiss = { volOppToRegister = null }
        )
    }

    // Invitation Dialog
    inviteCandidateDialog?.let { student ->
        AlertDialog(
            onDismissRequest = { inviteCandidateDialog = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = SkyBluePrimary)
                    Text("Invite ${student.name} to Team", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Send an in-platform invitation to ${student.name} (${student.branch}, ${student.year}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inviteNoteText,
                        onValueChange = { inviteNoteText = it },
                        label = { Text("Personal message") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendTeamInvitation(student, inviteNoteText)
                        inviteCandidateDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White)
                ) {
                    Text("Send Invite 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { inviteCandidateDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Post Opportunity Dialog
    if (showPostOppDialog) {
        PostOpportunityDialog(
            onDismiss = { showPostOppDialog = false },
            onPost = { title, cat, org, deadline, date, mode, loc, minTeam, maxTeam, skills, desc, branch, prizes ->
                viewModel.createOpportunity(
                    title = title,
                    category = cat,
                    organizer = org,
                    deadline = deadline,
                    date = date,
                    mode = mode,
                    location = loc,
                    teamSizeMin = minTeam,
                    teamSizeMax = maxTeam,
                    requiredSkills = skills,
                    description = desc,
                    preferredBranch = branch,
                    prizes = prizes
                )
                showPostOppDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOpportunityDialog(
    onDismiss: () -> Unit,
    onPost: (
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
        preferredBranch: String,
        prizes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(OpportunityCategory.HACKATHON) }
    var organizer by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("Oct 25, 2026") }
    var date by remember { mutableStateOf("Nov 05-07, 2026") }
    var selectedMode by remember { mutableStateOf(OpportunityMode.ONLINE) }
    var location by remember { mutableStateOf("Online") }
    var minTeam by remember { mutableStateOf("2") }
    var maxTeam by remember { mutableStateOf("4") }
    var skillsInput by remember { mutableStateOf("Python, Machine Learning, UI/UX") }
    var description by remember { mutableStateOf("") }
    var prizes by remember { mutableStateOf("₹30,000 Prize Pool") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📢", fontSize = 20.sp)
                Text("Post New Opportunity", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Opportunity Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(OpportunityCategory.entries) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text("${cat.iconEmoji} ${cat.displayName}", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = organizer,
                        onValueChange = { organizer = it },
                        label = { Text("Organizer / College Club") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = deadline,
                            onValueChange = { deadline = it },
                            label = { Text("Deadline") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Event Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Mode:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OpportunityMode.entries.forEach { mode ->
                            FilterChip(
                                selected = selectedMode == mode,
                                onClick = {
                                    selectedMode = mode
                                    if (mode == OpportunityMode.ONLINE) location = "Online"
                                },
                                label = { Text(mode.label, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = skillsInput,
                        onValueChange = { skillsInput = it },
                        label = { Text("Required Skills (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("e.g. Python, UI/UX, React, Presentation") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = prizes,
                        onValueChange = { prizes = it },
                        label = { Text("Prizes / Benefits") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedSkills = skillsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onPost(
                        title.ifBlank { "Campus Tech Challenge" },
                        selectedCategory,
                        organizer.ifBlank { "Student Tech Chapter" },
                        deadline,
                        date,
                        selectedMode,
                        location,
                        minTeam.toIntOrNull() ?: 2,
                        maxTeam.toIntOrNull() ?: 4,
                        if (parsedSkills.isNotEmpty()) parsedSkills else listOf("Python", "UI/UX"),
                        description.ifBlank { "Collaborative student event to build innovative solutions." },
                        "All",
                        prizes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White)
            ) {
                Text("Publish 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OpportunityHubView(
    viewModel: SkillSyncViewModel,
    filteredOpps: List<Pair<Opportunity, Int>>,
    currentStudent: StudentProfile?,
    uiState: com.example.viewmodel.UiState
) {
    val allSkills = listOf("Python", "React", "Machine Learning", "UI/UX", "Java", "C++", "Cloud/DevOps", "IoT/Electronics", "Presentation")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("opportunity_feed_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = uiState.oppSearchQuery,
                onValueChange = { viewModel.setOppSearchQuery(it) },
                placeholder = { Text("Search hackathons, projects, internships, skills...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SkyBluePrimary)
                },
                trailingIcon = {
                    if (uiState.oppSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setOppSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_opps"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.setCategoryFilter(null) },
                        label = { Text("All Opportunities") }
                    )
                }
                items(OpportunityCategory.values()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = {
                            viewModel.setCategoryFilter(if (uiState.selectedCategory == category) null else category)
                        },
                        label = { Text("${category.iconEmoji} ${category.displayName}") }
                    )
                }
            }
        }

        // Skill Filters
        item {
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedSkillFilter == null,
                        onClick = { viewModel.setSkillFilter(null) },
                        label = { Text("Any Skill") }
                    )
                }
                items(allSkills) { skill ->
                    FilterChip(
                        selected = uiState.selectedSkillFilter == skill,
                        onClick = {
                            viewModel.setSkillFilter(if (uiState.selectedSkillFilter == skill) null else skill)
                        },
                        label = { Text(skill) }
                    )
                }
            }
        }

        // Mode Filter (Online / In-Person / Hybrid)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(listOf(null to "All Modes", OpportunityMode.ONLINE to "🌐 Online", OpportunityMode.OFFLINE to "📍 In-Person", OpportunityMode.HYBRID to "⚡ Hybrid")) { (mode, title) ->
                    val isSelected = uiState.selectedModeFilter == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setModeFilter(mode) },
                        label = { Text(title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Results count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredOpps.size} Opportunities Found",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sorted by Skill Compatibility ⭐",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Opportunities Cards List
        if (filteredOpps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("No opportunities found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "No opportunities match your current filters. Clear filters to explore all listings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setCategoryFilter(null)
                                viewModel.setSkillFilter(null)
                                viewModel.setModeFilter(null)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 44.dp)
                                .testTag("btn_reset_filters")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Filters", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredOpps) { (opp, score) ->
                Box(modifier = Modifier.padding(vertical = 6.dp)) {
                    OpportunityCard(
                        opportunity = opp,
                        matchScore = score,
                        currentStudent = currentStudent,
                        onCardClick = { viewModel.selectOpportunity(opp.id) },
                        onFindTeammatesClick = { viewModel.openFindTeammates(opp.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentDiscoveryView(
    viewModel: SkillSyncViewModel,
    discoveredStudents: List<Pair<StudentProfile, Int>>,
    uiState: com.example.viewmodel.UiState,
    onInviteStudent: (StudentProfile) -> Unit
) {
    val sentInvitationRecipientIds by viewModel.sentInvitationRecipientIds.collectAsState()
    val branches = listOf("CSE", "IT", "ECE", "MECH", "CIVIL")
    val skills = listOf("React", "Python", "Machine Learning", "UI/UX", "Presentation", "IoT/Electronics", "Cloud/DevOps")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_discovery_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp)
    ) {
        // Header explanation from PPT: "84.2% relied on friends/classmates. SkillSync connects you beyond existing networks."
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Diversity3, contentDescription = null, tint = BrandIndigo)
                    Text(
                        text = "Discover peers with the exact skills your team lacks across branches & years.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Search Input
        item {
            OutlinedTextField(
                value = uiState.studentSearchQuery,
                onValueChange = { viewModel.setStudentSearchQuery(it) },
                placeholder = { Text("Search by skill (e.g. React, ML, UI/UX) or branch...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SkyBluePrimary)
                },
                trailingIcon = {
                    if (uiState.studentSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setStudentSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_students"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Branch filter chips
        item {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = uiState.studentBranchFilter == null,
                        onClick = { viewModel.setStudentBranchFilter(null) },
                        label = { Text("All Branches") }
                    )
                }
                items(branches) { branch ->
                    FilterChip(
                        selected = uiState.studentBranchFilter == branch,
                        onClick = {
                            viewModel.setStudentBranchFilter(if (uiState.studentBranchFilter == branch) null else branch)
                        },
                        label = { Text(branch) }
                    )
                }
            }
        }

        // Quick Skill filter chips
        item {
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = uiState.studentSkillFilter == null,
                        onClick = { viewModel.setStudentSkillFilter(null) },
                        label = { Text("Any Skill") }
                    )
                }
                items(skills) { skill ->
                    FilterChip(
                        selected = uiState.studentSkillFilter == skill,
                        onClick = {
                            viewModel.setStudentSkillFilter(if (uiState.studentSkillFilter == skill) null else skill)
                        },
                        label = { Text(skill) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // List of Student Cards
        if (discoveredStudents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("No Peers Found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "No students match your current branch or skill filters. Reset filters to view all campus peers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                viewModel.setStudentSearchQuery("")
                                viewModel.setStudentBranchFilter(null)
                                viewModel.setStudentSkillFilter(null)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Peer Filters", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(discoveredStudents) { (student, matchPercent) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("student_card_${student.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StudentAvatar(
                                student = student,
                                size = 42.dp
                            )
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    VerificationBadge(label = "Verified")
                                }
                                Text(
                                    text = "${student.branch} • ${student.year}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        MatchScoreBadge(score = matchPercent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio
                    if (student.bio.isNotBlank()) {
                        Text(
                            text = student.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Skills with visible levels (Point 10 from brief: "Make skills visually obvious")
                    Text(
                        text = "Skills & Proficiency:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(student.skills) { skill ->
                            SkillBadge(skill = skill)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Availability & Looking For
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandEmerald.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "⏰ ${student.availability.summaryText()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = student.lookingFor.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Button or Dispatched Status
                    val isAlreadyInvited = sentInvitationRecipientIds.contains(student.id)
                    if (isAlreadyInvited) {
                        Surface(
                            color = BrandEmerald.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("status_invitation_sent_${student.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BrandEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "INVITATION SENT",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BrandEmerald,
                                            letterSpacing = 0.6.sp
                                        )
                                    }
                                    Surface(
                                        color = BrandAmber.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Pending",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Invitation dispatched to ${student.name} • Awaiting student response",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = { onInviteStudent(student) },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = "Resend or Update Note →",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    } else {
                        // Action Button: Invite
                        Button(
                            onClick = { onInviteStudent(student) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_invite_student_${student.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyBluePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Invite to Team", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubRecruitmentView(
    viewModel: SkillSyncViewModel,
    filteredOpps: List<Pair<Opportunity, Int>>,
    currentStudent: StudentProfile?,
    onApplyClub: (Opportunity) -> Unit
) {
    val clubApplications by viewModel.clubApplications.collectAsState()
    val clubOpps = filteredOpps.filter { it.first.category == OpportunityCategory.CLUB }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("club_recruitment_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👥", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "SGSITS CLUBS & SOCIETIES HUB",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Official Campus Recruitment 2026-27",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Direct application pipeline for technical and cultural chapters: ACM, GDSC, E-Cell, Robotics Club, Pratibimb, and Rotaract.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // My Active Applications Section
        if (clubApplications.isNotEmpty()) {
            item {
                Text(
                    text = "My Active Club Applications (${clubApplications.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(clubApplications) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = app.clubName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Role: ${app.roleApplied}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                            val statusColors = SemanticStatus.resolve(
                                when (app.status) {
                                    ApplicationStatus.ACCEPTED -> StatusType.SUCCESS
                                    ApplicationStatus.UNDER_REVIEW -> StatusType.PENDING
                                    else -> StatusType.INFO
                                },
                                isDark
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColors.background,
                                border = androidx.compose.foundation.BorderStroke(1.dp, statusColors.border)
                            ) {
                                Text(
                                    text = app.status.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColors.text,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "\"${app.sop}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                            Text(
                                text = "Application forwarded to Club Executive Committee • Status tracks live",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Club Recruitments List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Open Club Recruitment Drives (${clubOpps.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Fall Semester 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (clubOpps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("No Club Drives Found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("No clubs match your current search query.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(
                            onClick = { viewModel.setOppSearchQuery("") },
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text("Clear Search", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List of Club Opportunities
        items(clubOpps) { (opp, matchScore) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectOpportunity(opp.id) }
                    .testTag("club_card_${opp.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "👥 ${opp.organizer}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        MatchScoreBadge(score = matchScore)
                    }

                    Text(
                        text = opp.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = opp.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(14.dp))
                            Text("Deadline: ${opp.deadline}", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(14.dp))
                            Text(opp.location, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }

                    // Required Skills Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(opp.requiredSkills) { skillName ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = skillName,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Perks
                    if (opp.prizes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandEmerald.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(13.dp))
                                Text(opp.prizes, style = MaterialTheme.typography.labelSmall, color = BrandEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Actions with strong visual hierarchy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onApplyClub(opp) },
                            modifier = Modifier
                                .weight(1.3f)
                                .defaultMinSize(minHeight = 44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Apply to Club", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openFindTeammates(opp.id) },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Find Squad", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VolunteeringDrivesView(
    viewModel: SkillSyncViewModel,
    filteredOpps: List<Pair<Opportunity, Int>>,
    currentStudent: StudentProfile?,
    onRegisterVolunteer: (Opportunity) -> Unit
) {
    val volunteerRegistrations by viewModel.volunteerRegistrations.collectAsState()
    val volunteerOpps = filteredOpps.filter { it.first.category == OpportunityCategory.VOLUNTEERING }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("volunteering_drives_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandEmerald,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤝", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "CAMPUS VOLUNTEERING & SERVICE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandEmerald,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Social Impact Drives & Fest Crew",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Join central campus initiatives: Aayam cultural fest volunteer taskforce, NSS 500-tree green plantation, university blood camps, and slum youth digital coding drives. Hours are verified and count towards university credit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // My Volunteer Registrations Section
        if (volunteerRegistrations.isNotEmpty()) {
            item {
                Text(
                    text = "My Volunteer Registrations (${volunteerRegistrations.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(volunteerRegistrations) { vol ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = vol.eventTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Assigned Role: ${vol.preferredRole} • ${vol.hoursAvailable}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                            val regColors = SemanticStatus.resolve(StatusType.SUCCESS, isDark)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = regColors.background,
                                border = androidx.compose.foundation.BorderStroke(1.dp, regColors.border)
                            ) {
                                Text(
                                    text = "REGISTERED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = regColors.text,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (vol.motivation.isNotBlank()) {
                            Text(
                                text = "\"${vol.motivation}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(13.dp))
                            Text(
                                text = "Official university duty leaves & volunteer certificate credited upon completion",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Available Volunteering Drives Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Active Campus Volunteering Drives (${volunteerOpps.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "NSS & Student Council",
                    style = MaterialTheme.typography.labelSmall,
                    color = SemanticStatus.Success
                )
            }
        }

        if (volunteerOpps.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("No Volunteering Drives Found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("No volunteering drives match your current search query.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(
                            onClick = { viewModel.setOppSearchQuery("") },
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text("Clear Search", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List of Volunteering Drives
        items(volunteerOpps) { (opp, matchScore) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectOpportunity(opp.id) }
                    .testTag("vol_card_${opp.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SemanticStatus.SuccessBg
                        ) {
                            Text(
                                text = "🤝 ${opp.organizer}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SemanticStatus.Success,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        MatchScoreBadge(score = matchScore)
                    }

                    Text(
                        text = opp.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = opp.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(14.dp))
                            Text("Date: ${opp.date}", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(14.dp))
                            Text(opp.location, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }

                    // Perks / Duty leave badge
                    if (opp.prizes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandEmerald.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(13.dp))
                                Text(opp.prizes, style = MaterialTheme.typography.labelSmall, color = BrandEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Actions with strong visual hierarchy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onRegisterVolunteer(opp) },
                            modifier = Modifier
                                .weight(1.3f)
                                .defaultMinSize(minHeight = 44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SemanticStatus.Success, contentColor = Color.White)
                        ) {
                            Text("Volunteer Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openFindTeammates(opp.id) },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Form Squad", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

