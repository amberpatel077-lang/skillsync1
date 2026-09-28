package com.example

import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testClubApplicationCreation() {
    val app = ClubApplication(
      id = "test_app_1",
      clubOpportunityId = "opp_acm_recruit",
      clubName = "ACM SGSITS",
      studentId = "student_amber",
      studentName = "Amber Patel",
      roleApplied = "Technical Associate",
      sop = "Interested in systems programming",
      portfolioUrl = "https://github.com/amber",
      status = ApplicationStatus.SUBMITTED
    )
    assertEquals("test_app_1", app.id)
    assertEquals(ApplicationStatus.SUBMITTED, app.status)
    assertEquals("Technical Associate", app.roleApplied)
  }

  @Test
  fun testVolunteerRegistrationCreation() {
    val reg = VolunteerRegistration(
      id = "test_vol_1",
      volunteerOpportunityId = "opp_aayam_volunteers",
      eventTitle = "Aayam 2026 Cultural Fest Taskforce",
      studentId = "student_amber",
      studentName = "Amber Patel",
      preferredRole = "Stage & Sound Operations",
      hoursAvailable = "20 Hours",
      status = ApplicationStatus.ACCEPTED
    )
    assertEquals("test_vol_1", reg.id)
    assertEquals("Stage & Sound Operations", reg.preferredRole)
    assertEquals(ApplicationStatus.ACCEPTED, reg.status)
  }

  @Test
  fun testSurveySubmissionCreation() {
    val survey = SurveySubmission(
      id = "survey_test",
      studentId = "student_amber",
      studentName = "Amber Patel",
      branch = "Computer Science",
      year = "2nd Year",
      missedOppDueToNoTeam = true,
      difficultyRating = 4,
      channelsUsed = listOf("Personal Friends", "WhatsApp"),
      obstaclesFaced = listOf("Don't know skill levels", "Hard to find other branches"),
      opportunitiesWanted = listOf("Hackathons", "Club Recruitment", "Volunteering"),
      clubsWanted = listOf("ACM", "GDSC", "Robotics"),
      volunteeringInterests = listOf("Aayam Taskforce", "NSS Green Campus"),
      wantsMentorship = true,
      feedback = "Great platform for finding teammates across branches"
    )
    assertTrue(survey.missedOppDueToNoTeam)
    assertEquals(4, survey.difficultyRating)
    assertTrue(survey.wantsMentorship)
    assertEquals(3, survey.opportunitiesWanted.size)
  }
}

