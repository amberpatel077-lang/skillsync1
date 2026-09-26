package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                    color = BrandIndigo.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${opp.category.iconEmoji} ${opp.category.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandIndigo,
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
                color = SkyBlueDark,
                fontWeight = FontWeight.Medium
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

                // Required Skills
                item {
                    Text("Required Skills", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        opp.requiredSkills.forEach { skillName ->
                            val hasSkill = currentStudent?.skills?.any {
                                it.name.equals(skillName, ignoreCase = true) || skillName.contains(it.name, ignoreCase = true)
                            } ?: false

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hasSkill) SkyBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (hasSkill) androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (hasSkill) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(12.dp))
                                    }
                                    Text(
                                        text = skillName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasSkill) SkyBlueDark else MaterialTheme.colorScheme.onSurface
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
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BrandEmerald.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = BrandEmerald)
                                Column {
                                    Text("Prizes & Perks", fontWeight = FontWeight.Bold, color = BrandEmerald, style = MaterialTheme.typography.labelMedium)
                                    Text(opp.prizes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar: Find Teammates (Primary Star) and Form Team
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val targetId = opp.id
                        onDismiss()
                        viewModel.openFindTeammates(targetId)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Find Teammates ⭐", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showCreateTeamDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text("Form Team")
                }
            }
        }
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
