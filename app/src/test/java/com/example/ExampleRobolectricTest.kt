package com.example

import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.model.Opportunity
import com.example.model.OpportunityCategory
import com.example.model.OpportunityMode
import com.example.model.SkillEntry
import com.example.model.SkillLevel
import com.example.viewmodel.RegistrationDeliveryResult
import com.example.viewmodel.SkillSyncViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private fun awaitCondition(timeoutMs: Long = 3000L, predicate: () -> Boolean) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (!predicate() && System.currentTimeMillis() < deadline) {
      Shadows.shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(20)
    }
    Shadows.shadowOf(Looper.getMainLooper()).idle()
  }

  private fun signUpAndAwait(
    viewModel: SkillSyncViewModel,
    email: String = "student_${System.nanoTime()}@nitt.edu",
    password: String = "CampusSecure#2026",
    college: String = "NIT Trichy"
  ): String {
    var completed = false
    viewModel.signup(
      name = "Ananya Iyer",
      college = college,
      branch = "Computer Science & Engineering",
      year = "3rd Year",
      email = email,
      password = password,
      skills = listOf(SkillEntry("Kotlin", SkillLevel.ADVANCED)),
      onSuccess = { completed = true }
    )
    awaitCondition { completed }
    assertTrue("Expected real sign-up to complete", completed)
    return email
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SkillSync", appName)
  }

  @Test
  fun `verify real signup login password validation and logout flow`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as Application)

    // Initial state is strictly unauthenticated with no pre-selected student ID
    assertFalse(viewModel.uiState.value.isAuthenticated)
    assertEquals("", viewModel.uiState.value.currentStudentId)

    // Register a real student account with a custom password
    val customPassword = "MyCustomPassword!99"
    val email = signUpAndAwait(
      viewModel = viewModel,
      password = customPassword,
      college = "IIT Bombay"
    )
    assertTrue(viewModel.uiState.value.isAuthenticated)
    assertTrue(viewModel.uiState.value.currentStudentId.startsWith("student_"))

    // Logout clears authenticated state and currentStudentId
    viewModel.logout()
    assertFalse(viewModel.uiState.value.isAuthenticated)
    assertEquals("", viewModel.uiState.value.currentStudentId)

    // Attempting login with an incorrect password fails (no universal fallback password)
    var wrongLoginDone = false
    var wrongLoginSuccess = true
    viewModel.login(email, "wrongPass123") { success ->
      wrongLoginSuccess = success
      wrongLoginDone = true
    }
    awaitCondition { wrongLoginDone }
    assertFalse(wrongLoginSuccess)
    assertFalse(viewModel.uiState.value.isAuthenticated)
    assertNotNull(viewModel.uiState.value.authError)

    // Logging in with the exact registered password succeeds
    var validLoginDone = false
    var validLoginSuccess = false
    viewModel.login(email, customPassword) { success ->
      validLoginSuccess = success
      validLoginDone = true
    }
    awaitCondition { validLoginDone }
    assertTrue(validLoginSuccess)
    assertTrue(viewModel.uiState.value.isAuthenticated)
  }

  @Test
  fun `verify splash screen initial visibility and dismissal`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as Application)

    assertTrue(viewModel.uiState.value.isSplashVisible)
    viewModel.dismissSplash()
    assertFalse(viewModel.uiState.value.isSplashVisible)
  }

  @Test
  fun `verify registration completion reports Sent or Not Sent accurately`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as Application)

    val volunteerOpp = Opportunity(
      id = "opp_test_vol",
      title = "Campus Green Drive",
      organizer = "NSS Unit",
      category = OpportunityCategory.VOLUNTEERING,
      date = "10 Nov 2026",
      deadline = "05 Nov 2026",
      mode = OpportunityMode.OFFLINE,
      location = "Main Lawn",
      teamSizeMin = 1,
      teamSizeMax = 10,
      requiredSkills = listOf("Leadership"),
      preferredBranch = "All",
      eligibleYears = listOf("2nd Year"),
      description = "Tree plantation drive"
    )

    // 0. Unauthenticated registration -> Not Sent
    var capturedResult: RegistrationDeliveryResult? = null
    viewModel.registerForVolunteering(
      opp = volunteerOpp,
      preferredRole = "Fieldwork Lead",
      hoursAvailable = "12 Hours",
      motivation = "Ready to help",
      onResult = { capturedResult = it }
    )
    assertTrue(capturedResult is RegistrationDeliveryResult.NotSent)

    // Authenticate via real account registration
    signUpAndAwait(viewModel = viewModel)

    // 1. Blank hours -> Not Sent
    viewModel.registerForVolunteering(
      opp = volunteerOpp,
      preferredRole = "Fieldwork Lead",
      hoursAvailable = "   ",
      motivation = "Ready to help",
      onResult = { capturedResult = it }
    )
    assertTrue(capturedResult is RegistrationDeliveryResult.NotSent)
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is RegistrationDeliveryResult.NotSent)

    // 2. Valid inputs -> Sent
    viewModel.registerForVolunteering(
      opp = volunteerOpp,
      preferredRole = "Fieldwork Lead",
      hoursAvailable = "12 Hours",
      motivation = "Ready to help",
      onResult = { capturedResult = it }
    )
    Shadows.shadowOf(Looper.getMainLooper()).idle()
    assertTrue(capturedResult is RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.volunteerRegistrations.value.any { it.volunteerOpportunityId == "opp_test_vol" })

    // 3. Club application with too-short SOP -> Not Sent
    val clubOpp = volunteerOpp.copy(
      id = "opp_test_club",
      title = "GDSC Recruitment",
      organizer = "GDSC Campus Chapter",
      category = OpportunityCategory.CLUB
    )
    viewModel.applyToClub(
      opp = clubOpp,
      roleApplied = "Android Domain Head",
      sop = "short",
      portfolioUrl = "",
      onResult = { capturedResult = it }
    )
    assertTrue(capturedResult is RegistrationDeliveryResult.NotSent)

    // 4. Club application with valid SOP -> Sent
    viewModel.applyToClub(
      opp = clubOpp,
      roleApplied = "Android Domain Head",
      sop = "Built production Jetpack Compose apps and ready to mentor peers.",
      portfolioUrl = "https://github.com/student",
      onResult = { capturedResult = it }
    )
    Shadows.shadowOf(Looper.getMainLooper()).idle()
    assertTrue(capturedResult is RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.clubApplications.value.any { it.clubOpportunityId == "opp_test_club" })
  }

  @Test
  fun `verify signup accepts any college and infers academic domains`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as Application)

    // 1. Blank college during signup -> NotSent
    viewModel.signup(
      name = "Ananya Iyer",
      college = "   ",
      branch = "Computer Science & Engineering",
      year = "3rd Year",
      email = "ananya@nitt.edu",
      password = "CampusPassword#1",
      skills = listOf(SkillEntry("Kotlin", SkillLevel.ADVANCED)),
      onSuccess = {}
    )
    Shadows.shadowOf(Looper.getMainLooper()).idle()
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is RegistrationDeliveryResult.NotSent)

    // 2. Custom college name from any university -> Sent & persisted accurately
    signUpAndAwait(
      viewModel = viewModel,
      email = "ananya_${System.nanoTime()}@nitt.edu",
      college = "NIT Trichy"
    )
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is RegistrationDeliveryResult.Sent)
    assertEquals("NIT Trichy", SkillSyncViewModel.inferCollegeFromEmail("ananya@nitt.edu"))
    assertEquals("IIT Bombay", SkillSyncViewModel.inferCollegeFromEmail("dev@iitb.ac.in"))
  }
}
