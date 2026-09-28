package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
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


        // Section: Explore Campus Opportunities (Bigger, Full-Width Mobile Bars)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = SkyBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Explore Campus Opportunities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Discover clubs, volunteer drives, teammates & competitions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.setOppSearchQuery("")
                            viewModel.setExploreSubTab(ExploreSubTab.OPPORTUNITIES)
                            viewModel.setTab(AppTab.EXPLORE)
                        },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Browse Hub",
                            color = SkyBluePrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Zero gap between heading and opportunities: 2 in a row
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp)
                ) {
                    // Row 1: Clubs & Chapters | Campus Volunteering
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampusOpportunityBar(
                            title = "Clubs & Chapters",
                            badgeText = "SGSITS Inductions",
                            liveStatusText = "Active",
                            description = "Join ACM, GDSC, Club inductions & core roles",
                            emoji = "👥",
                            accentColor = BrandIndigo,
                            actionLabel = "Explore",
                            quickTags = listOf("ACM", "GDSC"),
                            testTag = "btn_shortcut_clubs",
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setExploreSubTab(ExploreSubTab.CLUBS)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            onTagClick = { tag ->
                                viewModel.setExploreSubTab(ExploreSubTab.CLUBS)
                                viewModel.setOppSearchQuery(tag)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        CampusOpportunityBar(
                            title = "Volunteering",
                            badgeText = "Activity Credits",
                            liveStatusText = "5 Drives",
                            description = "NSS, Aayam festival teams & campus outreach",
                            emoji = "🤝",
                            accentColor = BrandEmerald,
                            actionLabel = "Volunteer",
                            quickTags = listOf("NSS", "Aayam"),
                            testTag = "btn_shortcut_volunteering",
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setExploreSubTab(ExploreSubTab.VOLUNTEERING)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            onTagClick = { tag ->
                                viewModel.setExploreSubTab(ExploreSubTab.VOLUNTEERING)
                                viewModel.setOppSearchQuery(tag)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    // Row 2: Find Teammates | Hackathons & Challenges
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampusOpportunityBar(
                            title = "Find Teammates",
                            badgeText = "Skill Matching",
                            liveStatusText = "Instant",
                            description = "Connect with SGSITS peers for hackathon squads",
                            emoji = "🔍",
                            accentColor = SkyBlueDark,
                            actionLabel = "Connect",
                            quickTags = listOf("AI / ML", "UI/UX"),
                            testTag = "btn_shortcut_peers",
                            onClick = {
                                viewModel.setStudentSearchQuery("")
                                viewModel.setExploreSubTab(ExploreSubTab.STUDENTS)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            onTagClick = { tag ->
                                viewModel.setExploreSubTab(ExploreSubTab.STUDENTS)
                                viewModel.setStudentSearchQuery(tag)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        CampusOpportunityBar(
                            title = "Hackathons",
                            badgeText = "Live Contests",
                            liveStatusText = "SIH 2026",
                            description = "Smart India Hackathon, college techfests & coding",
                            emoji = "🏆",
                            accentColor = BrandAmber,
                            actionLabel = "Compete",
                            quickTags = listOf("SIH 2026", "TechFest"),
                            testTag = "btn_shortcut_opportunities",
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setExploreSubTab(ExploreSubTab.HACKATHONS)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            onTagClick = { tag ->
                                viewModel.setExploreSubTab(ExploreSubTab.HACKATHONS)
                                viewModel.setOppSearchQuery(tag)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    // Row 3: My Teams & Workspaces | Research & Lab Projects
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampusOpportunityBar(
                            title = "My Teams",
                            badgeText = "Collaboration",
                            liveStatusText = "Live Sync",
                            description = "Manage joined squads, tasks, chat & milestones",
                            emoji = "⚡",
                            accentColor = BrandPurple,
                            actionLabel = "Open",
                            quickTags = listOf("Sprint", "Squad Chat"),
                            testTag = "btn_shortcut_teams",
                            onClick = {
                                viewModel.setTab(AppTab.TEAMS)
                            },
                            onTagClick = { _ ->
                                viewModel.setTab(AppTab.TEAMS)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        CampusOpportunityBar(
                            title = "Research & Labs",
                            badgeText = "Faculty Projects",
                            liveStatusText = "Grants Open",
                            description = "Join professor research groups, IoT labs & grants",
                            emoji = "🔬",
                            accentColor = Color(0xFF00897B),
                            actionLabel = "Apply",
                            quickTags = listOf("AI Lab", "IoT"),
                            testTag = "btn_shortcut_research",
                            onClick = {
                                viewModel.setOppSearchQuery("")
                                viewModel.setExploreSubTab(ExploreSubTab.RESEARCH)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            onTagClick = { tag ->
                                viewModel.setExploreSubTab(ExploreSubTab.RESEARCH)
                                viewModel.setOppSearchQuery(tag)
                                viewModel.setTab(AppTab.EXPLORE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
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

@Composable
fun CampusOpportunityBar(
    title: String,
    badgeText: String,
    description: String,
    emoji: String,
    accentColor: Color,
    testTag: String,
    liveStatusText: String? = null,
    quickTags: List<String> = emptyList(),
    actionLabel: String = "Explore",
    onClick: () -> Unit,
    onTagClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "barScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 1.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "barElevation"
    )

    val arrowOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "arrowOffset"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = if (isPressed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isPressed) 1.5.dp else 1.dp,
            color = if (isPressed) accentColor else accentColor.copy(alpha = 0.32f)
        ),
        shadowElevation = shadowElevation,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(11.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Row: Leading Emoji Container + Live Pulsing Activity Beacon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accentColor.copy(alpha = if (isPressed) 0.22f else 0.12f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = if (isPressed) 0.45f else 0.25f)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 20.sp
                            )
                        }
                    }

                    if (liveStatusText != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = pulseAlpha))
                            )
                            Text(
                                text = liveStatusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                // Badge Tag
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = accentColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.5.sp,
                    lineHeight = 14.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Quick Tags (clickable pills)
                if (quickTags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        quickTags.take(2).forEach { tag ->
                            Surface(
                                onClick = {
                                    if (onTagClick != null) {
                                        onTagClick(tag)
                                    } else {
                                        onClick()
                                    }
                                },
                                shape = RoundedCornerShape(5.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                border = BorderStroke(0.6.dp, accentColor.copy(alpha = 0.28f))
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Column {
                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Action CTA Button with animated arrow nudge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPressed) accentColor else accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = if (isPressed) 0.5f else 0.28f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(29.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPressed) Color.White else accentColor,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open $title",
                            tint = if (isPressed) Color.White else accentColor,
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = arrowOffset)
                        )
                    }
                }
            }
        }
    }
}

