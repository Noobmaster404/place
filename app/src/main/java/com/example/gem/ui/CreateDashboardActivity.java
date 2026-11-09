package com.example.gem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gem.R;
import com.example.gem.data.DashboardRepository;
import com.example.gem.model.BlockType;
import com.example.gem.model.Dashboard;
import com.example.gem.model.DashboardBlock;

import java.util.ArrayList;
import java.util.List;

public class CreateDashboardActivity extends AppCompatActivity {

    private EditText etDashboardName;
    private TextView tvCharCounter;
    private TextView tvBlocksSelectedCount;

    private String selectedCategory = "Своя";
    private String selectedColor = "#F2DFD2"; // Peach default

    private boolean isScheduleSelected = true;
    private boolean isTodoSelected = true;
    private boolean isNoteSelected = true;
    private boolean isMetricsSelected = false;
    private boolean isCalendarSelected = false;

    private ImageView checkGreen, checkPurple, checkPeach, checkYellow;
    private ImageView cbSchedule, cbTodo, cbNote, cbMetrics, cbCalendar;
    private View cardSchedule, cardTodo, cardNote, cardMetrics, cardCalendar;

    private TextView chipStudy, chipWork, chipPersonal, chipCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dashboard);

        initViews();
        setupCategoryChips();
        setupColorSelectors();
        setupBlockCheckboxes();
        setupListeners();
        updateSelectedBlocksCount();
    }

    private void initViews() {
        etDashboardName = findViewById(R.id.et_dashboard_name);
        tvCharCounter = findViewById(R.id.tv_char_counter);
        tvBlocksSelectedCount = findViewById(R.id.tv_blocks_selected_count);

        checkGreen = findViewById(R.id.check_green);
        checkPurple = findViewById(R.id.check_purple);
        checkPeach = findViewById(R.id.check_peach);
        checkYellow = findViewById(R.id.check_yellow);

        cbSchedule = findViewById(R.id.cb_schedule);
        cbTodo = findViewById(R.id.cb_todo);
        cbNote = findViewById(R.id.cb_note);
        cbMetrics = findViewById(R.id.cb_metrics);
        cbCalendar = findViewById(R.id.cb_calendar);

        cardSchedule = findViewById(R.id.card_block_schedule);
        cardTodo = findViewById(R.id.card_block_todo);
        cardNote = findViewById(R.id.card_block_note);
        cardMetrics = findViewById(R.id.card_block_metrics);
        cardCalendar = findViewById(R.id.card_block_calendar);

        chipStudy = findViewById(R.id.chip_study);
        chipWork = findViewById(R.id.chip_work);
        chipPersonal = findViewById(R.id.chip_personal);
        chipCustom = findViewById(R.id.chip_custom);
    }

    private void setupCategoryChips() {
        View.OnClickListener categoryListener = v -> {
            chipStudy.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipWork.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipPersonal.setBackgroundResource(R.drawable.bg_chip_unselected);
            chipCustom.setBackgroundResource(R.drawable.bg_chip_unselected);

            chipStudy.setTextColor(getColor(R.color.text_primary));
            chipWork.setTextColor(getColor(R.color.text_primary));
            chipPersonal.setTextColor(getColor(R.color.text_primary));
            chipCustom.setTextColor(getColor(R.color.text_primary));

            int id = v.getId();
            if (id == R.id.chip_study) {
                selectedCategory = "Учёба";
                chipStudy.setBackgroundResource(R.drawable.bg_chip_selected);
                chipStudy.setTextColor(getColor(R.color.brand_terracotta));
                selectColor("#E3E9DB");
            } else if (id == R.id.chip_work) {
                selectedCategory = "Работа";
                chipWork.setBackgroundResource(R.drawable.bg_chip_selected);
                chipWork.setTextColor(getColor(R.color.brand_terracotta));
                selectColor("#E8E3F0");
            } else if (id == R.id.chip_personal) {
                selectedCategory = "Личное";
                chipPersonal.setBackgroundResource(R.drawable.bg_chip_selected);
                chipPersonal.setTextColor(getColor(R.color.brand_terracotta));
                selectColor("#F3E9CA");
            } else {
                selectedCategory = "Своя";
                chipCustom.setBackgroundResource(R.drawable.bg_chip_selected);
                chipCustom.setTextColor(getColor(R.color.brand_terracotta));
                selectColor("#F2DFD2");
            }
        };

        chipStudy.setOnClickListener(categoryListener);
        chipWork.setOnClickListener(categoryListener);
        chipPersonal.setOnClickListener(categoryListener);
        chipCustom.setOnClickListener(categoryListener);
    }

    private void setupColorSelectors() {
        findViewById(R.id.color_green).setOnClickListener(v -> selectColor("#E3E9DB"));
        findViewById(R.id.color_purple).setOnClickListener(v -> selectColor("#E8E3F0"));
        findViewById(R.id.color_peach).setOnClickListener(v -> selectColor("#F2DFD2"));
        findViewById(R.id.color_yellow).setOnClickListener(v -> selectColor("#F3E9CA"));
    }

    private void selectColor(String hexColor) {
        selectedColor = hexColor;
        checkGreen.setVisibility(View.GONE);
        checkPurple.setVisibility(View.GONE);
        checkPeach.setVisibility(View.GONE);
        checkYellow.setVisibility(View.GONE);

        if ("#E3E9DB".equalsIgnoreCase(hexColor)) {
            checkGreen.setVisibility(View.VISIBLE);
        } else if ("#E8E3F0".equalsIgnoreCase(hexColor)) {
            checkPurple.setVisibility(View.VISIBLE);
        } else if ("#F3E9CA".equalsIgnoreCase(hexColor)) {
            checkYellow.setVisibility(View.VISIBLE);
        } else {
            checkPeach.setVisibility(View.VISIBLE);
        }
    }

    private void setupBlockCheckboxes() {
        cardSchedule.setOnClickListener(v -> {
            isScheduleSelected = !isScheduleSelected;
            updateBlockUi(cardSchedule, cbSchedule, isScheduleSelected);
        });

        cardTodo.setOnClickListener(v -> {
            isTodoSelected = !isTodoSelected;
            updateBlockUi(cardTodo, cbTodo, isTodoSelected);
        });

        cardNote.setOnClickListener(v -> {
            isNoteSelected = !isNoteSelected;
            updateBlockUi(cardNote, cbNote, isNoteSelected);
        });

        cardMetrics.setOnClickListener(v -> {
            isMetricsSelected = !isMetricsSelected;
            updateBlockUi(cardMetrics, cbMetrics, isMetricsSelected);
        });

        cardCalendar.setOnClickListener(v -> {
            isCalendarSelected = !isCalendarSelected;
            updateBlockUi(cardCalendar, cbCalendar, isCalendarSelected);
        });
    }

    private void updateBlockUi(View card, ImageView cb, boolean selected) {
        if (selected) {
            cb.setImageResource(R.drawable.bg_checkbox_checked);
            if (card instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) card).setStrokeColor(getColor(R.color.brand_terracotta));
                ((com.google.android.material.card.MaterialCardView) card).setStrokeWidth((int) (1.5f * getResources().getDisplayMetrics().density));
            }
        } else {
            cb.setImageResource(R.drawable.bg_checkbox_unchecked);
            if (card instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) card).setStrokeColor(getColor(R.color.card_stroke));
                ((com.google.android.material.card.MaterialCardView) card).setStrokeWidth((int) (1.0f * getResources().getDisplayMetrics().density));
            }
        }
        updateSelectedBlocksCount();
    }

    private void updateSelectedBlocksCount() {
        int count = 0;
        if (isScheduleSelected) count++;
        if (isTodoSelected) count++;
        if (isNoteSelected) count++;
        if (isMetricsSelected) count++;
        if (isCalendarSelected) count++;

        tvBlocksSelectedCount.setText("Выбрано " + count);
    }

    private void setupListeners() {
        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        etDashboardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(s.length() + " / 40");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btn_create_dashboard).setOnClickListener(v -> createDashboard());
    }

    private void createDashboard() {
        String name = etDashboardName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            name = selectedCategory.equals("Своя") ? "Моё пространство" : selectedCategory;
        }

        List<DashboardBlock> blocks = new ArrayList<>();
        if (isScheduleSelected) {
            blocks.add(DashboardBlock.createDefaultBlock(BlockType.WEEK_SCHEDULE));
        }
        if (isTodoSelected) {
            blocks.add(DashboardBlock.createDefaultBlock(BlockType.TODO_LIST));
        }
        if (isNoteSelected) {
            blocks.add(DashboardBlock.createDefaultBlock(BlockType.NOTE));
        }
        if (isMetricsSelected) {
            blocks.add(DashboardBlock.createDefaultBlock(BlockType.METRICS));
        }
        if (isCalendarSelected) {
            blocks.add(DashboardBlock.createDefaultBlock(BlockType.MONTH_CALENDAR));
        }

        if (blocks.isEmpty()) {
            Toast.makeText(this, "Выберите хотя бы один блок для дашборда", Toast.LENGTH_SHORT).show();
            return;
        }

        Dashboard dashboard = new Dashboard(name, "Ваше пространство для заметок и планов", selectedCategory, selectedColor);
        dashboard.getBlocks().addAll(blocks);
        dashboard.setQuote("Всё, что вам важно, в одном месте.");
        dashboard.setDetailsText("Создан сегодня");

        DashboardRepository.getInstance(this).addDashboard(dashboard);

        Intent intent = new Intent(this, DashboardDetailActivity.class);
        intent.putExtra("dashboard_id", dashboard.getId());
        startActivity(intent);
        finish();
    }
}
