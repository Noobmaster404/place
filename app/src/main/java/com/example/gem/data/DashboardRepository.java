package com.example.gem.data;

import android.content.Context;
import android.util.Log;

import com.example.gem.model.BlockType;
import com.example.gem.model.Dashboard;
import com.example.gem.model.DashboardBlock;
import com.example.gem.model.TodoItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DashboardRepository {
    private static final String TAG = "DashboardRepository";
    private static final String FILE_NAME = "dashboards_data.json";
    private static final int CURRENT_VERSION = 2; // Incremented to wipe out initial dummy seed data

    private static DashboardRepository instance;
    private final Context context;
    private final List<Dashboard> dashboards = new ArrayList<>();
    private final List<TodoItem> todayTasks = new ArrayList<>();

    private DashboardRepository(Context context) {
        this.context = context.getApplicationContext();
        loadData();
    }

    public static synchronized DashboardRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardRepository(context);
        }
        return instance;
    }

    private synchronized void loadData() {
        dashboards.clear();
        todayTasks.clear();

        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            saveData();
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject root = new JSONObject(sb.toString());
            int version = root.optInt("version", 1);

            // If version < CURRENT_VERSION, wipe old mock/sample data
            if (version < CURRENT_VERSION) {
                dashboards.clear();
                todayTasks.clear();
                saveData();
                return;
            }

            JSONArray dArr = root.optJSONArray("dashboards");
            if (dArr != null) {
                for (int i = 0; i < dArr.length(); i++) {
                    JSONObject dObj = dArr.optJSONObject(i);
                    if (dObj != null) {
                        dashboards.add(Dashboard.fromJsonObject(dObj));
                    }
                }
            }

            JSONArray tArr = root.optJSONArray("todayTasks");
            if (tArr != null) {
                for (int i = 0; i < tArr.length(); i++) {
                    JSONObject tObj = tArr.optJSONObject(i);
                    if (tObj != null) {
                        todayTasks.add(TodoItem.fromJsonObject(tObj));
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading data, initializing empty state", e);
            dashboards.clear();
            todayTasks.clear();
            saveData();
        }
    }

    public synchronized void saveData() {
        try {
            JSONObject root = new JSONObject();
            root.put("version", CURRENT_VERSION);

            JSONArray dArr = new JSONArray();
            for (Dashboard d : dashboards) {
                dArr.put(d.toJsonObject());
            }
            root.put("dashboards", dArr);

            JSONArray tArr = new JSONArray();
            for (TodoItem t : todayTasks) {
                tArr.put(t.toJsonObject());
            }
            root.put("todayTasks", tArr);

            File file = new File(context.getFilesDir(), FILE_NAME);
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                writer.write(root.toString(2));
                writer.flush();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving data", e);
        }
    }

    public synchronized List<Dashboard> getDashboards() {
        return new ArrayList<>(dashboards);
    }

    public synchronized Dashboard getDashboard(String id) {
        for (Dashboard d : dashboards) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    public synchronized void addDashboard(Dashboard dashboard) {
        dashboards.add(0, dashboard);
        saveData();
    }

    public synchronized void deleteDashboard(String dashboardId) {
        for (int i = 0; i < dashboards.size(); i++) {
            if (dashboards.get(i).getId().equals(dashboardId)) {
                dashboards.remove(i);
                break;
            }
        }
        saveData();
    }

    public synchronized void addBlockToDashboard(String dashboardId, DashboardBlock block) {
        Dashboard d = getDashboard(dashboardId);
        if (d != null) {
            d.getBlocks().add(block);
            saveData();
        }
    }

    public synchronized void removeBlockFromDashboard(String dashboardId, String blockId) {
        Dashboard d = getDashboard(dashboardId);
        if (d != null) {
            for (int i = 0; i < d.getBlocks().size(); i++) {
                if (d.getBlocks().get(i).getId().equals(blockId)) {
                    d.getBlocks().remove(i);
                    break;
                }
            }
            saveData();
        }
    }

    public synchronized void toggleTodo(String dashboardId, String blockId, String todoId) {
        Dashboard d = getDashboard(dashboardId);
        if (d != null) {
            for (DashboardBlock b : d.getBlocks()) {
                if (b.getId().equals(blockId)) {
                    for (TodoItem item : b.getTodoItems()) {
                        if (item.getId().equals(todoId)) {
                            item.setCompleted(!item.isCompleted());
                            b.updateTodoProgress();
                            saveData();
                            return;
                        }
                    }
                }
            }
        }
    }

    public synchronized List<TodoItem> getTodayTasks() {
        return new ArrayList<>(todayTasks);
    }

    public synchronized void addTodayTask(TodoItem item) {
        todayTasks.add(item);
        saveData();
    }

    public synchronized void toggleTodayTask(String taskId) {
        for (TodoItem t : todayTasks) {
            if (t.getId().equals(taskId)) {
                t.setCompleted(!t.isCompleted());
                saveData();
                return;
            }
        }
    }

    public synchronized void deleteTodayTask(String taskId) {
        for (int i = 0; i < todayTasks.size(); i++) {
            if (todayTasks.get(i).getId().equals(taskId)) {
                todayTasks.remove(i);
                break;
            }
        }
        saveData();
    }

    public synchronized List<DashboardBlock> getAllNotes() {
        List<DashboardBlock> notes = new ArrayList<>();
        for (Dashboard d : dashboards) {
            for (DashboardBlock b : d.getBlocks()) {
                if (b.getType() == BlockType.NOTE) {
                    notes.add(b);
                }
            }
        }
        return notes;
    }
}
