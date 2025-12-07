public class UserShowEntry {
    private String status;
    private int progress;
    private String showId;
    private int rating; // -1 = not rated, otherwise 0-10

    public UserShowEntry(String status, int progress) {
        this.status = status;
        this.progress = progress;
        this.rating = -1;
    }
    public UserShowEntry(String status, int progress, int rating) {
        this.status = status;
        this.progress = progress;
        this.rating = rating;
    }

    public String getStatus() { return status; }
    public int getProgress() { return progress; }
    public String getShowId() { return showId; }
    public void setShowId(String showId) { this.showId = showId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setStatus(String status) { this.status = status; }
}
