package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Modern ActionSelectionPanel with animated background matching WelcomePanel aesthetic.
 * Allows the user to select an action: View, Edit, Charts, or Comparison.
 */
public class ActionSelectionPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private Timer animationTimer;
    private float rotationAngle = 0;
    private Point mousePosition = new Point(0, 0);
    private JButton[] actionButtons;
    
    /**
     * Constructs an ActionSelectionPanel with modern design.
     *
     * @param mainFrame the main application frame
     */
    public ActionSelectionPanel(MainFrame mainFrame) {
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
        // Create action buttons
        createActionButtons();
        
        // Create back button
        createBackButton();
    }
    
    /**
     * Creates the 4 action buttons in a 2x2 grid.
     */
    private void createActionButtons() {
        actionButtons = new JButton[4];
        String[] titles = {"Προβολή", "Επεξεργασία", "Διαγράμματα", "Σύγκριση"};
        String[] icons = {"👁️", "✏️", "📊", "⚖️"};
        String[] descriptions = {
            "Προβολή προϋπολογισμού",
            "Επεξεργασία δεδομένων",
            "Οπτικοποίηση δεδομένων",
            "Σύγκριση ετών"
        };
        
        int startX = 300;
        int startY = 350;
        int buttonWidth = 280;
        int buttonHeight = 140;
        int gapX = 40;
        int gapY = 30;
        
        for (int i = 0; i < 4; i++) {
            int row = i / 2;
            int col = i % 2;
            int x = startX + col * (buttonWidth + gapX);
            int y = startY + row * (buttonHeight + gapY);
            
            actionButtons[i] = createActionButton(titles[i], icons[i], descriptions[i]);
            actionButtons[i].setBounds(x, y, buttonWidth, buttonHeight);
            add(actionButtons[i]);
        }
    }
    
    /**
     * Creates a single action button with modern styling.
     */
    private JButton createActionButton(String title, String icon, String description) {
        JButton button = new JButton() {
            private boolean isHovered = false;
            private float hoverProgress = 0f;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Background with gradient
                if (isHovered) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 102, 241, 30),
                        getWidth(), getHeight(), new Color(139, 92, 246, 30)
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(new Color(15, 23, 42, 180));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border
                g2.setColor(new Color(99, 102, 241, isHovered ? 150 : 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                
                // Icon
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                FontMetrics fmIcon = g2.getFontMetrics();
                int iconX = (getWidth() - fmIcon.stringWidth(icon)) / 2;
                g2.setColor(new Color(255, 255, 255, 230));
                g2.drawString(icon, iconX, 55);
                
                // Title
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.setColor(new Color(226, 232, 240));
                FontMetrics fmTitle = g2.getFontMetrics();
                int titleX = (getWidth() - fmTitle.stringWidth(title)) / 2;
                g2.drawString(title, titleX, 90);
                
                // Description
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.setColor(new Color(148, 163, 184));
                FontMetrics fmDesc = g2.getFontMetrics();
                int descX = (getWidth() - fmDesc.stringWidth(description)) / 2;
                g2.drawString(description, descX, 115);
                
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
                button.putClientProperty("hover", true);
                button.setBounds(button.getX(), button.getY() - 3, button.getWidth(), button.getHeight());
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.putClientProperty("hover", false);
                int row = getButtonIndex(button) / 2;
                int col = getButtonIndex(button) % 2;
                int x = 300 + col * 320;
                int y = 350 + row * 170;
                button.setBounds(x, y, button.getWidth(), button.getHeight());
                button.repaint();
            }
        });
        
        button.addActionListener(e -> handleAction(title));
        
        return button;
    }
    
    /**
     * Gets the index of a button in the actionButtons array.
     */
    private int getButtonIndex(JButton button) {
        for (int i = 0; i < actionButtons.length; i++) {
            if (actionButtons[i] == button) return i;
        }
        return 0;
    }
    
    /**
     * Creates the back button.
     */
    private void createBackButton() {
        JButton backButton = new JButton("← Προηγούμενο") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(new Color(15, 23, 42, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Border
                g2.setColor(new Color(99, 102, 241, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Text
                g2.setColor(new Color(226, 232, 240));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        
        backButton.setBounds(30, 820, 150, 40);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.YEAR_SELECTION));
        
        add(backButton);
    }
    
    /**
     * Handles action button clicks.
     */
    private void handleAction(String action) {
        String selectedYear = mainFrame.getSelectedYear();
        
        if (action.equals("Διαγράμματα")) {
            FinanceChartPanel chartPanel = FinanceChartPanel.createPanel(selectedYear);
            
            JFrame chartFrame = new JFrame("Διαγράμματα Έτους " + selectedYear);
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setSize(900, 700);
            chartFrame.setLocationRelativeTo(null);
            chartFrame.add(chartPanel);
            chartFrame.setVisible(true);
            
        } else if (action.equals("Προβολή")) {
            mainFrame.showBudgetView();
            
        } else if (action.equals("Επεξεργασία")) {
            mainFrame.showDataEditor("cashflow");
            
        } else if (action.equals("Σύγκριση")) {
            JOptionPane.showMessageDialog(
                this,
                "Η λειτουργία 'Σύγκριση' δεν έχει υλοποιηθεί ακόμα.",
                "Υπό Κατασκευή",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Sets up animation timer.
     */
    private void setupAnimations() {
        animationTimer = new Timer(30, e -> {
            rotationAngle += 0.5f;
            if (rotationAngle >= 360) rotationAngle = 0;
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
        
        // Draw header
        drawHeader(g2, width);
        
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
        // Draw logo
        Image logo = mainFrame.getLogoImage();
        if (logo != null) {
            int logoSize = 80;
            int logoX = width / 2 - 120;
            int logoY = 60;
            
            // Create circular clip
            Shape oldClip = g2.getClip();
            g2.setClip(new Ellipse2D.Double(logoX, logoY, logoSize, logoSize));
            g2.drawImage(logo, logoX, logoY, logoSize, logoSize, this);
            g2.setClip(oldClip);
            
            // Draw border
            g2.setColor(new Color(99, 102, 241));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(logoX, logoY, logoSize, logoSize);
        }
        
        // Title
        g2.setColor(new Color(255, 255, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String title = "GoverLens Pro";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, width / 2 - fm.stringWidth(title) / 2 + 30, 95);
        
        // Subtitle
        g2.setColor(new Color(148, 163, 184));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String subtitle = "Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού";
        fm = g2.getFontMetrics();
        g2.drawString(subtitle, width / 2 - fm.stringWidth(subtitle) / 2 + 30, 115);
        
        // Main instruction
        g2.setColor(new Color(226, 232, 240));
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String instruction = "Επιλέξτε Διαδικασία";
        fm = g2.getFontMetrics();
        g2.drawString(instruction, width / 2 - fm.stringWidth(instruction) / 2, 220);
        
        // Year indicator
        String selectedYear = mainFrame.getSelectedYear();
        if (selectedYear != null) {
            g2.setColor(new Color(99, 102, 241));
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String yearText = "Έτος: " + selectedYear;
            fm = g2.getFontMetrics();
            g2.drawString(yearText, width / 2 - fm.stringWidth(yearText) / 2, 260);
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