import javax.swing.*;

public abstract class Show {
    protected String id;
    protected String title;
    protected String imageUrl;

    public Show(String id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }

    public abstract String getType();
    public abstract String toFileString();
}
