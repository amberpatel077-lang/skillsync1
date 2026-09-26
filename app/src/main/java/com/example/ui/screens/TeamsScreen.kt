package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier
) {
    val activeTeam by viewModel.activeTeam.collectAsState()
    val allTeams by viewModel.teams.collectAsState()
    val members by viewModel.activeTeamMembers.collectAsState()
    val tasks by viewModel.activeTeamTasks.collectAsState()
    val messages by viewModel.activeTeamMessages.collectAsState()
    val resources by viewModel.activeTeamResources.collectAsState()
    val invitations by viewModel.currentStudentInvitations.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()
    val allStudents by viewModel.students.collectAsState()

    var activeSubSection by remember { mutableStateOf(0) } // 0: Workspace, 1: Invitations (${invitations.size})
    var selectedWorkspaceTab by remember { mutableStateOf(0) } // 0: Chat, 1: Tasks, 2: Members, 3: Resources

    var chatInputText by remember { mutableStateOf("") }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskAssignee by remember { mutableStateOf(currentStudent?.name ?: "Me") }
    var newTaskDue by remember { mutableStateOf("Oct 12") }

    var showAddResourceDialog by remember { mutableStateOf(false) }
    var newResourceTitle by remember { mutableStateOf("") }
    var newResourceUrl by remember { mutableStateOf("") }
    var newResourceType by remember { mutableStateOf("GitHub") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("teams_screen_root")
    ) {
        // Top Switcher: Team Workspace vs Invitations
        PrimaryTabRow(
            selectedTabIndex = activeSubSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SkyBluePrimary
        ) {
            Tab(
                selected = activeSubSection == 0,
                onClick = { activeSubSection = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Team Workspace", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_team_workspace")
            )
            Tab(
                selected = activeSubSection == 1,
                onClick = { activeSubSection = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (invitations.isNotEmpty()) "Invitations (${invitations.count { it.status == InvitationStatus.PENDING }})" else "Invitations",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                modifier = Modifier.testTag("tab_team_invitations")
            )
        }

        if (activeSubSection == 1) {
            // INVITATIONS VIEW
            InvitationsListView(
                invitations = invitations,
                onRespond = { inv, accepted -> viewModel.respondToInvitation(inv, accepted) }
            )
        } else {
            // TEAM WORKSPACE VIEW
            if (activeTeam == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active team", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Create or join a team from the Explore Opportunity feed.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                val team = activeTeam!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("workspace_scrollable_container"),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    // Team Switcher Chips (when user has multiple teams)
                    if (allTeams.size > 1) {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(allTeams) { tm ->
                                    FilterChip(
                                        selected = tm.id == team.id,
                                        onClick = { viewModel.setActiveTeam(tm.id) },
                                        label = { Text(tm.name, fontWeight = FontWeight.Bold) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Groups,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (tm.id == team.id) SkyBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Team Header Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkyBlueBorder)
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.img_teamwork_success),
                                    contentDescription = "Team Workspace",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = team.name,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "🔗 ${team.opportunityTitle}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SkyBlueDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Led by ${team.leaderName} • ${members.size}/${team.maxMembers} Members",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Find Missing Teammates Button
                                    Button(
                                        onClick = { viewModel.openFindTeammates(team.opportunityId) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SkyBluePrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("btn_workspace_find_teammates")
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Teammate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Application Status Stepper
                                ApplicationStatusStepper(
                                    currentStatus = team.status,
                                    onAdvance = { viewModel.advanceApplicationStatus(team) }
                                )
                            }
                        }
                    }
                }

                    // Workspace Section Tabs: 💬 Chat, 📋 Tasks, 👥 Members, 📁 Resources
                    item {
                        ScrollableTabRow(
                            selectedTabIndex = selectedWorkspaceTab,
                            containerColor = Color.Transparent,
                            edgePadding = 16.dp,
                            divider = {}
                        ) {
                            listOf(
                                "💬 Team Chat (${messages.size})",
                                "📋 Task List (${tasks.count { it.isCompleted }}/${tasks.size})",
                                "👥 Members (${members.size})",
                                "📁 Resources (${resources.size})"
                            ).forEachIndexed { index, title ->
                                val isSelected = selectedWorkspaceTab == index
                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedWorkspaceTab = index },
                                    color = if (isSelected) SkyBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary) else null
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SkyBlueDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Sub-tab Contents
                    when (selectedWorkspaceTab) {
                        0 -> {
                            // TEAM CHAT
                            items(messages) { msg ->
                                ChatMessageBubble(msg = msg, currentStudentId = currentStudent?.id ?: "")
                            }

                            item {
                                // Chat input row
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = chatInputText,
                                            onValueChange = { chatInputText = it },
                                            placeholder = { Text("Message team workspace...") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("input_team_chat"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedBorderColor = Color.Transparent
                                            ),
                                            singleLine = true
                                        )

                                        IconButton(
                                            onClick = {
                                                if (chatInputText.isNotBlank()) {
                                                    viewModel.sendChatMessage(chatInputText)
                                                    chatInputText = ""
                                                }
                                            },
                                            modifier = Modifier.testTag("btn_send_chat")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = "Send",
                                                tint = SkyBluePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // TASKS LIST
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Project Milestones & Deliverables",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = { showAddTaskDialog = true }) {
                                        Text("+ Add Task", color = SkyBlueDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            items(tasks) { task ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Checkbox(
                                                checked = task.isCompleted,
                                                onCheckedChange = { viewModel.toggleTask(task) },
                                                colors = CheckboxDefaults.colors(checkedColor = SkyBluePrimary)
                                            )
                                            Column {
                                                Text(
                                                    text = task.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Assigned: ${task.assignedToName} • Due: ${task.dueDate}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteTask(task.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete task",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // TEAM MEMBERS LIST
                            items(members) { member ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 5.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        val memberStudent = allStudents.firstOrNull { it.id == member.studentId }
                                        StudentAvatar(
                                            student = memberStudent ?: StudentProfile(
                                                id = member.studentId,
                                                name = member.studentName,
                                                college = "",
                                                branch = "",
                                                year = "",
                                                email = "",
                                                skills = emptyList()
                                            ),
                                            size = 42.dp
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = member.studentName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Surface(
                                                    color = SkyBluePale,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = member.role,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = SkyBlueDark,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${member.branch} • ${member.year}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Key Skills: ${member.keySkills.joinToString(", ")}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // SHARED RESOURCES & LINKS
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Workspace Resources & Docs",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = { showAddResourceDialog = true }) {
                                        Text("+ Add Link", color = SkyBlueDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            items(resources) { res ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 5.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            val icon = when (res.type) {
                                                "GitHub" -> Icons.Default.Code
                                                "Figma" -> Icons.Default.Palette
                                                else -> Icons.Default.FolderShared
                                            }
                                            Surface(
                                                shape = CircleShape,
                                                color = SkyBluePale,
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(imageVector = icon, contentDescription = null, tint = SkyBlueDark, modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Column {
                                                Text(res.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                Text(res.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Team Deliverable", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Task description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskAssignee,
                        onValueChange = { newTaskAssignee = it },
                        label = { Text("Assigned Member") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskDue,
                        onValueChange = { newTaskDue = it },
                        label = { Text("Due Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle, newTaskAssignee, newTaskDue)
                            newTaskTitle = ""
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White)
                ) {
                    Text("Add Task", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Resource Dialog
    if (showAddResourceDialog) {
        AlertDialog(
            onDismissRequest = { showAddResourceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📁", fontSize = 20.sp)
                    Text("Add Workspace Link", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newResourceTitle,
                        onValueChange = { newResourceTitle = it },
                        label = { Text("Resource Title *") },
                        placeholder = { Text("e.g. GitHub Repository, Figma Design") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newResourceUrl,
                        onValueChange = { newResourceUrl = it },
                        label = { Text("URL Link *") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Type:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GitHub", "Figma", "Drive", "Docs").forEach { type ->
                            FilterChip(
                                selected = newResourceType == type,
                                onClick = { newResourceType = type },
                                label = { Text(type) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newResourceTitle.isNotBlank() && newResourceUrl.isNotBlank()) {
                            viewModel.addTeamResource(newResourceTitle, newResourceUrl, newResourceType)
                            newResourceTitle = ""
                            newResourceUrl = ""
                            showAddResourceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White)
                ) {
                    Text("Add Resource", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddResourceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ApplicationStatusStepper(
    currentStatus: ApplicationStatus,
    onAdvance: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Application Status",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Stage ${currentStatus.stepNumber}/6: ${currentStatus.title}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlueDark
                    )
                }

                if (currentStatus != ApplicationStatus.ACCEPTED) {
                    OutlinedButton(
                        onClick = onAdvance,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Advance Step →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Progress Bar
            LinearProgressIndicator(
                progress = { currentStatus.stepNumber / 6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SkyBluePrimary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    msg: TeamChatMessage,
    currentStudentId: String
) {
    val isMe = msg.senderId == currentStudentId
    val isSystem = msg.isSystemMessage
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

    if (isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = BrandIndigo.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = msg.message,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandIndigo,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Text(
                text = "${msg.senderName} • $timeStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Surface(
                color = if (isMe) BrandIndigo else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isMe) 14.dp else 2.dp,
                    bottomEnd = if (isMe) 2.dp else 14.dp
                ),
                border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
            ) {
                Text(
                    text = msg.message,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InvitationsListView(
    invitations: List<TeamInvitation>,
    onRespond: (TeamInvitation, Boolean) -> Unit
) {
    if (invitations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.MarkEmailRead, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("No pending invitations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("When students invite you to their team, you'll see them here.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("invitations_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(invitations) { inv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = inv.teamName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = when (inv.status) {
                                    InvitationStatus.PENDING -> BrandAmber.copy(alpha = 0.15f)
                                    InvitationStatus.ACCEPTED -> BrandEmerald.copy(alpha = 0.15f)
                                    InvitationStatus.DECLINED -> BrandRose.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = inv.status.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (inv.status) {
                                        InvitationStatus.PENDING -> BrandAmber
                                        InvitationStatus.ACCEPTED -> BrandEmerald
                                        InvitationStatus.DECLINED -> BrandRose
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Opportunity: ${inv.opportunityTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SkyBlueDark
                        )
                        Text(
                            text = "Invited by ${inv.fromStudentName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (inv.note.isNotBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"${inv.note}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (inv.status == InvitationStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onRespond(inv, false) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Decline")
                                }

                                Button(
                                    onClick = { onRespond(inv, true) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Accept & Join", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
