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

exports.asklocation = onRequest({ region: "europe-west3" }, async (request, response) => {
  try {

    const assistant = await openai.beta.assistants.retrieve("asst_npYA2Cc8VynlL6hwZADo4koh");
    const thread = await openai.beta.threads.create();
    const message = "what is the best place in the earth?";

    await openai.beta.threads.messages.create(thread.id, {
      role: "user",
      content: message,
    });

    const run = await openai.beta.threads.runs.create(thread.id, {
      assistant_id: assistant.id,
    });

    let runStatus = await openai.beta.threads.runs.retrieve(thread.id, run.id);

    maxThreshold = 15;
    count = 0;
    while (runStatus.status !== "completed" && count < maxThreshold) {
      await new Promise((resolve) => setTimeout(resolve, 1000));
      runStatus = await openai.beta.threads.runs.retrieve(thread.id, run.id);
      console.log(runStatus.status);
      count++;
    }
    console.log("run Status:", runStatus.status);

    const messages = await openai.beta.threads.messages.list(thread.id);
    const lastMessageForRun = messages.data.filter(
      (message) => message.run_id == run.id && message.role === "assistant",
    );
    console.log(lastMessageForRun);
    if (lastMessageForRun) {
      console.log(`${lastMessageForRun.content}\n`);
    }
  } catch (error) {
    console.log(error);
  }

  response.send("Hello, World!");
  return;
});
