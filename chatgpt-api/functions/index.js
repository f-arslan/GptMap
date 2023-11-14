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
setGlobalOptions({ maxInstances: 20 });

const { OpenAI } = require("openai");

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

async function createAssistant() {
  return await openai.beta.assistants.retrieve("asst_npYA2Cc8VynlL6hwZADo4koh");
}

async function createThread() {
  return await openai.beta.threads.create();
}

async function processThread(thread, message) {
  await openai.beta.threads.messages.create(thread.id, {
    role: "user",
    content: message,
  });
}

async function createRun(thread, assistant) {
  return await openai.beta.threads.runs.create(thread.id, {
    assistant_id: assistant.id,
  });
}

async function runStatusObserver(thread, run) {
  return await openai.beta.threads.runs.retrieve(thread.id, run.id);
}

async function getMessages(thread) {
  return await openai.beta.threads.messages.list(thread.id);
}

exports.asklocation = onRequest({ region: "europe-west3" }, async (request, response) => {
  try {

    const assistant = createAssistant();

    const thread = createThread();

    const message = request;

    processThread(thread, message);

    const run = createRun(thread, assistant);

    let runStatus = runStatusObserver(thread, run);

    maxThreshold = 3;
    count = 0;
    while (runStatus !== "completed" && runcount < maxThreshold) {
      delay();
      runStatus = runStatusObserver();
    }
    if (runStatus !== completed) {
      return null;
    }

    const messages = getMessages(thread);

    const lastMessageForRun = messages.data.filter(
      (message) => message.run_id == run.id && message.role === "assistant",
    ).pop();

    if (lastMessageForRun) {
      console.log(`${lastMessageForRun.content[0].text.value}\n`);
    }
  } catch (error) {
    console.log(error);
  }
});


async function delay() {
  return await new Promise((resolve) => setTimeout(resolve, 1000));
}
