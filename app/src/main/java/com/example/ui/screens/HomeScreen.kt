package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.viewmodel.AppTab
import com.example.viewmodel.ExploreSubTab
import com.example.viewmodel.SkillSyncViewModel

@Composable
fun HomeScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val allStudents by viewModel.students.collectAsState()
    val recommendedOpps by viewModel.recommendedOpportunities.collectAsState()
    val activeTeam by viewModel.activeTeam.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showStudentSwitcher by remember { mutableStateOf(false) }
    var showResearchSidePanel by remember { mutableStateOf(false) }

    BackHandler(enabled = showResearchSidePanel) {
        showResearchSidePanel = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_column"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
        // App Top Brand Bar with Theme Logo
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo (Only the logo)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_skillsync_logo),
                        contentDescription = "SkillSync Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Theme Toggle Button in top corner
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clickable { viewModel.toggleTheme() }
                            .testTag("btn_quick_theme_toggle")
                    ) {
                        Box(
                            modifier = Modifier.padding(7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Light/Dark Theme",
                                tint = if (uiState.isDarkTheme) BrandAmber else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Navigation Panel Button at Corner Above of one side
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { showResearchSidePanel = true }
                            .testTag("top_corner_research_panel_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Research",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                            Icon(
                                imageVector = Icons.Default.MenuOpen,
                                contentDescription = "Open Research Navigation Panel",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hero Visual Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_collaboration),
                    contentDescription = "Students collaborating on tech projects",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Scrim adapted to theme background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Banner Overlay Text
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = BrandIndigo.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "CONNECT • COLLABORATE • GROW",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandIndigo,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Turn Interests Into Impact",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Smart skill-based matching for college hackathons & projects",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Student Profile Header & Persona Switcher ("Who am I?")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                student = currentStudent,
                                size = 46.dp,
                                modifier = Modifier.clickable { viewModel.setTab(AppTab.PROFILE) }
                            )

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentStudent?.name ?: "Student Profile",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    VerificationBadge(label = "Verified")
                                }
                                Text(
                                    text = "${currentStudent?.branch ?: "CSE"} • ${currentStudent?.year ?: "2nd Year"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Switch Profile Button
                            OutlinedButton(
                                onClick = { showStudentSwitcher = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_switch_student")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch", fontSize = 11.sp)
                            }

                            // Logout Button
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("btn_home_logout")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // My Visible Skills
                    Text(
                        text = "My Skills & Verified Levels:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(currentStudent?.skills ?: emptyList()) { skill ->
                            SkillBadge(skill = skill, isHighlighted = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Availability & Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandEmerald.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BrandEmerald)
                                )
                                Text(
                                    text = "Available: ${currentStudent?.availability?.summaryText() ?: "Active"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandEmerald,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }


        // Quick Campus Discovery Shortcuts (Clubs, Volunteering, Teammates, Hackathons)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "Explore Campus Opportunities",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Club Recruitment Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandIndigo.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setTab(AppTab.EXPLORE)
                                viewModel.setExploreSubTab(ExploreSubTab.CLUBS)
                            }
                            .testTag("btn_shortcut_clubs")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("👥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Clubs", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BrandIndigo)
                            Text("ACM • GDSC", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                        }
                    }

                    // Volunteering Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandEmerald.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setTab(AppTab.EXPLORE)
                                viewModel.setExploreSubTab(ExploreSubTab.VOLUNTEERING)
                            }
                            .testTag("btn_shortcut_volunteering")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🤝", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Volunteer", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BrandEmerald)
                            Text("NSS • Aayam", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                        }
                    }

                    // Find Teammates Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SkyBluePrimary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setTab(AppTab.EXPLORE)
                                viewModel.setExploreSubTab(ExploreSubTab.STUDENTS)
                            }
                            .testTag("btn_shortcut_peers")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Find Peers", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SkyBlueDark)
                            Text("All Branches", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                        }
                    }

                    // Teams Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandPurple.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setTab(AppTab.TEAMS)
                            }
                            .testTag("btn_shortcut_teams")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("My Teams", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BrandPurple)
                            Text("Workspace", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Campus Voice Survey Banner (Google Form questionnaire)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandPurple.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📋", fontSize = 22.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Take Campus Voice Survey",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Submit responses on student skills, team formation, volunteering & clubs directly in-app.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.openSurveyForm() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_home_take_survey")
                    ) {
                        Text("Fill Form", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: "⭐ Recommended for You"
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "⭐", fontSize = 16.sp)
                            Text(
                                text = "Recommended for You",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "AI-matched to your skills, interests & schedule",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(onClick = { viewModel.setTab(AppTab.EXPLORE) }) {
                        Text("View All", color = SkyBluePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Recommended Opportunities Cards
        items(recommendedOpps) { (opp, score) ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OpportunityCard(
                    opportunity = opp,
                    matchScore = score,
                    currentStudent = currentStudent,
                    onCardClick = { viewModel.selectOpportunity(opp.id) },
                    onFindTeammatesClick = { viewModel.openFindTeammates(opp.id) }
                )
            }
        }

        // Quick Active Team Reminder
        if (activeTeam != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable { viewModel.setTab(AppTab.TEAMS) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = activeTeam?.name ?: "Active Team",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Workspace: ${activeTeam?.opportunityTitle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Student Switcher Dialog
    if (showStudentSwitcher) {
        AlertDialog(
            onDismissRequest = { showStudentSwitcher = false },
            title = {
                Text(
                    text = "Switch Student Persona",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select a student to see how matching scores and recommendations dynamically change for different skill sets:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    allStudents.forEach { student ->
                        val isSelected = student.id == currentStudent?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.switchStudent(student.id)
                                    showStudentSwitcher = false
                                },
                            color = if (isSelected) SkyBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${student.branch} • ${student.skills.take(2).joinToString(", ") { it.name }}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SkyBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStudentSwitcher = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal Scrim for Side Navigation Panel
    if (showResearchSidePanel) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { showResearchSidePanel = false }
                .testTag("research_panel_scrim")
        )
    }

    // Side Navigation Panel at the corner above of one side
    AnimatedVisibility(
        visible = showResearchSidePanel,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        DesignThinkingResearchSidePanel(
            onClose = { showResearchSidePanel = false },
            onNavigateToSurvey = {
                showResearchSidePanel = false
                viewModel.setTab(AppTab.SURVEY)
            },
            onNavigateToStudents = {
                showResearchSidePanel = false
                viewModel.setExploreSubTab(ExploreSubTab.STUDENTS)
                viewModel.setTab(AppTab.EXPLORE)
            },
            onNavigateToTeams = {
                showResearchSidePanel = false
                viewModel.setTab(AppTab.TEAMS)
            },
            onNavigateToOpportunities = {
                showResearchSidePanel = false
                viewModel.setExploreSubTab(ExploreSubTab.OPPORTUNITIES)
                viewModel.setTab(AppTab.EXPLORE)
            }
        )
    }
}
}

@Composable
fun DesignThinkingResearchSidePanel(
    onClose: () -> Unit,
    onNavigateToSurvey: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToOpportunities: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(max = 340.dp)
            .fillMaxWidth(0.85f)
            .testTag("research_side_navigation_panel"),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Panel Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SkyBluePale)
                            .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = SkyBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Research Panel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Design Thinking Study",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_close_research_panel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Panel",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Study Context Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SGSITS Indore Campus Study",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "57 engineering students surveyed across CSE, IT, ECE, Mech, and Civil branches exploring hackathon team formation barriers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Key Research Findings Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Key Research Insights",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                ResearchPanelStatCard(
                    metric = "70.2%",
                    title = "Team Gap Barrier",
                    description = "Missed deadlines and hackathon submissions due to missing skills in their teams.",
                    accentColor = MaterialTheme.colorScheme.primary
                )

                ResearchPanelStatCard(
                    metric = "87.7%",
                    title = "Visible Verified Skills",
                    description = "Students strongly require a public directory showing validated technical competencies.",
                    accentColor = BrandIndigo
                )

                ResearchPanelStatCard(
                    metric = "84.2%",
                    title = "Limited Friend Circles",
                    description = "Currently rely exclusively on immediate classmates and friends to form teams.",
                    accentColor = BrandEmerald
                )

                ResearchPanelStatCard(
                    metric = "63.2%",
                    title = "Discovery Disconnect",
                    description = "Unaware of available club recruitments, NSS drives, and research project openings.",
                    accentColor = BrandAmber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Actions inside the Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Explore & Navigate",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onNavigateToSurvey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_panel_open_survey"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Poll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Full Survey & Analytics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                PanelNavigationCard(
                    title = "Verified Student Directory",
                    subtitle = "Browse peers by skill & branch",
                    icon = Icons.Default.People,
                    accentColor = SkyBlueDark,
                    onClick = onNavigateToStudents
                )

                PanelNavigationCard(
                    title = "Team Formation Matcher",
                    subtitle = "Complementary skill match scoring",
                    icon = Icons.Default.GroupAdd,
                    accentColor = BrandIndigo,
                    onClick = onNavigateToTeams
                )

                PanelNavigationCard(
                    title = "Campus Hackathons & Fests",
                    subtitle = "Discover active club & college drives",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = BrandEmerald,
                    onClick = onNavigateToOpportunities
                )
            }
        }
    }
}

@Composable
fun ResearchPanelStatCard(
    metric: String,
    title: String,
    description: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = metric,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                fontSize = 16.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun PanelNavigationCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
