package GUI;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    // ...existing code...

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // implementation
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 5, 5, 5);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
