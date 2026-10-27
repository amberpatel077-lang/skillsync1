const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
});

test("Authenticated user: can create their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Smith",
      email: "alice@example.com",
      branch: "CSE",
      year: "3rd Year",
    })
  );
});

test("Authenticated user: cannot update another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Smith",
      email: "alice@example.com",
      branch: "CSE",
      year: "3rd Year",
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      name: "Hacked Name",
    })
  );
});

test("Authenticated user: can create a team where they are leader and member", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("teams").doc("team_1").set({
      id: "team_1",
      name: "Hackathon Squad",
      leaderId: ALICE_UID,
      memberIds: [ALICE_UID],
    })
  );
});

test("Non-team member: cannot send message to team chat", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("teams").doc("team_1").set({
      id: "team_1",
      name: "Hackathon Squad",
      leaderId: ALICE_UID,
      memberIds: [ALICE_UID],
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("teams").doc("team_1").collection("messages").doc("msg_1").set({
      id: "msg_1",
      teamId: "team_1",
      senderId: BOB_UID,
      content: "Hello unauthorized",
      timestamp: Date.now(),
    })
  );
});

test("Team member: can send message to team chat", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("teams").doc("team_1").set({
      id: "team_1",
      name: "Hackathon Squad",
      leaderId: ALICE_UID,
      memberIds: [ALICE_UID, BOB_UID],
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(
    bobDb.collection("teams").doc("team_1").collection("messages").doc("msg_1").set({
      id: "msg_1",
      teamId: "team_1",
      senderId: BOB_UID,
      content: "Hello teammates!",
      timestamp: Date.now(),
    })
  );
});
