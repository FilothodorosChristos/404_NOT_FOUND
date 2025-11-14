package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class FirstGuiClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // MAIN FRAME SETUP
            JFrame frame = new JFrame("Καλώς Ήρθατε στην GoverLens.");
            frame.setSize(1000, 800);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // SET APP ICON
            frame.setIconImage(new ImageIcon("GoverLensLogo.jpg").getImage());

            // CARD LAYOUT FOR PANELS
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);
            
            // PANEL 1 (WELCOME)
            JPanel panel1 = new JPanel(new BorderLayout()) {
                Image image = new ImageIcon("GoverLensLogo.jpg").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int diameter = (int)(Math.min(getWidth(), getHeight()) * 0.7); // μικρότερος κύκλος

                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;

                    Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
                    g2.setClip(clip);
                    g2.drawImage(image, x, y, diameter, diameter, this);

                    g2.setClip(null);
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x, y, diameter, diameter);
                    g2.dispose();
                }
            };
                    panel1.setBackground(Color.WHITE);

                    JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton next1 = new JButton("Επόμενο >");
                    buttonPanel1.add(next1);
                    panel1.add(buttonPanel1, BorderLayout.SOUTH);

            // PANEL 2 (YEAR SELECTION) 
            JPanel panel2 = new JPanel(new BorderLayout());

            JPanel yearsPanel = new JPanel();
            yearsPanel.setLayout(new BoxLayout(yearsPanel, BoxLayout.Y_AXIS));
            yearsPanel.setOpaque(false);

            JLabel lbl1 = new JLabel("Επίλεξε έτος");
            lbl1.setFont(new Font("Tahoma", Font.PLAIN, 30));
            lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lbl2 = new JLabel("προϋπολογισμού");
            lbl2.setFont(new Font("Tahoma", Font.PLAIN, 30));
            lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btn2023 = new JButton("2023");
            JButton btn2024 = new JButton("2024");
            JButton btn2025 = new JButton("2025");

            JButton[] yearButtons = {btn2023, btn2024, btn2025};
            for (JButton btn : yearButtons) {
                btn.setForeground(new Color(0, 0, 0));
                btn.setBackground(new Color(70, 130, 180));
                btn.setFont(new Font("Tahoma", Font.BOLD, 28));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(300, 100));
                btn.addActionListener(e -> cardLayout.show(mainPanel, "panel3"));
            }

            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(lbl1);
            yearsPanel.add(lbl2);
            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(btn2023);
            yearsPanel.add(Box.createVerticalStrut(10));
            yearsPanel.add(btn2024);
            yearsPanel.add(Box.createVerticalStrut(10));
            yearsPanel.add(btn2025);

            panel2.add(yearsPanel, BorderLayout.CENTER);

            // BUTTON BACK PANEL 2
            JPanel bottomPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel2.setOpaque(false);
            bottomPanel2.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
            JButton prev2 = new JButton("< Προηγούμενο");
            prev2.addActionListener(e -> cardLayout.show(mainPanel, "panel1"));
            bottomPanel2.add(prev2);
            panel2.add(bottomPanel2, BorderLayout.SOUTH);

            //  PANEL 3 (USER ACTIONS) 
            JPanel panel3 = new JPanel(new BorderLayout());
            panel3.setBackground(new Color(30, 82, 74));

            JLabel label = new JLabel("Παρακαλώ επιλέξτε διαδικασία:");
            label.setForeground(new Color(119, 153, 204));
            label.setFont(new Font("Arial", Font.PLAIN, 40));
            label.setVerticalAlignment(JLabel.TOP);
            label.setBorder(BorderFactory.createEmptyBorder(100, 20, 0, 0));

            JButton viewButton = new JButton("Προβολή");
            JButton editButton = new JButton("Επεξεργασία");
            viewButton.setFont(new Font("Arial", Font.BOLD, 40));
            editButton.setFont(new Font("Arial", Font.BOLD, 40));

            JPanel actionButtonsPanel = new JPanel();
            actionButtonsPanel.setLayout(new BoxLayout(actionButtonsPanel, BoxLayout.Y_AXIS));
            actionButtonsPanel.setOpaque(false);
            viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            editButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            actionButtonsPanel.add(viewButton);
            actionButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            actionButtonsPanel.add(editButton);

            JPanel labelWithButtonsPanel = new JPanel();
            labelWithButtonsPanel.setLayout(new BoxLayout(labelWithButtonsPanel, BoxLayout.Y_AXIS));
            labelWithButtonsPanel.setOpaque(false);
            labelWithButtonsPanel.add(label);
            labelWithButtonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            labelWithButtonsPanel.add(actionButtonsPanel);

            panel3.add(labelWithButtonsPanel, BorderLayout.WEST);

            JPanel bottomPanel3 = new JPanel(new BorderLayout());
            bottomPanel3.setOpaque(false);
            bottomPanel3.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
            JButton prev3 = new JButton("< Προηγούμενο");
            prev3.addActionListener(e -> cardLayout.show(mainPanel, "panel2"));
            
            bottomPanel3.add(prev3, BorderLayout.WEST); // αριστερά

            panel3.add(bottomPanel3, BorderLayout.SOUTH);

            // ADD PANELS TO MAIN PANEL 
            mainPanel.add(panel1, "panel1");
            mainPanel.add(panel2, "panel2");
            mainPanel.add(panel3, "panel3");

            frame.add(mainPanel);
            frame.setVisible(true);

            // SHOW FIRST PANEL
            cardLayout.show(mainPanel, "panel1");

            // BUTTON NEXT PANEL 1
            next1.addActionListener(e -> cardLayout.show(mainPanel, "panel2"));
        });
    }
}      