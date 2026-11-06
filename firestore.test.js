const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: "demo-project",
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085
    }
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

test("Unauthenticated user cannot read or write user profiles", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection("users").doc("user123").get());
  await assertFails(unauthedDb.collection("users").doc("user123").set({ userId: "user123" }));
});

test("Authenticated owner can read and write their own user profile", async () => {
  const authedDb = testEnv.authenticatedContext("user123").firestore();
  await assertSucceeds(authedDb.collection("users").doc("user123").set({ userId: "user123" }));
  await assertSucceeds(authedDb.collection("users").doc("user123").get());
});

test("User cannot read another user's profile", async () => {
  const authedDb = testEnv.authenticatedContext("user123").firestore();
  await assertFails(authedDb.collection("users").doc("user456").get());
});

test("Authenticated parent can create a hive family", async () => {
  const authedDb = testEnv.authenticatedContext("parent123").firestore();
  await assertSucceeds(authedDb.collection("hives").doc("RUCHE1").set({
    hiveCode: "RUCHE1",
    familyName: "Famille Abeille",
    ownerUid: "parent123",
    parentPin: "1234",
    notifyOnValidation: true,
    requireValidationUnderAge7: true,
    defaultRequireParentValidation: false,
    memberUids: ["parent123"]
  }));
});

test("Member can access hive missions and completions", async () => {
  const adminDb = testEnv.authenticatedContext("parent123").firestore();
  await adminDb.collection("hives").doc("RUCHE1").set({
    hiveCode: "RUCHE1",
    familyName: "Famille Abeille",
    ownerUid: "parent123",
    parentPin: "1234",
    notifyOnValidation: true,
    requireValidationUnderAge7: true,
    defaultRequireParentValidation: false,
    memberUids: ["parent123", "child456"]
  });

  const childDb = testEnv.authenticatedContext("child456").firestore();
  await assertSucceeds(childDb.collection("hives").doc("RUCHE1").collection("missions").doc("m1").set({
    title: "Ranger son lit",
    points: 2
  }));
  await assertSucceeds(childDb.collection("hives").doc("RUCHE1").collection("missions").doc("m1").get());
});
