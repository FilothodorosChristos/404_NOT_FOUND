package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import java.io.File;

public class BudgetManagerPro extends JFrame {
    private Image logoImage;
    private Timer animationTimer;
    private float rotationAngle = 0;
    private int fadeInProgress = 0;
    private Point mousePosition = new Point(0, 0);

    public BudgetManagerPro() {
        setTitle("BudgetManager Pro - Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        
        // Load logo image
        try {
            logoImage = ImageIO.read(new File("GoverLensLogo.jpg"));
        } catch (Exception e) {
            System.err.println("Δεν βρέθηκε το αρχείο GoverLensLogo.jpg");
        }

        // Create main panel
        MainPanel mainPanel = new MainPanel();
        add(mainPanel);

        // Mouse motion listener for parallax effect
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
                mainPanel.setMousePosition(mousePosition);
            }
        });

        // Animation timer
        animationTimer = new Timer(30, e -> {
            rotationAngle += 0.5f;
            if (rotationAngle >= 360) rotationAngle = 0;
            
            if (fadeInProgress < 100) {
                fadeInProgress += 2;
            }
            
            mainPanel.setRotationAngle(rotationAngle);
            mainPanel.setFadeProgress(fadeInProgress);
            repaint();
        });
        animationTimer.start();
    }

    class MainPanel extends JPanel {
        private float rotationAngle = 0;
        private int fadeProgress = 0;
        private Point mousePos = new Point(0, 0);
        private JButton enterButton;
        private JPanel[] featureCards;

        public MainPanel() {
            setLayout(null);
            setBackground(new Color(10, 14, 39));
            
            // Create enter button
            enterButton = createStyledButton();
            add(enterButton);
            
            // Create feature cards
            createFeatureCards();
        }

        private JButton createStyledButton() {
            JButton button = new JButton("ΕΙΣΟΔΟΣ ΣΤΟ ΣΥΣΤΗΜΑ →") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 102, 241),
                        getWidth(), getHeight(), new Color(139, 92, 246)
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    
                    // Button text
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                    int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                    g2.drawString(getText(), textX, textY);
                    
                    g2.dispose();
                }
            };
            
            button.setBounds(475, 720, 250, 50);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBounds(475, 717, 250, 50);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBounds(475, 720, 250, 50);
                }
            });
            
            button.addActionListener(e -> {
                JOptionPane.showMessageDialog(
                    BudgetManagerPro.this,
                    "🔐 Συνδέεστε στο BudgetManager Pro...\n\n" +
                    "✓ Επαλήθευση διαπιστευτηρίων\n" +
                    "✓ Φόρτωση dashboard\n" +
                    "✓ Σύνδεση με βάση δεδομένων\n\n" +
                    "Το σύστημα είναι έτοιμο!",
                    "BudgetManager Pro",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
            
            return button;
        }

        private void createFeatureCards() {
            featureCards = new JPanel[6];
            String[] titles = {
                "Αναλυτικές Αναφορές",
                "Ασφάλεια Δεδομένων",
                "Πρόβλεψη & AI",
                "Διαφάνεια",
                "Ολοκλήρωση",
                "Multi-Platform"
            };
            String[] descriptions = {
                "Πλήρης ανάλυση δαπανών και εσόδων",
                "Κρυπτογράφηση 256-bit",
                "Προβλέψεις με τεχνητή νοημοσύνη",
                "Πλήρης καταγραφή συναλλαγών",
                "Σύνδεση με υπάρχοντα συστήματα",
                "Πρόσβαση από κάθε συσκευή"
            };
            String[] emojis = {"📈", "🔒", "⚡", "🌐", "🔄", "📱"};
            
            int startX = 150;
            int startY = 500;
            int cardWidth = 300;
            int cardHeight = 120;
            int gap = 25;
            
            for (int i = 0; i < 6; i++) {
                int row = i / 3;
                int col = i % 3;
                int x = startX + col * (cardWidth + gap);
                int y = startY + row * (cardHeight + gap);
                
                featureCards[i] = createFeatureCard(titles[i], descriptions[i], emojis[i]);
                featureCards[i].setBounds(x, y, cardWidth, cardHeight);
                add(featureCards[i]);
            }
        }

        private JPanel createFeatureCard(String title, String desc, String emoji) {
            JPanel card = new JPanel() {
                private boolean isHovered = false;
                
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Background with border
                    if (isHovered) {
                        g2.setColor(new Color(99, 102, 241, 20));
                    } else {
                        g2.setColor(new Color(15, 23, 42, 128));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    
                    // Border
                    g2.setColor(new Color(99, 102, 241, isHovered ? 100 : 50));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    
                    g2.dispose();
                }
            };
            
            card.setLayout(null);
            card.setOpaque(false);
            
            // Emoji label
            JLabel emojiLabel = new JLabel(emoji);
            emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            emojiLabel.setBounds(20, 15, 50, 50);
            card.add(emojiLabel);
            
            // Title label
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(new Color(226, 232, 240));
            titleLabel.setBounds(20, 65, 260, 20);
            card.add(titleLabel);
            
            // Description label
            JLabel descLabel = new JLabel("<html>" + desc + "</html>");
            descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            descLabel.setForeground(new Color(100, 116, 139));
            descLabel.setBounds(20, 85, 260, 30);
            card.add(descLabel);
            
            // Hover effect
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JPanel)e.getSource()).putClientProperty("hover", true);
                    card.repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    ((JPanel)e.getSource()).putClientProperty("hover", false);
                    card.repaint();
                }
            });
            
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            return card;
        }

        public void setRotationAngle(float angle) {
            this.rotationAngle = angle;
        }

        public void setFadeProgress(int progress) {
            this.fadeProgress = progress;
        }

        public void setMousePosition(Point pos) {
            this.mousePos = pos;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            
            // Draw animated grid
            drawAnimatedGrid(g2, width, height);
            
            // Draw floating orbs
            drawFloatingOrbs(g2, width, height);
            
            // Draw header with logo
            drawHeader(g2, width);
            
            // Draw main icon circle
            drawMainIcon(g2, width);
            
            // Draw stats bar
            drawStatsBar(g2, width);
            
            // Draw security badges
            drawSecurityBadges(g2, width, height);
            
            // Draw footer
            drawFooter(g2, width, height);
            
            g2.dispose();
        }

        private void drawAnimatedGrid(Graphics2D g2, int width, int height) {
            g2.setColor(new Color(99, 102, 241, 13));
            g2.setStroke(new BasicStroke(1));
            
            int gridSize = 50;
            int offset = (int)(rotationAngle % gridSize);
            
            for (int x = -offset; x < width; x += gridSize) {
                g2.drawLine(x, 0, x, height);
            }
            for (int y = -offset; y < height; y += gridSize) {
                g2.drawLine(0, y, width, y);
            }
        }

        private void drawFloatingOrbs(Graphics2D g2, int width, int height) {
            float parallax1 = (mousePos.x - width / 2) * 0.01f;
            float parallax2 = (mousePos.y - height / 2) * 0.01f;
            
            // Orb 1
            RadialGradientPaint gradient1 = new RadialGradientPaint(
                -200 + parallax1, -200 + parallax2, 250,
                new float[]{0f, 1f},
                new Color[]{new Color(99, 102, 241, 76), new Color(99, 102, 241, 0)}
            );
            g2.setPaint(gradient1);
            g2.fillOval((int)(-200 + parallax1), (int)(-200 + parallax2), 500, 500);
            
            // Orb 2
            RadialGradientPaint gradient2 = new RadialGradientPaint(
                width - 150 + parallax1 * 1.5f, height - 150 + parallax2 * 1.5f, 200,
                new float[]{0f, 1f},
                new Color[]{new Color(139, 92, 246, 76), new Color(139, 92, 246, 0)}
            );
            g2.setPaint(gradient2);
            g2.fillOval((int)(width - 350 + parallax1 * 1.5f), (int)(height - 350 + parallax2 * 1.5f), 400, 400);
        }

        private void drawHeader(Graphics2D g2, int width) {
            int alpha = Math.min(255, fadeProgress * 255 / 100);
            
            // Draw logo
            if (logoImage != null) {
                int logoSize = 60;
                int logoX = width / 2 - 150;
                int logoY = 40;
                
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
                g2.drawImage(logoImage, logoX, logoY, logoSize, logoSize, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            
            // Title
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String title = "BudgetManager Pro";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, width / 2 - fm.stringWidth(title) / 2 + 40, 75);
            
            // Subtitle
            g2.setColor(new Color(148, 163, 184, alpha));
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            String subtitle = "Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού";
            fm = g2.getFontMetrics();
            g2.drawString(subtitle, width / 2 - fm.stringWidth(subtitle) / 2 + 40, 95);
        }

        private void drawMainIcon(Graphics2D g2, int width) {
            int centerX = width / 2;
            int centerY = 250;
            int radius = 160;
            
            // Outer rotating gradient border
            g2.rotate(Math.toRadians(rotationAngle), centerX, centerY);
            g2.setStroke(new BasicStroke(2));
            GradientPaint borderGradient = new GradientPaint(
                centerX - radius, centerY - radius, new Color(99, 102, 241, 128),
                centerX + radius, centerY + radius, new Color(139, 92, 246, 128)
            );
            g2.setPaint(borderGradient);
            g2.drawOval(centerX - radius - 2, centerY - radius - 2, radius * 2 + 4, radius * 2 + 4);
            g2.rotate(-Math.toRadians(rotationAngle), centerX, centerY);
            
            // Inner circle background
            RadialGradientPaint circleGradient = new RadialGradientPaint(
                centerX, centerY, radius,
                new float[]{0f, 1f},
                new Color[]{new Color(99, 102, 241, 25), new Color(139, 92, 246, 25)}
            );
            g2.setPaint(circleGradient);
            g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            
            // Border
            g2.setColor(new Color(99, 102, 241, 50));
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            
            // Main emoji icon
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            String icon = "💼";
            FontMetrics fm = g2.getFontMetrics();
            int iconX = centerX - fm.stringWidth(icon) / 2;
            int iconY = centerY + fm.getAscent() / 2 - 10;
            g2.setColor(new Color(255, 255, 255, 230));
            g2.drawString(icon, iconX, iconY);
        }

        private void drawStatsBar(Graphics2D g2, int width) {
            String[] values = {"99.9%", "24/7", "ISO"};
            String[] labels = {"Ακρίβεια", "Διαθεσιμότητα", "Πιστοποίηση"};
            
            int startX = width / 2 - 300;
            int y = 420;
            int spacing = 200;
            
            for (int i = 0; i < 3; i++) {
                int x = startX + i * spacing;
                
                // Value
                g2.setFont(new Font("Arial", Font.BOLD, 32));
                GradientPaint valueGradient = new GradientPaint(
                    x, y, new Color(99, 102, 241),
                    x + 100, y + 20, new Color(139, 92, 246)
                );
                g2.setPaint(valueGradient);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(values[i], x + 50 - fm.stringWidth(values[i]) / 2, y);
                
                // Label
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                fm = g2.getFontMetrics();
                g2.drawString(labels[i].toUpperCase(), x + 50 - fm.stringWidth(labels[i]) / 2, y + 25);
            }
        }

        private void drawSecurityBadges(Graphics2D g2, int width, int height) {
            String[] badges = {"🔐 SSL Encrypted", "✓ GDPR Compliant", "🛡️ ISO 27001", "⚙️ SOC 2 Type II"};
            int badgeWidth = 140;
            int startX = width / 2 - (badges.length * badgeWidth + (badges.length - 1) * 10) / 2;
            int y = 790;
            
            for (int i = 0; i < badges.length; i++) {
                int x = startX + i * (badgeWidth + 10);
                
                // Background
                g2.setColor(new Color(15, 23, 42, 128));
                g2.fillRoundRect(x, y, badgeWidth, 30, 15, 15);
                
                // Border
                g2.setColor(new Color(99, 102, 241, 25));
                g2.drawRoundRect(x, y, badgeWidth, 30, 15, 15);
                
                // Text
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(badges[i], x + (badgeWidth - fm.stringWidth(badges[i])) / 2, y + 19);
            }
        }

        private void drawFooter(Graphics2D g2, int width, int height) {
            g2.setColor(new Color(99, 102, 241, 25));
            g2.drawLine(200, height - 50, width - 200, height - 50);
            
            g2.setColor(new Color(71, 85, 105));
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String footer = "© 2025 BudgetManager Pro. Όλα τα δικαιώματα διατηρούνται.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(footer, width / 2 - fm.stringWidth(footer) / 2, height - 25);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            BudgetManagerPro frame = new BudgetManagerPro();
            frame.setVisible(true);
        });
    }
}