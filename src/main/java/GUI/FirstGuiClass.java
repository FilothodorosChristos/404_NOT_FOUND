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

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton button = new JButton("Επόμενο >");
            buttonPanel.add(button);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            button.addActionListener(e -> {
                new SecondWindow();
                frame.dispose();
            });

            frame.setVisible(true);
        });
    }
}

// second window frame with "proypologismous" of 3 individual years
class SecondWindow extends JFrame {
    public SecondWindow() {
        setTitle("GoverLens");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl1 = new JLabel("Επίλεξε έτος");
        lbl1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl2 = new JLabel("προϋπολογισμού");
        lbl2.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn2023 = new JButton("2023");
        btn2023.setForeground(new Color(0, 0, 0));
        btn2023.setBackground(new Color(70, 130, 180));
        btn2023.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn2023.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn2024 = new JButton("2024");
        btn2024.setForeground(new Color(0, 0, 0));
        btn2024.setBackground(new Color(70, 130, 180));
        btn2024.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn2024.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn2025 = new JButton("2025");
        btn2025.setForeground(new Color(0, 0, 0));
        btn2025.setBackground(new Color(70, 130, 180));
        btn2025.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn2025.setAlignmentX(Component.CENTER_ALIGNMENT);

       
        panel.add(Box.createVerticalStrut(20));
        panel.add(lbl1);
        panel.add(lbl2);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btn2023);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btn2024);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btn2025);

        add(panel);
        setVisible(true);
    }
}



