package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.screens.*
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.SkillSyncViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SkillSyncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                // Display snackbar message when userMessage is emitted
                LaunchedEffect(uiState.userMessage) {
                    uiState.userMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearMessage()
                    }
                }

                if (!uiState.isAuthenticated) {
                    AuthScreen(viewModel = viewModel)
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("bottom_nav_bar"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val bottomNavTabs = listOf(
                                    AppTab.HOME,
                                    AppTab.EXPLORE,
                                    AppTab.TEAMS,
                                    AppTab.PROFILE
                                )
                                bottomNavTabs.forEach { tab ->
                                    val isSelected = uiState.activeTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = when (tab) {
                                                    AppTab.HOME -> if (isSelected) Icons.Default.Home else Icons.Outlined.Home
                                                    AppTab.EXPLORE -> if (isSelected) Icons.Default.Explore else Icons.Outlined.Explore
                                                    AppTab.TEAMS -> if (isSelected) Icons.Default.Groups else Icons.Outlined.Groups
                                                    AppTab.PROFILE -> if (isSelected) Icons.Default.Person else Icons.Outlined.Person
                                                    else -> Icons.Default.Home
                                                },
                                                contentDescription = tab.title
                                            )
                                        },
                                        label = { Text(tab.title, maxLines = 1) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.activeTab) {
                                AppTab.HOME -> HomeScreen(viewModel = viewModel)
                                AppTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                                AppTab.TEAMS -> TeamsScreen(viewModel = viewModel)
                                AppTab.SURVEY -> SurveyScreen(viewModel = viewModel)
                                AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Opportunity Detail Dialog
                    uiState.selectedOpportunityId?.let { oppId ->
                        OpportunityDetailDialog(
                            opportunityId = oppId,
                            viewModel = viewModel,
                            onDismiss = { viewModel.selectOpportunity(null) }
                        )
                    }

                    // Smart Teammates Matching Sheet
                    uiState.findTeammatesOppId?.let { oppId ->
                        FindTeammatesSheet(
                            opportunityId = oppId,
                            viewModel = viewModel,
                            onDismiss = { viewModel.closeFindTeammates() }
                        )
                    }

                    // Interactive Google Form Survey Dialog
                    if (uiState.isSurveyFormOpen) {
                        com.example.ui.components.GoogleFormSurveyDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.closeSurveyForm() }
                        )
                    }
                }
            }
        }
    }
}
