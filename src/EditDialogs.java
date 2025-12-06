import javax.swing.*;
import java.io.IOException;

public class EditDialogs {
    public static void editAnime(AnimeTrackerApp app, String animeId, String currentStatus) {
        Anime anime = null;
        for (Anime a : app.api.getAllAnime()) {
            if (a.getId().equals(animeId)) { anime = a; break; }
        }
        if (anime == null) return;
        UserShowEntry entry = app.api.getUserShowEntry(app.currentUser, animeId, "ANIME");
        if (entry == null) return;

        JPanel editPanel = new JPanel(new java.awt.GridLayout(4, 2, 10, 10));
        JLabel titleLabel = new JLabel("Editing: " + anime.getTitle());
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        JSpinner episodeSpinner = new JSpinner(new SpinnerNumberModel(
            entry.getProgress(), 0, anime.getTotalEpisodes(), 1));

        JCheckBox noRating = new JCheckBox("No Rating");
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(Math.max(0, entry.getRating()), 0, 10, 1));
        if (entry.getRating() < 0) noRating.setSelected(true);
        ratingSpinner.setEnabled(!noRating.isSelected());
        noRating.addActionListener(ev -> ratingSpinner.setEnabled(!noRating.isSelected()));

        String[] statuses = {"Watching", "Completed", "Plan to Watch", "Dropped"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(currentStatus);

        editPanel.add(titleLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Episodes Watched:"));
        editPanel.add(episodeSpinner);
        editPanel.add(new JLabel("Status:"));
        editPanel.add(statusCombo);
        editPanel.add(new JLabel("Rating (0-10):"));
        JPanel ratingPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        ratingPanel.add(ratingSpinner);
        ratingPanel.add(noRating);
        editPanel.add(ratingPanel);

        int result = JOptionPane.showConfirmDialog(app, editPanel, "Edit Anime Progress",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newProgress = (Integer) episodeSpinner.getValue();
                String newStatus = (String) statusCombo.getSelectedItem();
                int newRating = noRating.isSelected() ? -1 : (Integer) ratingSpinner.getValue();

                if (newProgress > anime.getTotalEpisodes()) {
                    JOptionPane.showMessageDialog(app, "Watched episodes cannot exceed total episodes.");
                    return;
                }

                if ("Completed".equals(newStatus)) {
                    newProgress = anime.getTotalEpisodes();
                }

                if (newProgress == anime.getTotalEpisodes() && !"Completed".equals(newStatus)) {
                    int choice = JOptionPane.showConfirmDialog(app,
                        "You've watched all episodes. Mark as Completed?",
                        "Complete Anime", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        newStatus = "Completed";
                    }
                }

                app.api.updateUserEntry(app.currentUser, animeId, "ANIME", newStatus, newProgress, newRating);
                JOptionPane.showMessageDialog(app, "Updated successfully!");
                app.showUserDashboard();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(app, "Error updating anime!");
            }
        }
    }

    public static void editManga(AnimeTrackerApp app, String mangaId, String currentStatus) {
        Manga manga = null;
        for (Manga m : app.api.getAllManga()) {
            if (m.getId().equals(mangaId)) { manga = m; break; }
        }
        if (manga == null) return;
        UserShowEntry entry = app.api.getUserShowEntry(app.currentUser, mangaId, "MANGA");
        if (entry == null) return;

        JPanel editPanel = new JPanel(new java.awt.GridLayout(4, 2, 10, 10));
        JLabel titleLabel = new JLabel("Editing: " + manga.getTitle());
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        JSpinner chapterSpinner = new JSpinner(new SpinnerNumberModel(
            entry.getProgress(), 0, manga.getTotalChapters(), 1));

        JCheckBox noRating = new JCheckBox("No Rating");
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(Math.max(0, entry.getRating()), 0, 10, 1));
        if (entry.getRating() < 0) noRating.setSelected(true);
        ratingSpinner.setEnabled(!noRating.isSelected());
        noRating.addActionListener(ev -> ratingSpinner.setEnabled(!noRating.isSelected()));

        String[] statuses = {"Reading", "Completed", "Plan to Read", "Dropped"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(currentStatus);

        editPanel.add(titleLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Chapters Read:"));
        editPanel.add(chapterSpinner);
        editPanel.add(new JLabel("Status:"));
        editPanel.add(statusCombo);
        editPanel.add(new JLabel("Rating (0-10):"));
        JPanel ratingPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        ratingPanel.add(ratingSpinner);
        ratingPanel.add(noRating);
        editPanel.add(ratingPanel);

        int result = JOptionPane.showConfirmDialog(app, editPanel, "Edit Manga Progress",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newProgress = (Integer) chapterSpinner.getValue();
                String newStatus = (String) statusCombo.getSelectedItem();
                int newRating = noRating.isSelected() ? -1 : (Integer) ratingSpinner.getValue();

                if (newProgress == manga.getTotalChapters() && !newStatus.equals("Completed")) {
                    int choice = JOptionPane.showConfirmDialog(app,
                        "You've read all chapters. Mark as Completed?",
                        "Complete Manga", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        newStatus = "Completed";
                    }
                }

                app.api.updateUserEntry(app.currentUser, mangaId, "MANGA", newStatus, newProgress, newRating);
                JOptionPane.showMessageDialog(app, "Updated successfully!");
                app.showUserDashboard();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(app, "Error updating manga!");
            }
        }
    }
}
