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

@Composable
fun GoogleFormSurveyDialog(
    viewModel: SkillSyncViewModel,
    onDismiss: () -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsState()

    var branch by remember { mutableStateOf(currentStudent?.branch ?: "Computer Science & Engineering") }
    var year by remember { mutableStateOf(currentStudent?.year ?: "2nd Year") }
    var missedOppDueToNoTeam by remember { mutableStateOf(true) }
    var difficultyRating by remember { mutableStateOf(4) } // 1 to 5

    val allChannels = listOf(
        "Personal Friends / Classmates",
        "Informal WhatsApp Groups",
        "College Groups",
        "Participate Alone / Miss Out",
        "Instagram / Social Media",
        "LinkedIn"
    )
    val selectedChannels = remember { mutableStateListOf("Personal Friends / Classmates", "Informal WhatsApp Groups") }

    val allObstacles = listOf(
        "Don't know who is interested on campus",
        "Difficult to contact suitable people",
        "Can't find right technical skills (AI, CAD, Web)",
        "Don't know people's actual skill levels",
        "Hard to find people from other branches (ECE, EE, Mech)",
        "Lack of timely information about college events",
        "People don't respond on WhatsApp"
    )
    val selectedObstacles = remember {
        mutableStateListOf(
            "Don't know who is interested on campus",
            "Can't find right technical skills (AI, CAD, Web)",
            "Hard to find people from other branches (ECE, EE, Mech)"
        )
    }

    val allOpportunities = listOf(
        "Internships",
        "Academic & Capstone Projects",
        "Hackathons (SIH, collegiate hackathons)",
        "Club Recruitment & Societies",
        "Startup / Entrepreneurship Cohorts",
        "Sports Events & Tournaments",
        "College Fests (Aayam)",
        "Volunteering Drives"
    )
    val selectedOpportunities = remember {
        mutableStateListOf("Hackathons (SIH, collegiate hackathons)", "Club Recruitment & Societies", "Internships")
    }

    val allClubs = listOf(
        "ACM Student Chapter",
        "GDSC (Google Developer Student Club)",
        "E-Cell SGSITS",
        "Robotics Club",
        "Rotaract / NSS",
        "Pratibimb Creative & Arts",
        "Sports Club"
    )
    val selectedClubs = remember { mutableStateListOf("ACM Student Chapter", "GDSC (Google Developer Student Club)") }

    val allVolunteering = listOf(
        "Aayam Cultural Fest Taskforce",
        "NSS Green Campus & Tree Plantation",
        "Mega Blood Donation Camp",
        "Slum Digital Literacy & Coding for Kids",
        "E-Waste Recycling & Campus Cleanliness"
    )
    val selectedVolunteering = remember { mutableStateListOf("Aayam Cultural Fest Taskforce", "NSS Green Campus & Tree Plantation") }

    var wantsMentorship by remember { mutableStateOf(true) }
    var suggestions by remember {
        mutableStateOf("SkillSync should help connect juniors with seniors for mentorship in hackathons and club recruitment.")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("google_form_survey_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPurple.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📋", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Campus Voice Survey Form",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Student Skills, Team Formation, Volunteering & Club Recruitment",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Introduction banner
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Your voice shapes campus opportunities! 🎓",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "This questionnaire mirrors the college-wide field survey at SGSITS Indore on student skill gaps, cross-branch teaming, club selection, and community volunteering.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Section 1: Demographics
                    item {
                        Text(
                            text = "1. Academic Profile",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SkyBluePrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Branch", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(branch, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Year of Study", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(year, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Section 2: Team Formation Hurdles
                    item {
                        Text(
                            text = "2. Team Formation & Opportunities",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandRose
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Have you ever missed a hackathon, project, or competition due to lack of teammates?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                            FilterChip(
                                selected = missedOppDueToNoTeam,
                                onClick = { missedOppDueToNoTeam = true },
                                label = { Text("🚨 Yes, missed opportunities") }
                            )
                            FilterChip(
                                selected = !missedOppDueToNoTeam,
                                onClick = { missedOppDueToNoTeam = false },
                                label = { Text("No, found teams") }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "How difficult is it to find suitable teammates on campus? (1 = Very Easy, 5 = Very Difficult)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            (1..5).forEach { rate ->
                                FilterChip(
                                    selected = difficultyRating == rate,
                                    onClick = { difficultyRating = rate },
                                    label = { Text("$rate ★") }
                                )
                            }
                        }
                    }

                    // Section 3: Current channels used
                    item {
                        Text(
                            text = "3. Current Teammate Sourcing Channels",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlueDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            allChannels.forEach { channel ->
                                val isChecked = selectedChannels.contains(channel)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedChannels.remove(channel)
                                            else selectedChannels.add(channel)
                                        }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedChannels.add(channel) else selectedChannels.remove(channel)
                                        }
                                    )
                                    Text(text = channel, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Section 4: Key Obstacles Faced
                    item {
                        Text(
                            text = "4. Key Obstacles Faced When Finding Peers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            allObstacles.forEach { obs ->
                                val isChecked = selectedObstacles.contains(obs)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedObstacles.remove(obs)
                                            else selectedObstacles.add(obs)
                                        }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedObstacles.add(obs) else selectedObstacles.remove(obs)
                                        }
                                    )
                                    Text(text = obs, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Section 5: Opportunities & Clubs
                    item {
                        Text(
                            text = "5. Which Opportunities Would You Use SkillSync For?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            allOpportunities.forEach { opp ->
                                val isChecked = selectedOpportunities.contains(opp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedOpportunities.remove(opp)
                                            else selectedOpportunities.add(opp)
                                        }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedOpportunities.add(opp) else selectedOpportunities.remove(opp)
                                        }
                                    )
                                    Text(text = opp, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Section 6: Club Recruitment Interest
                    item {
                        Text(
                            text = "6. Clubs & Societies You Want to Join",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SkyBluePrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            allClubs.forEach { club ->
                                val isChecked = selectedClubs.contains(club)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedClubs.remove(club)
                                            else selectedClubs.add(club)
                                        }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedClubs.add(club) else selectedClubs.remove(club)
                                        }
                                    )
                                    Text(text = club, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Section 7: Volunteering Drives
                    item {
                        Text(
                            text = "7. Campus Volunteering Drives Interest",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            allVolunteering.forEach { drive ->
                                val isChecked = selectedVolunteering.contains(drive)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedVolunteering.remove(drive)
                                            else selectedVolunteering.add(drive)
                                        }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedVolunteering.add(drive) else selectedVolunteering.remove(drive)
                                        }
                                    )
                                    Text(text = drive, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Section 8: Mixed-Skill Mentorship
                    item {
                        Text(
                            text = "8. Mixed-Skill Mentorship Pairing",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Switch(
                                checked = wantsMentorship,
                                onCheckedChange = { wantsMentorship = it }
                            )
                            Text(
                                text = "Pair with seniors/juniors of mixed skill levels to learn from each other.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Section 9: Suggestions & Open Voice
                    item {
                        Text(
                            text = "9. Open Student Voice & Suggestions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = suggestions,
                            onValueChange = { suggestions = it },
                            label = { Text("What other features or solutions would help you?") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }

                // Bottom submission action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            viewModel.submitGoogleFormSurvey(
                                missedOppDueToNoTeam = missedOppDueToNoTeam,
                                difficultyRating = difficultyRating,
                                channelsUsed = selectedChannels.toList(),
                                obstaclesFaced = selectedObstacles.toList(),
                                opportunitiesWanted = selectedOpportunities.toList(),
                                clubsWanted = selectedClubs.toList(),
                                volunteeringInterests = selectedVolunteering.toList(),
                                wantsMentorship = wantsMentorship,
                                feedback = suggestions
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Survey 📋", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
