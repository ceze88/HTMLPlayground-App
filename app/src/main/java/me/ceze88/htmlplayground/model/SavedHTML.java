package me.ceze88.htmlplayground.model;

public class SavedHTML {

    private String uid;
    private String title;
    private String content;
    private String user_uid;
    private long created_at;

    public SavedHTML() {
    }

    public SavedHTML(String uid, String title, String content, String user_uid, long created_at) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.user_uid = user_uid;
        this.created_at = created_at;
    }

    public String getId() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return user_uid;
    }

    public long getCreatedAt() {
        return created_at;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String user_uid) {
        this.user_uid = user_uid;
    }

    public void setCreatedAt(long created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "SavedHTML{" +
                "uid=" + uid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", user_uid=" + user_uid +
                ", created_at=" + created_at +
                '}';
    }
}
