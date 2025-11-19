package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;

import java.awt.*;
import java.awt.geom.Ellipse2D;

// Custom JPanel με background εικόνα
class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        this.backgroundImage = new ImageIcon(imagePath).getImage();
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class FirstGuiClass {

    // Η κλάση RoundedBorder ως static nested class
    static class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder() {
            this.radius = 10;
        }

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.BLACK); // Χρώμα περιγράμματος
            g2.setStroke(new BasicStroke(2)); // Πάχος περιγράμματος
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // MAIN FRAME
            JFrame frame = new JFrame("Καλώς Ήρθατε στην GoverLens.");
            frame.setSize(1000, 800);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(new ImageIcon("GoverLensLogo.jpg").getImage());

            // CARD LAYOUT
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // PANEL 1 - WELCOME (με κύκλο και λογότυπο)
            JPanel panel1 = new JPanel(new BorderLayout()) {
                Image image = new ImageIcon("GoverLensLogo.jpg").getImage();
                Image background = new ImageIcon("BackroundPhoto.jpg").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Background εικόνα
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int diameter = (int) (Math.min(getWidth(), getHeight()) * 0.4);

                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 3;

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

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            JLabel paragraph = new JLabel("Καλώς ήρθατε στην GoverLens! ");
            paragraph.setFont(new Font("Tahoma", Font.PLAIN, 50));
            paragraph.setForeground(Color.WHITE);
            paragraph.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel.add(Box.createVerticalStrut(40)); // λίγο κενό πάνω
            textPanel.add(paragraph);
            panel1.add(textPanel, BorderLayout.CENTER);

            JPanel bottomTextPanel = new JPanel();
            bottomTextPanel.setOpaque(false);
            bottomTextPanel.setLayout(new BoxLayout(bottomTextPanel, BoxLayout.Y_AXIS));

            JLabel paragraph2 = new JLabel("________________________________________________________");

            paragraph2.setFont(new Font("Tahoma", Font.PLAIN, 24));
            paragraph2.setForeground(Color.WHITE);
            paragraph2.setAlignmentX(Component.CENTER_ALIGNMENT);

            bottomTextPanel.add(Box.createVerticalStrut(80));

            bottomTextPanel.add(paragraph2);

            JLabel paragraph3 = new JLabel("Περισσότερη διαφάνεια , λιγότερη δυσκολία!");
            paragraph3.setFont(new Font("Tahoma", Font.PLAIN, 24));
            paragraph3.setForeground(Color.WHITE);
            paragraph3.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel paragraph4 = new JLabel("Μάθε που πηγαίνει κάθε ευρώ και επεξεργάσου τα δεδομένα σαν υπουργός .");
            paragraph4.setFont(new Font("Tahoma", Font.PLAIN, 24));
            paragraph4.setForeground(Color.WHITE);
            paragraph4.setAlignmentX(Component.CENTER_ALIGNMENT);

            bottomTextPanel.add(Box.createVerticalStrut(40));
            bottomTextPanel.add(paragraph3);
            bottomTextPanel.add(Box.createVerticalStrut(10));
            bottomTextPanel.add(paragraph4);
            bottomTextPanel.add(Box.createVerticalStrut(80));

            JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel1.setOpaque(false);
            JButton next1 = new JButton("Επόμενο >");

            // ⭐ ΝΕΕΣ ΓΡΑΜΜΕΣ ΓΙΑ ΑΥΞΗΣΗ ΜΕΓΕΘΟΥΣ
            Dimension nextBtnSize = new Dimension(180, 40);
            next1.setPreferredSize(nextBtnSize);
            next1.setMinimumSize(nextBtnSize);
            next1.setMaximumSize(nextBtnSize);

            buttonPanel1.add(next1);
            JPanel southWrapper = new JPanel();
            southWrapper.setLayout(new BorderLayout());
            southWrapper.setOpaque(false);

            southWrapper.add(bottomTextPanel, BorderLayout.CENTER);
            southWrapper.add(buttonPanel1, BorderLayout.SOUTH);

            panel1.add(southWrapper, BorderLayout.SOUTH);

            // PANEL 2 -- OLD OR NEW PROJECT

            JPanel panel2 = new JPanel(new BorderLayout()) {
                Image image = new ImageIcon("GoverLensLogo.jpg").getImage();
                Image background = new ImageIcon("BackroundPhoto.jpg").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Background εικόνα
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int diameter = (int) (Math.min(getWidth(), getHeight()) * 0.4);

                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 3;

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

            // ... (textPanel2 setup remains the same, but it's not strictly necessary for
            // the layout fix)
            JPanel textPanel2 = new JPanel();
            textPanel2.setOpaque(false);
            textPanel2.setLayout(new BoxLayout(textPanel2, BoxLayout.Y_AXIS));

            JLabel paragraph5 = new JLabel("Καλώς ήρθατε στην GoverLens! ");
            paragraph5.setFont(new Font("Tahoma", Font.PLAIN, 50));
            paragraph5.setForeground(Color.WHITE);
            paragraph5.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel2.add(Box.createVerticalStrut(40)); // λίγο κενό πάνω
            textPanel2.add(paragraph5);
            panel2.add(textPanel2, BorderLayout.CENTER);

            // --- BUTTON STYLE ---
            Font btnFont = new Font("Arial", Font.BOLD, 24);
            Dimension btnSize = new Dimension(350, 90);

            // Create buttons
            JButton oldButton = new JButton("Συνέχεια Προσομοίωσης");
            JButton newButton = new JButton("Καινούργια Προσομοίωση");

            JButton[] buttons = { oldButton, newButton };

            for (JButton b : buttons) {
                b.setBorder(new RoundedBorder(15));
                b.setFocusPainted(false);
                b.setForeground(new Color(0, 0, 128));
                b.setBackground(Color.WHITE);
                b.setFont(btnFont);
                b.setPreferredSize(btnSize);
                b.setMaximumSize(btnSize);
                b.setMinimumSize(btnSize);
                b.setContentAreaFilled(true);
                b.setOpaque(true);
            }

            // --- ACTIONS ---
            // Και τα 2 πάνε στο panel3
            oldButton.addActionListener(e -> cardLayout.show(mainPanel, "panel3"));
            newButton.addActionListener(e -> cardLayout.show(mainPanel, "panel3"));

            // --- UNIFIED SOUTH LAYOUT (PREV LEFT + OLD/NEW BUTTONS CENTERED) ---

            // Panel for the two main buttons, ensuring they are centered
            JPanel bigButtonsPanel = new JPanel();
            bigButtonsPanel.setOpaque(false);
            bigButtonsPanel.setLayout(new BoxLayout(bigButtonsPanel, BoxLayout.X_AXIS));

            bigButtonsPanel.add(Box.createHorizontalGlue());
            bigButtonsPanel.add(oldButton);
            bigButtonsPanel.add(Box.createHorizontalStrut(40));
            bigButtonsPanel.add(newButton);
            bigButtonsPanel.add(Box.createHorizontalGlue());

            // Create the PREVIOUS button
            JButton prev1 = new JButton("< Προηγούμενο");
            prev1.addActionListener(e -> cardLayout.show(mainPanel, "panel1"));

         
            prev1.setPreferredSize(new Dimension(150, 40));
            prev1.setMinimumSize(new Dimension(150, 40));
            prev1.setMaximumSize(new Dimension(150, 40));

            // Wrapper to hold the Previous button on the left (BOTTOM-LEFT ALIGNMENT)
            JPanel prevWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
            prevWrapper.setOpaque(false);
            prevWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));
            prevWrapper.add(prev1);

            // Outer panel for the SOUTH region, using BorderLayout
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setOpaque(false);

            // Add the main action buttons to the NORTH of the south panel (centered high)
            southPanel.add(bigButtonsPanel, BorderLayout.NORTH);
            // Add the Previous button to the SOUTH of the south panel (bottom-left)
            southPanel.add(prevWrapper, BorderLayout.SOUTH);

            panel2.add(southPanel, BorderLayout.SOUTH);

            // PANEL 3 - YEAR SELECTION

            JPanel panel3 = new ImagePanel("BackroundPhoto.jpg");
            panel3.setLayout(new BorderLayout());

            JPanel yearsPanel = new JPanel();
            yearsPanel.setLayout(new BoxLayout(yearsPanel, BoxLayout.Y_AXIS));
            yearsPanel.setOpaque(false);

            JLabel lbl1 = new JLabel("Επίλεξε έτος προυπολογισμού:");
            lbl1.setForeground(Color.WHITE);
            lbl1.setFont(new Font("Tahoma", Font.PLAIN, 30));
            lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btn2023 = new JButton("2023");
            JButton btn2024 = new JButton("2024");
            JButton btn2025 = new JButton("2025");

            JButton[] yearButtons = { btn2023, btn2024, btn2025 };
            for (JButton btn : yearButtons) {
                btn.setForeground(new Color(0, 0, 128));
                btn.setBackground(new Color(255, 255, 255));
                btn.setFont(new Font("Tahoma", Font.BOLD, 28));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(300, 70));
                btn.setBorder(new RoundedBorder(30));
                btn.setFocusPainted(true);
                btn.addActionListener(e -> cardLayout.show(mainPanel, "panel4"));
            }

            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(lbl1);

            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(btn2023);
            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(btn2024);
            yearsPanel.add(Box.createVerticalStrut(20));
            yearsPanel.add(btn2025);

            panel3.add(yearsPanel, BorderLayout.CENTER);

            JPanel bottomPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel4.setOpaque(false);
            bottomPanel4.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
            JButton prev2 = new JButton("< Προηγούμενο");
            prev2.addActionListener(e -> cardLayout.show(mainPanel, "panel2"));

       
            prev2.setPreferredSize(new Dimension(150, 40));
            prev2.setMinimumSize(new Dimension(150, 40));
            prev2.setMaximumSize(new Dimension(150, 40));

            bottomPanel4.add(prev2);
            panel3.add(bottomPanel4, BorderLayout.SOUTH);

            // PANEL 4 - USER ACTIONS

            JPanel panel4 = new ImagePanel("BackroundPhoto.jpg");
            panel4.setLayout(new BorderLayout());

            JLabel label = new JLabel("Παρακαλώ επιλέξτε διαδικασία:");
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.PLAIN, 40));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));

            JButton viewButton = new JButton("Προβολή");
            JButton editButton = new JButton("Επεξεργασία");
            JButton diagramButton = new JButton("Διαγράμματα");
            JButton comparationButton = new JButton("Σύγκριση");

            JButton[] allButtons = { viewButton, editButton, diagramButton, comparationButton };

            for (JButton b : allButtons) {
                b.setBorder(new RoundedBorder(30));
                b.setFocusPainted(false);
                b.setForeground(new Color(0, 0, 128));
                b.setFont(btnFont);
                b.setMaximumSize(btnSize);

                b.setPreferredSize(btnSize); // Ορίζει προτεινόμενο μέγεθος
                b.setMinimumSize(btnSize); // Ορίζει ελάχιστο μέγεθος
                b.setContentAreaFilled(true); // Να γεμίζει το background
                b.setOpaque(true);

            }
            JPanel buttonsGridPanel = new JPanel(new GridBagLayout());
            buttonsGridPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(20, 30, 20, 30);

            gbc.gridx = 0;
            gbc.gridy = 0;
            buttonsGridPanel.add(viewButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            buttonsGridPanel.add(editButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            buttonsGridPanel.add(diagramButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            buttonsGridPanel.add(comparationButton, gbc);

            // Panel για να κεντράρει το label και τα κουμπιά ΟΡΙΖΟΝΤΙΑ
            // αλλά παραμένουν ΠΑΝΩ
            JPanel topCenterPanel = new JPanel();
            topCenterPanel.setLayout(new BoxLayout(topCenterPanel, BoxLayout.Y_AXIS));
            topCenterPanel.setOpaque(false);

            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonsGridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            topCenterPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            topCenterPanel.add(label);
            topCenterPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            topCenterPanel.add(buttonsGridPanel);

            panel4.add(topCenterPanel, BorderLayout.NORTH);

            JPanel bottomPanel5 = new JPanel(new BorderLayout());
            bottomPanel5.setOpaque(false);
            bottomPanel5.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

            JButton prev4 = new JButton("< Προηγούμενο");
            prev4.addActionListener(e -> cardLayout.show(mainPanel, "panel3"));

            // ------------------------------------------------------------------
            // ⭐ ΔΙΟΡΘΩΣΗ: ΑΦΑΙΡΟΥΜΕ ΤΟ CUSTOM STYLING ΓΙΑ ΝΑ ΕΙΝΑΙ DEFAULT
            // ------------------------------------------------------------------
            // prev4.setBorder(new RoundedBorder(10)); // <-- ΑΦΑΙΡΕΘΗΚΕ
            // prev4.setFocusPainted(false); // <-- ΑΦΑΙΡΕΘΗΚΕ
            // prev4.setForeground(Color.WHITE); // <-- ΑΦΑΙΡΕΘΗΚΕ
            // prev4.setBackground(new Color(0, 0, 128)); // <-- ΑΦΑΙΡΕΘΗΚΕ
            // prev4.setFont(new Font("Arial", Font.BOLD, 18));
            prev4.setPreferredSize(new Dimension(150, 40));
            prev4.setMinimumSize(new Dimension(150, 40));
            prev4.setMaximumSize(new Dimension(150, 40));

            bottomPanel5.add(prev4, BorderLayout.WEST);

            panel4.add(bottomPanel5, BorderLayout.SOUTH);

            mainPanel.add(panel1, "panel1");
            mainPanel.add(panel2, "panel2");
            mainPanel.add(panel3, "panel3");
            mainPanel.add(panel4, "panel4");

            frame.add(mainPanel);
            frame.setVisible(true);

            cardLayout.show(mainPanel, "panel1");

            next1.addActionListener(e -> cardLayout.show(mainPanel, "panel2"));

        });
    }
}