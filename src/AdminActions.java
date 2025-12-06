import javax.swing.*;

public class AdminActions {
    public static void addAnimeToDatabase(AnimeTrackerApp app) {
        JPanel inputPanel = new JPanel(new java.awt.GridLayout(4, 2, 10, 10));
        JTextField titleField = new JTextField();
        JTextField episodesField = new JTextField();
        JTextField imageUrlField = new JTextField();

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Total Episodes:"));
        inputPanel.add(episodesField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);

        int result = JOptionPane.showConfirmDialog(app, inputPanel, "Add Anime",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                int episodes = Integer.parseInt(episodesField.getText());
                String imageUrl = imageUrlField.getText();

                if (title == null || title.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(app, "Title cannot be empty.");
                    return;
                }
                if (episodes < 0) {
                    JOptionPane.showMessageDialog(app, "Total episodes must be 0 or greater.");
                    return;
                }

                String id = app.api.generateAnimeId();
                Anime anime = new Anime(id, title, imageUrl, episodes);
                app.api.addAnime(anime);
                JOptionPane.showMessageDialog(app, "Anime added to database! ID: " + id);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(app, "Total Episodes must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app, "Error adding anime: " + ex.getMessage());
            }
        }
    }

    public static void addMangaToDatabase(AnimeTrackerApp app) {
        JPanel inputPanel = new JPanel(new java.awt.GridLayout(4, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextField chaptersField = new JTextField();
        JTextField imageUrlField = new JTextField();

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Total Chapters:"));
        inputPanel.add(chaptersField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);

        int result = JOptionPane.showConfirmDialog(app, inputPanel, "Add Manga",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                int chapters = Integer.parseInt(chaptersField.getText());
                String imageUrl = imageUrlField.getText();

                if (title == null || title.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(app, "Title cannot be empty.");
                    return;
                }
                if (chapters < 0) {
                    JOptionPane.showMessageDialog(app, "Total chapters must be 0 or greater.");
                    return;
                }

                String id = app.api.generateMangaId();
                Manga manga = new Manga(id, title, imageUrl, chapters);
                app.api.addManga(manga);
                JOptionPane.showMessageDialog(app, "Manga added to database! ID: " + id);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(app, "Total Chapters must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app, "Error adding manga: " + ex.getMessage());
            }
        }
    }
}
