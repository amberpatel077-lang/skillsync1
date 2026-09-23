package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun SkillBadge(
    skill: SkillEntry,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    val levelColor = when (skill.level) {
        SkillLevel.ADVANCED -> BrandEmerald
        SkillLevel.INTERMEDIATE -> BrandCyan
        SkillLevel.BEGINNER -> BrandAmber
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = if (isHighlighted) levelColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isHighlighted) androidx.compose.foundation.BorderStroke(1.dp, levelColor.copy(alpha = 0.7f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = skill.iconEmoji, fontSize = 12.sp)
            Text(
                text = skill.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(levelColor)
            )
            Text(
                text = skill.level.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = levelColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MatchScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when {
        score >= 85 -> Triple(BrandCyan.copy(alpha = 0.15f), BrandCyan, "High Match")
        score >= 70 -> Triple(BrandEmerald.copy(alpha = 0.15f), BrandEmerald, "Good Match")
        else -> Triple(BrandAmber.copy(alpha = 0.15f), BrandAmber, "Partial Fit")
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(20.dp)),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$score%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun VerificationBadge(
    label: String = "SGSITS Verified",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = BrandEmerald.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = BrandEmerald,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = BrandEmerald,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    matchScore: Int,
    currentStudent: StudentProfile?,
    onCardClick: () -> Unit,
    onFindTeammatesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("opportunity_card_${opportunity.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (opportunity.isFeatured) BrandIndigo.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Mode + Match Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = BrandIndigo.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${opportunity.category.iconEmoji} ${opportunity.category.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandIndigo,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = opportunity.mode.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                MatchScoreBadge(score = matchScore)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title and Organizer
            Text(
                text = opportunity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "By ${opportunity.organizer}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Meta Info: Dates, Team Size, Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = opportunity.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${opportunity.currentMembersCount}/${opportunity.teamSizeMax} Members",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Required Skills list with highlight if student possesses it
            Text(
                text = "Required Skills:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                opportunity.requiredSkills.take(3).forEach { skillName ->
                    val hasSkill = currentStudent?.skills?.any {
                        it.name.equals(skillName, ignoreCase = true) || skillName.contains(it.name, ignoreCase = true)
                    } ?: false

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (hasSkill) BrandCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (hasSkill) androidx.compose.foundation.BorderStroke(1.dp, BrandCyan.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            if (hasSkill) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BrandCyan,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Text(
                                text = skillName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasSkill) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (opportunity.requiredSkills.size > 3) {
                    Text(
                        text = "+${opportunity.requiredSkills.size - 3} more",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Find Teammates (Primary Star) and Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onFindTeammatesClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_find_teammates_${opportunity.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandCyan,
                        contentColor = Color(0xFF00363D)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonSearch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Find Teammates ⭐",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onCardClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("Details")
                }
            }
        }
    }
}

@Composable
fun SurveyInsightCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceElevated
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandIndigo.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = BrandIndigo,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Column {
                    Text(
                        text = "SGSITS Design Thinking Research",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "57 students surveyed across CSE, IT, ECE, Mech, Civil",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(metric = "70.2%", label = "Missed due to team gap")
                StatItem(metric = "87.7%", label = "Want skills visible")
                StatItem(metric = "84.2%", label = "Rely on limited friends")
            }
        }
    }
}

@Composable
private fun StatItem(metric: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = metric,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = BrandCyan
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}
