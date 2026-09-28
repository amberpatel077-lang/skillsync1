package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.*
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillSyncViewModel

enum class AuthTab {
    LOG_IN,
    SIGN_UP
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: SkillSyncViewModel,
    modifier: Modifier = Modifier,
    initialTab: AuthTab = AuthTab.LOG_IN
) {
    val uiState by viewModel.uiState.collectAsState()
    val allStudents by viewModel.students.collectAsState()
    val focusManager = LocalFocusManager.current

    var currentTab by remember { mutableStateOf(initialTab) }

    // Log In form state
    var logInEmail by remember { mutableStateOf("") }
    var logInPassword by remember { mutableStateOf("") }
    var logInPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordSent by remember { mutableStateOf(false) }

    // Sign Up form state
    val context = LocalContext.current
    var signUpPhotoUri by remember { mutableStateOf<String?>(null) }
    val signUpPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            signUpPhotoUri = it.toString()
        }
    }

    var signUpName by remember { mutableStateOf("") }
    var signUpCollege by remember { mutableStateOf("SGSITS Indore") }
    var signUpBranch by remember { mutableStateOf("Computer Science & Engineering") }
    var signUpYear by remember { mutableStateOf("2nd Year") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpBio by remember { mutableStateOf("") }
    var signUpLookingFor by remember { mutableStateOf(LookingFor.BOTH) }
    var selectedSkills by remember {
        mutableStateOf(
            listOf(
                SkillEntry("Python", SkillLevel.INTERMEDIATE),
                SkillEntry("React", SkillLevel.BEGINNER)
            )
        )
    }

    // Available popular skills for sign-up picking
    val popularSkills = listOf(
        "Python", "Machine Learning", "React", "UI/UX",
        "Cloud/DevOps", "Java", "C++", "Android",
        "Data Science", "SQL", "Presentation"
    )

    // Popular colleges
    val popularColleges = listOf(
        "SGSITS Indore", "IIT Bombay", "BITS Pilani", "NIT Trichy", "Delhi University"
    )

    // Branches
    val popularBranches = listOf(
        "Computer Science & Engineering",
        "Information Technology",
        "Electronics & Comm.",
        "AI & Data Science",
        "Design & Media"
    )

    // Academic Years
    val academicYears = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "Postgrad")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            // App Branding Header matching official SkillSync identity
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    // Top Taglines from official diagram
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CONNECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandPink,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(BrandPink)
                        )
                        Text(
                            text = "COLLABORATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandIndigo,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(BrandCyan)
                        )
                        Text(
                            text = "GROW",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandCyan,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Text(
                        text = "STUDENTS   ×   OPPORTUNITIES   ×   TEAMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    // Central Glowing Logo Badge from image
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(listOf(BrandIndigo.copy(alpha = 0.4f), BrandCyan.copy(alpha = 0.6f)))
                        ),
                        shadowElevation = 6.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEEF2FF), Color(0xFFE0F2FE))
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(BrandIndigo.copy(alpha = 0.3f), BrandCyan.copy(alpha = 0.4f))
                                        ),
                                        RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_skillsync_logo),
                                    contentDescription = "SkillSync Official Logo",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "SkillSync",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Turn Interests Into Impact",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Connected Nodes pills from the official graphic
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        val pillars = listOf(
                            "</> Hackathons" to BrandPink,
                            "📄 Projects" to BrandCyan,
                            "🏆 Competitions" to Color(0xFF60A5FA),
                            "👥 Clubs" to Color(0xFFA78BFA),
                            "💖 Volunteering" to BrandPink,
                            "🚀 Opportunities" to BrandCyan
                        )
                        items(pillars) { (title, color) ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = color.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Student Opportunity & Skill-Based Team Matching Platform",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Auth Tab Switcher (Log In vs Sign Up)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    TabRow(
                        selectedTabIndex = if (currentTab == AuthTab.LOG_IN) 0 else 1,
                        containerColor = Color.Transparent,
                        contentColor = BrandCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(
                                    tabPositions[if (currentTab == AuthTab.LOG_IN) 0 else 1]
                                ),
                                color = BrandCyan
                            )
                        }
                    ) {
                        Tab(
                            selected = currentTab == AuthTab.LOG_IN,
                            onClick = {
                                currentTab = AuthTab.LOG_IN
                                viewModel.clearAuthError()
                            },
                            text = {
                                Text(
                                    "Log In",
                                    fontWeight = if (currentTab == AuthTab.LOG_IN) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            },
                            icon = { Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.testTag("tab_login")
                        )
                        Tab(
                            selected = currentTab == AuthTab.SIGN_UP,
                            onClick = {
                                currentTab = AuthTab.SIGN_UP
                                viewModel.clearAuthError()
                            },
                            text = {
                                Text(
                                    "Sign Up",
                                    fontWeight = if (currentTab == AuthTab.SIGN_UP) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            },
                            icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.testTag("tab_signup")
                        )
                    }
                }
            }

            // Error Banner (if any)
            if (uiState.authError != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("card_auth_error"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = uiState.authError ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearAuthError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SUCCESS BANNER (if any)
            if (uiState.authSuccessMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = uiState.authSuccessMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF064E3B),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // TAB CONTENT
            if (currentTab == AuthTab.LOG_IN) {
                // LOG IN VIEW
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_login_form"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Welcome Back",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Log in to access your hackathons, team chat & matching opportunities",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Email Field
                            OutlinedTextField(
                                value = logInEmail,
                                onValueChange = {
                                    logInEmail = it
                                    viewModel.clearAuthError()
                                },
                                label = { Text("College Email Address") },
                                placeholder = { Text("e.g. amberpatel077@sgsits.ac.in") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = BrandCyan)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_email"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                            )

                            // Password Field
                            OutlinedTextField(
                                value = logInPassword,
                                onValueChange = {
                                    logInPassword = it
                                    viewModel.clearAuthError()
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Enter your password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCyan)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { logInPasswordVisible = !logInPasswordVisible }) {
                                        Icon(
                                            imageVector = if (logInPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (logInPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (logInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_password"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.login(logInEmail, logInPassword)
                                    }
                                )
                            )

                            // Remember me & Forgot password
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(checkedColor = BrandCyan)
                                    )
                                    Text(
                                        text = "Remember me",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        forgotPasswordEmail = logInEmail
                                        forgotPasswordSent = false
                                        showForgotPasswordDialog = true
                                    }
                                ) {
                                    Text(
                                        text = "Forgot password?",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrandCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Log In Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.login(logInEmail, logInPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_login_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandCyan,
                                    contentColor = Color(0xFF00363D)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "Log In",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Hint regarding demo password
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandIndigo.copy(alpha = 0.2f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = BrandCyan, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Campus accounts password: password123 (or pick a Demo Student below)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 1-Tap Quick Demo Log-In Section
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⚡ Instant Demo Log-In",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "1-Tap Access",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Select a pre-configured student profile to test SkillSync instantly:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            val demoStudents = allStudents.take(4)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                demoStudents.forEach { student ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                logInEmail = student.email
                                                logInPassword = "password123"
                                                viewModel.quickLogin(student.id)
                                            }
                                            .testTag("btn_quick_login_${student.id}"),
                                        color = SurfaceCard,
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                StudentAvatar(student = student, size = 36.dp)
                                                Column {
                                                    Text(
                                                        text = student.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${student.branch} • ${student.year}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Log In",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = BrandCyan,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Icon(
                                                    Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = BrandCyan,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Switch to Sign Up prompt
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Don't have a student account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sign Up here",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandCyan,
                            modifier = Modifier
                                .clickable {
                                    currentTab = AuthTab.SIGN_UP
                                    viewModel.clearAuthError()
                                }
                                .padding(4.dp)
                                .testTag("btn_switch_to_signup")
                        )
                    }
                }
            } else {
                // SIGN UP / REGISTER VIEW
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_signup_form"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Create Student Profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Join your college innovation network & find dream team members",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Profile Photo Selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        signUpPhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, BrandIndigo, CircleShape)
                                        .background(BrandIndigo.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!signUpPhotoUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = signUpPhotoUri,
                                            contentDescription = "Selected profile photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            tint = BrandIndigo,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (signUpPhotoUri != null) "Profile Photo Added" else "Add Profile Photo (Optional)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (signUpPhotoUri != null) "Tap to change photo" else "Tap to choose a picture from device",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (signUpPhotoUri != null) {
                                    IconButton(
                                        onClick = { signUpPhotoUri = null }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove photo",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    FilledTonalButton(
                                        onClick = {
                                            signUpPhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Choose", fontSize = 12.sp)
                                    }
                                }
                            }

                            // Full Name
                            OutlinedTextField(
                                value = signUpName,
                                onValueChange = {
                                    signUpName = it
                                    viewModel.clearAuthError()
                                },
                                label = { Text("Full Name *") },
                                placeholder = { Text("e.g. Vikram Malhotra") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = BrandCyan)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_name"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            // College Email
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedTextField(
                                    value = signUpEmail,
                                    onValueChange = {
                                        signUpEmail = it
                                        viewModel.clearAuthError()
                                    },
                                    label = { Text("College Email Address *") },
                                    placeholder = { Text("e.g. vikram@sgsits.ac.in or .edu") },
                                    leadingIcon = {
                                        Icon(Icons.Default.School, contentDescription = null, tint = BrandCyan)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_signup_email"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                )

                                val isVerifiedEmail = signUpEmail.endsWith(".edu") || signUpEmail.endsWith(".ac.in") ||
                                        signUpEmail.contains("college") || signUpEmail.contains("univ")
                                if (isVerifiedEmail) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Verified student domain detected!",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Password
                            OutlinedTextField(
                                value = signUpPassword,
                                onValueChange = {
                                    signUpPassword = it
                                    viewModel.clearAuthError()
                                },
                                label = { Text("Password (min. 6 chars) *") },
                                placeholder = { Text("Create a secure password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCyan)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                        Icon(
                                            imageVector = if (signUpPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_password"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            // College / University
                            OutlinedTextField(
                                value = signUpCollege,
                                onValueChange = { signUpCollege = it },
                                label = { Text("College / University *") },
                                placeholder = { Text("Enter college name") },
                                leadingIcon = {
                                    Icon(Icons.Default.Apartment, contentDescription = null, tint = BrandCyan)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_college"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            // Popular Colleges Quick Selector
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(popularColleges) { col ->
                                    FilterChip(
                                        selected = signUpCollege == col,
                                        onClick = { signUpCollege = col },
                                        label = { Text(col, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Branch / Department
                            Text("Branch / Department *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(popularBranches) { br ->
                                    FilterChip(
                                        selected = signUpBranch == br,
                                        onClick = { signUpBranch = br },
                                        label = { Text(br, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Academic Year
                            Text("Academic Year *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                academicYears.forEach { yr ->
                                    FilterChip(
                                        selected = signUpYear == yr,
                                        onClick = { signUpYear = yr },
                                        label = { Text(yr, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Primary Skills Selector
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Primary Skills (Select at least 1) *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${selectedSkills.size} selected",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandCyan
                                    )
                                }

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    popularSkills.forEach { skillName ->
                                        val isSelected = selectedSkills.any { it.name.equals(skillName, ignoreCase = true) }
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                if (isSelected) {
                                                    selectedSkills = selectedSkills.filterNot { it.name.equals(skillName, ignoreCase = true) }
                                                } else {
                                                    selectedSkills = selectedSkills + SkillEntry(skillName, SkillLevel.INTERMEDIATE)
                                                }
                                            },
                                            label = {
                                                Text(skillName, fontSize = 11.sp)
                                            },
                                            leadingIcon = if (isSelected) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                            } else null
                                        )
                                    }
                                }
                            }

                            // Bio / Goals
                            OutlinedTextField(
                                value = signUpBio,
                                onValueChange = { signUpBio = it },
                                label = { Text("Bio & Goals (Optional)") },
                                placeholder = { Text("e.g. Enthusiastic about SIH hackathon and looking for a frontend partner!") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_signup_bio"),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 2
                            )

                            // Looking For
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("What are you looking for?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    LookingFor.entries.forEach { opt ->
                                        FilterChip(
                                            selected = signUpLookingFor == opt,
                                            onClick = { signUpLookingFor = opt },
                                            label = { Text(opt.label, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }

                            // Submit Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.signup(
                                        name = signUpName,
                                        college = signUpCollege,
                                        branch = signUpBranch,
                                        year = signUpYear,
                                        email = signUpEmail,
                                        password = signUpPassword,
                                        skills = selectedSkills,
                                        bio = signUpBio,
                                        lookingFor = signUpLookingFor,
                                        avatarPhotoUri = signUpPhotoUri,
                                        autoLogin = true,
                                        onSuccess = {
                                            // Navigation to authenticated state handled by uiState
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_signup_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandCyan,
                                    contentColor = Color(0xFF00363D)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "Create Account & Log In",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch to Log In prompt
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have a SkillSync account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Log In here",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandCyan,
                            modifier = Modifier
                                .clickable {
                                    currentTab = AuthTab.LOG_IN
                                    viewModel.clearAuthError()
                                }
                                .padding(4.dp)
                                .testTag("btn_switch_to_login")
                        )
                    }
                }
            }

            // Official Team SkillSync Credits Footer from User Image
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = BrandIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Team SkillSync",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(BrandIndigo)
                            )
                            Text(
                                text = "Design Thinking Project",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandIndigo,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Abhinav Purohit  •  Abhinav Mehta  •  Akash Patel  •  Amber Patel  •  Ishmit Shukla",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Student Opportunity & Skill-Based Team Matching Platform",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = BrandCyan)
                    Text("Reset Campus Password", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!forgotPasswordSent) {
                        Text(
                            text = "Enter your registered college email. We will send a secure verification token and reset link.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = forgotPasswordEmail,
                            onValueChange = { forgotPasswordEmail = it },
                            label = { Text("College Email") },
                            placeholder = { Text("e.g. amberpatel077@sgsits.ac.in") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandCyan) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandIndigo.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Note: For demo accounts, the default password is password123",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandCyan,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            Text(
                                text = "Password reset instructions sent to $forgotPasswordEmail! Please check your campus inbox.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!forgotPasswordSent) {
                    Button(
                        onClick = {
                            if (forgotPasswordEmail.isNotBlank()) {
                                forgotPasswordSent = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                    ) {
                        Text("Send Reset Link", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { showForgotPasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF00363D))
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!forgotPasswordSent) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
