public class Manga extends Show {
    private int totalChapters;
    private int readChapters;

    public Manga(String id, String title, String imageUrl, int totalChapters) {
        super(id, title, imageUrl);
        this.totalChapters = totalChapters;
        this.readChapters = 0;
    }

    public int getTotalChapters() { return totalChapters; }
    public int getReadChapters() { return readChapters; }
    public void setReadChapters(int chapters) { this.readChapters = chapters; }

    @Override
    public String getType() { return "MANGA"; }

    @Override
    public String toFileString() {
        return id + "|" + title + "|" + imageUrl + "|" + totalChapters;
    }

    public static Manga fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Manga(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
    }
}
