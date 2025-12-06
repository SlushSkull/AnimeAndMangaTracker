import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class AddToListActions {
    public static void addAnimeToList(AnimeTrackerApp app) {
        List<Anime> allAnime = app.api.getAllAnime();
        if (allAnime.isEmpty()) {
            JOptionPane.showMessageDialog(app, "No anime in database! Ask admin to add anime first.");
            return;
        }

        String[] animeNames = allAnime.stream().map(a -> a.getTitle()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(app, "Select Anime:",
            "Add Anime", JOptionPane.QUESTION_MESSAGE, null, animeNames, animeNames[0]);

        if (selected != null) {
            String[] statuses = {"Watching", "Completed", "Plan to Watch", "Dropped"};
            String status = (String) JOptionPane.showInputDialog(app, "Select Status:",
                "Add Anime", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
            if (status != null) {
                Anime chosenAnime = null;
                String sel = selected == null ? "" : selected.trim();
                for (Anime a : allAnime) {
                    if (a.getTitle() != null && a.getTitle().trim().equalsIgnoreCase(sel)) { chosenAnime = a; break; }
                }
                if (chosenAnime == null) {
                    for (Anime a : allAnime) {
                        if (a.getTitle() != null && a.getTitle().toLowerCase().contains(sel.toLowerCase())) { chosenAnime = a; break; }
                    }
                }
                if (chosenAnime == null) {
                    try {
                        String idFromSel = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                        for (Anime a : allAnime) if (a.getId().equals(idFromSel)) { chosenAnime = a; break; }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (chosenAnime == null) {
                    JOptionPane.showMessageDialog(app, "Could not find the selected anime in the database.");
                    return;
                }

                String id = chosenAnime.getId();
                try {
                    int initialProgress = 0;
                    if ("Completed".equals(status)) {
                        initialProgress = chosenAnime.getTotalEpisodes();
                    }
                    String ratingStr = JOptionPane.showInputDialog(app, "Enter initial rating (0-10) or leave blank:");
                    int rating = -1;
                    if (ratingStr != null && !ratingStr.trim().isEmpty()) {
                        try { rating = Integer.parseInt(ratingStr); } catch (NumberFormatException nfe) { rating = -1; }
                    }
                    boolean added = app.api.addToUserList(app.currentUser, id, status, "ANIME", initialProgress, rating);
                    if (added) {
                        JOptionPane.showMessageDialog(app, "Anime added successfully!");
                        app.showUserDashboard();
                    } else {
                        JOptionPane.showMessageDialog(app, "This anime is already in your list.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(app, "Error adding anime!");
                }
            }
        }
    }

    public static void addMangaToList(AnimeTrackerApp app) {
        List<Manga> allManga = app.api.getAllManga();
        if (allManga.isEmpty()) {
            JOptionPane.showMessageDialog(app, "No manga in database! Ask admin to add manga first.");
            return;
        }

        String[] mangaNames = allManga.stream().map(m -> m.getTitle()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(app, "Select Manga:",
            "Add Manga", JOptionPane.QUESTION_MESSAGE, null, mangaNames, mangaNames[0]);

        if (selected != null) {
            String[] statuses = {"Reading", "Completed", "Plan to Read", "Dropped"};
            String status = (String) JOptionPane.showInputDialog(app, "Select Status:",
                "Add Manga", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
            if (status != null) {
                Manga chosenManga = null;
                String sel = selected == null ? "" : selected.trim();
                for (Manga m : allManga) {
                    if (m.getTitle() != null && m.getTitle().trim().equalsIgnoreCase(sel)) { chosenManga = m; break; }
                }
                if (chosenManga == null) {
                    for (Manga m : allManga) {
                        if (m.getTitle() != null && m.getTitle().toLowerCase().contains(sel.toLowerCase())) { chosenManga = m; break; }
                    }
                }
                if (chosenManga == null) {
                    try {
                        String idFromSel = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                        for (Manga m : allManga) if (m.getId().equals(idFromSel)) { chosenManga = m; break; }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (chosenManga == null) { JOptionPane.showMessageDialog(app, "Could not find the selected manga in the database."); return; }

                String id = chosenManga.getId();
                try {
                    int initialProgress = 0;
                    if ("Completed".equals(status)) {
                        initialProgress = chosenManga.getTotalChapters();
                    }
                    String ratingStr = JOptionPane.showInputDialog(app, "Enter initial rating (0-10) or leave blank:");
                    int rating = -1;
                    if (ratingStr != null && !ratingStr.trim().isEmpty()) {
                        try { rating = Integer.parseInt(ratingStr); } catch (NumberFormatException nfe) { rating = -1; }
                    }
                    boolean added = app.api.addToUserList(app.currentUser, id, status, "MANGA", initialProgress, rating);
                    if (added) {
                        JOptionPane.showMessageDialog(app, "Manga added successfully!");
                        app.showUserDashboard();
                    } else {
                        JOptionPane.showMessageDialog(app, "This manga is already in your list.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(app, "Error adding manga!");
                }
            }
        }
    }
}
