package com.tps.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppWebhookController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // Inject your Respond.io API key from properties or env
    private String respondIoApiKey ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTM0OTIsInNwYWNlSWQiOjI5NDcwNCwib3JnSWQiOjI5MDk4OSwidHlwZSI6ImFwaSIsImlhdCI6MTc1MDI2Mzc3NX0.owMR_Nc2PixsxEGiNTpboywwOA0DlH9VtCvFPPBmGxI";

    // temporary memory to track conversation state by contactId
    private final Map<Long, String> userState = new ConcurrentHashMap<>();

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveMessage(@RequestBody String payload) {
        try {
            JsonNode root = mapper.readTree(payload);

            if (root.isArray()) {
                for (JsonNode evt : root) handleEvent(evt);
            } else {
                handleEvent(root);
            }
            return ResponseEntity.ok("EVENT_RECEIVED");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_PAYLOAD");
        }
    }

    private void handleEvent(JsonNode evt) {
        String eventType = evt.path("event_type").asText("");
        if (!"message.received".equals(eventType)) return;

        JsonNode contactNode = evt.path("contact");
        long contactId = contactNode.path("id").asLong();
        String firstName = contactNode.path("firstName").asText("");
        String lastName  = contactNode.path("lastName").asText("");

        JsonNode msgNode  = evt.path("message");
        long channelId    = msgNode.path("channelId").asLong();
        String msgType    = msgNode.path("message").path("type").asText("");
        String text       = msgNode.path("message").path("text").asText("");
        String normalized = text == null ? "" : text.trim().toLowerCase();

        System.out.printf("event=%s | contactId=%s | channelId=%s | name=%s %s | text=%s%n",
                eventType, contactId, channelId, firstName, lastName, text);

        if (!"text".equalsIgnoreCase(msgType)) {
            sendRespondIoText(contactId, channelId, "🙏 Thank you for contacting us! Please send text messages only.");
            return;
        }
    
        String state = userState.get(contactId);

        // If we are awaiting a Consumer ID, treat the incoming text as that ID and call DataNamix
        if ("AWAITING_CONSUMER_ID".equals(state)) {
            String consumerIdInput = text.trim();
            // A quick validation: DataNamix example used numeric ID (13 digits). We accept any non-empty for now.
            if (consumerIdInput.isEmpty()) {
                sendRespondIoText(contactId, channelId, "⚠️ Please provide a valid Consumer ID (cannot be empty).");
                return;
            }
            if (!consumerIdInput.matches("\\d{13}")) {
                sendRespondIoText(contactId, channelId,
                    "⚠️ Invalid ID Number.\n" +
                    "Please enter a valid *13-digit* South African ID Number.");
                return;
            }
            // Inform user we are processing
            sendRespondIoText(
            	    contactId, 
            	    channelId, 
            	    "🔎 Thank you! Looking up Consumer ID: *" + consumerIdInput + "* …\n" +
            	    "⏳ We are connecting to *DataNamix* and searching consumer details. Please wait…"
            	);

            // Call DataNamix
            try {
                String reply = callDataNamixAndFormatReply(consumerIdInput);
                sendRespondIoText(contactId, channelId, reply);
            } catch (Exception ex) {
                ex.printStackTrace();
                sendRespondIoText(contactId, channelId, "❌ Sorry, we encountered an error while fetching consumer data. Please try again later.");
            } finally {
                // Clear the state
                userState.remove(contactId);
            }
            return;
        }
        

        // Normal chat flow
        switch (normalized) {
            case "hi":
            case "hello":
                sendRespondIoText(contactId, channelId,
                        "👋 Hi " + (firstName.isEmpty() ? "there" : firstName) + "! Welcome to TPS Credit Services.");
                sendRespondIoText(contactId, channelId,
                        "🔍 Please type *Search Consumer Option* to continue.");
                break;

            case "search consumer option":
                sendRespondIoText(contactId, channelId,
                        "✅ You selected *Search Consumer Option*.\nPlease provide your *Consumer ID* to proceed.");
                // set user state
                userState.put(contactId, "AWAITING_CONSUMER_ID");
                break;

            default:
                sendRespondIoText(contactId, channelId,
                        "🙏 Thank you for contacting us! We will get back to you shortly.");
                break;
        }
    }

 
    // ========= SENDER HELPERS (all use /contact/id:{contactId}/message) =========

    /** Send plain text */
    private void sendRespondIoText(long contactId, long channelId, String messageText) {
        String url = "https://api.respond.io/v2/contact/id:" + contactId + "/message";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(respondIoApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("channelId", channelId);
        body.put("message", Map.of("type", "text", "text", messageText));

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            System.out.println("✅ Text sent → " + response.getBody());
        } catch (Exception ex) {
            System.err.println("❌ Send text failed: " + ex.getMessage());
        }
    }

    private String callDataNamixAndFormatReply(String idNumber) throws Exception {
        // Build the JSON payload manually as a raw string
        String requestJson = String.format("""
            {
              "EnvironmentType": "SANDBOX",
              "EnquiryReason": "Credit Check",
              "IdNumber": "%s",
              "Reference": "test"
            }
            """, idNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6ImZpY2FfZmFjdG9yeV9jb25zdW1lcl9zdGFydCBkYXRhbmFtaXhfY29uc3VtZXJfcmVwb3J0IGZpY2FfZmFjdG9yeV9jb25zdW1lciBmaWNhX2ZhY3RvcnlfYnVzaW5lc3MgYnJhbmRfZmV0Y2hfZGF0YV9lbnJpY2htZW50IGdvb2dsZV9wbGFjZXMiLCJzdWIiOiJQQi1BQkEwMDEiLCJwcm9maWxlX2lkIjoiNTU1Iiwia2lkIjoiZGF0YW5hbWl4IiwibmJmIjoxNzYyNzkxMDI2LCJleHAiOjE3NjI4MDU0MjYsImlzcyI6Imh0dHBzOi8vYXBpLmRhdGFuYW1peC5jb20iLCJhdWQiOiJodHRwczovL2FwaS5kYXRhbmFtaXguY29tIn0.IHX0qKSqSjmL0HxgQQU9akabSDQw4ZwVyM_35Hd3d4L6Ngas-jwNF94Ia6LjvHaZiW0uvh8YN1AY-F59lfbcap6ZqKbWyOj_ZRFdmWeev_Jh_JEuHc76JfeGWj7Dsk-8qB4kZqKkdXKjrHNrgzrPSEdYCPGT9aHbhTrx-Wf0R0LGEYk9EXBmU7dhI43e1-YZE-1spuyCrNdq8TAjYIdOqgVLUeU7EO4fMwBcS27xCHl9bxswc__o0OJkp2CoFP31_HuhATo2Dnvm8ieTXJa-lpeEle0AwMjlzYaLK8ZZd6jtyVKO5o2hhAZAQgmJQwvUVr_Iwde_8x4VUwhMrrzacA" ); // or "x-api-key" if required

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "https://api.datanamix.com/v1/credit/datanamix/consumer-search",
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("DataNamix call failed: status=" + resp.getStatusCodeValue());
        }

        JsonNode root = mapper.readTree(resp.getBody());
        boolean success = root.path("Success").asBoolean(false);

        if (!success) {
            return "⚠️ DataNamix returned no results for this ID.";
        }

        JsonNode consumerArray = root.path("ConsumerDetails");
        if (!consumerArray.isArray() || consumerArray.size() == 0) {
            return "🔎 No consumer details found for ID: *" + idNumber + "*.";
        }

        StringBuilder sb = new StringBuilder("✅ Consumer Search Results\n\n");

        for (int i = 0; i < consumerArray.size(); i++) {
            JsonNode c = consumerArray.get(i);

            sb.append("🔹 *Result " + (i + 1) + "*\n");
            sb.append("• *ConsumerID:* " + c.path("ConsumerID").asText("") + "\n");
            sb.append("• *Name:* " 
                    + c.path("FirstName").asText("") + " "
                    + c.path("SecondName").asText("") + " "
                    + c.path("ThirdName").asText("") + " "
                    + c.path("Surname").asText("") + "\n");
            sb.append("• *ID No:* " + c.path("IDNo").asText("") + "\n");
            sb.append("• *Passport:* " + c.path("PassportNo").asText("") + "\n");
            sb.append("• *Birth Date:* " + c.path("BirthDate").asText("") + "\n");
            sb.append("• *Gender:* " + c.path("GenderInd").asText("") + "\n");
            sb.append("• *Temp Ref:* " + c.path("TempReference").asText("") + "\n");
            sb.append("• *EnquiryID:* " + c.path("EnquiryID").asText("") + "\n");
            sb.append("• *ResultID:* " + c.path("EnquiryResultID").asText("") + "\n");
            sb.append("• *Reference:* " + c.path("Reference").asText("") + "\n\n");
        }

        sb.append("🟢 Reply with the *ConsumerID* to get detailed info.");
        return sb.toString();

    }

   
 

}
