package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Opportunity
import com.example.model.OpportunityCategory
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubApplicationDialog(
    opportunity: Opportunity,
    viewModel: SkillSyncViewModel,
    onDismiss: () -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val availableRoles = when {
        opportunity.title.contains("ACM", ignoreCase = true) -> listOf("Technical Associate", "Web & App Lead", "Competitive Programming Mentor", "Creative Design Lead")
        opportunity.title.contains("GDSC", ignoreCase = true) -> listOf("Android Domain Head", "AI/ML Circle Lead", "Cloud/DevOps Associate", "PR & Event Lead")
        opportunity.title.contains("E-Cell", ignoreCase = true) -> listOf("Startup Incubation Associate", "Pitch Deck Strategist", "Corporate Sponsorship Head", "Creative Designer")
        opportunity.title.contains("Robotics", ignoreCase = true) -> listOf("Embedded Systems Engineer", "Mechanical CAD Designer", "Autonomous Navigation Dev", "Hardware Lead")
        opportunity.title.contains("Pratibimb", ignoreCase = true) -> listOf("Graphic & UI Designer", "Photographer & Videographer", "Stage Decor Lead", "Content & Editorial")
        opportunity.title.contains("Rotaract", ignoreCase = true) -> listOf("Community Outreach Lead", "Blood Camp Coordinator", "Youth Leadership Officer", "Digital Media")
        else -> listOf("Core Associate", "Technical Lead", "Event Coordinator", "Design & Media")
    }

    var selectedRole by remember { mutableStateOf(availableRoles.first()) }
    var sopText by remember {
        mutableStateOf("I want to contribute my skills in ${currentStudent?.skills?.firstOrNull()?.name ?: "technology"} and gain practical leadership experience working with seniors in ${opportunity.organizer}.")
    }
    var portfolioUrl by remember { mutableStateOf("https://github.com/student-profile") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandIndigo.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👥", fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = "Apply to ${opportunity.organizer}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Official Club Recruitment 2026-27",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlueDark
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Select Preferred Domain / Role:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableRoles.forEach { role ->
                            val isSelected = role == selectedRole
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SkyBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, SkyBluePrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = role,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SkyBlueDark else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SkyBluePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = sopText,
                        onValueChange = { sopText = it },
                        label = { Text("Statement of Purpose (SOP) *") },
                        supportingText = { Text("Why do you want to join this club and what value can you bring?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                item {
                    OutlinedTextField(
                        value = portfolioUrl,
                        onValueChange = { portfolioUrl = it },
                        label = { Text("Portfolio / GitHub / Drive Link") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Your profile skills (${currentStudent?.skills?.take(3)?.joinToString { it.name } ?: "General"}), branch (${currentStudent?.branch ?: "Engineering"}), and academic year will be forwarded automatically.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.applyToClub(
                        opp = opportunity,
                        roleApplied = selectedRole,
                        sop = sopText,
                        portfolioUrl = portfolioUrl
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_club_application")
            ) {
                Text("Submit Application 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerRegistrationDialog(
    opportunity: Opportunity,
    viewModel: SkillSyncViewModel,
    onDismiss: () -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val roles = when {
        opportunity.title.contains("Aayam", ignoreCase = true) -> listOf("Stage & Acoustic Operations", "Guest Reception & VIP Escort", "Technical Lighting Logistics", "Fest Media & Photography", "Crowd & Gate Management")
        opportunity.title.contains("Green", ignoreCase = true) -> listOf("Plantation Fieldwork Lead", "E-Waste Collection Officer", "Campus Awareness Outreach", "Logistics & Disposal")
        opportunity.title.contains("Blood", ignoreCase = true) -> listOf("Donor Registration Desk", "Refreshment & Hospitality", "Donor Care & First-Aid Support", "Queue Coordination")
        opportunity.title.contains("Digital", ignoreCase = true) -> listOf("Scratch & Python Tutor", "Lab Assistant & Machine Setup", "Student Mentor & Motivator", "Content Preparer")
        else -> listOf("General Event Coordination", "Logistics & Supply Support", "Registration & Helpdesk", "Media & Documentation")
    }

    var selectedRole by remember { mutableStateOf(roles.first()) }
    var availableHours by remember { mutableStateOf("15 Hours Total") }
    var motivationText by remember {
        mutableStateOf("Excited to support campus social initiatives and earn NSS activity credit while helping organize ${opportunity.title}.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandEmerald.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🤝", fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = "Volunteer for Drive",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = opportunity.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandEmerald,
                        maxLines = 1
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Select Preferred Volunteer Role:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        roles.forEach { role ->
                            val isSelected = role == selectedRole
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) BrandEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, BrandEmerald) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = role,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandEmerald else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BrandEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = availableHours,
                        onValueChange = { availableHours = it },
                        label = { Text("Available Time Commitment") },
                        supportingText = { Text("e.g. 10 Hours on weekend, Event Days Only") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = motivationText,
                        onValueChange = { motivationText = it },
                        label = { Text("Notes / Motivation") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BrandEmerald.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Volunteering hours will be officially logged on your student profile and credited towards university activity certification and duty leaves.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.registerForVolunteering(
                        opp = opportunity,
                        preferredRole = selectedRole,
                        hoursAvailable = availableHours,
                        motivation = motivationText
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_volunteer_registration")
            ) {
                Text("Confirm Registration 🤝", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

