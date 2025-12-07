import javax.swing.*;
import java.awt.*;
 

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
        // Delegate to the dedicated AnimeCard class
        return new AnimeCard(anime, entry, status, app);
    }

    public static JPanel createMangaCard(Manga manga, UserShowEntry entry, String status, AnimeTrackerApp app) {
        // Delegate to the dedicated MangaCard class
        return new MangaCard(manga, entry, status, app);
    }

    // Card creation moved to dedicated Card/AnimeCard/MangaCard classes.

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
