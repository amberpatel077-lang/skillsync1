package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunityDetailDialog(
    opportunityId: String,
    viewModel: SkillSyncViewModel,
    onDismiss: () -> Unit
) {
    val opportunities by viewModel.opportunities.collectAsState()
    val opp = opportunities.find { it.id == opportunityId } ?: return
    val currentStudent by viewModel.currentStudent.collectAsState()

    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var teamNameInput by remember { mutableStateOf("Team ${currentStudent?.name?.split(" ")?.firstOrNull() ?: "Alpha"}'s ${opp.title.take(12)}") }
    var showClubAppDialog by remember { mutableStateOf(false) }
    var showVolunteerRegDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("opportunity_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${opp.category.iconEmoji} ${opp.category.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = opp.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Organized by ${opp.organizer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Event Showcase Banner
                item {
                    Image(
                        painter = painterResource(id = R.drawable.img_hackathon_showcase),
                        contentDescription = "Event Showcase",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Key Meta Grid
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(16.dp))
                                Text("Event Dates: ${opp.date}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(16.dp))
                                Text("Application Deadline: ${opp.deadline}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                                Text("Format: ${opp.mode.label} • ${opp.location}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(16.dp))
                                Text("Team Size: ${opp.teamSizeMin} to ${opp.teamSizeMax} Members (Currently: ${opp.currentMembersCount})", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Description
                item {
                    Text("About This Opportunity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = opp.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                // Required Skills (Horizontally scrollable so long names like "Competitive Programming" never truncate)
                item {
                    Text("Required Skills", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(opp.requiredSkills) { skillName ->
                            val hasSkill = currentStudent?.skills?.any {
                                it.name.equals(skillName, ignoreCase = true) || skillName.contains(it.name, ignoreCase = true)
                            } ?: false

                            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                            val statusColors = SemanticStatus.resolve(StatusType.SUCCESS, isDark)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hasSkill) statusColors.background else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (hasSkill) androidx.compose.foundation.BorderStroke(1.dp, statusColors.border) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (hasSkill) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = statusColors.text, modifier = Modifier.size(13.dp))
                                    }
                                    Text(
                                        text = skillName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (hasSkill) FontWeight.Bold else FontWeight.Medium,
                                        color = if (hasSkill) statusColors.text else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Eligibility
                item {
                    Text("Eligibility & Criteria", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Preferred Branches: ${opp.preferredBranch}", style = MaterialTheme.typography.bodySmall)
                    Text("• Eligible Years: ${opp.eligibleYears.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                }

                // Prizes & Benefits
                if (opp.prizes.isNotBlank()) {
                    item {
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        val successColors = SemanticStatus.resolve(StatusType.SUCCESS, isDark)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = successColors.background),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, successColors.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = successColors.text)
                                Column {
                                    Text("Prizes & Perks", fontWeight = FontWeight.Bold, color = successColors.text, style = MaterialTheme.typography.labelMedium)
                                    Text(opp.prizes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar: Adaptive based on category with strong visual hierarchy & accessibility touch targets
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (opp.category == OpportunityCategory.CLUB) {
                    Button(
                        onClick = { showClubAppDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply to Club Recruitment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else if (opp.category == OpportunityCategory.VOLUNTEERING) {
                    Button(
                        onClick = { showVolunteerRegDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SemanticStatus.Success,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register as Campus Volunteer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val targetId = opp.id
                            onDismiss()
                            viewModel.openFindTeammates(targetId)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp),
                        colors = if (opp.category == OpportunityCategory.CLUB || opp.category == OpportunityCategory.VOLUNTEERING)
                            ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Teammates", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = { showCreateTeamDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Form Team", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showClubAppDialog) {
        com.example.ui.components.ClubApplicationDialog(
            opportunity = opp,
            viewModel = viewModel,
            onDismiss = { showClubAppDialog = false }
        )
    }

    if (showVolunteerRegDialog) {
        com.example.ui.components.VolunteerRegistrationDialog(
            opportunity = opp,
            viewModel = viewModel,
            onDismiss = { showVolunteerRegDialog = false }
        )
    }

    if (showCreateTeamDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTeamDialog = false },
            title = { Text("Form Team for ${opp.title}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You will be the team leader. You can invite complementary teammates right away.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("Team Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createTeamForOpportunity(opp, teamNameInput)
                        showCreateTeamDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White)
                ) {
                    Text("Create Team 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTeamDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
