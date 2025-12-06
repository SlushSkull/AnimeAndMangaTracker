public class Anime extends Show {
    private int totalEpisodes;
    private int watchedEpisodes;

    public Anime(String id, String title, String imageUrl, int totalEpisodes) {
        super(id, title, imageUrl);
        this.totalEpisodes = totalEpisodes;
        this.watchedEpisodes = 0;
    }

    public int getTotalEpisodes() { return totalEpisodes; }
    public int getWatchedEpisodes() { return watchedEpisodes; }
    public void setWatchedEpisodes(int episodes) { this.watchedEpisodes = episodes; }

    @Override
    public String getType() { return "ANIME"; }

    @Override
    public String toFileString() {
        return id + "|" + title + "|" + imageUrl + "|" + totalEpisodes;
    }

    public static Anime fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Anime(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
    }
}
