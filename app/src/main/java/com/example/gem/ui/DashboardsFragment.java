package com.example.gem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gem.MainActivity;
import com.example.gem.R;
import com.example.gem.data.DashboardRepository;
import com.example.gem.model.Dashboard;
import com.example.gem.model.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardsFragment extends Fragment {

    private DashboardRepository repository;
    private DashboardAdapter adapter;
    private TextView tvCount;
    private TextView tvCurrentDate;
    private EditText etSearch;

    private View cardEmptyDashboards;
    private RecyclerView rvDashboards;

    private View cardFocusItem;
    private View cardEmptyFocus;
    private TextView tvFocusTime;
    private TextView tvFocusDuration;
    private TextView tvFocusTitle;
    private TextView tvFocusSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = DashboardRepository.getInstance(requireContext());

        tvCount = view.findViewById(R.id.tv_dashboards_count);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        etSearch = view.findViewById(R.id.et_search);

        cardEmptyDashboards = view.findViewById(R.id.card_empty_dashboards);
        rvDashboards = view.findViewById(R.id.rv_dashboards);

        cardFocusItem = view.findViewById(R.id.card_focus_item);
        cardEmptyFocus = view.findViewById(R.id.card_empty_focus);
        tvFocusTime = view.findViewById(R.id.tv_focus_time);
        tvFocusDuration = view.findViewById(R.id.tv_focus_duration);
        tvFocusTitle = view.findViewById(R.id.tv_focus_title);
        tvFocusSource = view.findViewById(R.id.tv_focus_source);

        // Format current date matching Figma: "ПОНЕДЕЛЬНИК, 19 МАЯ"
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM", new Locale("ru"));
            tvCurrentDate.setText(sdf.format(new Date()).toUpperCase(new Locale("ru")));
        } catch (Exception e) {
            tvCurrentDate.setText("СЕГОДНЯ");
        }

        // Configure 2-Column Square Grid
        rvDashboards.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new DashboardAdapter(dashboard -> {
            Intent intent = new Intent(requireContext(), DashboardDetailActivity.class);
            intent.putExtra("dashboard_id", dashboard.getId());
            startActivity(intent);
        });
        rvDashboards.setAdapter(adapter);

        View.OnClickListener createListener = v -> {
            startActivity(new Intent(requireContext(), CreateDashboardActivity.class));
        };
        view.findViewById(R.id.btn_create_dashboard_quick).setOnClickListener(createListener);
        view.findViewById(R.id.btn_create_first_dashboard).setOnClickListener(createListener);

        // "В фокусе сегодня" actions
        View.OnClickListener focusClickListener = v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_today);
            }
        };
        view.findViewById(R.id.btn_view_full_day).setOnClickListener(focusClickListener);
        cardFocusItem.setOnClickListener(focusClickListener);
        cardEmptyFocus.setOnClickListener(focusClickListener);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDashboards(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Dashboard> list = repository.getDashboards();
        tvCount.setText(String.valueOf(list.size()));

        if (list.isEmpty()) {
            cardEmptyDashboards.setVisibility(View.VISIBLE);
            rvDashboards.setVisibility(View.GONE);
        } else {
            cardEmptyDashboards.setVisibility(View.GONE);
            rvDashboards.setVisibility(View.VISIBLE);
        }

        filterDashboards(etSearch != null ? etSearch.getText().toString() : "");
        updateFocusCard();
    }

    private void updateFocusCard() {
        List<TodoItem> tasks = repository.getTodayTasks();
        if (tasks.isEmpty()) {
            cardFocusItem.setVisibility(View.GONE);
            cardEmptyFocus.setVisibility(View.VISIBLE);
        } else {
            TodoItem first = tasks.get(0);
            for (TodoItem t : tasks) {
                if (!t.isCompleted()) {
                    first = t;
                    break;
                }
            }
            tvFocusTime.setText(first.getTime().isEmpty() ? "Сегодня" : first.getTime());
            tvFocusDuration.setText(first.getPriority().equals("HIGH") ? "Важно" : "План");
            tvFocusTitle.setText(first.getTitle());
            tvFocusSource.setText(first.getDetails().isEmpty() ? "Планы на день" : first.getDetails());

            cardFocusItem.setVisibility(View.VISIBLE);
            cardEmptyFocus.setVisibility(View.GONE);
        }
    }

    private void filterDashboards(String query) {
        List<Dashboard> all = repository.getDashboards();
        if (query == null || query.trim().isEmpty()) {
            adapter.setItems(all);
            return;
        }

        String lower = query.trim().toLowerCase();
        List<Dashboard> filtered = new ArrayList<>();
        for (Dashboard d : all) {
            if (d.getTitle().toLowerCase().contains(lower) ||
                d.getSubtitle().toLowerCase().contains(lower) ||
                d.getCategory().toLowerCase().contains(lower)) {
                filtered.add(d);
            }
        }
        adapter.setItems(filtered);
    }
}
