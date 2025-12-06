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
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 320));
        card.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY, 2));
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(200, 280));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageLoader.loadImageAsync(anime.getImageUrl(), 196, 280, imageLabel);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Theme.BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("<html><center>" + anime.getTitle() + "</center></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String ratingText = (entry.getRating() >= 0) ? " • Rating: " + entry.getRating() : "";
        JLabel progressLabel = new JLabel(entry.getProgress() + "/" + anime.getTotalEpisodes() + " eps" + ratingText);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressLabel.setForeground(Theme.PRIMARY);

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(progressLabel, BorderLayout.SOUTH);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editAnime(app, anime.getId(), status); }
            public void mouseEntered(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 3)); }
            public void mouseExited(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY, 2)); }
        };

        card.addMouseListener(clickListener);
        imageLabel.addMouseListener(clickListener);

        return card;
    }

    public static JPanel createMangaCard(Manga manga, UserShowEntry entry, String status, AnimeTrackerApp app) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 320));
        card.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY, 2));
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(200, 280));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageLoader.loadImageAsync(manga.getImageUrl(), 196, 280, imageLabel);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Theme.BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("<html><center>" + manga.getTitle() + "</center></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String ratingText = (entry.getRating() >= 0) ? " • Rating: " + entry.getRating() : "";
        JLabel progressLabel = new JLabel(entry.getProgress() + "/" + manga.getTotalChapters() + " chs" + ratingText);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressLabel.setForeground(Theme.PRIMARY);

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(progressLabel, BorderLayout.SOUTH);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        MouseAdapter clickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { EditDialogs.editManga(app, manga.getId(), status); }
            public void mouseEntered(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 3)); }
            public void mouseExited(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY, 2)); }
        };

        card.addMouseListener(clickListener);
        imageLabel.addMouseListener(clickListener);

        return card;
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
