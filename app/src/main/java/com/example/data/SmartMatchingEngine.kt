package com.example.data

import com.example.model.*
import java.util.Locale

object SmartMatchingEngine {

    /**
     * Calculates complementary teammate match score when building a team for a specific opportunity.
     * Considers skills already present in the team so that candidates with complementary gap skills
     * are ranked highest.
     */
    fun calculateTeammateMatch(
        candidate: StudentProfile,
        opportunity: Opportunity,
        currentTeamMembers: List<StudentProfile>
    ): TeammateMatch {
        val requiredSkills = opportunity.requiredSkills.map { it.trim().lowercase(Locale.ROOT) }

        // Find skills already covered by current team
        val coveredSkills = currentTeamMembers.flatMap { member ->
            member.skills.map { it.name.trim().lowercase(Locale.ROOT) }
        }.toSet()

        // Missing skills needed by the team for this opportunity
        val missingSkills = requiredSkills.filter { req ->
            coveredSkills.none { cov -> cov.contains(req) || req.contains(cov) }
        }

        // Candidate's skills
        val candidateSkillMap = candidate.skills.associateBy { it.name.trim().lowercase(Locale.ROOT) }

        // 1. Skill Compatibility (Max 50 points)
        // Highly rewards candidate who covers missing team skills!
        var skillPoints = 10.0 // Base competence points
        val complementaryCovered = mutableListOf<String>()
        val matchedCandidateSkills = mutableListOf<SkillEntry>()

        if (missingSkills.isNotEmpty()) {
            for (missing in missingSkills) {
                val candidateSkill = candidateSkillMap.entries.firstOrNull {
                    it.key.contains(missing) || missing.contains(it.key)
                }?.value

                if (candidateSkill != null) {
                    matchedCandidateSkills.add(candidateSkill)
                    complementaryCovered.add(candidateSkill.name)
                    val levelMultiplier = candidateSkill.level.weight // 1.0 for Advanced, 0.8 Intermediate, 0.5 Beginner
                    skillPoints += (35.0 / missingSkills.size) * levelMultiplier
                }
            }
            // Reward any additional required skills that strengthen team
            val additionalMatches = candidateSkillMap.entries.filter { entry ->
                entry.value !in matchedCandidateSkills && requiredSkills.any { req ->
                    entry.key.contains(req) || req.contains(entry.key)
                }
            }
            skillPoints += (additionalMatches.size * 5.0)
        } else {
            // Team already covers basic requirements, evaluate overall depth
            for (req in requiredSkills) {
                val candidateSkill = candidateSkillMap.entries.firstOrNull {
                    it.key.contains(req) || req.contains(it.key)
                }?.value
                if (candidateSkill != null) {
                    matchedCandidateSkills.add(candidateSkill)
                    skillPoints += (35.0 / requiredSkills.size) * candidateSkill.level.weight
                }
            }
        }
        val finalSkillScore = skillPoints.coerceIn(5.0, 50.0).toInt()

        // 2. Interest Compatibility (Max 20 points)
        var interestPoints = 6.0
        val oppCategory = opportunity.category.displayName.lowercase(Locale.ROOT)
        val candidateInterests = candidate.interests.map { it.lowercase(Locale.ROOT) }

        if (candidateInterests.any {
                val tr = it.removeSuffix("s")
                oppCategory.contains(tr) || tr.contains(oppCategory) ||
                        opportunity.title.lowercase(Locale.ROOT).contains(tr)
            }) {
            interestPoints += 7.0
        }
        val matchingSkillInterests = candidateInterests.count { interest ->
            val tr = interest.removeSuffix("s")
            requiredSkills.any { req -> tr.contains(req) || req.contains(tr) }
        }
        interestPoints += (matchingSkillInterests * 4.0)
        val finalInterestScore = interestPoints.coerceIn(4.0, 20.0).toInt()

        // 3. Availability (Max 15 points)
        var availPoints = 6.0
        val isFullTimeEvent = opportunity.category == OpportunityCategory.HACKATHON ||
                opportunity.mode == OpportunityMode.OFFLINE

        if (isFullTimeEvent && candidate.availability.fullTimeEvent) {
            availPoints += 4.0
        }
        if (candidate.availability.weekends) availPoints += 3.0
        if (candidate.availability.evenings) availPoints += 2.0
        val finalAvailScore = availPoints.coerceIn(5.0, 15.0).toInt()

        // 4. Opportunity Requirements & Eligibility (Max 15 points)
        var reqPoints = 8.0
        if (opportunity.preferredBranch.equals("All", ignoreCase = true) ||
            opportunity.preferredBranch.contains(candidate.branch, ignoreCase = true) ||
            candidate.branch.contains(opportunity.preferredBranch, ignoreCase = true)
        ) {
            reqPoints += 4.0
        }
        if (opportunity.eligibleYears.isEmpty() ||
            opportunity.eligibleYears.any { it.contains(candidate.year, ignoreCase = true) }
        ) {
            reqPoints += 3.0
        }
        val finalReqScore = reqPoints.coerceIn(4.0, 15.0).toInt()

        val totalScore = (finalSkillScore + finalInterestScore + finalAvailScore + finalReqScore).coerceIn(10, 99)

        val remainingMissing = missingSkills.filter { missing ->
            complementaryCovered.none { it.contains(missing, ignoreCase = true) || missing.contains(it, ignoreCase = true) }
        }.map { it.replaceFirstChar { char -> char.titlecase(Locale.ROOT) } }

        val highlights = when {
            complementaryCovered.isNotEmpty() ->
                "Fills team gap in ${complementaryCovered.joinToString(", ")}"
            matchedCandidateSkills.isNotEmpty() ->
                "Strong background in ${matchedCandidateSkills.first().name}"
            else -> "Active student aligned with ${opportunity.category.displayName}"
        }

        return TeammateMatch(
            candidate = candidate,
            breakdown = MatchBreakdown(
                totalScore = totalScore,
                skillScore = finalSkillScore,
                interestScore = finalInterestScore,
                availabilityScore = finalAvailScore,
                requirementScore = finalReqScore,
                complementarySkillsCovered = complementaryCovered,
                candidateSkillsMatched = matchedCandidateSkills,
                missingSkillsStillNeeded = remainingMissing,
                highlights = highlights
            )
        )
    }

