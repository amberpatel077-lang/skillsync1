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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val student = currentStudent ?: return

    var showAddSkillDialog by remember { mutableStateOf(false) }
    var newSkillName by remember { mutableStateOf("") }
    var newSkillLevel by remember { mutableStateOf(SkillLevel.INTERMEDIATE) }

    var showEditAvailabilityDialog by remember { mutableStateOf(false) }
    var availWeekdays by remember { mutableStateOf(student.availability.weekdays) }
    var availWeekends by remember { mutableStateOf(student.availability.weekends) }
    var availEvenings by remember { mutableStateOf(student.availability.evenings) }
    var availFullTime by remember { mutableStateOf(student.availability.fullTimeEvent) }
    var availUntil by remember { mutableStateOf(student.availability.availableUntil) }

    var showAddProjectDialog by remember { mutableStateOf(false) }
    var newProjTitle by remember { mutableStateOf("") }
    var newProjDesc by remember { mutableStateOf("") }
    var newProjStack by remember { mutableStateOf("") }
    var newProjRole by remember { mutableStateOf("") }

    var showTrustSafetyDialog by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember(student) { mutableStateOf(student.name) }
    var editCollege by remember(student) { mutableStateOf(student.college) }
    var editBranch by remember(student) { mutableStateOf(student.branch) }
    var editYear by remember(student) { mutableStateOf(student.year) }
    var editBio by remember(student) { mutableStateOf(student.bio) }
    var editLookingFor by remember(student) { mutableStateOf(student.lookingFor) }
    var editContact by remember(student) { mutableStateOf(student.contactPreference) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_root"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Identity Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = student.name.take(1),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = BrandCyan
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                VerificationBadge(label = "SGSITS Verified")
                            }
                            Text(
                                text = "${student.college}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${student.branch} • ${student.year}",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrandCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = student.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contact & Looking For
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandIndigo.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🎯 ${student.lookingFor.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandIndigo,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "💬 ${student.contactPreference}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            editName = student.name
                            editCollege = student.college
                            editBranch = student.branch
                            editYear = student.year
                            editBio = student.bio
                            editLookingFor = student.lookingFor
                            editContact = student.contactPreference
                            showEditProfileDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_edit_profile_dialog"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Profile Details", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Section: Structured Skills & Levels (Points 1 & 10)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Verified Skills & Proficiency ⭐",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Used by SkillSync to calculate complementary team matches",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { showAddSkillDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Skill", tint = BrandCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        student.skills.forEach { skill ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = skill.iconEmoji, fontSize = 18.sp)
                                        Text(
                                            text = skill.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = when (skill.level) {
                                                SkillLevel.ADVANCED -> BrandEmerald.copy(alpha = 0.2f)
                                                SkillLevel.INTERMEDIATE -> BrandCyan.copy(alpha = 0.2f)
                                                SkillLevel.BEGINNER -> BrandAmber.copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = skill.level.displayName,
                                                color = when (skill.level) {
                                                    SkillLevel.ADVANCED -> BrandEmerald
                                                    SkillLevel.INTERMEDIATE -> BrandCyan
                                                    SkillLevel.BEGINNER -> BrandAmber
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val updatedSkills = student.skills.filter { it.name != skill.name }
                                                viewModel.updateProfile(student.copy(skills = updatedSkills))
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Availability & Schedule (Point 8 from user brief)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Availability & Schedule ⏰",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ensures matched teammates have compatible work schedules",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(onClick = {
                            availWeekdays = student.availability.weekdays
                            availWeekends = student.availability.weekends
                            availEvenings = student.availability.evenings
                            availFullTime = student.availability.fullTimeEvent
                            availUntil = student.availability.availableUntil
                            showEditAvailabilityDialog = true
                        }) {
                            Text("Edit", color = BrandCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Weekdays" to student.availability.weekdays,
                            "Weekends" to student.availability.weekends,
                            "Evenings" to student.availability.evenings,
                            "Full-Time" to student.availability.fullTimeEvent
                        ).forEach { (label, isActive) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isActive) BrandEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isActive) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isActive) BrandEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isActive) BrandEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Available until: ${student.availability.availableUntil}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Interests & Domains
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Interests & Domain Passions 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(student.interests) { interest ->
                            Surface(
                                color = BrandIndigo.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = interest,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandIndigo,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Projects & Experience
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Previous Projects & Portfolio 💼",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddProjectDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Project", tint = BrandCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (student.projects.isEmpty()) {
                        Text("No projects added yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        student.projects.forEach { proj ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(proj.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text(proj.role, color = BrandCyan, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(proj.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Stack: ${proj.techStack}", style = MaterialTheme.typography.labelSmall, color = BrandIndigo)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Trust & Safety (Point 11 from brief)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTrustSafetyDialog = true },
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f))
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
                        Icon(Icons.Default.Security, contentDescription = null, tint = BrandEmerald)
                        Column {
                            Text("Trust & Safety Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Verification badges, privacy controls & reporting", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Section: Account & Session Management
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_profile_account"),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = BrandCyan)
                        Text(
                            text = "Account & Authentication",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Logged in as ${student.name} (${student.email})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_profile_logout"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out / Switch Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Add Skill Dialog
    if (showAddSkillDialog) {
        AlertDialog(
            onDismissRequest = { showAddSkillDialog = false },
            title = { Text("Add New Skill", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSkillName,
                        onValueChange = { newSkillName = it },
                        label = { Text("Skill name (e.g. Flutter, Kotlin, CAD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Proficiency Level:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SkillLevel.values().forEach { level ->
                            val isSel = newSkillLevel == level
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { newSkillLevel = level },
                                color = if (isSel) BrandCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, BrandCyan) else null
                            ) {
                                Text(
                                    text = level.displayName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSkillName.isNotBlank()) {
                            val updated = student.skills + SkillEntry(newSkillName.trim(), newSkillLevel)
                            viewModel.updateProfile(student.copy(skills = updated))
                            newSkillName = ""
                            showAddSkillDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Add Skill", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSkillDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Availability Dialog
    if (showEditAvailabilityDialog) {
        AlertDialog(
            onDismissRequest = { showEditAvailabilityDialog = false },
            title = { Text("Update Availability", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = availWeekdays, onCheckedChange = { availWeekdays = it })
                        Text("Available Weekdays")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = availWeekends, onCheckedChange = { availWeekends = it })
                        Text("Available Weekends")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = availEvenings, onCheckedChange = { availEvenings = it })
                        Text("Available Evenings")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = availFullTime, onCheckedChange = { availFullTime = it })
                        Text("Available for Full-time Hackathons")
                    }
                    OutlinedTextField(
                        value = availUntil,
                        onValueChange = { availUntil = it },
                        label = { Text("Available until date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newAvail = Availability(
                            weekdays = availWeekdays,
                            weekends = availWeekends,
                            evenings = availEvenings,
                            fullTimeEvent = availFullTime,
                            availableUntil = availUntil
                        )
                        viewModel.updateProfile(student.copy(availability = newAvail))
                        showEditAvailabilityDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Save Availability", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAvailabilityDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Project Dialog
    if (showAddProjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            title = { Text("Add Project / Experience", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProjTitle,
                        onValueChange = { newProjTitle = it },
                        label = { Text("Project Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProjRole,
                        onValueChange = { newProjRole = it },
                        label = { Text("Your Role (e.g. Frontend Lead)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProjStack,
                        onValueChange = { newProjStack = it },
                        label = { Text("Tech Stack (e.g. Python, Docker)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProjDesc,
                        onValueChange = { newProjDesc = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjTitle.isNotBlank()) {
                            val newProject = ProjectExperience(
                                title = newProjTitle,
                                description = newProjDesc,
                                techStack = newProjStack,
                                role = newProjRole
                            )
                            val updated = student.projects + newProject
                            viewModel.updateProfile(student.copy(projects = updated))
                            newProjTitle = ""
                            newProjDesc = ""
                            newProjStack = ""
                            newProjRole = ""
                            showAddProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Add Project", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Trust & Safety Dialog
    if (showTrustSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showTrustSafetyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrandEmerald)
                    Text("Trust & Safety Settings", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("• Verified College Email: ${student.email}", style = MaterialTheme.typography.bodySmall)
                    Text("• Verified Institution: ${student.college}", style = MaterialTheme.typography.bodySmall)
                    Text("• Branch Verification: ${student.branch} (SGSITS Reg. Confirmed)", style = MaterialTheme.typography.bodySmall)
                    Text("• Profile Visibility: Public to registered college students only", style = MaterialTheme.typography.bodySmall)
                    Text("• Privacy Guarantee: Contact details are only shared after you accept a team invitation.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrustSafetyDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Edit Profile Details Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BrandCyan)
                    Text("Edit Student Profile", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editCollege,
                            onValueChange = { editCollege = it },
                            label = { Text("College / University *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editBranch,
                                onValueChange = { editBranch = it },
                                label = { Text("Branch") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editYear,
                                onValueChange = { editYear = it },
                                label = { Text("Year") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Bio & Goals") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editContact,
                            onValueChange = { editContact = it },
                            label = { Text("Contact Preference") },
                            placeholder = { Text("e.g. Discord, Slack, WhatsApp") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text("Looking For:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LookingFor.entries.forEach { opt ->
                                FilterChip(
                                    selected = editLookingFor == opt,
                                    onClick = { editLookingFor = opt },
                                    label = { Text(opt.label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editCollege.isNotBlank()) {
                            viewModel.updateProfileInfo(
                                name = editName,
                                college = editCollege,
                                branch = editBranch,
                                year = editYear,
                                bio = editBio,
                                lookingFor = editLookingFor,
                                contactPreference = editContact
                            )
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
