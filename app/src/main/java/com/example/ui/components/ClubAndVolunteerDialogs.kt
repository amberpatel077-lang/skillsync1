package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Opportunity
import com.example.ui.theme.*
import com.example.viewmodel.RegistrationDeliveryResult
import com.example.viewmodel.SkillSyncViewModel
import kotlinx.coroutines.delay

/**
 * Floating top status banner confirming whether a completed registration was Sent or Not Sent.
 */
@Composable
fun RegistrationStatusBanner(
    status: RegistrationDeliveryResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(status) {
        if (status != null) {
            delay(4500L)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = status != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        status?.let { current ->
            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
            val isSent = current is RegistrationDeliveryResult.Sent
            val colors = SemanticStatus.resolve(
                if (isSent) StatusType.SUCCESS else StatusType.CRITICAL,
                isDark
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag(if (isSent) "banner_registration_sent" else "banner_registration_not_sent"),
                shape = RoundedCornerShape(14.dp),
                color = colors.background,
                border = BorderStroke(1.dp, colors.border),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isSent) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = stringResource(
                            if (isSent) R.string.registration_sent_icon_desc
                            else R.string.registration_not_sent_icon_desc
                        ),
                        tint = colors.text,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = when (current) {
                                    is RegistrationDeliveryResult.Sent -> current.title
                                    is RegistrationDeliveryResult.NotSent -> current.title
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.text
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.text.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = stringResource(
                                        if (isSent) R.string.status_sent_badge
                                        else R.string.status_not_sent_badge
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colors.text,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = when (current) {
                                is RegistrationDeliveryResult.Sent -> current.detail
                                is RegistrationDeliveryResult.NotSent -> current.reason
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.dismiss_status_desc),
                            tint = colors.text,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact persistent "✓ Sent" status pill shown on cards and detail sheets after registration.
 */
@Composable
fun RegistrationSentPill(
    label: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val statusColors = SemanticStatus.resolve(StatusType.SUCCESS, isDark)

    Surface(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = RoundedCornerShape(10.dp),
        color = statusColors.background,
        border = BorderStroke(1.dp, statusColors.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.registration_sent_icon_desc),
                tint = statusColors.text,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColors.text.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Compact inline "✕ Not Sent" alert shown right inside forms/dialogs when submission fails.
 */
@Composable
fun InlineRegistrationNotSentCard(
    title: String,
    reason: String,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val errorColors = SemanticStatus.resolve(StatusType.CRITICAL, isDark)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = errorColors.background,
        border = BorderStroke(1.dp, errorColors.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = stringResource(R.string.registration_not_sent_icon_desc),
                tint = errorColors.text,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$title • ${stringResource(R.string.status_not_sent_badge)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = errorColors.text
                )
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.dismiss_status_desc),
                        tint = errorColors.text,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogSentConfirmationBody(
    sentResult: RegistrationDeliveryResult.Sent,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val successColors = SemanticStatus.resolve(StatusType.SUCCESS, isDark)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("registration_status_sent"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = successColors.background,
            border = BorderStroke(1.5.dp, successColors.border),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.registration_sent_icon_desc),
                    tint = successColors.text,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = successColors.background,
            border = BorderStroke(1.dp, successColors.border)
        ) {
            Text(
                text = "${stringResource(R.string.status_sent_badge)}  •  " + stringResource(
                    if (sentResult.syncedToCloud) R.string.status_synced_cloud
                    else R.string.status_saved_campus_hub
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = successColors.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Text(
            text = sentResult.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = sentResult.detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

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
        mutableStateOf("I want to contribute my skills in ${currentStudent?.skills?.firstOrNull()?.name ?: "technology"} and collaborate with ${opportunity.organizer}.")
    }
    var portfolioUrl by remember { mutableStateOf("https://github.com/student-profile") }
    var isSending by remember { mutableStateOf(false) }
    var submissionResult by remember { mutableStateOf<RegistrationDeliveryResult?>(null) }

    val sentState = submissionResult as? RegistrationDeliveryResult.Sent
    val notSentState = submissionResult as? RegistrationDeliveryResult.NotSent

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (sentState == null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Apply to ${opportunity.organizer}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Club Recruitment",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        text = {
            if (sentState != null) {
                DialogSentConfirmationBody(sentResult = sentState)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (notSentState != null) {
                        item {
                            InlineRegistrationNotSentCard(
                                title = notSentState.title,
                                reason = notSentState.reason,
                                modifier = Modifier.testTag("registration_status_not_sent")
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Preferred Role",
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
                                        .defaultMinSize(minHeight = 48.dp)
                                        .clickable {
                                            selectedRole = role
                                            if (notSentState != null) submissionResult = null
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = role,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f, fill = false),
                                            maxLines = 2
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
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
                            onValueChange = {
                                sopText = it
                                if (notSentState != null) submissionResult = null
                            },
                            label = { Text("Statement of Purpose *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_club_sop"),
                            minLines = 2,
                            maxLines = 4
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = portfolioUrl,
                            onValueChange = { portfolioUrl = it },
                            label = { Text("Portfolio / GitHub Link (Optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_club_portfolio"),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (sentState != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SemanticStatus.Success,
                        contentColor = LightSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_registration_done")
                ) {
                    Text(stringResource(R.string.btn_done), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        isSending = true
                        viewModel.applyToClub(
                            opp = opportunity,
                            roleApplied = selectedRole,
                            sop = sopText,
                            portfolioUrl = portfolioUrl,
                            onResult = { result ->
                                isSending = false
                                submissionResult = result
                            }
                        )
                    },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_confirm_club_application")
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.status_sending_label), fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = if (notSentState != null) stringResource(R.string.btn_retry_send) else "Submit Application",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (sentState == null) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Cancel")
                }
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
        mutableStateOf("Excited to support ${opportunity.title} and contribute to campus service.")
    }
    var isSending by remember { mutableStateOf(false) }
    var submissionResult by remember { mutableStateOf<RegistrationDeliveryResult?>(null) }

    val sentState = submissionResult as? RegistrationDeliveryResult.Sent
    val notSentState = submissionResult as? RegistrationDeliveryResult.NotSent

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (sentState == null) {
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
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = BrandEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Volunteer Registration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = opportunity.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandEmerald,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        text = {
            if (sentState != null) {
                DialogSentConfirmationBody(sentResult = sentState)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (notSentState != null) {
                        item {
                            InlineRegistrationNotSentCard(
                                title = notSentState.title,
                                reason = notSentState.reason,
                                modifier = Modifier.testTag("registration_status_not_sent")
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Preferred Role",
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
                                        .defaultMinSize(minHeight = 48.dp)
                                        .clickable {
                                            selectedRole = role
                                            if (notSentState != null) submissionResult = null
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = role,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) SemanticStatus.Success else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f, fill = false),
                                            maxLines = 2
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SemanticStatus.Success,
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
                            onValueChange = {
                                availableHours = it
                                if (notSentState != null) submissionResult = null
                            },
                            label = { Text("Time Commitment *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_volunteer_hours"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = motivationText,
                            onValueChange = { motivationText = it },
                            label = { Text("Notes (Optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_volunteer_notes"),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (sentState != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SemanticStatus.Success,
                        contentColor = LightSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_registration_done")
                ) {
                    Text(stringResource(R.string.btn_done), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        isSending = true
                        viewModel.registerForVolunteering(
                            opp = opportunity,
                            preferredRole = selectedRole,
                            hoursAvailable = availableHours,
                            motivation = motivationText,
                            onResult = { result ->
                                isSending = false
                                submissionResult = result
                            }
                        )
                    },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SemanticStatus.Success,
                        contentColor = LightSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_confirm_volunteer_registration")
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = LightSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.status_sending_label), fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = if (notSentState != null) stringResource(R.string.btn_retry_send) else "Confirm Registration",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (sentState == null) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
