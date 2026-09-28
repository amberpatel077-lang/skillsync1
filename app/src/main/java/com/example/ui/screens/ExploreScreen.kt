package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen_root")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar Tabs: Opportunities Hub vs Student Discovery
            PrimaryTabRow(
                selectedTabIndex = if (uiState.exploreSubTab == ExploreSubTab.OPPORTUNITIES) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SkyBluePrimary
            ) {
                Tab(
                    selected = uiState.exploreSubTab == ExploreSubTab.OPPORTUNITIES,
                    onClick = { viewModel.setExploreSubTab(ExploreSubTab.OPPORTUNITIES) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Opportunity Hub", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_opportunity_hub")
                )
                Tab(
                    selected = uiState.exploreSubTab == ExploreSubTab.STUDENTS,
                    onClick = { viewModel.setExploreSubTab(ExploreSubTab.STUDENTS) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Student Discovery", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_student_discovery")
                )
            }

            if (uiState.exploreSubTab == ExploreSubTab.OPPORTUNITIES) {
                // OPPORTUNITY HUB VIEW
                OpportunityHubView(
                    viewModel = viewModel,
                    filteredOpps = filteredOpps,
                    currentStudent = currentStudent,
                    uiState = uiState
                )
            } else {
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
        }

        // Floating Action Button to post an opportunity
        if (uiState.exploreSubTab == ExploreSubTab.OPPORTUNITIES) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(null to "All Modes", OpportunityMode.ONLINE to "🌐 Online", OpportunityMode.OFFLINE to "📍 In-Person", OpportunityMode.HYBRID to "⚡ Hybrid").forEach { (mode, title) ->
                    val isSelected = uiState.selectedModeFilter == mode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setModeFilter(mode) },
                        color = if (isSelected) BrandIndigo.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo) else null
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
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
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No opportunities found", fontWeight = FontWeight.Bold)
                        Text("Try resetting filters or searching with different keywords.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            color = BrandIndigo.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = student.lookingFor.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandIndigo,
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
