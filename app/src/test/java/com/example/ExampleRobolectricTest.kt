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
}
