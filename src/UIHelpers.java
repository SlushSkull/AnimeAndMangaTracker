import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIHelpers {
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    public static JLabel createImageLabel(String url, int width, int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (url == null || url.trim().isEmpty()) {
            label.setText("No Image");
            return label;
        }
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(url));
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            label.setText("");
        } catch (Exception e) {
            label.setText("No Image");
        }
        return label;
    }

    public static JPanel createEntryPanelForAnime(Anime anime, int userProgress, String status) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);

        JLabel img = createImageLabel(anime.getImageUrl(), 120, 160);
        panel.add(img, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setBackground(Color.WHITE);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Title: " + anime.getTitle());
        title.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel rating = new JLabel("Episodes: " + userProgress + " / " + anime.getTotalEpisodes());
        JLabel stat = new JLabel("Status: " + status);

        info.add(title);
        info.add(Box.createVerticalStrut(6));
        info.add(rating);
        info.add(Box.createVerticalStrut(6));
        info.add(stat);

        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel createAnimeCard(Anime anime, UserShowEntry entry, String status, AnimeTrackerApp app) {
        // Build common card parts and then wire anime-specific behavior
        CardParts parts = createCard(anime.getId(), anime.getTitle(), anime.getImageUrl(), entry.getProgress(), anime.getTotalEpisodes(), entry.getRating(), "TV", app);

        // Click opens edit dialog
        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editAnime(app, anime.getId(), status); }
            public void mouseEntered(MouseEvent e) { parts.card.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2)); }
            public void mouseExited(MouseEvent e) { parts.card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); }
        };
        parts.card.addMouseListener(clickListener);
        parts.poster.addMouseListener(clickListener);

        // +1 button behavior
        parts.inc.addActionListener(ae -> {
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

            parts.progressLabel.setText(newVal + " / " + anime.getTotalEpisodes());
            parts.progressBar.setValue(newVal);

            try {
                app.api.updateUserEntry(app.currentUser, anime.getId(), "ANIME", entry.getStatus(), newVal, entry.getRating());
                app.showUserDashboard();
            } catch (Exception ex) { }
        });

        return parts.card;
    }

    public static JPanel createMangaCard(Manga manga, UserShowEntry entry, String status, AnimeTrackerApp app) {
        // Build common card parts and then wire manga-specific behavior
        CardParts parts = createCard(manga.getId(), manga.getTitle(), manga.getImageUrl(), entry.getProgress(), manga.getTotalChapters(), entry.getRating(), "Manga", app);

        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editManga(app, manga.getId(), status); }
            public void mouseEntered(MouseEvent e) { parts.card.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2)); }
            public void mouseExited(MouseEvent e) { parts.card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); }
        };
        parts.card.addMouseListener(clickListener);
        parts.poster.addMouseListener(clickListener);

        parts.inc.addActionListener(ae -> {
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

            parts.progressLabel.setText(newVal + " / " + manga.getTotalChapters());
            parts.progressBar.setValue(newVal);
            try {
                app.api.updateUserEntry(app.currentUser, manga.getId(), "MANGA", entry.getStatus(), newVal, entry.getRating());
                app.showUserDashboard();
            } catch (Exception ex) { }
        });

        return parts.card;
    }

    // Helper container for components returned by createCard
    private static class CardParts {
        JPanel card;
        JLabel poster;
        JLabel titleLabel;
        JLabel typeLabel;
        JLabel progressLabel;
        JProgressBar progressBar;
        JButton inc;
    }

    /**
     * Builds the shared card UI and returns its parts so caller can wire behavior.
     */
    private static CardParts createCard(String id, String title, String imageUrl, int progress, int total, int rating, String typeText, AnimeTrackerApp app) {
        int cardHeight = 140;
        int posterWidth = 105;

        RoundedPanel card = new RoundedPanel(Theme.BACKGROUND, 10);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(420, cardHeight + 10));
        // prevent vertical expansion beyond preferred height
        card.setMaximumSize(card.getPreferredSize());
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // ensure BoxLayout parents align cards to the top
        card.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel poster = new JLabel();
        poster.setPreferredSize(new Dimension(posterWidth, cardHeight));
        poster.setMaximumSize(new Dimension(posterWidth, cardHeight));
        poster.setHorizontalAlignment(SwingConstants.CENTER);
        ImageLoader.loadImageAsync(imageUrl, posterWidth - 4, cardHeight, poster);
        card.add(poster, BorderLayout.WEST);
        poster.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        // prevent right column from forcing card to expand vertically
        rightCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardHeight));
        rightCol.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Theme.SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel(typeText);
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(Theme.SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // use a fixed spacer so BoxLayout does not expand glue to fill extra space
        Component verticalSpacer = Box.createVerticalStrut(8);

        JPanel metaRow = new JPanel();
        metaRow.setOpaque(false);
        metaRow.setLayout(new BoxLayout(metaRow, BoxLayout.X_AXIS));
        metaRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel ratingBox = new JPanel();
        ratingBox.setOpaque(false);
        ratingBox.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JLabel star = new JLabel("\u2605");
        star.setForeground(new Color(255, 176, 0));
        // Use a logical font that reliably contains the star glyph on most platforms
        star.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JLabel ratingLabel = new JLabel((rating >= 0) ? formatRating(rating) : "—");
        ratingLabel.setForeground(Theme.SECONDARY);
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ratingBox.add(star);
        ratingBox.add(ratingLabel);

        Component hSpacer = Box.createHorizontalGlue();

        JPanel progressBox = new JPanel();
        progressBox.setOpaque(false);
        progressBox.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel progressLabel = new JLabel(progress + " / " + total);
        progressLabel.setForeground(Theme.SECONDARY);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JButton inc = new JButton("+1");
        inc.setFocusable(false);
        inc.setFont(new Font("Arial", Font.BOLD, 12));
        inc.setBackground(Theme.PRIMARY);
        inc.setForeground(Color.WHITE);
        inc.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        progressBox.add(progressLabel);
        progressBox.add(inc);

        metaRow.add(ratingBox);
        metaRow.add(hSpacer);
        metaRow.add(progressBox);

        JProgressBar progressBar = new JProgressBar(0, Math.max(1, total));
        int prog = Math.max(0, Math.min(progress, total));
        progressBar.setValue(prog);
        progressBar.setPreferredSize(new Dimension(200, 10));
        progressBar.setMaximumSize(new Dimension(200, 10));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightCol.add(titleLabel);
        rightCol.add(Box.createVerticalStrut(4));
        rightCol.add(typeLabel);
        rightCol.add(verticalSpacer);
        rightCol.add(metaRow);
        rightCol.add(Box.createVerticalStrut(6));
        rightCol.add(progressBar);

        card.add(rightCol, BorderLayout.CENTER);

        CardParts parts = new CardParts();
        parts.card = card;
        parts.poster = poster;
        parts.titleLabel = titleLabel;
        parts.typeLabel = typeLabel;
        parts.progressLabel = progressLabel;
        parts.progressBar = progressBar;
        parts.inc = inc;
        return parts;
    }

    // Small helper rounded panel to get the card background with rounded corners
    static class RoundedPanel extends JPanel {
        private Color bgColor;
        private int radius;

        public RoundedPanel(Color bgColor, int radius) {
            super();
            this.bgColor = bgColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        public Dimension getMaximumSize() {
            // refuse to grow beyond preferred size to keep BoxLayout behavior predictable
            Dimension pref = getPreferredSize();
            return pref == null ? super.getMaximumSize() : pref;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Helper to format rating consistently
    private static String formatRating(int rating) {
        if (rating < 0) return "—";
        return String.valueOf(rating);
    }

    public static JPanel createEntryPanelForManga(Manga manga, int userProgress, String status) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setBackground(Color.WHITE);

        JLabel img = createImageLabel(manga.getImageUrl(), 120, 160);
        panel.add(img, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setBackground(Color.WHITE);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Title: " + manga.getTitle());
        title.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel progress = new JLabel("Chapters: " + userProgress + " / " + manga.getTotalChapters());
        JLabel stat = new JLabel("Status: " + status);

        info.add(title);
        info.add(Box.createVerticalStrut(6));
        info.add(progress);
        info.add(Box.createVerticalStrut(6));
        info.add(stat);

        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }
}
