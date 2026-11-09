package com.example.gem.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.gem.R;
import com.example.gem.model.TodoItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTaskDialog extends BottomSheetDialogFragment {

    public interface OnTaskCreatedListener {
        void onTaskCreated(TodoItem task);
    }

    private OnTaskCreatedListener listener;
    private String selectedPriority = "MEDIUM";
    private String defaultDate = "Сегодня";

    public static AddTaskDialog newInstance(String defaultDate, OnTaskCreatedListener listener) {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.defaultDate = defaultDate != null ? defaultDate : "Сегодня";
        dialog.listener = listener;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etTitle = view.findViewById(R.id.et_task_title);
        EditText etDate = view.findViewById(R.id.et_task_date);
        EditText etTime = view.findViewById(R.id.et_task_time);
        EditText etNote = view.findViewById(R.id.et_task_note);

        TextView chipLow = view.findViewById(R.id.chip_priority_low);
        TextView chipMedium = view.findViewById(R.id.chip_priority_medium);
        TextView chipHigh = view.findViewById(R.id.chip_priority_high);

        etDate.setText(defaultDate);

        View.OnClickListener priorityClickListener = v -> {
            chipLow.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipMedium.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipHigh.setBackgroundResource(R.drawable.bg_chip_unselected);

            if (v.getId() == R.id.chip_priority_low) {
                selectedPriority = "LOW";
                chipLow.setBackgroundResource(R.drawable.bg_chip_selected);
            } else if (v.getId() == R.id.chip_priority_high) {
                selectedPriority = "HIGH";
                chipHigh.setBackgroundResource(R.drawable.bg_chip_selected);
            } else {
                selectedPriority = "MEDIUM";
                chipMedium.setBackgroundResource(R.drawable.bg_chip_selected);
            }
        };

        chipLow.setOnClickListener(priorityClickListener);
        chipMedium.setOnClickListener(priorityClickListener);
        chipHigh.setOnClickListener(priorityClickListener);

        view.findViewById(R.id.btn_submit_task).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Пожалуйста, введите название дела", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String note = etNote.getText().toString().trim();

            TodoItem task = new TodoItem(title, note, time, selectedPriority);
            task.setDate(date);

            if (listener != null) {
                listener.onTaskCreated(task);
            }
            dismiss();
        });
    }
}
