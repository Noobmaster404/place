package com.example.gem.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class ScheduleEvent {
    private String id;
    private String time;
    private String title;
    private String duration;
    private boolean completed;

    public ScheduleEvent(String time, String title, String duration) {
        this.id = UUID.randomUUID().toString();
        this.time = time != null ? time : "";
        this.title = title != null ? title : "";
        this.duration = duration != null ? duration : "";
        this.completed = false;
    }

    public ScheduleEvent(String id, String time, String title, String duration, boolean completed) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.time = time != null ? time : "";
        this.title = title != null ? title : "";
        this.duration = duration != null ? duration : "";
        this.completed = completed;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("time", time);
        obj.put("title", title);
        obj.put("duration", duration);
        obj.put("completed", completed);
        return obj;
    }

    public static ScheduleEvent fromJsonObject(JSONObject obj) {
        String id = obj.optString("id", UUID.randomUUID().toString());
        String time = obj.optString("time", "");
        String title = obj.optString("title", "");
        String duration = obj.optString("duration", "");
        boolean completed = obj.optBoolean("completed", false);
        return new ScheduleEvent(id, time, title, duration, completed);
    }

    public String getId() { return id; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
