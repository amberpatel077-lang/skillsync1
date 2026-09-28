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
    version = 4,
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
                ),
                StudentEntity(
                    id = "student_parth",
                    name = "Parth Jain",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Computer Science & Engineering",
                    year = "1st Year",
                    email = "parth.jain@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "C++:INTERMEDIATE;Python:INTERMEDIATE;Web Development:BEGINNER;Data Structures:INTERMEDIATE",
                    interestsRaw = "Competitive Programming;Hackathons;Coding Clubs;Open Source",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "20 Dec 2026",
                    preferredTypesRaw = "Hackathon;Project;Club",
                    lookingFor = "BOTH",
                    contactPreference = "Discord (@parth_j) & WhatsApp",
                    bio = "1st-year enthusiast eager to learn from seniors! Actively practicing DSA and looking for collaborative teams.",
                    projectsRaw = "AlgoVisualizer::Sorting algorithm step visualizer::Python, Pygame::Creator::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_suhani",
                    name = "Suhani Saxena",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Information Technology",
                    year = "2nd Year",
                    email = "suhani.s@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "UI/UX:ADVANCED;Figma:ADVANCED;Volunteering:ADVANCED;Event Management:INTERMEDIATE",
                    interestsRaw = "Design;Volunteering;Cultural Fests;Clubs;Social Impact",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "15 Dec 2026",
                    preferredTypesRaw = "Volunteering;Club;Project",
                    lookingFor = "BOTH",
                    contactPreference = "Email & Instagram",
                    bio = "UI/UX designer and active campus volunteer. Managed student operations for Aayam 2025 and design for college magazines.",
                    projectsRaw = "Aayam 2025 Campaign::Social media assets & campus directional signage::Figma, Illustrator::Design Lead::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_saksham",
                    name = "Saksham Bhatt",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Electronics & Communication",
                    year = "2nd Year",
                    email = "saksham.b@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "IoT/Electronics:ADVANCED;C++:ADVANCED;Robotics:INTERMEDIATE;Hardware:ADVANCED",
                    interestsRaw = "Robotics;Embedded Systems;Hardware Hackathons;Sports",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "30 Dec 2026",
                    preferredTypesRaw = "Project;Hackathon;Sports",
                    lookingFor = "TEAM",
                    contactPreference = "Discord & WhatsApp",
                    bio = "Embedded systems developer. Building drone circuits and micro-controller telemetry. Ready to team up for robotics and hardware hackathons.",
                    projectsRaw = "AeroSensor Node::LoRa-based long-range weather telemetry node::C++, Arduino, PCB::Hardware Dev::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_nishith",
                    name = "Nishith Jain",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Electrical Engineering",
                    year = "1st Year",
                    email = "nishith.j@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Python:INTERMEDIATE;Machine Learning:BEGINNER;Mathematics:ADVANCED",
                    interestsRaw = "AI/ML;Electrical Systems;Hackathons;Robotics",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = false,
                    availableUntil = "10 Jan 2027",
                    preferredTypesRaw = "Hackathon;Project;Club",
                    lookingFor = "TEAM",
                    contactPreference = "Email (nishith.j@sgsits.ac.in)",
                    bio = "1st-year electrical student eager to contribute to AI/ML and microgrid software projects. Seeking mentor-led teams.",
                    projectsRaw = "Campus Energy Calc::Simple load estimator tool::Python::Author::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_shraddha",
                    name = "Shraddha Rai",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Electronics & Instrumentation",
                    year = "1st Year",
                    email = "shraddha.r@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "Presentation:ADVANCED;Volunteering:ADVANCED;Public Speaking:INTERMEDIATE;UI/UX:BEGINNER",
                    interestsRaw = "Volunteering;College Clubs;Debates;Event Planning;Startups",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "28 Dec 2026",
                    preferredTypesRaw = "Volunteering;Club;Competition",
                    lookingFor = "BOTH",
                    contactPreference = "WhatsApp & College Email",
                    bio = "Outgoing student lead interested in campus volunteering drives and pitch presentations. Enthusiastic organizer.",
                    projectsRaw = "NSS EcoDrive::Lead student organizer for campus recycling drive::Event Head::",
                    isPublic = true
                ),
                StudentEntity(
                    id = "student_piyush",
                    name = "Piyush Sahu",
                    college = "Shri G.S. Institute of Tech & Science (SGSITS)",
                    branch = "Mechanical Engineering",
                    year = "2nd Year",
                    email = "piyush.sahu@sgsits.ac.in",
                    isEmailVerified = true,
                    isBranchVerified = true,
                    skillsRaw = "CAD:ADVANCED;Sports:ADVANCED;IoT/Electronics:INTERMEDIATE;Python:BEGINNER",
                    interestsRaw = "Sports;Automobile;CAD Modeling;Robotics;College Fest",
                    availabilityWeekdays = true,
                    availabilityWeekends = true,
                    availabilityEvenings = true,
                    availabilityFullTime = true,
                    availableUntil = "15 Dec 2026",
                    preferredTypesRaw = "Sports;Project;Club",
                    lookingFor = "TEAM",
                    contactPreference = "WhatsApp & Phone",
                    bio = "Mechanical engineering sophomore & departmental cricket vice-captain. Experienced in SolidWorks 3D CAD modeling.",
                    projectsRaw = "Formula Student Chassis::Tubular space-frame suspension analysis::SolidWorks, ANSYS::CAD Engineer::",
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
                ),
                OpportunityEntity(
                    id = "opp_aayam_volunteers",
                    title = "Aayam 2026 Cultural Fest Volunteer Taskforce",
                    organizer = "SGSITS Central Student Council",
                    category = "VOLUNTEERING",
                    date = "Feb 20 - 23, 2027",
                    deadline = "Nov 15, 2026",
                    mode = "OFFLINE",
                    location = "Central Auditorium & Open Air Theatre, SGSITS",
                    teamSizeMin = 2,
                    teamSizeMax = 10,
                    requiredSkillsRaw = "Volunteering;Event Management;Presentation",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year",
                    description = "Join the frontline team powering Central India's largest collegiate cultural festival! Roles in stage coordination, guest reception, media coverage, and technical sound/lighting logistics.",
                    currentMembersCount = 6,
                    prizes = "Official Certificate of Merit + Duty Leaves + VIP Fest Passes",
                    isFeatured = true,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_nss_green",
                    title = "Green Campus & E-Waste Recycling Drive",
                    organizer = "SGSITS NSS & Eco Club Chapter",
                    category = "VOLUNTEERING",
                    date = "Nov 10, 2026",
                    deadline = "Nov 08, 2026",
                    mode = "OFFLINE",
                    location = "Campus Gardens & CIDI Ground",
                    teamSizeMin = 1,
                    teamSizeMax = 5,
                    requiredSkillsRaw = "Volunteering;Event Planning;Community Work",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year;4th Year",
                    description = "Volunteer initiative for 500-tree campus plantation and collection/disposal of academic electronic waste with government-approved recyclers.",
                    currentMembersCount = 3,
                    prizes = "NSS Activity Credit Points + Volunteer Badges",
                    isFeatured = false,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_acm_recruit",
                    title = "ACM Student Chapter Core Induction 2026-27",
                    organizer = "ACM SGSITS Student Chapter",
                    category = "CLUB",
                    date = "Nov 01 - Nov 05, 2026",
                    deadline = "Oct 28, 2026",
                    mode = "HYBRID",
                    location = "CS Dept Seminar Hall & Online",
                    teamSizeMin = 1,
                    teamSizeMax = 3,
                    requiredSkillsRaw = "Web Development;Competitive Programming;UI/UX;Python",
                    preferredBranch = "CSE, IT, ECE",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year",
                    description = "Annual recruitment drive for technical associates, contest leads, editorial designers, and web developers for the premier computing chapter at SGSITS.",
                    currentMembersCount = 5,
                    prizes = "Core Committee Membership + ACM Global Student Membership",
                    isFeatured = true,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_gdsc_leads",
                    title = "Google DSC Leads & Tech Domain Heads",
                    organizer = "Google Developer Student Club (GDSC) SGSITS",
                    category = "CLUB",
                    date = "Nov 15, 2026",
                    deadline = "Nov 02, 2026",
                    mode = "HYBRID",
                    location = "Computer Center, SGSITS",
                    teamSizeMin = 1,
                    teamSizeMax = 4,
                    requiredSkillsRaw = "Android;Machine Learning;Cloud/DevOps;React",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year",
                    description = "Calling passionate developers and designers to lead Android, AI/ML, Cloud, and Design circles. Host study jams and lead campus developer communities.",
                    currentMembersCount = 2,
                    prizes = "Google DSC Official Lead Credentials + Google Cloud Credits",
                    isFeatured = false,
                    isVerified = true
                ),
                OpportunityEntity(
                    id = "opp_inter_sports",
                    title = "SGSITS Inter-Branch Sports Tournament",
                    organizer = "Physical Education & Sports Department",
                    category = "SPORTS",
                    date = "Dec 10 - 18, 2026",
                    deadline = "Nov 25, 2026",
                    mode = "OFFLINE",
                    location = "SGSITS Sports Complex & Cricket Ground",
                    teamSizeMin = 5,
                    teamSizeMax = 15,
                    requiredSkillsRaw = "Sports;Teamwork;Athletics;Fitness",
                    preferredBranch = "All Branches",
                    eligibleYearsRaw = "1st Year;2nd Year;3rd Year;4th Year",
                    description = "Inter-branch athletic championship across Cricket, Football, Volleyball, Badminton, and Chess. Form branch teams or join mixed-year squads to compete for the Chancellor's Trophy.",
                    currentMembersCount = 8,
                    prizes = "Championship Gold Medals + Trophies + University Team Selections",
                    isFeatured = true,
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
                    toStudentName = "Arjun Sharma",
                    note = "Hey Arjun! We noticed your strong Presentation and IoT skills. Our SIH team has 3/4 members and we'd love you to lead the pitch & hardware angle!",
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 3600000L
                ),
                TeamInvitationEntity(
                    id = "inv_2",
                    teamId = "team_ai_vision",
                    teamName = "VisionX Research",
                    opportunityTitle = "AI Healthcare Innovation Challenge",
                    fromStudentId = "student_priya",
                    fromStudentName = "Priya Sharma",
                    toStudentId = "student_amber",
                    toStudentName = "Amber Patel",
                    note = "Hi Amber! Your UI/UX and Kotlin skills would be amazing for our Computer Vision mobile prototype. Would you like to join?",
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 1800000L
                )
            )
            invitations.forEach { dao.insertInvitation(it) }
        }
    }
}
