package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import com.example.eyo.data.Mail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchRequest implements ApiRequest<List<Mail>> {
    private String searchQuery;
    private String authToken;

    public SearchRequest(String searchQuery, String authToken) {
        this.searchQuery = searchQuery;
        this.authToken = authToken;
    }

    @Override
    public String getEndpoint() {
        // URL encode the search query
        String encodedQuery = searchQuery.replace(" ", "%20");
        return "/search/" + encodedQuery;
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public String getRequestBody() {
        // GET request, no body needed
        return "";
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            headers.put("Authorization", authToken);
        }
        return headers;
    }

    @Override
    public List<Mail> parseResponse(int responseCode, String responseBody) {
        try {
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Mail> mails = new ArrayList<>();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mailJson = jsonArray.getJSONObject(i);
                
                Mail mail = new Mail();
                mail.setId(mailJson.getInt("id"));
                mail.setFrom(mailJson.getString("from"));
                mail.setSubject(mailJson.optString("subject", ""));
                mail.setBody(mailJson.optString("body", ""));
                mail.setRead(mailJson.optBoolean("read", false));
                
                // Parse date
                String dateString = mailJson.optString("date", "");
                if (!dateString.isEmpty()) {
                    try {
                        Date date = dateFormat.parse(dateString);
                        mail.setDate(date);
                    } catch (ParseException e) {
                        // If parsing fails, try alternative format
                        try {
                            SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date date = altFormat.parse(dateString);
                            mail.setDate(date);
                        } catch (ParseException ex) {
                            mail.setDate(new Date()); // Default to current date
                        }
                    }
                }
                
                // Parse 'to' array
                JSONArray toArray = mailJson.optJSONArray("to");
                List<String> toList = new ArrayList<>();
                if (toArray != null) {
                    for (int j = 0; j < toArray.length(); j++) {
                        toList.add(toArray.getString(j));
                    }
                }
                mail.setTo(toList);
                
                // Parse 'cc' array
                JSONArray ccArray = mailJson.optJSONArray("cc");
                List<String> ccList = new ArrayList<>();
                if (ccArray != null) {
                    for (int j = 0; j < ccArray.length(); j++) {
                        ccList.add(ccArray.getString(j));
                    }
                }
                mail.setCc(ccList);
                
                // Parse 'labels' array
                JSONArray labelsArray = mailJson.optJSONArray("labels");
                List<String> labelsList = new ArrayList<>();
                if (labelsArray != null) {
                    for (int j = 0; j < labelsArray.length(); j++) {
                        // Handle both integer and string labels from server
                        String label = labelsArray.get(j).toString();
                        labelsList.add(label);
                    }
                }
                mail.setLabels(labelsList);
                
                // Parse 'attachments' array
                JSONArray attachmentsArray = mailJson.optJSONArray("attachments");
                List<String> attachmentsList = new ArrayList<>();
                if (attachmentsArray != null) {
                    for (int j = 0; j < attachmentsArray.length(); j++) {
                        attachmentsList.add(attachmentsArray.getString(j));
                    }
                }
                mail.setAttachments(attachmentsList);
                
                mails.add(mail);
            }
            
            return mails;
            
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse search response: " + e.getMessage());
        }
    }

    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Search failed with code: " + responseCode);
        } catch (JSONException e) {
            return "Search failed with code: " + responseCode;
        }
    }
} 