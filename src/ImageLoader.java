import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;

public class ImageLoader {
    private static final Map<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    
    public static void loadImageAsync(String url, int width, int height, JLabel label) {
        if (url == null || url.trim().isEmpty()) {
            label.setIcon(createPlaceholderIcon(width, height));
            return;
        }
        
        if (imageCache.containsKey(url)) {
            label.setIcon(imageCache.get(url));
            return;
        }
        
        label.setIcon(createPlaceholderIcon(width, height));
        
        executor.submit(() -> {
            try {
                BufferedImage img = ImageIO.read(new URL(url));
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImg);
                    imageCache.put(url, icon);
                    SwingUtilities.invokeLater(() -> label.setIcon(icon));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> label.setIcon(createPlaceholderIcon(width, height)));
            }
        });
    }
    
    private static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String txt = "No Image";
        int tx = (width - fm.stringWidth(txt)) / 2;
        int ty = height / 2 + fm.getAscent() / 2;
        g2d.setColor(new Color(100,100,100));
        g2d.drawString(txt, tx, ty);
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
}
