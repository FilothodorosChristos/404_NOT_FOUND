package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Modern WelcomePanel with animated background and professional design.
 * Displays the GoverLens logo, welcome message, and navigation.
 */
public class WelcomePanel extends JPanel {
    
    private final MainFrame mainFrame;
    private Timer animationTimer;
    private float rotationAngle = 0;
    private int fadeInProgress = 0;
    private Point mousePosition = new Point(0, 0);
    private JButton enterButton;
    private JPanel[] featureCards;
    
    /**
     * Constructs a WelcomePanel with modern design.
     *
     * @param mainFrame the main application frame
     */
    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(10, 14, 39));
        
        createUI();
        setupAnimations();
        setupMouseTracking();
    }
    
    /**
     * Creates all UI components.
     */
    private void createUI() {
        // Components will be positioned dynamically in paintComponent
        // Create enter button
        enterButton = createStyledButton();
        add(enterButton);
        
        // Create feature cards
        createFeatureCards();
        
        // Add component listener to reposition on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });
    }
    
    /**
     * Repositions all components to center of panel.
     */
    private void repositionComponents() {
        int width = getWidth();
        
        // Center button - moved up
        int buttonWidth = 250;
        int buttonHeight = 50;
        enterButton.setBounds((width - buttonWidth) / 2, 480, buttonWidth, buttonHeight);
        
        // Reposition feature cards - 3 cards in one row
        int cardWidth = 300;
        int cardHeight = 120;
        int gap = 25;
        
        // Calculate start position to center all 3 cards
        int totalWidth = 3 * cardWidth + 2 * gap;
        int startX = (width - totalWidth) / 2;
        int startY = 560;
        
        // Single row: 3 cards
        for (int i = 0; i < 3; i++) {
            int x = startX + i * (cardWidth + gap);
            featureCards[i].setBounds(x, startY, cardWidth, cardHeight);
        }
    }
    
    /**
     * Creates the main styled button.
     */
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
        
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.setBounds(btn.getX(), btn.getY() - 3, btn.getWidth(), btn.getHeight());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                repositionComponents();
            }
        });
        
        button.addActionListener(e -> 
            mainFrame.showPanel(MainFrame.PROJECT_SELECTION)
        );
        return button;
    }
    
    /**
     * Creates the 3 feature cards.
     */
    private void createFeatureCards() {
        featureCards = new JPanel[3];
        String[] titles = {
            "Διαγράμματα",
            "Έγκυρα Δεδομένα",
            "Δυνατότητα Επεξεργασίας"
        };
        String[] descriptions = {
            "Πλήρης ανάλυση δαπανών και εσόδων",
            "Κρυπτογράφηση 256-bit",
            "Παρουσίαση εσόδων εξόδων"
        };
        String[] emojis = {"📈", "🔒", "⚡"};
        
        // Create all cards
        for (int i = 0; i < 3; i++) {
            featureCards[i] = createFeatureCard(titles[i], descriptions[i], emojis[i]);
            add(featureCards[i]);
        }
        
        // Position them
        repositionComponents();
    }
    
    /**
     * Creates a single feature card.
     */
    private JPanel createFeatureCard(String title, String desc, String emoji) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
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
        
        // Emoji
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        emojiLabel.setBounds(20, 15, 50, 50);
        card.add(emojiLabel);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(226, 232, 240));
        titleLabel.setBounds(20, 65, 260, 20);
        card.add(titleLabel);
        
        // Description
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(100, 116, 139));
        descLabel.setBounds(20, 85, 260, 30);
        card.add(descLabel);
        
        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JPanel source = (JPanel) e.getSource();
                source.putClientProperty("hover", true);
                card.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                JPanel source = (JPanel) e.getSource();
                source.putClientProperty("hover", false);
                card.repaint();
            }
        });
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return card;
    }
    
    /**
     * Sets up animation timer.
     */
    private void setupAnimations() {
        animationTimer = new Timer(30, e -> {
            rotationAngle += 0.5f;
            if (rotationAngle >= 360) rotationAngle = 0;
            
            if (fadeInProgress < 100) {
                fadeInProgress += 2;
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    /**
     * Sets up mouse tracking for parallax effect.
     */
    private void setupMouseTracking() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });
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
        
        // Draw header with logo (larger and more prominent)
        drawHeader(g2, width);
        
        // Draw stats bar
        drawStatsBar(g2, width);
        
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
        float parallax1 = (mousePosition.x - width / 2f) * 0.01f;
        float parallax2 = (mousePosition.y - height / 2f) * 0.01f;
        
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
        int alpha = Math.min(255, fadeInProgress * 255 / 100);
        
        // Draw logo (moved up)
        Image logo = mainFrame.getLogoImage();
        if (logo != null) {
            int logoSize = 150;
            int logoX = width / 2 - logoSize / 2;
            int logoY = 80;
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
            
            // Create circular clip
            Shape oldClip = g2.getClip();
            g2.setClip(new Ellipse2D.Double(logoX, logoY, logoSize, logoSize));
            g2.drawImage(logo, logoX, logoY, logoSize, logoSize, this);
            g2.setClip(oldClip);
            
            // Draw border
            g2.setColor(new Color(99, 102, 241, alpha));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(logoX, logoY, logoSize, logoSize);
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        
        // Title (moved up)
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "GoverLens Pro";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, width / 2 - fm.stringWidth(title) / 2, 270);
        
        // Subtitle (moved up)
        g2.setColor(new Color(148, 163, 184, alpha));
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String subtitle = "Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού";
        fm = g2.getFontMetrics();
        g2.drawString(subtitle, width / 2 - fm.stringWidth(subtitle) / 2, 300);
    }
    
    private void drawStatsBar(Graphics2D g2, int width) {
        String[] values = {"99.9%", "24/7", "ISO"};
        String[] labels = {"Ακρίβεια", "Διαθεσιμότητα", "Πιστοποίηση"};
        
        int startX = width / 2 - 300;
        int y = 380;
        int spacing = 200;
        
        for (int i = 0; i < 3; i++) {
            int x = startX + i * spacing;
            
            // Value
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            GradientPaint valueGradient = new GradientPaint(
                x, y, new Color(99, 102, 241),
                x + 100, y + 20, new Color(139, 92, 246)
            );
            g2.setPaint(valueGradient);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(values[i], x + 50 - fm.stringWidth(values[i]) / 2, y);
            
            // Label
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            fm = g2.getFontMetrics();
            g2.drawString(labels[i].toUpperCase(), x + 50 - fm.stringWidth(labels[i]) / 2, y + 20);
        }
    }
    
    private void drawFooter(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(71, 85, 105));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        String footer = "© 2025 GoverLens Pro. Όλα τα δικαιώματα διατηρούνται.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(footer, width / 2 - fm.stringWidth(footer) / 2, height - 15);
    }
}