package com.example.gem.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gem.R;
import com.example.gem.data.DashboardRepository;
import com.example.gem.model.BlockType;
import com.example.gem.model.Dashboard;
import com.example.gem.model.DashboardBlock;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class NotesFragment extends Fragment {

    private DashboardRepository repository;
    private LinearLayout layoutAllNotes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = DashboardRepository.getInstance(requireContext());
        layoutAllNotes = view.findViewById(R.id.layout_all_notes);
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        layoutAllNotes.removeAllViews();
        List<DashboardBlock> notes = repository.getAllNotes();

        if (notes.isEmpty()) {
            View emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.card_empty_state, layoutAllNotes, false);
            android.widget.ImageView ivIcon = emptyView.findViewById(R.id.iv_empty_icon);
            TextView tvTitle = emptyView.findViewById(R.id.tv_empty_title);
            TextView tvSubtitle = emptyView.findViewById(R.id.tv_empty_subtitle);

            ivIcon.setImageResource(R.drawable.ic_sticky_note);
            tvTitle.setText("У вас пока нет заметок");
            tvSubtitle.setText("Создайте дашборд и добавьте блок «Заметки», чтобы сохранять мысли и цитаты.");

            layoutAllNotes.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (DashboardBlock note : notes) {
            View noteView = inflater.inflate(R.layout.block_note, layoutAllNotes, false);
            TextView tvTitle = noteView.findViewById(R.id.tv_note_title);
            TextView tvContent = noteView.findViewById(R.id.tv_note_content);
            TextView tvTag = noteView.findViewById(R.id.tv_note_tag);

            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getNoteContent());
            tvTag.setText(note.getNoteTag().isEmpty() ? "Заметка ↗" : note.getNoteTag());

            View.OnClickListener editListener = v -> showEditNoteDialog(note);
            noteView.findViewById(R.id.btn_edit_note).setOnClickListener(editListener);
            noteView.setOnClickListener(editListener);

            noteView.findViewById(R.id.btn_delete_note_block).setVisibility(View.GONE);

            layoutAllNotes.addView(noteView);
        }
    }

    private void showEditNoteDialog(DashboardBlock note) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTitle = new EditText(requireContext());
        etTitle.setHint("Заголовок заметки");
        etTitle.setText(note.getTitle());
        layout.addView(etTitle);

        final EditText etContent = new EditText(requireContext());
        etContent.setHint("Текст заметки");
        etContent.setText(note.getNoteContent());
        etContent.setMinLines(3);
        layout.addView(etContent);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Редактировать заметку")
                .setView(layout)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    note.setTitle(etTitle.getText().toString().trim());
                    note.setNoteContent(etContent.getText().toString().trim());
                    repository.saveData();
                    loadNotes();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
