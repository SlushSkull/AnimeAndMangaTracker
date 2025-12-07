import javax.swing.*;
import java.awt.*;

/**
 * Base Card component used by AnimeCard and MangaCard.
 * Keeps shared layout and exposes components for subclasses to wire behavior.
 */
public abstract class Card extends JPanel {
    protected JLabel poster;
    protected JLabel titleLabel;
    protected JLabel typeLabel;
    protected JLabel progressLabel;
    protected JProgressBar progressBar;
    protected JButton inc;

    public Card(String title, String imageUrl, int progress, int total, int rating, String typeText) {
        int cardHeight = 140;
        int posterWidth = 105;

        setLayout(new BorderLayout());
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(Theme.BACKGROUND, 10);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(420, cardHeight + 10));
        card.setMaximumSize(card.getPreferredSize());
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentY(Component.TOP_ALIGNMENT);

        poster = new JLabel();
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
        rightCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardHeight));
        rightCol.setAlignmentY(Component.TOP_ALIGNMENT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Theme.SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        typeLabel = new JLabel(typeText);
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(Theme.SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        star.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JLabel ratingLabel = new JLabel((rating >= 0) ? String.valueOf(rating) : "—");
        ratingLabel.setForeground(Theme.SECONDARY);
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ratingBox.add(star);
        ratingBox.add(ratingLabel);

        Component hSpacer = Box.createHorizontalGlue();

        JPanel progressBox = new JPanel();
        progressBox.setOpaque(false);
        progressBox.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        progressLabel = new JLabel(progress + " / " + total);
        progressLabel.setForeground(Theme.SECONDARY);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        inc = new JButton("+1");
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

        progressBar = new JProgressBar(0, Math.max(1, total));
        int prog = Math.max(0, Math.min(progress, total));
        progressBar.setValue(prog);
        // Allow the progress bar to expand to the available horizontal space
        progressBar.setPreferredSize(new Dimension(0, 10));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightCol.add(titleLabel);
        rightCol.add(Box.createVerticalStrut(4));
        rightCol.add(typeLabel);
        rightCol.add(verticalSpacer);
        rightCol.add(metaRow);
        rightCol.add(Box.createVerticalStrut(6));
        rightCol.add(progressBar);

        card.add(rightCol, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(card, BorderLayout.CENTER);

        // ensure this panel declares preferred size so parents/layouts respect it
        setAlignmentY(Component.TOP_ALIGNMENT);
    }

    // Small helper rounded panel moved into Card so cards are self-contained
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
}
