package com.example.gem.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dashboard {
    private String id;
    private String title;
    private String subtitle;
    private String category;
    private String coverColor;
    private String detailsText;
    private String quote;
    private List<DashboardBlock> blocks;

    public Dashboard(String title, String subtitle, String category, String coverColor) {
        this.id = UUID.randomUUID().toString();
        this.title = title != null ? title : "";
        this.subtitle = subtitle != null ? subtitle : "";
        this.category = category != null ? category : "Своя";
        this.coverColor = coverColor != null ? coverColor : "#E3E9DB";
        this.detailsText = "Новый дашборд";
        this.quote = "Ваше личное пространство.";
        this.blocks = new ArrayList<>();
    }

    public Dashboard(String id, String title, String subtitle, String category, String coverColor, String detailsText, String quote, List<DashboardBlock> blocks) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.title = title != null ? title : "";
        this.subtitle = subtitle != null ? subtitle : "";
        this.category = category != null ? category : "Своя";
        this.coverColor = coverColor != null ? coverColor : "#E3E9DB";
        this.detailsText = detailsText != null ? detailsText : "";
        this.quote = quote != null ? quote : "";
        this.blocks = blocks != null ? blocks : new ArrayList<>();
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("subtitle", subtitle);
        obj.put("category", category);
        obj.put("coverColor", coverColor);
        obj.put("detailsText", detailsText);
        obj.put("quote", quote);

        JSONArray blocksArr = new JSONArray();
        for (DashboardBlock b : blocks) {
            blocksArr.put(b.toJsonObject());
        }
        obj.put("blocks", blocksArr);
        return obj;
    }

    public static Dashboard fromJsonObject(JSONObject obj) {
        String id = obj.optString("id", UUID.randomUUID().toString());
        String title = obj.optString("title", "");
        String subtitle = obj.optString("subtitle", "");
        String category = obj.optString("category", "Своя");
        String coverColor = obj.optString("coverColor", "#E3E9DB");
        String detailsText = obj.optString("detailsText", "");
        String quote = obj.optString("quote", "");

        List<DashboardBlock> blocks = new ArrayList<>();
        JSONArray blocksArr = obj.optJSONArray("blocks");
        if (blocksArr != null) {
            for (int i = 0; i < blocksArr.length(); i++) {
                JSONObject bObj = blocksArr.optJSONObject(i);
                if (bObj != null) {
                    blocks.add(DashboardBlock.fromJsonObject(bObj));
                }
            }
        }
        return new Dashboard(id, title, subtitle, category, coverColor, detailsText, quote, blocks);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCoverColor() { return coverColor; }
    public void setCoverColor(String coverColor) { this.coverColor = coverColor; }
    public String getDetailsText() { return detailsText; }
    public void setDetailsText(String detailsText) { this.detailsText = detailsText; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public List<DashboardBlock> getBlocks() { return blocks; }

    public int getBlocksCount() {
        return blocks != null ? blocks.size() : 0;
    }

    public String getBlocksCountFormatted() {
        int count = getBlocksCount();
        if (count % 10 == 1 && count % 100 != 11) {
            return count + " блок";
        } else if ((count % 10 >= 2 && count % 10 <= 4) && (count % 100 < 10 || count % 100 >= 20)) {
            return count + " блока";
        } else {
            return count + " блоков";
        }
    }
}
