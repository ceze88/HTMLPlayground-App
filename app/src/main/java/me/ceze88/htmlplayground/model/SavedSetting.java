package me.ceze88.htmlplayground.model;

public class SavedSetting {

    private String uid;
    private boolean autoRefresh;

    public SavedSetting() {
    }

    public SavedSetting(String uid, boolean autoRefresh) {
        this.uid = uid;
        this.autoRefresh = autoRefresh;
    }

    public String getUid() {
        return uid;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }
}
