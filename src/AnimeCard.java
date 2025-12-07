import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AnimeCard extends Card {
    public AnimeCard(Anime anime, UserShowEntry entry, String status, AnimeTrackerApp app) {
        super(anime.getTitle(), anime.getImageUrl(), entry.getProgress(), anime.getTotalEpisodes(), entry.getRating(), "TV");

        // Click opens edit dialog
        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editAnime(app, anime.getId(), status); }
            public void mouseEntered(MouseEvent e) { setBorderOnInnerCard(true); }
            public void mouseExited(MouseEvent e) { setBorderOnInnerCard(false); }
        };
        this.addMouseListener(clickListener);
        this.poster.addMouseListener(clickListener);

        // +1 button behavior
        this.inc.addActionListener(ae -> {
            int newVal = Math.min(anime.getTotalEpisodes(), entry.getProgress() + 1);
            entry.setProgress(newVal);

            if (newVal >= anime.getTotalEpisodes() && !"Completed".equals(entry.getStatus())) {
                int choice = JOptionPane.showConfirmDialog(app,
                    "You've watched all episodes. Mark as Completed?",
                    "Complete Anime", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    entry.setStatus("Completed");
                }
            }

            this.progressLabel.setText(newVal + " / " + anime.getTotalEpisodes());
            this.progressBar.setValue(newVal);

            try {
                app.api.updateUserEntry(app.currentUser, anime.getId(), "ANIME", entry.getStatus(), newVal, entry.getRating());
                app.showUserDashboard();
            } catch (Exception ex) { }
        });
    }

    private void setBorderOnInnerCard(boolean highlight) {
        // the RoundedPanel is the first child; set its border
        Component c = getComponentCount() > 0 ? getComponent(0) : null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (highlight) jc.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2));
            else jc.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        }
    }
}
