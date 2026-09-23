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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindTeammatesSheet(
    opportunityId: String,
    viewModel: SkillSyncViewModel,
    onDismiss: () -> Unit
) {
    val opportunities by viewModel.opportunities.collectAsState()
    val opp = opportunities.find { it.id == opportunityId } ?: opportunities.firstOrNull()
    val teammateMatches by viewModel.teammateMatches.collectAsState()
    val activeTeamMembers by viewModel.activeTeamMembers.collectAsState()

    var selectedCandidateForDetail by remember { mutableStateOf<StudentProfile?>(null) }
    var inviteCandidateDialog by remember { mutableStateOf<StudentProfile?>(null) }
    var inviteNoteText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("find_teammates_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "⭐", fontSize = 18.sp)
                        Text(
                            text = "Smart Teammate Matching",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = opp?.title ?: "Opportunity",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyan,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Roster Status & Skill Gap Analysis
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Team: ${activeTeamMembers.size}/${opp?.teamSizeMax ?: 4} Members",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = BrandEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Complementary Search ON",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Algorithm prioritizes candidates who fill your missing skills (Presentation, UI/UX, Cloud) without duplicating already covered skills.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Recommended Teammates (${teammateMatches.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of Complementary Matches
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("teammates_match_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(teammateMatches) { match ->
                    val candidate = match.candidate
                    val breakdown = match.breakdown

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_card_${candidate.id}"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (breakdown.totalScore >= 85) BrandCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Avatar + Name + Match Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = BrandCyan.copy(alpha = 0.2f),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = candidate.name.take(1),
                                                fontWeight = FontWeight.Bold,
                                                color = BrandCyan,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = candidate.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            VerificationBadge("Verified")
                                        }
                                        Text(
                                            text = "${candidate.branch} • ${candidate.year}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                MatchScoreBadge(score = breakdown.totalScore)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Highlight Complementary Skill
                            if (breakdown.complementarySkillsCovered.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandEmerald.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BrandEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Fills Gap: ${breakdown.complementarySkillsCovered.joinToString(", ")}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BrandEmerald,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Match Breakdown Grid (Formula: Skill 50%, Interest 20%, Avail 15%, Req 15%)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Skill (50%)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${breakdown.skillScore}/50", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandCyan)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Interest (20%)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${breakdown.interestScore}/20", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandIndigo)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Avail (15%)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${breakdown.availabilityScore}/15", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandEmerald)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Req (15%)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${breakdown.requirementScore}/15", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandAmber)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Candidate Skills
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(candidate.skills) { skill ->
                                    val isMatch = breakdown.complementarySkillsCovered.any { it.equals(skill.name, ignoreCase = true) }
                                    SkillBadge(skill = skill, isHighlighted = isMatch)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons: View Profile and Invite
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { selectedCandidateForDetail = candidate },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("View Profile", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = {
                                        inviteCandidateDialog = candidate
                                        inviteNoteText = "Hey ${candidate.name}! We're forming a team for ${opp?.title ?: "SIH 2026"} and your skills in ${candidate.skills.take(2).joinToString(", ") { it.name }} are a perfect complementary fit. Let's win this together!"
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_invite_${candidate.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandCyan,
                                        contentColor = Color(0xFF00363D)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Invite", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Profile Dialog
    selectedCandidateForDetail?.let { candidate ->
        AlertDialog(
            onDismissRequest = { selectedCandidateForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(candidate.name, fontWeight = FontWeight.Bold)
                    VerificationBadge("SGSITS")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${candidate.college} • ${candidate.branch} (${candidate.year})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (candidate.bio.isNotBlank()) {
                        Text(candidate.bio, style = MaterialTheme.typography.bodySmall)
                    }

                    Text("Skills:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    candidate.skills.forEach { skill ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(skill.iconEmoji)
                            Text(skill.name, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                            Text("(${skill.level.displayName})", color = BrandCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text("Availability:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Text(candidate.availability.summaryText(), style = MaterialTheme.typography.bodySmall, color = BrandEmerald)

                    Text("Contact Preference:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Text(candidate.contactPreference, style = MaterialTheme.typography.bodySmall)

                    if (candidate.projects.isNotEmpty()) {
                        Text("Projects / Experience:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                        candidate.projects.forEach { proj ->
                            Text("• ${proj.title}: ${proj.description} (${proj.techStack})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = candidate
                        selectedCandidateForDetail = null
                        inviteCandidateDialog = s
                        inviteNoteText = "Hey ${s.name}! We'd love to have you on our team for ${opp?.title ?: "this opportunity"}."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Invite to Team", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCandidateForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Invitation Confirmation Dialog
    inviteCandidateDialog?.let { candidate ->
        AlertDialog(
            onDismissRequest = { inviteCandidateDialog = null },
            title = {
                Text("Send Team Invitation", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Inviting ${candidate.name} to join ${opp?.title ?: "team"}.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = inviteNoteText,
                        onValueChange = { inviteNoteText = it },
                        label = { Text("Invitation Note") },
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
                        viewModel.sendTeamInvitation(candidate, inviteNoteText)
                        inviteCandidateDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                ) {
                    Text("Send Invitation 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { inviteCandidateDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
