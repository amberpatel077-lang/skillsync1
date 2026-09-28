package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.StudentQuote
import com.example.model.SurveyChartItem
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.setTab(AppTab.HOME)
    }

    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("Core Metrics", "Opportunity Demand", "Demographics", "Student Voice")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("survey_screen_root"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Back navigation bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.setTab(AppTab.HOME) },
                    modifier = Modifier.testTag("survey_btn_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Research & Survey Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        // Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SkyBluePrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Poll,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CAMPUS RESEARCH INSIGHTS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SkyBlueDark,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "57 Verified Responses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Student Skills, Team Formation, Volunteering & Club Recruitment",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Field survey of undergraduate engineering students at SGSITS Indore revealing critical gaps in finding multidisciplinary teammates and discovering college opportunities.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Highlight Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HighlightChip(
                            title = "70.2%",
                            subtitle = "Missed Teams",
                            color = BrandRose,
                            modifier = Modifier.weight(1f)
                        )
                        HighlightChip(
                            title = "89.5%",
                            subtitle = "Team Struggle",
                            color = BrandAmber,
                            modifier = Modifier.weight(1f)
                        )
                        HighlightChip(
                            title = "87.7%",
                            subtitle = "Want Skill Sync",
                            color = SkyBluePrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SkyBluePrimary,
                edgePadding = 16.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                sections.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSection == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        when (selectedSection) {
            0 -> {
                // Section 0: Core Metrics & Pain Points
                item {
                    SurveyCard(
                        title = "🚨 Have you ever missed an opportunity due to no team?",
                        description = "Over 70% of students were forced to sit out hackathons, startup pitches, or projects simply because they couldn't find teammates in time.",
                        chartItems = listOf(
                            SurveyChartItem("Yes (Missed Opportunity)", 70.2f, 40, "Major barrier to student growth"),
                            SurveyChartItem("No", 29.8f, 17, "Had pre-existing contacts")
                        ),
                        accentColor = BrandRose
                    )
                }

                item {
                    SurveyCard(
                        title = "⚠️ Difficulty Finding Suitable Teammates",
                        description = "How hard is it to find compatible peers with the right technical skills and availability on campus?",
                        chartItems = listOf(
                            SurveyChartItem("Moderate to Very High Difficulty", 89.5f, 51, "50.9% Moderate • 33.3% High • 7.0% Extreme"),
                            SurveyChartItem("Low Difficulty", 10.5f, 6, "Senior circle only")
                        ),
                        accentColor = BrandAmber
                    )
                }

                item {
                    SurveyCard(
                        title = "🔍 Current Team Sourcing Channels",
                        description = "Students are currently trapped in limited peer bubbles without campus-wide visibility.",
                        chartItems = listOf(
                            SurveyChartItem("Personal Friends / Classmates", 84.2f, 48, "Restricts multidisciplinary collaboration"),
                            SurveyChartItem("Informal WhatsApp Groups", 47.4f, 27, "High noise, untracked responses"),
                            SurveyChartItem("College Groups", 38.6f, 22, "Fragmented announcements"),
                            SurveyChartItem("Participate Alone / Miss Out", 19.3f, 11, "Miss team-based events"),
                            SurveyChartItem("Instagram / Social Media", 19.3f, 11, "Informal outreach"),
                            SurveyChartItem("LinkedIn", 8.8f, 5, "Low fresher adoption")
                        ),
                        accentColor = SkyBluePrimary
                    )
                }

                item {
                    SurveyCard(
                        title = "⚡ Key Obstacles Faced",
                        description = "Top reported reasons why finding collaborators fails:",
                        chartItems = listOf(
                            SurveyChartItem("Don't know who is interested", 54.4f, 31, "SkillSync provides live availability badges"),
                            SurveyChartItem("Difficult to contact suitable people", 40.4f, 23, "Integrated chat and invitation notes"),
                            SurveyChartItem("Can't find right technical skills", 38.6f, 22, "Smart skill match breakdown %"),
                            SurveyChartItem("Don't know people's skill levels", 38.6f, 22, "Beginner / Intermediate / Advanced tags"),
                            SurveyChartItem("Hard to find other branches (Mech/ECE/EI)", 36.8f, 21, "Campus-wide cross-branch directory"),
                            SurveyChartItem("Lack of information about events", 33.3f, 19, "Consolidated Opportunity Hub"),
                            SurveyChartItem("People don't respond on WhatsApp", 22.8f, 13, "Formal application tracking pipeline")
                        ),
                        accentColor = BrandPurple
                    )
                }
            }

            1 -> {
                // Section 1: Opportunity Demand
                item {
                    SurveyCard(
                        title = "🎯 Which opportunities would you use this platform for?",
                        description = "Ranked demand across 57 undergraduate engineering respondents:",
                        chartItems = listOf(
                            SurveyChartItem("Internships", 84.2f, 48, "Highest demand across all branches"),
                            SurveyChartItem("Academic & Competitive Projects", 77.2f, 44, "Capstone, SIH, research prototypes"),
                            SurveyChartItem("Hackathons (SIH, HackNova)", 73.7f, 42, "36-48h collegiate hackathons"),
                            SurveyChartItem("Club Recruitment & Societies", 70.2f, 40, "ACM, GDSC, E-Cell, Robotics"),
                            SurveyChartItem("Startup / Entrepreneurship Cohorts", 70.2f, 40, "Incubation, seed grants, pitch decks"),
                            SurveyChartItem("Sports Events & Tournaments", 57.9f, 33, "Inter-branch cricket, football, athletics"),
                            SurveyChartItem("College Fests (Aayam)", 56.1f, 32, "Fest coordination & committees"),
                            SurveyChartItem("Technical Competitions", 52.6f, 30, "Coding, CAD, circuit design sprints"),
                            SurveyChartItem("Volunteering Drives", 42.1f, 24, "NSS, tree plantation, digital literacy")
                        ),
                        accentColor = SkyBluePrimary
                    )
                }

                item {
                    SurveyCard(
                        title = "📋 Application Tracking Feature Importance",
                        description = "How critical is real-time status tracking (Submitted, Under Review, Shortlisted)?",
                        chartItems = listOf(
                            SurveyChartItem("Important to Extremely Important", 91.2f, 52, "36.8% High • 28.1% Extreme • 26.3% Moderate"),
                            SurveyChartItem("Not Important", 8.8f, 5, "Informal preference")
                        ),
                        accentColor = BrandEmerald
                    )
                }
            }

            2 -> {
                // Section 2: Demographics
                item {
                    SurveyCard(
                        title = "🎓 Academic Year Distribution",
                        description = "Survey participation by year of study:",
                        chartItems = listOf(
                            SurveyChartItem("1st Year (Freshers)", 56.1f, 32, "Eager to explore clubs & find mentors"),
                            SurveyChartItem("2nd Year (Sophomores)", 42.1f, 24, "Actively forming hackathon & project teams"),
                            SurveyChartItem("3rd & 4th Year (Seniors)", 1.8f, 1, "Guiding projects & campus recruitment")
                        ),
                        accentColor = SkyBlueDark
                    )
                }

                item {
                    SurveyCard(
                        title = "🏢 Engineering Branch Representation",
                        description = "Multidisciplinary participation across campus departments:",
                        chartItems = listOf(
                            SurveyChartItem("Computer Science & Eng (CSE)", 22.8f, 13, "Software, AI/ML, Fullstack"),
                            SurveyChartItem("Information Technology (IT)", 19.3f, 11, "Cloud, Web, UI/UX Design"),
                            SurveyChartItem("Electronics & Communication (ECE)", 14.0f, 8, "IoT, Embedded, Telemetry"),
                            SurveyChartItem("Electrical Engineering (EE)", 14.0f, 8, "Microgrid, Power Systems, Hardware"),
                            SurveyChartItem("Electronics & Instrumentation (EI)", 12.3f, 7, "Sensors, Automation, Circuits"),
                            SurveyChartItem("Mechanical Engineering (MECH)", 7.0f, 4, "SolidWorks CAD, Robotics, Sports"),
                            SurveyChartItem("Civil & AI Robotics (AIR)", 10.6f, 6, "Structures, Drone Tech, Modeling")
                        ),
                        accentColor = SkyBluePrimary
                    )
                }
            }

            3 -> {
                // Section 3: Student Voice & Direct Solutions
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Direct Student Suggestions & Implemented Solutions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val feedbackList = listOf(
                            StudentQuote(
                                quote = "Yes, there are many students who are shy and feel fear to speak in front of people and finding difficulties to interact.",
                                category = "Introvert & Shy Student Support",
                                appSolution = "SkillSync features structured icebreaker invitation notes, direct skill matching without cold calling, and verified college profiles to remove interaction anxiety."
                            ),
                            StudentQuote(
                                quote = "You can match up the students who have low level skills than the required skills in something to the team in which other members are more skilled so that the one can learn and get experienced from them.",
                                category = "Mixed-Skill Mentorship Pairing",
                                appSolution = "SkillSync matches experienced seniors (Advanced level) with motivated beginners (Beginner level), granting teams bonus mentorship points."
                            ),
                            StudentQuote(
                                quote = "Club/team for specific skill improvement and learning like coding, tournaments and competition informations around city, state and all along like sports tournament, notification.",
                                category = "Sports & Competitions",
                                appSolution = "Dedicated 'Sports & Athletics' category along with Club Recruitment and Volunteering drives, ensuring athletes and coders alike build squads."
                            ),
                            StudentQuote(
                                quote = "Please be strict about everything don't do unnecessary things so that a basic discipline is maintained.",
                                category = "Verification & Authenticity",
                                appSolution = "College email verification and SGSITS branch badges ensure authentic student profiles and zero spam."
                            ),
                            StudentQuote(
                                quote = "Try to find different people for better networking across branches.",
                                category = "Cross-Branch Networking",
                                appSolution = "Discovery engine highlights complementary skills from EE, ECE, MECH, CSE, breaking department silos."
                            )
                        )

                        feedbackList.forEach { item ->
                            StudentVoiceCard(item = item)
                        }
                    }
                }
            }
        }

        // Call to Action Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ready to form your dream team?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SkillSync solves the 70.2% team gap by syncing verified talent across all 8 branches.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setTab(AppTab.TEAMS) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find Teammates", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.setTab(AppTab.EXPLORE) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary)
                        ) {
                            Text("Explore Hub", color = SkyBluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HighlightChip(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SurveyCard(
    title: String,
    description: String,
    chartItems: List<SurveyChartItem>,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            chartItems.forEach { item ->
                ChartBarRow(item = item, accentColor = accentColor)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ChartBarRow(
    item: SurveyChartItem,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${String.format("%.1f", item.percentage)}% (${item.count})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (item.percentage / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor)
            )
        }

        if (item.detail.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun StudentVoiceCard(item: StudentQuote) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "“${item.quote}”",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "How SkillSync Solves This:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandEmerald
                        )
                        Text(
                            text = item.appSolution,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
