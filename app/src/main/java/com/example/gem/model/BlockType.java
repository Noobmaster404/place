package com.example.gem.model;

public enum BlockType {
    WEEK_SCHEDULE,
    MONTH_CALENDAR,
    TODO_LIST,
    NOTE,
    METRICS,
    FOOD_DIARY;

    public String getDisplayName() {
        switch (this) {
            case WEEK_SCHEDULE: return "Расписание";
            case MONTH_CALENDAR: return "Календарь";
            case TODO_LIST: return "Список дел";
            case NOTE: return "Заметки";
            case METRICS: return "Показатели";
            case FOOD_DIARY: return "Питание";
            default: return name();
        }
    }

    public String getSubtext() {
        switch (this) {
            case WEEK_SCHEDULE: return "Занятия, встречи и важные даты";
            case MONTH_CALENDAR: return "Планы на месяц и события";
            case TODO_LIST: return "Небольшие шаги к большой цели";
            case NOTE: return "Мысли, материалы и полезные ссылки";
            case METRICS: return "Привычки, бюджет и личный прогресс";
            case FOOD_DIARY: return "Дневник питания и калории";
            default: return "";
        }
    }
}
