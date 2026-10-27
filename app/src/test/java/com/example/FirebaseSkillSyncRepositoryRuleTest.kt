package com.example

import com.example.data.FirebaseSkillSyncRepository
import com.example.model.*
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FirebaseSkillSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private lateinit var repository: FirebaseSkillSyncRepository

    @Before
    override fun setUpFirebase() {
        super.setUpFirebase()
        repository = FirebaseSkillSyncRepository(firestore, auth)
    }

    @Test
    fun unauthenticatedUser_cannotWriteProfile() = runBlocking {
        auth.signOut()
        val student = StudentProfile(
            id = "unauth_user",
            name = "Test Student",
            college = "SGSITS",
            branch = "CSE",
            year = "3rd Year",
            email = "test@sgsits.ac.in"
        )
        try {
            repository.saveStudentProfile(student)
            fail("Expected FirebaseFirestoreException for unauthenticated write")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun authenticatedUser_canSaveTheirOwnProfile() = runBlocking {
        val uid = signInTestUser("student_alice@sgsits.ac.in")
        val student = StudentProfile(
            id = uid,
            name = "Alice Sharma",
            college = "SGSITS Indore",
            branch = "CSE",
            year = "3rd Year",
            email = "student_alice@sgsits.ac.in",
            skills = listOf(SkillEntry("Kotlin", SkillLevel.ADVANCED))
        )
        repository.saveStudentProfile(student)
        val doc = firestore.collection("users").document(uid).get().await()
        assertTrue(doc.exists())
        assertEquals("Alice Sharma", doc.getString("name"))
    }

    @Test
    fun authenticatedUser_canCreateTeam() = runBlocking {
        val uid = signInTestUser("student_leader@sgsits.ac.in")
        val team = Team(
            id = "team_hack_2026",
            name = "SGSITS Hackers",
            opportunityId = "opp_1",
            opportunityTitle = "SIH 2026",
            organizer = "MHRD",
            leaderId = uid,
            leaderName = "Leader Student"
        )
        repository.createTeam(team, listOf(uid))
        val doc = firestore.collection("teams").document("team_hack_2026").get().await()
        assertTrue(doc.exists())
        assertEquals("SGSITS Hackers", doc.getString("name"))
    }
}
