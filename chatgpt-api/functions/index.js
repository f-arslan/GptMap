/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest, onCall } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const { setGlobalOptions } = require("firebase-functions/v2");
require("dotenv").config();
const { initializeApp } = require("firebase-admin/app");
initializeApp();

// Set the maximum instances to 10 for all functions
setGlobalOptions({ maxInstances: 10 });

const { OpenAI } = require("openai");

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

async function retrieveAssistant(assistantId) {
  try {
    return await openai.beta.assistants.retrieve(assistantId);
  } catch (error) {
    console.log(error);
  }
}

async function createThread() {
  try {
    return await openai.beta.threads.create();
  } catch (error) {
    console.log(error);
  }
}

async function createMessage(threadId, role, content) {
  try {
    return await openai.beta.threads.messages.create(threadId, {
      role: role,
      content: content,
    });
  } catch (error) {
    console.log(error);
  }
}

async function createRun(threadId, assistantId) {
  try {
    return await openai.beta.threads.runs.create(threadId, {
      assistant_id: assistantId,
    });
  } catch (error) {
    console.log(error);
  }
}

async function retrieveRun(threadId, runId) {
  try {
    return await openai.beta.threads.runs.retrieve(threadId, runId);
  } catch (error) {
    console.log(error);
  }
}

async function listMessages(threadId) {
  try {
    return await openai.beta.threads.messages.list(threadId);
  } catch (error) {
    console.log(error);
  }
}

exports.askLocation = onRequest({ region: "europe-west3" }, async (request, response) => {
  const assistantId = "asst_npYA2Cc8VynlL6hwZADo4koh";
  const message = "what is the best place in the earth?";

  const assistant = await retrieveAssistant(assistantId);
  const thread = await createThread();
  await createMessage(thread.id, "user", message);
  const run = await createRun(thread.id, assistant.id);

  let runStatus = await retrieveRun(thread.id, run.id);

  const maxThreshold = 15;
  let count = 0;
  while (runStatus.status !== "completed" && count < maxThreshold) {
    await new Promise((resolve) => setTimeout(resolve, 1000));
    runStatus = await retrieveRun(thread.id, run.id);
    console.log(runStatus.status);
    count++;
  }

  const messages = await listMessages(thread.id);
  const lastMessageForRun = messages.data.filter(
    (message) => message.run_id == run.id && message.role === "assistant"
  );

  if (lastMessageForRun) {
    console.log(`${lastMessageForRun.content}\n`);
  }

  return;
});

