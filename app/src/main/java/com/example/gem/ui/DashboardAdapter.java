package com.example.gem.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gem.R;
import com.example.gem.model.Dashboard;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    public interface OnDashboardClickListener {
        void onDashboardClick(Dashboard dashboard);
    }

    private final List<Dashboard> items = new ArrayList<>();
    private final OnDashboardClickListener listener;

    public DashboardAdapter(OnDashboardClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Dashboard> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dashboard dashboard = items.get(position);
        holder.bind(dashboard, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView ivCategoryIcon;
        private final TextView tvBlocksCount;
        private final TextView tvDashboardTitle;
        private final TextView tvDashboardSubtitle;
        private final TextView tvDashboardDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_dashboard);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvBlocksCount = itemView.findViewById(R.id.tv_blocks_count);
            tvDashboardTitle = itemView.findViewById(R.id.tv_dashboard_title);
            tvDashboardSubtitle = itemView.findViewById(R.id.tv_dashboard_subtitle);
            tvDashboardDetails = itemView.findViewById(R.id.tv_dashboard_details);
        }

        public void bind(Dashboard dashboard, OnDashboardClickListener listener) {
            tvDashboardTitle.setText(dashboard.getTitle());
            tvDashboardSubtitle.setText(dashboard.getSubtitle().isEmpty() ? "Личное пространство" : dashboard.getSubtitle());
            tvBlocksCount.setText(dashboard.getBlocksCountFormatted());
            tvDashboardDetails.setText(dashboard.getDetailsText().isEmpty() ? "Активно" : dashboard.getDetailsText());

            try {
                int color = Color.parseColor(dashboard.getCoverColor());
                cardView.setCardBackgroundColor(color);
            } catch (Exception e) {
                cardView.setCardBackgroundColor(Color.parseColor("#E3E9DB"));
            }

            String cat = dashboard.getCategory();
            if ("Учёба".equalsIgnoreCase(cat)) {
                ivCategoryIcon.setImageResource(R.drawable.ic_book_open);
            } else if ("Работа".equalsIgnoreCase(cat)) {
                ivCategoryIcon.setImageResource(R.drawable.ic_briefcase);
            } else if ("Личное".equalsIgnoreCase(cat)) {
                ivCategoryIcon.setImageResource(R.drawable.ic_heart);
            } else if ("Спорт".equalsIgnoreCase(cat) || "dash_sport".equals(dashboard.getId())) {
                ivCategoryIcon.setImageResource(R.drawable.ic_chart_column);
            } else {
                ivCategoryIcon.setImageResource(R.drawable.ic_sparkles);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDashboardClick(dashboard);
                }
            });
        }
    }
}
