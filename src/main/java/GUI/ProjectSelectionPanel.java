package GUI;

import javax.swing.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import service.SimulationService;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


/**
 * Modern ActionSelectionPanel with unified aesthetic matching WelcomePanel.
 * Allows the user to select: Continue Simulation or Start New.
 */
public class ProjectSelectionPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private Timer animationTimer;
    private float rotationAngle = 0;
    private int fadeInProgress = 0;
    private Point mousePosition = new Point(0, 0);
    private JButton continueButton;
    private JButton newButton;
    private JButton backButton;
    
    /**
     * Constructs an ActionSelectionPanel with modern design.
     *
     * @param mainFrame the main application frame
     */

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Necessary for GUI communication")

    public ProjectSelectionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(10, 14, 39));
        
        createUI();
        setupAnimations();
        setupMouseTracking();
        
        // Add component listener to reposition on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });
    }
    
    /**
     * Creates all UI components.
     */
    private void createUI() {
        createActionButtons();
        createBackButton();
        repositionComponents();
    }
    
    /**
     * Repositions all components to center of panel.
     */
    private void repositionComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // Position action buttons side by side
        int buttonWidth = 320;
        int buttonHeight = 180;
        int gap = 50;
        
        // Calculate center position
        int totalWidth = 2 * buttonWidth + gap;
        int startX = (width - totalWidth) / 2;
        int startY = 400;
        
        continueButton.setBounds(startX, startY, buttonWidth, buttonHeight);
        newButton.setBounds(startX + buttonWidth + gap, startY, buttonWidth, buttonHeight);
        
        // Position back button
        backButton.setBounds(30, height - 70, 150, 40);
    }
    
    /**
     * Creates the 2 action buttons.
     */
    private void createActionButtons() {
        continueButton = createActionButton(
            "Συνέχεια Προσομοίωσης", 
            "▶️", 
            "Συνέχιση υπάρχοντος έργου"
        );
        
        newButton = createActionButton(
            "Εκκίνηση Νέας", 
            "🆕", 
            "Δημιουργία νέου έργου"
        );
        
        add(continueButton);
        add(newButton);
    }
    
    /**
     * Creates a single action button with unified styling.
     */
    private JButton createActionButton(String title, String icon, String description) {
        JButton button = new JButton() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Background with gradient (same as WelcomePanel feature cards)
                if (isHovered) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 102, 241, 30),
                        getWidth(), getHeight(), new Color(139, 92, 246, 30)
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(new Color(15, 23, 42, 128));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Border (matching WelcomePanel cards)
                g2.setColor(new Color(99, 102, 241, isHovered ? 100 : 50));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                // Icon
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
                FontMetrics fmIcon = g2.getFontMetrics();
                int iconX = (getWidth() - fmIcon.stringWidth(icon)) / 2;
                g2.setColor(new Color(255, 255, 255, 230));
                g2.drawString(icon, iconX, 70);
                
                // Title
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(new Color(226, 232, 240));
                FontMetrics fmTitle = g2.getFontMetrics();
                int titleX = (getWidth() - fmTitle.stringWidth(title)) / 2;
                g2.drawString(title, titleX, 115);
                
                // Description
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.setColor(new Color(100, 116, 139));
                FontMetrics fmDesc = g2.getFontMetrics();
                int descX = (getWidth() - fmDesc.stringWidth(description)) / 2;
                g2.drawString(description, descX, 145);
                
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
                btn.putClientProperty("hover", true);
                btn.setBounds(btn.getX(), btn.getY() - 5, btn.getWidth(), btn.getHeight());
                btn.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.putClientProperty("hover", false);
                repositionComponents();
                btn.repaint();
            }
        });
        
        button.addActionListener(e -> handleAction(title));
        
        return button;
    }
    
    /**
     * Creates the back button with unified styling.
     */
    private void createBackButton() {
        backButton = new JButton("← Προηγούμενο") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                
                g2.setColor(new Color(15, 23, 42, 128));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                
                g2.setColor(new Color(99, 102, 241, 80));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                
                g2.setColor(new Color(226, 232, 240));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.WELCOME));
        
        add(backButton);
    }
    
    /**
     * Handles action button clicks.
     */
private void handleAction(String action) {
    if (action.equals("Εκκίνηση Νέας")) {
        /**  Εμφάνιση confirmation dialog */
        int response = JOptionPane.showConfirmDialog(
            this,
            "Θέλετε να ξεκινήσετε νέα προσομοίωση?\n" +
            "ΠΡΟΣΟΧΗ: Όλες οι τρέχουσες αλλαγές θα χαθούν!",
            "Επιβεβαίωση Νέας Προσομοίωσης",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            try {
                /**  Καλούμε τη μέθοδο για νέα προσομοίωση*/
                SimulationService.startNewSimulation();
                
                /**  Εμφάνιση μηνύματος επιτυχίας */
                JOptionPane.showMessageDialog(
                    this,
                    "Η νέα προσομοίωση ξεκίνησε επιτυχώς!\n" +
                    "Τα δεδομένα επαναφέρθηκαν στην αρχική τους κατάσταση.",
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                /** Μεταβαίνουμε στην επιλογή έτους */
                mainFrame.showPanel(MainFrame.YEAR_SELECTION);
                
            } catch (Exception ex) {
                /**  Σε περίπτωση σφάλματος */
                JOptionPane.showMessageDialog(
                    this,
                    "Σφάλμα κατά την εκκίνηση νέας προσομοίωσης:\n" + ex.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    } else {
        /**  Για το κουμπί "Συνέχεια Προσομοίωσης" */
        mainFrame.showPanel(MainFrame.YEAR_SELECTION);
    }
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
        
        // Draw animated grid (same as WelcomePanel)
        drawAnimatedGrid(g2, width, height);
        
        // Draw floating orbs (same as WelcomePanel)
        drawFloatingOrbs(g2, width, height);
        
        // Draw header with logo
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
        
        // Orb 1 (top-left)
        RadialGradientPaint gradient1 = new RadialGradientPaint(
            -200 + parallax1, -200 + parallax2, 250,
            new float[]{0f, 1f},
            new Color[]{new Color(99, 102, 241, 76), new Color(99, 102, 241, 0)}
        );
        g2.setPaint(gradient1);
        g2.fillOval((int)(-200 + parallax1), (int)(-200 + parallax2), 500, 500);
        
        // Orb 2 (bottom-right)
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
        
        // Draw logo
        Image logo = mainFrame.getLogoImage();
        if (logo != null) {
            int logoSize = 100;
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
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(logoX, logoY, logoSize, logoSize);
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        
        // Title
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "GoverLens Pro";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, width / 2 - fm.stringWidth(title) / 2, 215);
        
        // Subtitle
        g2.setColor(new Color(148, 163, 184, alpha));
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String subtitle = "Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού";
        fm = g2.getFontMetrics();
        g2.drawString(subtitle, width / 2 - fm.stringWidth(subtitle) / 2, 240);
        
        // Main instruction
        g2.setColor(new Color(226, 232, 240, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String instruction = "Επιλέξτε Ενέργεια";
        fm = g2.getFontMetrics();
        g2.drawString(instruction, width / 2 - fm.stringWidth(instruction) / 2, 310);
    }
    
    private void drawFooter(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(71, 85, 105));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        String footer = "© 2025 GoverLens Pro. Όλα τα δικαιώματα διατηρούνται.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(footer, width / 2 - fm.stringWidth(footer) / 2, height - 15);
    }
}