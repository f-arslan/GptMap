/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
require('dotenv').config()
const { initializeApp } = require("firebase-admin/app");
initializeApp()

const { OpenAI } = require("openai");

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY
});

async function createAssistant() {
  const assistant = await openai.beta.assistants.retrieve("asst_npYA2Cc8VynlL6hwZADo4koh");
  return assistant;
}




exports.helloWorld = onRequest(async (request, response) => {
  const assistant = await createAssistant();
  logger.info("Hello logs!", { structuredData: true });
  response.send(assistant);
});