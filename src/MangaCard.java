import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MangaCard extends Card {
    public MangaCard(Manga manga, UserShowEntry entry, String status, AnimeTrackerApp app) {
        super(manga.getTitle(), manga.getImageUrl(), entry.getProgress(), manga.getTotalChapters(), entry.getRating(), "Manga");

        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editManga(app, manga.getId(), status); }
            public void mouseEntered(MouseEvent e) { setBorderOnInnerCard(true); }
            public void mouseExited(MouseEvent e) { setBorderOnInnerCard(false); }
        };
        this.addMouseListener(clickListener);
        this.poster.addMouseListener(clickListener);

        this.inc.addActionListener(ae -> {
            int newVal = Math.min(manga.getTotalChapters(), entry.getProgress() + 1);
            entry.setProgress(newVal);

            if (newVal >= manga.getTotalChapters() && !"Completed".equals(entry.getStatus())) {
                int choice = JOptionPane.showConfirmDialog(app,
                    "You've read all chapters. Mark as Completed?",
                    "Complete Manga", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    entry.setStatus("Completed");
                }
            }

            this.progressLabel.setText(newVal + " / " + manga.getTotalChapters());
            this.progressBar.setValue(newVal);
            try {
                app.api.updateUserEntry(app.currentUser, manga.getId(), "MANGA", entry.getStatus(), newVal, entry.getRating());
                app.showUserDashboard();
            } catch (Exception ex) { }
        });
    }

    private void setBorderOnInnerCard(boolean highlight) {
        Component c = getComponentCount() > 0 ? getComponent(0) : null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (highlight) jc.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2));
            else jc.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        }
    }
}
