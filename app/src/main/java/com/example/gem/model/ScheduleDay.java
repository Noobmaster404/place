package com.example.gem.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDay {
    private String dayName; // Пн, Вт, Ср, Чт, Пт, Сб, Вс
    private int dayNumber;  // 19, 20, 21...
    private List<ScheduleEvent> events;

    public ScheduleDay(String dayName, int dayNumber) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.events = new ArrayList<>();
    }

    public ScheduleDay(String dayName, int dayNumber, List<ScheduleEvent> events) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.events = events != null ? events : new ArrayList<>();
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("dayName", dayName);
        obj.put("dayNumber", dayNumber);
        JSONArray evArr = new JSONArray();
        for (ScheduleEvent ev : events) {
            evArr.put(ev.toJsonObject());
        }
        obj.put("events", evArr);
        return obj;
    }

    public static ScheduleDay fromJsonObject(JSONObject obj) {
        String dayName = obj.optString("dayName", "Пн");
        int dayNumber = obj.optInt("dayNumber", 1);
        List<ScheduleEvent> events = new ArrayList<>();
        JSONArray evArr = obj.optJSONArray("events");
        if (evArr != null) {
            for (int i = 0; i < evArr.length(); i++) {
                JSONObject evObj = evArr.optJSONObject(i);
                if (evObj != null) {
                    events.add(ScheduleEvent.fromJsonObject(evObj));
                }
            }
        }
        return new ScheduleDay(dayName, dayNumber, events);
    }

    public String getDayName() { return dayName; }
    public int getDayNumber() { return dayNumber; }
    public List<ScheduleEvent> getEvents() { return events; }
    public boolean hasEvents() { return events != null && !events.isEmpty(); }
}