    /**
     * Calculates opportunity match percentage for a single student profile (Proactive Opportunity Recommendation).
     */
    fun calculateOpportunityFit(
        student: StudentProfile,
        opportunity: Opportunity
    ): Int {
        val requiredSkills = opportunity.requiredSkills.map { it.lowercase(Locale.ROOT) }

        var skillScore = 0.0
        if (requiredSkills.isNotEmpty()) {
            for (req in requiredSkills) {
                val matched = student.skills.firstOrNull {
                    it.name.lowercase(Locale.ROOT).contains(req) || req.contains(it.name.lowercase(Locale.ROOT))
                }
                if (matched != null) {
                    skillScore += (50.0 / requiredSkills.size) * matched.level.weight
                }
            }
        } else {
            skillScore = 30.0
        }

        var interestScore = 8.0
        val oppName = opportunity.title.lowercase(Locale.ROOT)
        val catName = opportunity.category.displayName.lowercase(Locale.ROOT)
        val studentInterests = student.interests.map { it.lowercase(Locale.ROOT) }

        for (interest in studentInterests) {
            val trimmed = interest.removeSuffix("s")
            if (oppName.contains(trimmed) || trimmed.contains(oppName) ||
                catName.contains(trimmed) || trimmed.contains(catName) ||
                opportunity.description.lowercase(Locale.ROOT).contains(trimmed)
            ) {
                interestScore += 7.0
                break
            }
        }

        val availScore = if (student.availability.weekends || student.availability.evenings || student.availability.fullTimeEvent) 15.0 else 10.0
        val branchScore = if (opportunity.preferredBranch.equals("All", ignoreCase = true) ||
            opportunity.preferredBranch.contains(student.branch, ignoreCase = true) ||
            student.branch.contains(opportunity.preferredBranch, ignoreCase = true)
        ) 15.0 else 9.0

        val total = (skillScore + interestScore + availScore + branchScore).coerceIn(20.0, 98.0).toInt()
        return total
    }
}
