package com.example.eyo.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Mail implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String from;
    private List<String> to;
    private List<String> cc;
    private String subject;
    private String body;
    private Date date;
    private boolean read;
    private List<String> attachments;
    private List<String> labels;

    public Mail() {
    }

    public Mail(int id, String from, List<String> to, List<String> cc, String subject, 
                String body, Date date, boolean read, List<String> attachments, List<String> labels) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.read = read;
        this.attachments = attachments;
        this.labels = labels;
    }

    // Getters
    public int getId() { return id; }
    public String getFrom() { return from; }
    public List<String> getTo() { return to; }
    public List<String> getCc() { return cc; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Date getDate() { return date; }
    public boolean isRead() { return read; }
    public List<String> getAttachments() { return attachments; }
    public List<String> getLabels() { return labels; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFrom(String from) { this.from = from; }
    public void setTo(List<String> to) { this.to = to; }
    public void setCc(List<String> cc) { this.cc = cc; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }
    public void setDate(Date date) { this.date = date; }
    public void setRead(boolean read) { this.read = read; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    // Helper methods
    public String getPreviewText() {
        if (body == null || body.isEmpty()) {
            return "";
        }
        
        // Replace all whitespace characters (including newlines) with single spaces
        String cleanedBody = body.replaceAll("\\s+", " ").trim();
        
        // Return the cleaned text (maxLines and ellipsize in layout will handle truncation)
        return cleanedBody;
    }

    public String getFormattedDate() {
        if (date != null) {
            return android.text.format.DateFormat.format("MMM dd, yyyy", date).toString();
        }
        return "";
    }

    public String getFormattedTime() {
        if (date != null) {
            return android.text.format.DateFormat.format("h:mm a", date).toString();
        }
        return "";
    }
    
    // Helper method to get clean subject (remove extra whitespace)
    public String getCleanSubject() {
        if (subject == null || subject.isEmpty()) {
            return "";
        }
        return subject.replaceAll("\\s+", " ").trim();
    }
    
    // Helper method to get clean sender name (remove extra whitespace)
    public String getCleanFrom() {
        if (from == null || from.isEmpty()) {
            return "";
        }
        return from.replaceAll("\\s+", " ").trim();
    }

    public boolean isStarred() {
        return labels != null && labels.contains("Starred");
    }

    public boolean isInInbox() {
        return labels != null && labels.contains("Inbox");
    }

    public boolean isInSent() {
        return labels != null && labels.contains("Sent");
    }

    public boolean isDraft() {
        return labels != null && labels.contains("Drafts");
    }

    public boolean isSpam() {
        return labels != null && labels.contains("Spam");
    }
} 