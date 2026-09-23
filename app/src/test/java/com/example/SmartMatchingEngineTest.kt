package com.example

import com.example.data.SmartMatchingEngine
import com.example.model.*
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartMatchingEngineTest {

    @Test
    fun testComplementaryTeammateMatching() {
        val studentA = StudentProfile(
            id = "s_a",
            name = "Student A",
            college = "SGSITS Indore",
            branch = "CSE",
            year = "3rd Year",
            email = "studenta@sgsits.ac.in",
            skills = listOf(
                SkillEntry("Python", SkillLevel.ADVANCED),
                SkillEntry("Machine Learning", SkillLevel.INTERMEDIATE)
            ),
            interests = listOf("AI/ML", "Hackathons"),
            availability = Availability(weekdays = true, weekends = true, evenings = true, fullTimeEvent = true, availableUntil = "Dec 2026"),
            lookingFor = LookingFor.BOTH,
            contactPreference = "In-App Chat"
        )

        val candidateB = StudentProfile(
            id = "s_b",
            name = "Student B",
            college = "SGSITS Indore",
            branch = "IT",
            year = "3rd Year",
            email = "studentb@sgsits.ac.in",
            skills = listOf(
                SkillEntry("UI/UX", SkillLevel.ADVANCED),
                SkillEntry("Presentation", SkillLevel.ADVANCED),
                SkillEntry("Figma", SkillLevel.ADVANCED)
            ),
            interests = listOf("Design", "Hackathons"),
            availability = Availability(weekdays = true, weekends = true, evenings = true, fullTimeEvent = true, availableUntil = "Dec 2026"),
            lookingFor = LookingFor.TEAM,
            contactPreference = "In-App Chat"
        )

        val opp = Opportunity(
            id = "opp_sih",
            title = "Smart India Hackathon",
            category = OpportunityCategory.HACKATHON,
            organizer = "Ministry of Education",
            date = "Oct 24-26",
            deadline = "Oct 15",
            mode = OpportunityMode.OFFLINE,
            location = "New Delhi",
            teamSizeMin = 3,
            teamSizeMax = 6,
            requiredSkills = listOf("Python", "Machine Learning", "UI/UX", "Presentation"),
            description = "National innovation competition",
            preferredBranch = "CSE / IT / ECE",
            eligibleYears = listOf("2nd Year", "3rd Year", "4th Year")
        )

        // When Student A (Python, ML) evaluates Candidate B (UI/UX, Presentation)
        val match = SmartMatchingEngine.calculateTeammateMatch(
            candidate = candidateB,
            opportunity = opp,
            currentTeamMembers = listOf(studentA)
        )

        // Candidate B should fill the missing UI/UX and Presentation requirements
        assertTrue(match.breakdown.complementarySkillsCovered.any { it.contains("UI/UX") })
        // High score expected for candidate covering all team gaps
        assertTrue("Expected score >= 80, got ${match.breakdown.totalScore}", match.breakdown.totalScore >= 80)
    }

    @Test
    fun testOpportunityFitCalculation() {
        val student = StudentProfile(
            id = "s_test",
            name = "Tester",
            college = "SGSITS Indore",
            branch = "CSE",
            year = "2nd Year",
            email = "test@sgsits.ac.in",
            skills = listOf(
                SkillEntry("Python", SkillLevel.ADVANCED),
                SkillEntry("Machine Learning", SkillLevel.INTERMEDIATE)
            ),
            interests = listOf("AI/ML", "Hackathons"),
            availability = Availability(fullTimeEvent = true),
            lookingFor = LookingFor.BOTH,
            contactPreference = "In-App Chat"
        )

        val matchingOpp = Opportunity(
            id = "opp_ai",
            title = "AI Hackathon",
            category = OpportunityCategory.HACKATHON,
            organizer = "Tech Fest",
            date = "Nov 1",
            deadline = "Oct 20",
            mode = OpportunityMode.ONLINE,
            location = "Online",
            teamSizeMin = 2,
            teamSizeMax = 4,
            requiredSkills = listOf("Python", "Machine Learning"),
            description = "AI challenge",
            preferredBranch = "CSE",
            eligibleYears = listOf("2nd Year", "3rd Year")
        )

        val score = SmartMatchingEngine.calculateOpportunityFit(student, matchingOpp)
        assertTrue("Fit score should be high (>=85), got $score", score >= 85)
    }
}
