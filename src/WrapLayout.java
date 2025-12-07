import java.awt.*;
import javax.swing.*;

/**
 * Simple WrapLayout: a FlowLayout that wraps components to new rows
 * properly inside a JScrollPane viewport.
 */
public class WrapLayout extends FlowLayout {
    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension d = layoutSize(target, false);
        d.width -= (getHgap() + 1);
        return d;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int hgap = getHgap();
            int vgap = getVgap();
            int maxWidth = target.getWidth();
            if (maxWidth <= 0) {
                // Use parent's width if not yet valid
                Container p = target.getParent();
                if (p instanceof JViewport) {
                    maxWidth = p.getWidth();
                }
            }
            if (maxWidth <= 0) maxWidth = Integer.MAX_VALUE / 2;

            Insets insets = target.getInsets();
            int maxW = maxWidth - (insets.left + insets.right + hgap*2);
            int x = 0;
            int y = insets.top + vgap;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();
            for (int i=0;i<nmembers;i++) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;
                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (x == 0 || x + d.width <= maxW) {
                    // place in current row
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                } else {
                    // new row
                    y += rowHeight + vgap;
                    x = d.width + hgap;
                    rowHeight = d.height;
                }
            }
            y += rowHeight + vgap + insets.bottom;
            if (y < 0) y = 0;
            int width = Math.min(maxWidth, maxW + insets.left + insets.right + hgap*2);
            return new Dimension(width, y);
        }
    }
}
