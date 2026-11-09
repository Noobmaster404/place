package com.example.gem.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gem.R;
import com.example.gem.data.DashboardRepository;
import com.example.gem.model.TodoItem;

import java.util.List;

public class TodayFragment extends Fragment {

    private DashboardRepository repository;
    private TableLayout tableCalendarDays;
    private LinearLayout layoutTodayTasks;
    private TextView tvTodayDateFull;
    private int selectedDayNumber = 24;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = DashboardRepository.getInstance(requireContext());

        tableCalendarDays = view.findViewById(R.id.table_calendar_days);
        layoutTodayTasks = view.findViewById(R.id.layout_today_tasks);
        tvTodayDateFull = view.findViewById(R.id.tv_today_date_full);

        view.findViewById(R.id.btn_jump_today).setOnClickListener(v -> {
            selectedDayNumber = 24;
            updateCalendarGrid();
            tvTodayDateFull.setText("Суббота, 24 октября 2026");
        });

        view.findViewById(R.id.btn_today_add_task).setOnClickListener(v -> {
            AddTaskDialog dialog = AddTaskDialog.newInstance("24 окт. 2026", task -> {
                repository.addTodayTask(task);
                loadTasks();
            });
            dialog.show(getParentFragmentManager(), "add_today_task");
        });

        updateCalendarGrid();
        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void updateCalendarGrid() {
        tableCalendarDays.removeAllViews();
        // October 2026 starts on Thursday (index 3 if Monday is 0)
        // Days: 1 to 31
        // Previous month days: 28, 29, 30
        int startDayOfWeek = 3; // 0=Mon, 1=Tue, 2=Wed, 3=Thu, 4=Fri, 5=Sat, 6=Sun
        int totalDaysInMonth = 31;
        int currentDay = 1;
        int prevMonthDay = 28;

        for (int week = 0; week < 5; week++) {
            TableRow row = new TableRow(requireContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);

            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                TextView dayTv = new TextView(requireContext());
                dayTv.setGravity(Gravity.CENTER);
                dayTv.setTextSize(13);
                int pad = (int) (8 * getResources().getDisplayMetrics().density);
                dayTv.setPadding(0, pad, 0, pad);

                if (week == 0 && dayOfWeek < startDayOfWeek) {
                    // Previous month
                    dayTv.setText(String.valueOf(prevMonthDay + dayOfWeek));
                    dayTv.setTextColor(getColor(R.color.text_tertiary));
                } else if (currentDay <= totalDaysInMonth) {
                    final int dayNum = currentDay;
                    dayTv.setText(String.valueOf(dayNum));

                    if (dayNum == selectedDayNumber) {
                        dayTv.setBackgroundResource(R.drawable.bg_avatar_circle);
                        dayTv.setBackgroundTintList(requireContext().getColorStateList(R.color.brand_terracotta));
                        dayTv.setTextColor(Color.WHITE);
                    } else if (dayOfWeek >= 5) {
                        // Weekend
                        dayTv.setTextColor(getColor(R.color.brand_terracotta));
                    } else {
                        dayTv.setTextColor(getColor(R.color.text_primary));
                    }

                    dayTv.setOnClickListener(v -> {
                        selectedDayNumber = dayNum;
                        updateCalendarGrid();
                        tvTodayDateFull.setText(dayNum + " октября 2026");
                    });

                    currentDay++;
                } else {
                    // Next month
                    int nextMonthDay = currentDay - totalDaysInMonth;
                    dayTv.setText(String.valueOf(nextMonthDay));
                    dayTv.setTextColor(getColor(R.color.text_tertiary));
                    currentDay++;
                }

                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                dayTv.setLayoutParams(params);
                row.addView(dayTv);
            }
            tableCalendarDays.addView(row);
        }
    }

    private void loadTasks() {
        layoutTodayTasks.removeAllViews();
        List<TodoItem> tasks = repository.getTodayTasks();

        if (tasks.isEmpty()) {
            View emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.card_empty_state, layoutTodayTasks, false);
            ImageView ivIcon = emptyView.findViewById(R.id.iv_empty_icon);
            TextView tvTitle = emptyView.findViewById(R.id.tv_empty_title);
            TextView tvSubtitle = emptyView.findViewById(R.id.tv_empty_subtitle);

            ivIcon.setImageResource(R.drawable.ic_calendar_days);
            tvTitle.setText("Пока нет запланированных дел");
            tvSubtitle.setText("Нажмите «Добавить дело» ниже, чтобы запланировать задачу на этот день.");

            layoutTodayTasks.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (TodoItem task : tasks) {
            View itemView = inflater.inflate(R.layout.item_today_task, layoutTodayTasks, false);
            ImageView btnCheck = itemView.findViewById(R.id.btn_today_check);
            TextView tvTitle = itemView.findViewById(R.id.tv_today_title);
            TextView tvTime = itemView.findViewById(R.id.tv_today_time);
            TextView tvPriority = itemView.findViewById(R.id.tv_today_priority);

            tvTitle.setText(task.getTitle());
            tvTime.setText(task.getTime().isEmpty() ? "В течение дня" : task.getTime());

            // Priority label & color
            String p = task.getPriority();
            if ("HIGH".equalsIgnoreCase(p)) {
                tvPriority.setText("Высокий");
                tvPriority.setTextColor(getColor(R.color.priority_high));
            } else if ("LOW".equalsIgnoreCase(p)) {
                tvPriority.setText("Низкий");
                tvPriority.setTextColor(getColor(R.color.priority_low));
            } else {
                tvPriority.setText("Средний");
                tvPriority.setTextColor(getColor(R.color.priority_medium));
            }

            if (task.isCompleted()) {
                btnCheck.setImageResource(R.drawable.bg_checkbox_checked);
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(getColor(R.color.text_tertiary));
            } else {
                btnCheck.setImageResource(R.drawable.bg_checkbox_unchecked);
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setTextColor(getColor(R.color.text_primary));
            }

            btnCheck.setOnClickListener(v -> {
                repository.toggleTodayTask(task.getId());
                loadTasks();
            });

            itemView.findViewById(R.id.btn_today_delete).setOnClickListener(v -> {
                repository.deleteTodayTask(task.getId());
                loadTasks();
            });

            layoutTodayTasks.addView(itemView);
        }
    }

    private int getColor(int resId) {
        return requireContext().getColor(resId);
    }
}
