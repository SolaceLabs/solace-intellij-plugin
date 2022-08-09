package com.solace.aaron.ideaplugin1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.intellij.openapi.diagnostic.Logger;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;

public final class EventPortalRestService
{
    private static final Logger LOG = Logger.getInstance(EventPortalRestService.class);

    private EventPortalRestService()
    {
    }

    public static final String BASE_HOST = "http://api.solace.cloud/api/v2";

    public static String makeRestCall(String apiCall) {
        String authString = "Bearer " + TokenHolder.props.get("token");
        String searchUrl = BASE_HOST + "/" + apiCall;

        StringBuilder responseBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URI(searchUrl).toURL().openConnection();
            connection.setRequestProperty("Authorization", authString);
//        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()))))
             bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                responseBuilder.append(line);
            }
            return responseBuilder.toString();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("invalid uri!!",e);
        } catch (IOException e) {
            LOG.error("Err something went wrong!", e.getMessage());
            // In case demo fails!
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(EventPortalRestService.class.getResourceAsStream("/response.json"))))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    responseBuilder.append(line).append(System.lineSeparator());
                }
            } catch (IOException e2) {
                throw new RuntimeException(responseBuilder.toString(),e);
            }
            throw new RuntimeException(responseBuilder.toString(),e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                System.err.println("Somehow ended in the finally block trying close the buffered reader.");
            }
        }



    }


    public static List<EventPortalDomain> getEventPortalDomains() {
//        String stackBaseUrl = "https://api.solace.cloud/api/v2/architecture/applicationDomains";
//        String authString = "Bearer " + TokenHolder.props.get("token");

//        String searchUrl = String.format(stackBaseUrl, URLEncoder.encode(tag, Charsets.UTF_8), URLEncoder.encode(title, Charsets.UTF_8));
//        String searchUrl = stackBaseUrl;

        String response = makeRestCall("architecture/applicationDomains");

        DocumentContext parse = JsonPath.parse(response);
        List<Map<String, Object>> items = parse.read("data");
        List<EventPortalDomain> questions = new ArrayList<>();
        for (Map<String, Object> eachItem : items) {

            /**
             *     {
             *             "createdTime": "2022-07-12T20:07:57.808Z",
             *             "updatedTime": "2022-07-12T20:07:57.808Z",
             *             "createdBy": "67tr8tku41",
             *             "changedBy": "67tr8tku41",
             *             "id": "x4oo4skfh5e",
             *             "name": "Aaron Test 1",
             *             "description": "this is a test",
             *             "uniqueTopicAddressEnforcementEnabled": true,
             *             "topicDomainEnforcementEnabled": false,
             *             "type": "applicationDomain"
             *         },
             */

            EventPortalDomain question = new EventPortalDomain();
//            JSONArray tags = (JSONArray) eachItem.get("tags");
//            question.setTags(tags.stream().map(Object::toString).collect(Collectors.joining(", ")));
            question.setTitle(eachItem.get("name").toString());
            question.setTags(eachItem.get("id").toString());
            question.setLink("https://solace-sso.solace.cloud/ep/designer/domains/" + eachItem.get("id"));
            question.setAnswerCount(3);
            String creationDateString = eachItem.get("createdTime").toString();
            question.setCreationDate(creationDateString);//Date.from(Instant.ofEpochSecond(creationDate)));
            String lastActivityDateString = eachItem.get("updatedTime").toString();
            question.setLastActivity(lastActivityDateString);
            questions.add(question);
        }
        return questions;
    }
}
