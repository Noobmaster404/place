package com.example.gem.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class TodoItem {
    private String id;
    private String title;
    private String details;
    private boolean completed;
    private String time;
    private String priority; // LOW, MEDIUM, HIGH
    private String date; // YYYY-MM-DD
    private String dashboardId;

    public TodoItem(String title, String details, String time, String priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.details = details != null ? details : "";
        this.completed = false;
        this.time = time != null ? time : "";
        this.priority = priority != null ? priority : "MEDIUM";
        this.date = "";
        this.dashboardId = "";
    }

    public TodoItem(String id, String title, String details, boolean completed, String time, String priority, String date, String dashboardId) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.title = title;
        this.details = details != null ? details : "";
        this.completed = completed;
        this.time = time != null ? time : "";
        this.priority = priority != null ? priority : "MEDIUM";
        this.date = date != null ? date : "";
        this.dashboardId = dashboardId != null ? dashboardId : "";
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("details", details);
        obj.put("completed", completed);
        obj.put("time", time);
        obj.put("priority", priority);
        obj.put("date", date);
        obj.put("dashboardId", dashboardId);
        return obj;
    }

    public static TodoItem fromJsonObject(JSONObject obj) {
        String id = obj.optString("id", UUID.randomUUID().toString());
        String title = obj.optString("title", "");
        String details = obj.optString("details", "");
        boolean completed = obj.optBoolean("completed", false);
        String time = obj.optString("time", "");
        String priority = obj.optString("priority", "MEDIUM");
        String date = obj.optString("date", "");
        String dashboardId = obj.optString("dashboardId", "");
        return new TodoItem(id, title, details, completed, time, priority, date, dashboardId);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDashboardId() { return dashboardId; }
    public void setDashboardId(String dashboardId) { this.dashboardId = dashboardId; }
}
