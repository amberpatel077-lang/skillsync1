package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        OpportunityEntity::class,
        TeamEntity::class,
        TeamMemberEntity::class,
        TeamTaskEntity::class,
        TeamChatMessageEntity::class,
        TeamInvitationEntity::class,
        TeamResourceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SkillSyncDatabase : RoomDatabase() {

    abstract fun skillSyncDao(): SkillSyncDao

    companion object {
        @Volatile
        private var INSTANCE: SkillSyncDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SkillSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillSyncDatabase::class.java,
                    "skillsync_database"
                )
                    .addCallback(SkillSyncDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SkillSyncDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.skillSyncDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: SkillSyncDao) {
            // Seed Students
            val students = listOf(
                StudentEntity(
                    id = "student_amber",
                    name = "Amber Patel",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "2nd Year",
                    email = "amberpatel077@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Python:ADVANCED;Machine Learning:INTERMEDIATE;React:INTERMEDIATE;UI/UX:BEGINNER;SQL:INTERMEDIATE",
                    interestsRaw = "Hackathons;AI/ML;Web Development;Competitive Programming;Open Source",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "30 Nov 2026",
                    preferredTypesRaw = "Hackathon;Project;Competition;Internship",
                    lookingFor = "BOTH",
                    contactPreference = "College Email & Discord (@amber_patel)",
                    bio = "CS sophomore passionate about applied AI and hackathons. Looking for complementary teammates with frontend/Figma and presentation skills!",
                    projectsRaw = "MediScan AI::Early pneumonia detection using PyTorch and CNNs::Python, PyTorch, FastAPI::ML Engineer::https://github.com/medi-scan###CampusHub::Real-time student club notices and event RSVP platform::React, Node.js, SQLite::Fullstack Dev::https://github.com/campushub",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_rahul",
                    name = "Rahul Kumar",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Information Technology",
                    year = "2nd Year",
                    email = "rahul.it@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "UI/UX:ADVANCED;Figma:ADVANCED;Presentation:INTERMEDIATE;React:BEGINNER",
                    interestsRaw = "UI/UX Design;Design Systems;Hackathons;Product Management",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "15 Dec 2026",
                    preferredTypesRaw = "Hackathon;Project;Club",
                    lookingFor = "TEAM",
                    contactPreference = "Discord (@rahul_ux) & WhatsApp",
                    bio = "Design enthusiast who turns wireframes into pixel-perfect user journeys. Seeking dev teammates for SIH and campus hackathons.",
                    projectsRaw = "SGSITS Redesign Concept::Complete modern redesign of university portal::Figma, Wireframing::Lead Designer::https://figma.com/@rahul###EcoTrack App::Mobile UI design for campus recycling rewards::Figma, Prototyping::Product Designer::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_priya",
                    name = "Priya Sharma",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "3rd Year",
                    email = "priya.sharma@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Machine Learning:ADVANCED;Python:ADVANCED;Data Science:ADVANCED;Cloud/DevOps:INTERMEDIATE",
                    interestsRaw = "AI/ML;Computer Vision;Research;Hackathons;Healthcare Tech",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = false,
                    availableUntil = "31 Dec 2026",
                    preferredTypesRaw = "Hackathon;Research;Internship",
                    lookingFor = "BOTH",
                    contactPreference = "Email (priya.sharma@sgsits.ac.in)",
                    bio = "Machine learning researcher and problem solver. Experienced in model optimization, NLP and deploying AI on cloud.",
                    projectsRaw = "AgriDetect::Crop pest classification with 96% accuracy on edge TPU::Python, TensorFlow, Docker::Lead Researcher::https://github.com/agrdetect",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_arjun",
                    name = "Arjun Verma",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Electronics & Communication",
                    year = "2nd Year",
                    email = "arjun.ece@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Presentation:ADVANCED;IoT/Electronics:ADVANCED;C++:INTERMEDIATE;Pitching:ADVANCED",
                    interestsRaw = "Robotics;Hardware;Startups;Pitch Competitions;Public Speaking",
                    availabilityWeekdays = false,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "20 Nov 2026",
                    preferredTypesRaw = "Competition;Hackathon;Club",
                    lookingFor = "TEAM",
                    contactPreference = "Discord (@arjun_v)",
                    bio = "Hardware hacker and team pitch lead. Won 1st place in Regional E-Summit pitch battle 2025. Love bringing tech stories to life.",
                    projectsRaw = "Smart Grid Node::ESP32-based energy monitoring telemetry::C++, MQTT, Hardware::Embedded Dev & Pitch Lead::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_sneha",
                    name = "Sneha Gupta",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Information Technology",
                    year = "3rd Year",
                    email = "sneha.g@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "React:ADVANCED;Web Development:ADVANCED;JavaScript:ADVANCED;Cloud/DevOps:INTERMEDIATE",
                    interestsRaw = "Web Development;Startups;Cloud;Open Source",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "30 Oct 2026",
                    preferredTypesRaw = "Hackathon;Project;Internship",
                    lookingFor = "TEAM",
                    contactPreference = "GitHub & Telegram",
                    bio = "Full-stack React engineer. Fast at crafting responsive web apps, state management and API integrations.",
                    projectsRaw = "DevConnect::Peer mentorship platform for engineering freshers::React, Next.js, Tailwind::Solo Creator::https://github.com/sneha/devconnect",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_abhinav_p",
                    name = "Abhinav Purohit",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "2nd Year",
                    email = "abhinav.p@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Python:INTERMEDIATE;Cloud/DevOps:INTERMEDIATE;Presentation:INTERMEDIATE;SQL:INTERMEDIATE",
                    interestsRaw = "Hackathons;Cloud;Systems Architecture;Design Thinking",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "10 Dec 2026",
                    preferredTypesRaw = "Hackathon;Project;Competition",
                    lookingFor = "BOTH",
                    contactPreference = "Discord & College Email",
                    bio = "CS sophomore focusing on cloud systems, system design and collaboration frameworks.",
                    projectsRaw = "CloudMetrics::Dockerized microservice uptime monitor::Go, Docker, AWS::Backend Dev::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_akash",
                    name = "Akash Patel",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "2nd Year",
                    email = "akash.patel@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Android:ADVANCED;Kotlin:ADVANCED;Java:ADVANCED;UI/UX:INTERMEDIATE",
                    interestsRaw = "Mobile Development;Jetpack Compose;Hackathons;Robotics",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "15 Dec 2026",
                    preferredTypesRaw = "Hackathon;Project;Internship",
                    lookingFor = "BOTH",
                    contactPreference = "Telegram (@akash_dev)",
                    bio = "Native Android architect building fluid Kotlin Jetpack Compose experiences. Ready to team up for tech contests.",
                    projectsRaw = "TaskPulse::Offline-first task manager with Room & M3::Kotlin, Compose, Room::Android Dev::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_ishmit",
                    name = "Ishmit Shukla",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "2nd Year",
                    email = "ishmit.s@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Presentation:ADVANCED;Pitching:ADVANCED;Product Design:INTERMEDIATE;Python:BEGINNER",
                    interestsRaw = "Entrepreneurship;Hackathons;Public Speaking;AI Products",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "30 Nov 2026",
                    preferredTypesRaw = "Hackathon;Competition;Club",
                    lookingFor = "TEAM",
                    contactPreference = "Discord & College Email",
                    bio = "Passionate presenter, pitch deck crafter and product thinker. Helping tech teams articulate high-impact solutions to judges.",
                    projectsRaw = "CivicPulse Pitch::Finalist pitch deck for Smart Governance Challenge::Product Lead & Pitcher::",
                    isPublic = true
                )
            )
            dao.insertStudents(students)

            // Seed Opportunities
            val opportunities = listOf(
                OpportunityEntity(
                    id = "opp_sih_2026",
                    title = "Smart India Hackathon (SIH 2026)",
                    organizer = "Ministry of Education & AICTE",
                    category = "HACKATHON",
                    date = "Nov 14 - 16, 2026",
                    deadline = "Oct 15, 2026",
                    mode = "HYBRID",
                    location = "New Delhi / Remote Nodal Centers",
                    teamSizeMin = 4,
                    teamSizeMax = 6,
                    requiredSkillsRaw = "Python;Machine Learning;UI/UX;Presentation;Cloud/DevOps",
                    preferredBranch = "CSE, IT, ECE",
                    eligibleYearsRaw = "2nd Year;3rd Year;4th Year",
                    description = "India's premier nationwide hackathon to provide students with a platform to solve pressing problems of ministries, departments, and industry. Themes include Smart Automation, Clean Tech, Smart Education, and Healthcare.",
                    currentMembersCount = 3,
                    prizes = "₹1,00,000 per problem statement + National recognition",
                    isFeatured = true,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_hacknova",
                    title = "SGSITS HackNova 2026",
                    organizer = "Computer Science Dept & ACM Chapter",
                    category = "HACKATHON",
                    date = "Nov 28 - 29, 2026",
                    deadline = "Nov 05, 2026",
                    mode = "OFFLINE",
                    location = "CIDI Lab, SGSITS Campus, Indore",
                    teamSizeMin = 3,
                    teamSizeMax = 4,
                    requiredSkillsRaw = "React;Python;Web Development;UI/UX",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year;4th Year",
                    description = "36-hour annual collegiate hackathon bringing multidisciplinary minds together. Build working software prototypes solving campus and city challenges in Indore.",
                    currentMembersCount = 2,
                    prizes = "₹50,000 Cash Pool + Cloud Credits & Trophies",
                    isFeatured = true,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_ai_sprint",
                    title = "AI Campus Innovation Sprint",
                    organizer = "Google Developer Student Club (GDSC)",
                    category = "COMPETITION",
                    date = "Nov 02, 2026",
                    deadline = "Oct 28, 2026",
                    mode = "ONLINE",
                    location = "Virtual Event",
                    teamSizeMin = 2,
                    teamSizeMax = 4,
                    requiredSkillsRaw = "Machine Learning;Python;Data Science",
                    preferredBranch = "CSE, IT",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year;4th Year",
                    description = "Build intelligent solutions using Gemini models and multimodal AI to assist university education and accessible learning.",
                    currentMembersCount = 1,
                    prizes = "Google Swag Kits + Google Cloud Certification vouchers",
                    isFeatured = true,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_ecell_venture",
                    title = "E-Cell VentureLab Startup Cohort",
                    organizer = "E-Cell SGSITS",
                    category = "CLUB",
                    date = "Dec 01, 2026 - Mar 2027",
                    deadline = "Nov 12, 2026",
                    mode = "OFFLINE",
                    location = "Incubation Center, SGSITS",
                    teamSizeMin = 2,
                    teamSizeMax = 5,
                    requiredSkillsRaw = "Presentation;UI/UX;Web Development;Pitching",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year",
                    description = "12-week student startup accelerator program with mentorship from alumni founders, seed capital grants, and demo day pitching to angel investors.",
                    currentMembersCount = 2,
                    prizes = "₹2,50,000 Seed Grants + Incubation Space",
                    isFeatured = false,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_robotics_rover",
                    title = "Autonomous Rover Team Recruitment",
                    organizer = "Robotics Club SGSITS",
                    category = "PROJECT",
                    date = "Ongoing 2026-27",
                    deadline = "Oct 30, 2026",
                    mode = "OFFLINE",
                    location = "Robotics Workshop, SGSITS",
                    teamSizeMin = 4,
                    teamSizeMax = 8,
                    requiredSkillsRaw = "IoT/Electronics;C++;CAD;Python",
                    preferredBranch = "ECE, MECH, EI, CSE",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year",
                    description = "Recruiting core sub-system members for the University Rover Challenge (URC). Hardware telemetry, mechanical suspension, and autonomous navigation.",
                    currentMembersCount = 4,
                    prizes = "URC Competition Sponsorship & Lab Equipment",
                    isFeatured = false,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_fintech_intern",
                    title = "FinTech FullStack Summer Internship",
                    organizer = "Razorpay Campus Talent Network",
                    category = "INTERNSHIP",
                    date = "June - Aug 2027",
                    deadline = "Nov 20, 2026",
                    mode = "HYBRID",
                    location = "Bengaluru / Hybrid",
                    teamSizeMin = 1,
                    teamSizeMax = 2,
                    requiredSkillsRaw = "React;Java;Cloud/DevOps;SQL",
                    preferredBranch = "CSE, IT, ECE",
                    eligibleYearsRaw = "2nd Year;3rd Year",
                    description = "Work on payment gateway infrastructure, developer APIs, and real-time checkout experiences with high scalability.",
                    currentMembersCount = 1,
                    prizes = "Stipend: ₹45,000/month + PPO Opportunity",
                    isFeatured = false,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_energy_research",
                    title = "Smart Campus Microgrid Energy Research",
                    organizer = "Dept of Electrical Engineering",
                    category = "RESEARCH",
                    date = "Winter Semester 2026",
                    deadline = "Dec 01, 2026",
                    mode = "OFFLINE",
                    location = "Power Systems Lab, SGSITS",
                    teamSizeMin = 2,
                    teamSizeMax = 3,
                    requiredSkillsRaw = "Data Science;Python;IoT/Electronics",
                    preferredBranch = "EE, EI, CSE",
                    eligibleYearsRaw = "2nd Year;3rd Year;4th Year",
                    description = "Faculty-guided research initiative modeling solar generation and load forecasting on SGSITS campus microgrid. Paper submission to IEEE conference.",
                    currentMembersCount = 1,
                    prizes = "IEEE Publication Credit + Research Stipend",
                    isFeatured = false,
                    isVerified = true
                )
            )
            dao.insertOpportunities(opportunities)

            // Seed Active Team Workspace
            val sampleTeam = TeamEntity(
                id = "team_sih_apex",
                name = "Team Apex Innovators",
                opportunityId = "opp_sih_2026",
                opportunityTitle = "Smart India Hackathon (SIH 2026)",
                organizer = "Ministry of Education & AICTE",
                leaderId = "student_amber",
                leaderName = "Amber Patel",
                maxMembers = 4,
                status = "TEAM_FORMING",
                createdAt = System.currentTimeMillis() - (86400000L * 2) // 2 days ago
            )
            dao.insertTeam(sampleTeam)

            val teamMembers = listOf(
                TeamMemberEntity(
                    teamId = "team_sih_apex",
                    studentId = "student_amber",
                    studentName = "Amber Patel",
                    role = "Team Lead & ML/Backend",
                    branch = "CSE",
                    year = "2nd Year",
                    keySkillsRaw = "Python;Machine Learning;SQL"
                ),
                TeamMemberEntity(
                    teamId = "team_sih_apex",
                    studentId = "student_rahul",
                    studentName = "Rahul Kumar",
                    role = "UI/UX & Product Design",
                    branch = "IT",
                    year = "2nd Year",
                    keySkillsRaw = "UI/UX;Figma;Presentation"
                ),
                TeamMemberEntity(
                    teamId = "team_sih_apex",
                    studentId = "student_priya",
                    studentName = "Priya Sharma",
                    role = "AI/ML Modeling",
                    branch = "CSE",
                    year = "3rd Year",
                    keySkillsRaw = "Machine Learning;Python;Data Science"
                )
            )
            dao.insertTeamMembers(teamMembers)

            // Seed Tasks for Team
            val tasks = listOf(
                TeamTaskEntity(
                    id = "task_1",
                    teamId = "team_sih_apex",
                    title = "Analyze Ministry Problem Statement ST1420 & requirements",
                    assignedToName = "Amber Patel",
                    isCompleted = true,
                    dueDate = "Oct 02"
                ),
                TeamTaskEntity(
                    id = "task_2",
                    teamId = "team_sih_apex",
                    title = "Draft high-fidelity UI wireframes in Figma",
                    assignedToName = "Rahul Kumar",
                    isCompleted = true,
                    dueDate = "Oct 06"
                ),
                TeamTaskEntity(
                    id = "task_3",
                    teamId = "team_sih_apex",
                    title = "Train baseline PyTorch model on open government dataset",
                    assignedToName = "Priya Sharma",
                    isCompleted = false,
                    dueDate = "Oct 10"
                ),
                TeamTaskEntity(
                    id = "task_4",
                    teamId = "team_sih_apex",
                    title = "Find 4th member with Pitch / Presentation skills!",
                    assignedToName = "Amber Patel",
                    isCompleted = false,
                    dueDate = "Oct 08"
                )
            )
            dao.insertTasks(tasks)

            // Seed Chat Messages
            val messages = listOf(
                TeamChatMessageEntity(
                    id = "msg_sys_1",
                    teamId = "team_sih_apex",
                    senderId = "system",
                    senderName = "SkillSync System",
                    message = "Team Apex Innovators formed for Smart India Hackathon (SIH 2026)! 🚀",
                    timestamp = System.currentTimeMillis() - 7200000L,
                    isSystemMessage = true
                ),
                TeamChatMessageEntity(
                    id = "msg_1",
                    teamId = "team_sih_apex",
                    senderId = "student_amber",
                    senderName = "Amber Patel",
                    message = "Hey team! Welcome Rahul and Priya! Our skill stack covers Python ML and UI/UX nicely.",
                    timestamp = System.currentTimeMillis() - 7000000L,
                    isSystemMessage = false
                ),
                TeamChatMessageEntity(
                    id = "msg_2",
                    teamId = "team_sih_apex",
                    senderId = "student_rahul",
                    senderName = "Rahul Kumar",
                    message = "Great connecting! I've already set up the Figma board with initial design tokens.",
                    timestamp = System.currentTimeMillis() - 6500000L,
                    isSystemMessage = false
                ),
                TeamChatMessageEntity(
                    id = "msg_3",
                    teamId = "team_sih_apex",
                    senderId = "student_priya",
                    senderName = "Priya Sharma",
                    message = "Sounds good. We still need one teammate who excels in Pitching/Presentation for the jury round.",
                    timestamp = System.currentTimeMillis() - 5800000L,
                    isSystemMessage = false
                ),
                TeamChatMessageEntity(
                    id = "msg_4",
                    teamId = "team_sih_apex",
                    senderId = "student_amber",
                    senderName = "Amber Patel",
                    message = "I'm checking SkillSync's Find Teammates matching engine right now — Arjun Verma and Ishmit Shukla match with 92%!",
                    timestamp = System.currentTimeMillis() - 3600000L,
                    isSystemMessage = false
                )
            )
            dao.insertChatMessages(messages)

            // Seed Resources
            val resources = listOf(
                TeamResourceEntity(
                    id = "res_1",
                    teamId = "team_sih_apex",
                    title = "GitHub Project Repository",
                    url = "https://github.com/apex-innovators/sih-2026-solution",
                    type = "GitHub"
                ),
                TeamResourceEntity(
                    id = "res_2",
                    teamId = "team_sih_apex",
                    title = "Figma Design & Prototype Canvas",
                    url = "https://figma.com/file/apex-sih2026-prototype",
                    type = "Figma"
                ),
                TeamResourceEntity(
                    id = "res_3",
                    teamId = "team_sih_apex",
                    title = "Google Drive Research Papers & Notes",
                    url = "https://drive.google.com/drive/folders/apex-sih",
                    type = "Drive"
                )
            )
            dao.insertResources(resources)

            // Seed sample invitations
            val invitations = listOf(
                TeamInvitationEntity(
                    id = "inv_1",
                    teamId = "team_sih_apex",
                    teamName = "Team Apex Innovators",
                    opportunityTitle = "Smart India Hackathon (SIH 2026)",
                    fromStudentId = "student_amber",
                    fromStudentName = "Amber Patel",
                    toStudentId = "student_arjun",
                    note = "Hey Arjun! We noticed your strong Presentation and IoT skills. Our SIH team has 3/4 members and we'd love you to lead the pitch & hardware angle!",
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )
            dao.insertInvitation(invitations[0])
        }
    }
}
