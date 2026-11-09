package com.example.gem.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gem.R;
import com.example.gem.data.DashboardRepository;
import com.example.gem.model.BlockType;
import com.example.gem.model.Dashboard;
import com.example.gem.model.DashboardBlock;
import com.example.gem.model.MealItem;
import com.example.gem.model.MetricItem;
import com.example.gem.model.ScheduleDay;
import com.example.gem.model.ScheduleEvent;
import com.example.gem.model.TodoItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class DashboardDetailActivity extends AppCompatActivity {

    private String dashboardId;
    private Dashboard dashboard;
    private DashboardRepository repository;

    private View layoutHeroHeader;
    private TextView tvDetailTitle;
    private TextView tvDetailQuote;
    private TextView tvDetailBadge;
    private LinearLayout containerBlocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_detail);

        repository = DashboardRepository.getInstance(this);
        dashboardId = getIntent().getStringExtra("dashboard_id");

        initViews();
        loadDashboardData();
    }

    private void initViews() {
        layoutHeroHeader = findViewById(R.id.layout_hero_header);
        tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailQuote = findViewById(R.id.tv_detail_quote);
        tvDetailBadge = findViewById(R.id.tv_detail_badge);
        containerBlocks = findViewById(R.id.container_blocks);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_delete_dashboard).setOnClickListener(v -> confirmDeleteDashboard());

        findViewById(R.id.btn_add_block).setOnClickListener(v -> openAddBlockDialog());
    }

    private void loadDashboardData() {
        dashboard = repository.getDashboard(dashboardId);
        if (dashboard == null) {
            Toast.makeText(this, "Дашборд не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(dashboard.getTitle());
        tvDetailQuote.setText(dashboard.getQuote().isEmpty() ? "Ваше личное пространство" : dashboard.getQuote());
        tvDetailBadge.setText(dashboard.getBlocksCountFormatted() + " · только для меня");

        try {
            int color = Color.parseColor(dashboard.getCoverColor());
            layoutHeroHeader.setBackgroundColor(color);
        } catch (Exception e) {
            layoutHeroHeader.setBackgroundColor(Color.parseColor("#E3E9DB"));
        }

        renderBlocks();
    }

    private void renderBlocks() {
        containerBlocks.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (DashboardBlock block : dashboard.getBlocks()) {
            switch (block.getType()) {
                case WEEK_SCHEDULE:
                    renderWeekScheduleBlock(inflater, block);
                    break;
                case TODO_LIST:
                    renderTodoListBlock(inflater, block);
                    break;
                case METRICS:
                    renderMetricsBlock(inflater, block);
                    break;
                case NOTE:
                    renderNoteBlock(inflater, block);
                    break;
                case FOOD_DIARY:
                    renderFoodDiaryBlock(inflater, block);
                    break;
                case MONTH_CALENDAR:
                    renderMonthCalendarBlock(inflater, block);
                    break;
            }
        }
    }

    private void renderWeekScheduleBlock(LayoutInflater inflater, DashboardBlock block) {
        View blockView = inflater.inflate(R.layout.block_week_schedule, containerBlocks, false);

        TextView tvTitle = blockView.findViewById(R.id.tv_block_title);
        TextView tvSubtitle = blockView.findViewById(R.id.tv_block_subtitle);
        tvTitle.setText(block.getTitle());
        tvSubtitle.setText(block.getSubtitle());

        blockView.findViewById(R.id.btn_delete_block).setOnClickListener(v -> confirmDeleteBlock(block));

        LinearLayout layoutDays = blockView.findViewById(R.id.layout_week_days);
        LinearLayout layoutEvents = blockView.findViewById(R.id.layout_schedule_events);
        layoutDays.removeAllViews();

        int selectedIdx = block.getSelectedDayIndex();
        if (selectedIdx < 0 || selectedIdx >= block.getScheduleDays().size()) {
            selectedIdx = 0;
            block.setSelectedDayIndex(0);
        }

        for (int i = 0; i < block.getScheduleDays().size(); i++) {
            final int dayIndex = i;
            ScheduleDay day = block.getScheduleDays().get(i);
            View dayView = inflater.inflate(R.layout.item_schedule_day, layoutDays, false);

            TextView tvDayName = dayView.findViewById(R.id.tv_day_name);
            TextView tvDayNumber = dayView.findViewById(R.id.tv_day_number);
            View dot = dayView.findViewById(R.id.dot_indicator);
            View dayContainer = dayView.findViewById(R.id.day_container);

            tvDayName.setText(day.getDayName());
            tvDayNumber.setText(String.valueOf(day.getDayNumber()));
            dot.setVisibility(day.hasEvents() ? View.VISIBLE : View.INVISIBLE);

            if (i == selectedIdx) {
                dayContainer.setBackgroundResource(R.drawable.bg_day_selected);
                tvDayName.setTextColor(Color.WHITE);
                tvDayNumber.setTextColor(Color.WHITE);
                dot.setBackgroundTintList(getColorStateList(R.color.white));
            } else {
                dayContainer.setBackgroundResource(R.drawable.bg_day_normal);
                tvDayName.setTextColor(getColor(R.color.text_secondary));
                tvDayNumber.setTextColor(getColor(R.color.text_primary));
                dot.setBackgroundTintList(getColorStateList(R.color.brand_terracotta));
            }

            dayView.setOnClickListener(v -> {
                block.setSelectedDayIndex(dayIndex);
                repository.saveData();
                renderBlocks();
            });

            layoutDays.addView(dayView);
        }

        // Render events for the active day
        layoutEvents.removeAllViews();
        if (selectedIdx < block.getScheduleDays().size()) {
            ScheduleDay activeDay = block.getScheduleDays().get(selectedIdx);
            if (activeDay.getEvents().isEmpty()) {
                TextView tvEmpty = new TextView(this);
                tvEmpty.setText("На этот день ничего не запланировано");
                tvEmpty.setTextColor(getColor(R.color.text_secondary));
                tvEmpty.setTextSize(13);
                tvEmpty.setPadding(0, 16, 0, 16);
                layoutEvents.addView(tvEmpty);
            } else {
                for (ScheduleEvent ev : activeDay.getEvents()) {
                    View evView = inflater.inflate(R.layout.item_schedule_event, layoutEvents, false);
                    TextView tvTime = evView.findViewById(R.id.tv_event_time);
                    TextView tvEvTitle = evView.findViewById(R.id.tv_event_title);
                    TextView tvEvSub = evView.findViewById(R.id.tv_event_subtitle);

                    tvTime.setText(ev.getTime());
                    tvEvTitle.setText(ev.getTitle());
                    tvEvSub.setText(ev.getDuration());

                    evView.findViewById(R.id.btn_delete_event).setOnClickListener(v -> {
                        activeDay.getEvents().remove(ev);
                        repository.saveData();
                        renderBlocks();
                    });

                    layoutEvents.addView(evView);
                }
            }

            blockView.findViewById(R.id.btn_add_event).setOnClickListener(v -> showAddEventDialog(activeDay));
        }

        containerBlocks.addView(blockView);
    }

    private void renderTodoListBlock(LayoutInflater inflater, DashboardBlock block) {
        View blockView = inflater.inflate(R.layout.block_todo_list, containerBlocks, false);

        TextView tvTitle = blockView.findViewById(R.id.tv_todo_title);
        TextView tvProgress = blockView.findViewById(R.id.tv_todo_progress);
        tvTitle.setText(block.getTitle());
        block.updateTodoProgress();
        tvProgress.setText(block.getSubtitle());

        blockView.findViewById(R.id.btn_delete_todo_block).setOnClickListener(v -> confirmDeleteBlock(block));

        LinearLayout layoutItems = blockView.findViewById(R.id.layout_todo_items);
        layoutItems.removeAllViews();

        if (block.getTodoItems().isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Пока нет задач. Нажмите «+ Добавить задачу» ниже.");
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            tvEmpty.setTextSize(13);
            tvEmpty.setPadding(0, 12, 0, 12);
            layoutItems.addView(tvEmpty);
        } else {
            for (TodoItem item : block.getTodoItems()) {
                View itemView = inflater.inflate(R.layout.item_todo, layoutItems, false);
                ImageView btnCheck = itemView.findViewById(R.id.btn_toggle_todo);
                TextView tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
                TextView tvTaskDetails = itemView.findViewById(R.id.tv_task_details);

                tvTaskTitle.setText(item.getTitle());
                tvTaskDetails.setText(item.getDetails().isEmpty() ? (item.getTime().isEmpty() ? "Задача" : item.getTime()) : item.getDetails());

                if (item.isCompleted()) {
                    btnCheck.setImageResource(R.drawable.bg_checkbox_checked);
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTaskTitle.setTextColor(getColor(R.color.text_tertiary));
                } else {
                    btnCheck.setImageResource(R.drawable.bg_checkbox_unchecked);
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    tvTaskTitle.setTextColor(getColor(R.color.text_primary));
                }

                btnCheck.setOnClickListener(v -> {
                    item.setCompleted(!item.isCompleted());
                    block.updateTodoProgress();
                    repository.saveData();
                    renderBlocks();
                });

                itemView.findViewById(R.id.btn_delete_todo).setOnClickListener(v -> {
                    block.getTodoItems().remove(item);
                    block.updateTodoProgress();
                    repository.saveData();
                    renderBlocks();
                });

                layoutItems.addView(itemView);
            }
        }

        blockView.findViewById(R.id.btn_add_todo_item).setOnClickListener(v -> {
            AddTaskDialog dialog = AddTaskDialog.newInstance("Сегодня", task -> {
                block.getTodoItems().add(task);
                block.updateTodoProgress();
                repository.saveData();
                renderBlocks();
            });
            dialog.show(getSupportFragmentManager(), "add_todo_item");
        });

        containerBlocks.addView(blockView);
    }

    private void renderMetricsBlock(LayoutInflater inflater, DashboardBlock block) {
        View blockView = inflater.inflate(R.layout.block_metrics, containerBlocks, false);

        TextView tvTitle = blockView.findViewById(R.id.tv_metrics_title);
        tvTitle.setText(block.getTitle());

        blockView.findViewById(R.id.btn_delete_metrics_block).setOnClickListener(v -> confirmDeleteBlock(block));

        LinearLayout layoutItems = blockView.findViewById(R.id.layout_metric_items);
        layoutItems.removeAllViews();

        if (block.getMetricItems().isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Пока нет показателей. Нажмите «+ Добавить показатель» ниже.");
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            tvEmpty.setTextSize(13);
            tvEmpty.setPadding(0, 12, 0, 12);
            layoutItems.addView(tvEmpty);
        } else {
            for (MetricItem m : block.getMetricItems()) {
                View itemView = inflater.inflate(R.layout.item_metric, layoutItems, false);
                TextView tvMetricTitle = itemView.findViewById(R.id.tv_metric_title);
                TextView tvMetricValue = itemView.findViewById(R.id.tv_metric_value);
                TextView tvMetricSubtitle = itemView.findViewById(R.id.tv_metric_subtitle);
                LinearProgressIndicator progressIndicator = itemView.findViewById(R.id.progress_metric);

                tvMetricTitle.setText(m.getTitle());
                tvMetricValue.setText(m.getValue() + (m.getUnit().isEmpty() ? "" : " " + m.getUnit()));
                tvMetricSubtitle.setText(m.getGoal().isEmpty() ? m.getSubtitle() : (m.getGoal() + (m.getSubtitle().isEmpty() ? "" : " · " + m.getSubtitle())));
                progressIndicator.setProgress(Math.min(100, Math.max(0, m.getProgressPercent())));

                itemView.setOnClickListener(v -> showEditMetricDialog(m, block));

                layoutItems.addView(itemView);
            }
        }

        blockView.findViewById(R.id.btn_add_metric).setOnClickListener(v -> showAddMetricDialog(block));

        containerBlocks.addView(blockView);
    }

    private void renderNoteBlock(LayoutInflater inflater, DashboardBlock block) {
        View blockView = inflater.inflate(R.layout.block_note, containerBlocks, false);

        TextView tvTitle = blockView.findViewById(R.id.tv_note_title);
        TextView tvContent = blockView.findViewById(R.id.tv_note_content);
        TextView tvTag = blockView.findViewById(R.id.tv_note_tag);

        tvTitle.setText(block.getTitle());
        tvContent.setText(block.getNoteContent().isEmpty() ? "Нажмите для добавления текста..." : block.getNoteContent());
        tvTag.setText(block.getNoteTag().isEmpty() ? "Заметка ↗" : block.getNoteTag());

        blockView.findViewById(R.id.btn_delete_note_block).setOnClickListener(v -> confirmDeleteBlock(block));

        View.OnClickListener editListener = v -> showEditNoteDialog(block);
        blockView.findViewById(R.id.btn_edit_note).setOnClickListener(editListener);
        blockView.setOnClickListener(editListener);

        containerBlocks.addView(blockView);
    }

    private void renderFoodDiaryBlock(LayoutInflater inflater, DashboardBlock block) {
        View blockView = inflater.inflate(R.layout.block_food_diary, containerBlocks, false);

        TextView tvTitle = blockView.findViewById(R.id.tv_food_title);
        TextView tvSub = blockView.findViewById(R.id.tv_food_subtitle);
        tvTitle.setText(block.getTitle());
        tvSub.setText(block.getSubtitle());

        blockView.findViewById(R.id.btn_delete_food_block).setOnClickListener(v -> confirmDeleteBlock(block));

        LinearLayout layoutMeals = blockView.findViewById(R.id.layout_meals_container);
        layoutMeals.removeAllViews();

        if (block.getMealItems().isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Пока нет записей о питании. Нажмите «+ Добавить приём пищи» ниже.");
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            tvEmpty.setTextSize(13);
            tvEmpty.setPadding(0, 12, 0, 12);
            layoutMeals.addView(tvEmpty);
        } else {
            for (MealItem meal : block.getMealItems()) {
                View mealView = inflater.inflate(R.layout.item_meal, layoutMeals, false);
                TextView tvName = mealView.findViewById(R.id.tv_meal_name);
                TextView tvFoods = mealView.findViewById(R.id.tv_meal_foods);
                TextView tvCals = mealView.findViewById(R.id.tv_meal_calories);

                tvName.setText(meal.getMealName());
                tvFoods.setText(meal.getFoods());
                tvCals.setText(meal.getCalories() + " ккал");

                layoutMeals.addView(mealView);
            }
        }

        blockView.findViewById(R.id.btn_add_meal).setOnClickListener(v -> showAddMealDialog(block));

        containerBlocks.addView(blockView);
    }

    private void renderMonthCalendarBlock(LayoutInflater inflater, DashboardBlock block) {
        // Reuse month calendar or todo representation
        View blockView = inflater.inflate(R.layout.block_todo_list, containerBlocks, false);
        TextView tvTitle = blockView.findViewById(R.id.tv_todo_title);
        TextView tvProgress = blockView.findViewById(R.id.tv_todo_progress);

        tvTitle.setText("Календарь: " + block.getMonthYear());
        tvProgress.setText("Выбран день: " + block.getSelectedDateNumber());

        blockView.findViewById(R.id.btn_delete_todo_block).setOnClickListener(v -> confirmDeleteBlock(block));

        LinearLayout layoutItems = blockView.findViewById(R.id.layout_todo_items);
        layoutItems.removeAllViews();

        if (block.getTodoItems().isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("На этот день пока нет событий. Нажмите «+ Добавить задачу» ниже.");
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            tvEmpty.setTextSize(13);
            tvEmpty.setPadding(0, 12, 0, 12);
            layoutItems.addView(tvEmpty);
        } else {
            for (TodoItem item : block.getTodoItems()) {
                View itemView = inflater.inflate(R.layout.item_todo, layoutItems, false);
                ImageView btnCheck = itemView.findViewById(R.id.btn_toggle_todo);
                TextView tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
                TextView tvTaskDetails = itemView.findViewById(R.id.tv_task_details);

                tvTaskTitle.setText(item.getTitle());
                tvTaskDetails.setText(item.getTime() + (item.getDetails().isEmpty() ? "" : " · " + item.getDetails()));

                if (item.isCompleted()) {
                    btnCheck.setImageResource(R.drawable.bg_checkbox_checked);
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    btnCheck.setImageResource(R.drawable.bg_checkbox_unchecked);
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                btnCheck.setOnClickListener(v -> {
                    item.setCompleted(!item.isCompleted());
                    repository.saveData();
                    renderBlocks();
                });

                itemView.findViewById(R.id.btn_delete_todo).setOnClickListener(v -> {
                    block.getTodoItems().remove(item);
                    repository.saveData();
                    renderBlocks();
                });

                layoutItems.addView(itemView);
            }
        }

        blockView.findViewById(R.id.btn_add_todo_item).setOnClickListener(v -> {
            AddTaskDialog dialog = AddTaskDialog.newInstance("24 окт. 2026", task -> {
                block.getTodoItems().add(task);
                repository.saveData();
                renderBlocks();
            });
            dialog.show(getSupportFragmentManager(), "add_calendar_event");
        });

        containerBlocks.addView(blockView);
    }

    private void showAddEventDialog(ScheduleDay activeDay) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTime = new EditText(this);
        etTime.setHint("Время (например, 18:30)");
        layout.addView(etTime);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Название события");
        layout.addView(etTitle);

        final EditText etDuration = new EditText(this);
        etDuration.setHint("Длительность или место (60 мин · зал)");
        layout.addView(etDuration);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Новое событие")
                .setView(layout)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String time = etTime.getText().toString().trim();
                    String title = etTitle.getText().toString().trim();
                    String dur = etDuration.getText().toString().trim();
                    if (!TextUtils.isEmpty(title)) {
                        activeDay.getEvents().add(new ScheduleEvent(time.isEmpty() ? "12:00" : time, title, dur));
                        repository.saveData();
                        renderBlocks();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditNoteDialog(DashboardBlock block) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Заголовок");
        etTitle.setText(block.getTitle());
        layout.addView(etTitle);

        final EditText etContent = new EditText(this);
        etContent.setHint("Текст заметки");
        etContent.setText(block.getNoteContent());
        etContent.setMinLines(3);
        layout.addView(etContent);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Редактировать заметку")
                .setView(layout)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    block.setTitle(etTitle.getText().toString().trim());
                    block.setNoteContent(etContent.getText().toString().trim());
                    repository.saveData();
                    renderBlocks();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditMetricDialog(MetricItem metric, DashboardBlock block) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etVal = new EditText(this);
        etVal.setHint("Текущее значение (например, 1 500)");
        etVal.setText(metric.getValue());
        layout.addView(etVal);

        final EditText etPercent = new EditText(this);
        etPercent.setHint("Прогресс в % (0 - 100)");
        etPercent.setText(String.valueOf(metric.getProgressPercent()));
        layout.addView(etPercent);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Изменить показатель: " + metric.getTitle())
                .setView(layout)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    metric.setValue(etVal.getText().toString().trim());
                    try {
                        metric.setProgressPercent(Integer.parseInt(etPercent.getText().toString().trim()));
                    } catch (Exception ignored) {}
                    repository.saveData();
                    renderBlocks();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAddMetricDialog(DashboardBlock block) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Название показателя (например, Вода, Книги)");
        layout.addView(etTitle);

        final EditText etVal = new EditText(this);
        etVal.setHint("Значение (например, 1,5 л)");
        layout.addView(etVal);

        final EditText etGoal = new EditText(this);
        etGoal.setHint("Цель (например, цель 2 л)");
        layout.addView(etGoal);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Новый показатель")
                .setView(layout)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String val = etVal.getText().toString().trim();
                    String goal = etGoal.getText().toString().trim();
                    if (!TextUtils.isEmpty(title)) {
                        block.getMetricItems().add(new MetricItem(title, val.isEmpty() ? "0" : val, "", goal, "", 50));
                        repository.saveData();
                        renderBlocks();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAddMealDialog(DashboardBlock block) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etName = new EditText(this);
        etName.setHint("Приём пищи (например, Ужин)");
        layout.addView(etName);

        final EditText etFoods = new EditText(this);
        etFoods.setHint("Состав (Лосось, брокколи)");
        layout.addView(etFoods);

        final EditText etCals = new EditText(this);
        etCals.setHint("Калории (550)");
        layout.addView(etCals);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Добавить приём пищи")
                .setView(layout)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String foods = etFoods.getText().toString().trim();
                    int cals = 400;
                    try {
                        cals = Integer.parseInt(etCals.getText().toString().trim());
                    } catch (Exception ignored) {}
                    if (!TextUtils.isEmpty(name)) {
                        block.getMealItems().add(new MealItem(name, foods, cals));
                        repository.saveData();
                        renderBlocks();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void confirmDeleteBlock(DashboardBlock block) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Удалить блок?")
                .setMessage("Вы уверены, что хотите удалить блок «" + block.getTitle() + "»?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    repository.removeBlockFromDashboard(dashboard.getId(), block.getId());
                    loadDashboardData();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void confirmDeleteDashboard() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Удалить дашборд?")
                .setMessage("Вы уверены, что хотите удалить «" + dashboard.getTitle() + "»? Все данные будут удалены.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    repository.deleteDashboard(dashboard.getId());
                    finish();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void openAddBlockDialog() {
        AddBlockBottomSheetDialog dialog = AddBlockBottomSheetDialog.newInstance(type -> {
            DashboardBlock newBlock = DashboardBlock.createDefaultBlock(type);
            repository.addBlockToDashboard(dashboard.getId(), newBlock);
            loadDashboardData();
            Toast.makeText(this, "Блок «" + type.getDisplayName() + "» добавлен", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "add_block_sheet");
    }
}
