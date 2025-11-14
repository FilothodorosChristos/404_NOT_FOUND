package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class FirstGuiClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // WELCOME FRAME SETUP
            JFrame frame = new JFrame("Καλώς Ήρθατε στην GoverLens.");
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // SET APP ICON
            frame.setIconImage(new ImageIcon("GoverLensLogo.jpg").getImage());

            // LOGO PANEL WITH CIRCULAR IMAGE
            JPanel imagePanel = new JPanel() {
                Image image = new ImageIcon("GoverLensLogo.jpg").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int diameter = Math.min(getWidth(), getHeight()) - 20;
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;

                    Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
                    g2.setClip(clip);
                    g2.drawImage(image, x, y, diameter, diameter, this);

                    // DRAW CIRCLE BORDER
                    g2.setClip(null);
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x, y, diameter, diameter);

                    g2.dispose();
                }
            };
            frame.add(imagePanel, BorderLayout.CENTER);
            // NEXT BUTTON 1
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton button = new JButton("Επόμενο >");
            buttonPanel.add(button);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
