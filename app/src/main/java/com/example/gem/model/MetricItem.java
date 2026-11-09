package com.example.gem.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MetricItem {
    private String id;
    private String title;
    private String value;
    private String unit;
    private String goal;
    private String subtitle;
    private int progressPercent;

    public MetricItem(String title, String value, String unit, String goal, String subtitle, int progressPercent) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.value = value;
        this.unit = unit != null ? unit : "";
        this.goal = goal != null ? goal : "";
        this.subtitle = subtitle != null ? subtitle : "";
        this.progressPercent = progressPercent;
    }

    public MetricItem(String id, String title, String value, String unit, String goal, String subtitle, int progressPercent) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.title = title;
        this.value = value;
        this.unit = unit != null ? unit : "";
        this.goal = goal != null ? goal : "";
        this.subtitle = subtitle != null ? subtitle : "";
        this.progressPercent = progressPercent;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("value", value);
        obj.put("unit", unit);
        obj.put("goal", goal);
        obj.put("subtitle", subtitle);
        obj.put("progressPercent", progressPercent);
        return obj;
    }

    public static MetricItem fromJsonObject(JSONObject obj) {
        String id = obj.optString("id", UUID.randomUUID().toString());
        String title = obj.optString("title", "");
        String value = obj.optString("value", "");
        String unit = obj.optString("unit", "");
        String goal = obj.optString("goal", "");
        String subtitle = obj.optString("subtitle", "");
        int progressPercent = obj.optInt("progressPercent", 0);
        return new MetricItem(id, title, value, unit, goal, subtitle, progressPercent);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
