package com.example.gem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gem.R;
import com.example.gem.model.BlockType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddBlockBottomSheetDialog extends BottomSheetDialogFragment {

    public interface OnBlockTypeSelectedListener {
        void onBlockTypeSelected(BlockType type);
    }

    private OnBlockTypeSelectedListener listener;

    public static AddBlockBottomSheetDialog newInstance(OnBlockTypeSelectedListener listener) {
        AddBlockBottomSheetDialog dialog = new AddBlockBottomSheetDialog();
        dialog.listener = listener;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_block, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_add_block_schedule).setOnClickListener(v -> selectAndDismiss(BlockType.WEEK_SCHEDULE));
        view.findViewById(R.id.btn_add_block_todo).setOnClickListener(v -> selectAndDismiss(BlockType.TODO_LIST));
        view.findViewById(R.id.btn_add_block_note).setOnClickListener(v -> selectAndDismiss(BlockType.NOTE));
        view.findViewById(R.id.btn_add_block_metrics).setOnClickListener(v -> selectAndDismiss(BlockType.METRICS));
        view.findViewById(R.id.btn_add_block_calendar).setOnClickListener(v -> selectAndDismiss(BlockType.MONTH_CALENDAR));
    }

    private void selectAndDismiss(BlockType type) {
        if (listener != null) {
            listener.onBlockTypeSelected(type);
        }
        dismiss();
    }
}
