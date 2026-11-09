package com.example.gem.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DashboardBlock {
    private String id;
    private BlockType type;
    private String title;
    private String subtitle;

    // Week schedule data
    private int selectedDayIndex = 0;
    private List<ScheduleDay> scheduleDays = new ArrayList<>();

    // Month calendar data
    private String monthYear = "Октябрь 2026";
    private int selectedDateNumber = 24;

    // Todo data
    private List<TodoItem> todoItems = new ArrayList<>();

    // Note data
    private String noteContent = "";
    private String noteTag = "";

    // Metrics data
    private List<MetricItem> metricItems = new ArrayList<>();

    // Food diary data
    private List<MealItem> mealItems = new ArrayList<>();

    public DashboardBlock(BlockType type, String title, String subtitle) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.title = title != null ? title : type.getDisplayName();
        this.subtitle = subtitle != null ? subtitle : "";
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("type", type.name());
        obj.put("title", title);
        obj.put("subtitle", subtitle);

        // Schedule
        obj.put("selectedDayIndex", selectedDayIndex);
        JSONArray daysArr = new JSONArray();
        for (ScheduleDay day : scheduleDays) {
            daysArr.put(day.toJsonObject());
        }
        obj.put("scheduleDays", daysArr);

        // Calendar
        obj.put("monthYear", monthYear);
        obj.put("selectedDateNumber", selectedDateNumber);

        // Todo
        JSONArray todoArr = new JSONArray();
        for (TodoItem item : todoItems) {
            todoArr.put(item.toJsonObject());
        }
        obj.put("todoItems", todoArr);

        // Note
        obj.put("noteContent", noteContent);
        obj.put("noteTag", noteTag);

        // Metrics
        JSONArray metricsArr = new JSONArray();
        for (MetricItem m : metricItems) {
            metricsArr.put(m.toJsonObject());
        }
        obj.put("metricItems", metricsArr);

        // Meals
        JSONArray mealsArr = new JSONArray();
        for (MealItem meal : mealItems) {
            mealsArr.put(meal.toJsonObject());
        }
        obj.put("mealItems", mealsArr);

        return obj;
    }

    public static DashboardBlock fromJsonObject(JSONObject obj) {
        String typeStr = obj.optString("type", BlockType.NOTE.name());
        BlockType type;
        try {
            type = BlockType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            type = BlockType.NOTE;
        }

        String title = obj.optString("title", type.getDisplayName());
        String subtitle = obj.optString("subtitle", "");
        DashboardBlock block = new DashboardBlock(type, title, subtitle);
        block.id = obj.optString("id", UUID.randomUUID().toString());
        block.selectedDayIndex = obj.optInt("selectedDayIndex", 0);
        block.monthYear = obj.optString("monthYear", "Октябрь 2026");
        block.selectedDateNumber = obj.optInt("selectedDateNumber", 24);
        block.noteContent = obj.optString("noteContent", "");
        block.noteTag = obj.optString("noteTag", "");

        JSONArray daysArr = obj.optJSONArray("scheduleDays");
        if (daysArr != null) {
            for (int i = 0; i < daysArr.length(); i++) {
                JSONObject dObj = daysArr.optJSONObject(i);
                if (dObj != null) {
                    block.scheduleDays.add(ScheduleDay.fromJsonObject(dObj));
                }
            }
        }

        JSONArray todoArr = obj.optJSONArray("todoItems");
        if (todoArr != null) {
            for (int i = 0; i < todoArr.length(); i++) {
                JSONObject tObj = todoArr.optJSONObject(i);
                if (tObj != null) {
                    block.todoItems.add(TodoItem.fromJsonObject(tObj));
                }
            }
        }

        JSONArray metricsArr = obj.optJSONArray("metricItems");
        if (metricsArr != null) {
            for (int i = 0; i < metricsArr.length(); i++) {
                JSONObject mObj = metricsArr.optJSONObject(i);
                if (mObj != null) {
                    block.metricItems.add(MetricItem.fromJsonObject(mObj));
                }
            }
        }

        JSONArray mealsArr = obj.optJSONArray("mealItems");
        if (mealsArr != null) {
            for (int i = 0; i < mealsArr.length(); i++) {
                JSONObject mealObj = mealsArr.optJSONObject(i);
                if (mealObj != null) {
                    block.mealItems.add(MealItem.fromJsonObject(mealObj));
                }
            }
        }

        return block;
    }

    public static DashboardBlock createDefaultBlock(BlockType type) {
        DashboardBlock block = new DashboardBlock(type, type.getDisplayName(), type.getSubtext());
        switch (type) {
            case WEEK_SCHEDULE:
                String[] dayNames = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
                int startNumber = 19;
                for (int i = 0; i < 7; i++) {
                    ScheduleDay day = new ScheduleDay(dayNames[i], startNumber + i);
                    block.scheduleDays.add(day);
                }
                block.selectedDayIndex = 0;
                block.subtitle = "Расписание недели";
                break;

            case MONTH_CALENDAR:
                block.monthYear = "Октябрь 2026";
                block.selectedDateNumber = 24;
                break;

            case TODO_LIST:
                block.title = "Список дел";
                block.subtitle = "Нет задач";
                break;

            case NOTE:
                block.title = "Заметка";
                block.noteContent = "";
                block.noteTag = "Заметка";
                break;

            case METRICS:
                block.title = "Показатели";
                break;

            case FOOD_DIARY:
                block.title = "Питание";
                block.subtitle = "Дневник питания";
                break;
        }
        return block;
    }

    public void updateTodoProgress() {
        if (todoItems.isEmpty()) {
            subtitle = "Нет задач";
            return;
        }
        int done = 0;
        for (TodoItem item : todoItems) {
            if (item.isCompleted()) done++;
        }
        subtitle = done + " из " + todoItems.size() + " готово";
    }

    public String getId() { return id; }
    public BlockType getType() { return type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public int getSelectedDayIndex() { return selectedDayIndex; }
    public void setSelectedDayIndex(int selectedDayIndex) { this.selectedDayIndex = selectedDayIndex; }
    public List<ScheduleDay> getScheduleDays() { return scheduleDays; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    public int getSelectedDateNumber() { return selectedDateNumber; }
    public void setSelectedDateNumber(int selectedDateNumber) { this.selectedDateNumber = selectedDateNumber; }

    public List<TodoItem> getTodoItems() { return todoItems; }
    public String getNoteContent() { return noteContent; }
    public void setNoteContent(String noteContent) { this.noteContent = noteContent; }
    public String getNoteTag() { return noteTag; }
    public void setNoteTag(String noteTag) { this.noteTag = noteTag; }

    public List<MetricItem> getMetricItems() { return metricItems; }
    public List<MealItem> getMealItems() { return mealItems; }
}
