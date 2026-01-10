package GUI;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Modern ActionSelectionPanel with unified aesthetic matching WelcomePanel.
 * Allows the user to select an action: View, Edit, Charts, or Comparison.
 */
public class ActionSelectionPanel extends JPanel {
    
  private final MainFrame mainFrame;
  private Timer animationTimer;
  private float rotationAngle = 0;
  private int fadeInProgress = 0;
  private Point mousePosition = new Point(0, 0);
  private JButton[] actionButtons;
  private JButton backButton;
    
  /**
   * Constructs an ActionSelectionPanel with modern design.
   *
   * @param mainFrame the main application frame
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Necessary for GUI communication")
                   
    public ActionSelectionPanel(MainFrame mainFrame) {
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

    int buttonWidth = 280;
    int buttonHeight = 140;
    int gapX = 40;
    int gapY = 30;

    int firstRowButtons = 3;
    int secondRowButtons = 2;

    int startY = 380;

    for (int i = 0; i < actionButtons.length; i++) {
      int row = (i < 3) ? 0 : 1;  // Πρώτη σειρά για i=0..2, δεύτερη για i=3..4
      int col;

      if (row == 0) {
        col = i;  // 0,1,2
      } else {
        col = i - 3; // 0,1 για δεύτερη σειρά
      }

      int buttonsInRow = (row == 0) ? firstRowButtons : secondRowButtons;
      int totalWidth = buttonsInRow * buttonWidth + (buttonsInRow - 1) * gapX;
      int startX = (width - totalWidth) / 2;

      int x = startX + col * (buttonWidth + gapX);
      int y = startY + row * (buttonHeight + gapY);

      actionButtons[i].setBounds(x, y, buttonWidth, buttonHeight);
    }

    // Back button
    backButton.setBounds(30, height - 70, 150, 40);
  }
    
  /**
   * Creates the 4 action buttons.
   */
  private void createActionButtons() {
    actionButtons = new JButton[5];
    String[] titles = {"Προβολή", "Επεξεργασία", "Διαγράμματα", "Σύγκριση", "Μαζικές Αλλαγές"};
    String[] icons = {"👁️", "✏️", "📊", "⚖️", "🔄"};
    String[] descriptions = {
        "Προβολή προϋπολογισμού",
        "Επεξεργασία δεδομένων",
        "Οπτικοποίηση δεδομένων",
        "Σύγκριση ετών",
        "Εφαρμογή μαζικών αλλαγών"

        };
        
    for (int i = 0; i < 5; i++) {
      actionButtons[i] = createActionButton(titles[i], icons[i], descriptions[i]);
      add(actionButtons[i]);
    }
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
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
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            FontMetrics fmIcon = g2.getFontMetrics();
            int iconX = (getWidth() - fmIcon.stringWidth(icon)) / 2;
            int iconY = (getHeight() / 2) - 20; 
            g2.setColor(new Color(255, 255, 255, 230));
            g2.drawString(icon, iconX, iconY);
            
                
            // Title
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(new Color(226, 232, 240));
            FontMetrics fmTitle = g2.getFontMetrics();
            int titleX = (getWidth() - fmTitle.stringWidth(title)) / 2;
            int titleY = (getHeight() / 2) + 15; 
            g2.drawString(title, titleX, titleY);
                
            // Description
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(new Color(100, 116, 139));
            FontMetrics fmDesc = g2.getFontMetrics();
            int descX = (getWidth() - fmDesc.stringWidth(description)) / 2;
            int descY = (getHeight() / 2) + 40; 
            g2.drawString(description, descX, descY);
                
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
            btn.setBounds(btn.getX(), btn.getY() - 3, btn.getWidth(), btn.getHeight());
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
                
            // Background (matching style)
            g2.setColor(new Color(15, 23, 42, 128));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
            // Border
            g2.setColor(new Color(99, 102, 241, 80));
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
    if (action.equals("Διαγράμματα")) {
      mainFrame.showFinanceChart();
    } else if (action.equals("Προβολή")) {
      mainFrame.showBudgetView();
    } else if (action.equals("Επεξεργασία")) {
      mainFrame.showDataEditor("cashflow");
    } else if (action.equals("Σύγκριση")) {
      mainFrame.showComparison();

    } else if (action.equals("Μαζικές Αλλαγές")) {
      mainFrame.showMassiveChanges();
    }
  }
  
  /**
   * Sets up animation timer.
   */
  private void setupAnimations() {
    animationTimer = new Timer(30, e -> {
      rotationAngle += 0.5f;
      if (rotationAngle >= 360) {
        rotationAngle = 0;
      }     
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
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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
    int offset = (int) (rotationAngle % gridSize);
        
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
    g2.fillOval((int) (-200 + parallax1), (int) (-200 + parallax2), 500, 500);
        
    // Orb 2 (bottom-right)
    RadialGradientPaint gradient2 = new RadialGradientPaint(
            width - 150 + parallax1 * 1.5f, height - 150 + parallax2 * 1.5f, 200,
            new float[]{0f, 1f},
            new Color[]{new Color(139, 92, 246, 76), new Color(139, 92, 246, 0)}
        );
    g2.setPaint(gradient2);
    g2.fillOval((int) (width - 350 + parallax1 * 1.5f),
         (int) (height - 350 + parallax2 * 1.5f), 400, 400);
  }
    
  private void drawHeader(Graphics2D g2, int width) {
    int alpha = Math.min(255, fadeInProgress * 255 / 100);
        
    // Draw logo
    Image logo = mainFrame.getLogoImage();
    if (logo != null) {
      int logoSize = 100;
      int logoX = width / 2 - logoSize / 2;
      int logoY = 60;
            
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
    String title = "GoverLens";
    FontMetrics fm = g2.getFontMetrics();
    g2.drawString(title, width / 2 - fm.stringWidth(title) / 2, 195);
        
    // Subtitle
    g2.setColor(new Color(148, 163, 184, alpha));
    g2.setFont(new Font("Arial", Font.PLAIN, 14));
    String subtitle = "Σύστημα Διαχείρισης Κρατικού Προϋπολογισμού";
    fm = g2.getFontMetrics();
    g2.drawString(subtitle, width / 2 - fm.stringWidth(subtitle) / 2, 220);
        
    // Main instruction
    g2.setColor(new Color(226, 232, 240, alpha));
    g2.setFont(new Font("Arial", Font.BOLD, 28));
    String instruction = "Επιλέξτε Διαδικασία";
    fm = g2.getFontMetrics();
    g2.drawString(instruction, width / 2 - fm.stringWidth(instruction) / 2, 280);
        
    // Year indicator
    String selectedYear = mainFrame.getSelectedYear();
    if (selectedYear != null) {
      g2.setColor(new Color(99, 102, 241, alpha));
      g2.setFont(new Font("Arial", Font.BOLD, 16));
      String yearText = "Έτος: " + selectedYear;
      fm = g2.getFontMetrics();
      g2.drawString(yearText, width / 2 - fm.stringWidth(yearText) / 2, 315);
    }
  }
    
  private void drawFooter(Graphics2D g2, int width, int height) {
    g2.setColor(new Color(71, 85, 105));
    g2.setFont(new Font("Arial", Font.PLAIN, 10));
    String footer = "© 2025 GoverLens. Όλα τα δικαιώματα διατηρούνται.";
    FontMetrics fm = g2.getFontMetrics();
    g2.drawString(footer, width / 2 - fm.stringWidth(footer) / 2, height - 15);
  }
}