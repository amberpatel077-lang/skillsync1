package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SkillEntry
import com.example.model.SkillLevel
import com.example.viewmodel.SkillSyncViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SkillSync", appName)
  }

  @Test
  fun `verify login and signup viewmodel flow`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as android.app.Application)

    // Initial state is unauthenticated
    assertEquals(false, viewModel.uiState.value.isAuthenticated)

    // Quick demo login sets authenticated state
    viewModel.quickLogin("student_amber")
    assertEquals(true, viewModel.uiState.value.isAuthenticated)
    assertEquals("student_amber", viewModel.uiState.value.currentStudentId)

    // Logout sets unauthenticated
    viewModel.logout()
    assertEquals(false, viewModel.uiState.value.isAuthenticated)
  }

  @Test
  fun `verify splash screen initial visibility and dismissal`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as android.app.Application)

    assertEquals(true, viewModel.uiState.value.isSplashVisible)
    viewModel.dismissSplash()
    assertEquals(false, viewModel.uiState.value.isSplashVisible)
  }

  @Test
  fun `verify registration completion reports Sent or Not Sent accurately`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as android.app.Application)
    viewModel.quickLogin("student_amber")

    val volunteerOpp = com.example.model.Opportunity(
      id = "opp_test_vol",
      title = "Campus Green Drive",
      organizer = "NSS Unit",
      category = com.example.model.OpportunityCategory.VOLUNTEERING,
      date = "10 Nov 2026",
      deadline = "05 Nov 2026",
      mode = com.example.model.OpportunityMode.OFFLINE,
      location = "Main Lawn",
      teamSizeMin = 1,
      teamSizeMax = 10,
      requiredSkills = listOf("Leadership"),
      preferredBranch = "All",
      eligibleYears = listOf("2nd Year"),
      description = "Tree plantation drive"
    )

    // 1. Blank hours -> Not Sent
    var capturedResult: com.example.viewmodel.RegistrationDeliveryResult? = null
    viewModel.registerForVolunteering(
      opp = volunteerOpp,
      preferredRole = "Fieldwork Lead",
      hoursAvailable = "   ",
      motivation = "Ready to help",
      onResult = { capturedResult = it }
    )
    assertTrue(capturedResult is com.example.viewmodel.RegistrationDeliveryResult.NotSent)
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is com.example.viewmodel.RegistrationDeliveryResult.NotSent)

    // 2. Valid inputs -> Sent
    viewModel.registerForVolunteering(
      opp = volunteerOpp,
      preferredRole = "Fieldwork Lead",
      hoursAvailable = "12 Hours",
      motivation = "Ready to help",
      onResult = { capturedResult = it }
    )
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    assertTrue(capturedResult is com.example.viewmodel.RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is com.example.viewmodel.RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.volunteerRegistrations.value.any { it.volunteerOpportunityId == "opp_test_vol" })

    // 3. Club application with too-short SOP -> Not Sent
    val clubOpp = volunteerOpp.copy(
      id = "opp_test_club",
      title = "GDSC Recruitment",
      organizer = "GDSC SGSITS",
      category = com.example.model.OpportunityCategory.CLUB
    )
    viewModel.applyToClub(
      opp = clubOpp,
      roleApplied = "Android Domain Head",
      sop = "short",
      portfolioUrl = "",
      onResult = { capturedResult = it }
    )
    assertTrue(capturedResult is com.example.viewmodel.RegistrationDeliveryResult.NotSent)

    // 4. Club application with valid SOP -> Sent
    viewModel.applyToClub(
      opp = clubOpp,
      roleApplied = "Android Domain Head",
      sop = "Built production Jetpack Compose apps and ready to mentor peers.",
      portfolioUrl = "https://github.com/amber",
      onResult = { capturedResult = it }
    )
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    assertTrue(capturedResult is com.example.viewmodel.RegistrationDeliveryResult.Sent)
    assertTrue(viewModel.clubApplications.value.any { it.clubOpportunityId == "opp_test_club" })
  }

  @Test
  fun `verify demo access restricted to single account and signup accepts any college`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = SkillSyncViewModel(context as android.app.Application)

    // 1. Even if another student ID is passed, quickLogin enforces ONLY the single demo account
    viewModel.quickLogin("student_rahul")
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    assertEquals(true, viewModel.uiState.value.isAuthenticated)
    assertEquals(SkillSyncViewModel.DEMO_STUDENT_ID, viewModel.uiState.value.currentStudentId)

    viewModel.logout()

    // 2. Blank college during signup -> NotSent
    viewModel.signup(
      name = "Ananya Iyer",
      college = "   ",
      branch = "Computer Science & Engineering",
      year = "3rd Year",
      email = "ananya@nitt.edu",
      password = "password123",
      skills = listOf(SkillEntry("Kotlin", SkillLevel.ADVANCED)),
      onSuccess = {}
    )
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is com.example.viewmodel.RegistrationDeliveryResult.NotSent)

    // 3. Custom college name from any university -> Sent & persisted accurately
    var signUpCompleted = false
    val uniqueEmail = "ananya_${System.currentTimeMillis()}@nitt.edu"
    viewModel.signup(
      name = "Ananya Iyer",
      college = "NIT Trichy",
      branch = "Computer Science & Engineering",
      year = "3rd Year",
      email = uniqueEmail,
      password = "password123",
      skills = listOf(SkillEntry("Kotlin", SkillLevel.ADVANCED)),
      onSuccess = { signUpCompleted = true }
    )
    val deadline = System.currentTimeMillis() + 3000L
    while (!signUpCompleted && System.currentTimeMillis() < deadline) {
      org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
      Thread.sleep(20)
    }
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    assertTrue(signUpCompleted)
    assertTrue(viewModel.uiState.value.lastRegistrationStatus is com.example.viewmodel.RegistrationDeliveryResult.Sent)
    assertEquals("NIT Trichy", SkillSyncViewModel.inferCollegeFromEmail("ananya@nitt.edu"))
    assertEquals("IIT Bombay", SkillSyncViewModel.inferCollegeFromEmail("dev@iitb.ac.in"))
  }
}
