package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ObjectStreamException;


/**
 * Main GUI application class for GoverLens.
 * Handles navigation between panels using CardLayout.
 * Manages shared data and resources across all panels.
 * Uses Singleton pattern to ensure only one instance exists.
 */
public class MainFrame extends JFrame { 
    
    
    private static final long serialVersionUID = 1L;

    /** Singleton instance of MainFrame (volatile for thread safety). */
    private static volatile transient MainFrame instance; 
    
    /** CardLayout for managing panel switching. */
    private CardLayout cardLayout;

    /** Main container panel holding all panels. */
    private JPanel mainPanel;
    
    /** Selected budget year by the user. */
    private String selectedYear;
    
    /** Cached logo image (Final for encapsulation). */
    private final transient BufferedImage logoImage; 
    
    /** Cached background image (Final for encapsulation). */
    private final transient BufferedImage backgroundImage;
    
    /** Panel name constant for welcome screen. */
    public static final String WELCOME = "welcome";
    
    /** Panel name constant for project selection screen. */
    public static final String PROJECT_SELECTION = "projectSelection";
    
    /** Panel name constant for year selection screen. */
    public static final String YEAR_SELECTION = "yearSelection";
    
    /** Panel name constant for action selection screen. */
    public static final String ACTION_SELECTION = "actionSelection";

    public static final String FINANCE_CHART = "financeChart";

    public static final String BUDGET_VIEW = "budgetView";
    
    /**
     * Private constructor for Singleton pattern.
     * Initializes images, frame settings, and all panels.
     */
    private MainFrame() {
        BufferedImage tempLogo = null;
        BufferedImage tempBackground = null;
        try {
            
            tempLogo = toBufferedImage(new ImageIcon("GoverLensLogo.jpg").getImage());
            tempBackground = toBufferedImage(new ImageIcon("BackroundPhoto.jpg").getImage());
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
        this.logoImage = tempLogo;
        this.backgroundImage = tempBackground;
        
        setupFrame();
        initializePanels();
    }

    
    private static BufferedImage toBufferedImage(Image img) {
        if (img == null) return null;
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    /**
     * Returns the singleton instance of MainFrame.
     * Creates a new instance if one does not exist (Thread-safe using DCL).
     *
     * @return the singleton MainFrame instance
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) {
                    instance = new MainFrame();
                }
            }
        }
        return instance;
    }
    
    /**
     * Configures the main application frame.
     * Sets title, size, location, close operation, icon, and layout.
     */
    private void setupFrame() {
        setTitle("Καλώς Ήρθατε στην GoverLens.");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(logoImage); 
        
        
        cardLayout = new CardLayout(); 
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }
    
    public void showFinanceChart() {
        if (selectedYear == null) {
            JOptionPane.showMessageDialog(this, "Πρέπει να επιλέξετε πρώτα έτος.", "Προειδοποίηση", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        FinanceChartPanel chartPanel = FinanceChartPanel.createPanel(selectedYear);
        
        mainPanel.add(chartPanel, FINANCE_CHART); 
        showPanel(FINANCE_CHART);
    }

    /**
     * Initializes and adds all panel instances to the CardLayout.
     * Each panel is registered with its corresponding name constant.
     */
    private void initializePanels() {
        
        mainPanel.add(new WelcomePanel(this), WELCOME);
        mainPanel.add(new ProjectSelectionPanel(this), PROJECT_SELECTION);
        mainPanel.add(new YearSelectionPanel(this), YEAR_SELECTION);
        mainPanel.add(new ActionSelectionPanel(this), ACTION_SELECTION);
        mainPanel.add(new BudgetViewPanel(this), BUDGET_VIEW);
    }
    
    /**
     * Navigates to the specified panel using CardLayout.
     *
     * @param panelName the name of the panel to display
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    /**
     * Returns the logo image.
     *
     * @return a copy of the logo Image object
     */
    public Image getLogoImage() { 
        
        return logoImage != null ? copyImage(logoImage) : null;
    }
    
    /**
     * Returns the background image.
     *
     * @return a copy of the background Image object
     */
    public Image getBackgroundImage() { 
        
        return backgroundImage != null ? copyImage(backgroundImage) : null;
    }

    private BufferedImage copyImage(BufferedImage img) {
        if (img == null) return null;
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    /**
     * Returns the currently selected year.
     *
     * @return the selected year as a String
     */
    public String getSelectedYear() { 
        return selectedYear; 
    }
    
    /**
     * Sets the selected budget year.
     * Prints the selected year to console.
     *
     * @param year the year to set as selected
     */
    public void setSelectedYear(String year) {
        this.selectedYear = year;
        System.out.println("Selected year: " + selectedYear);
    }
    
    /**
     * Called during deserialization to replace the object being deserialized
     * with the existing singleton instance, preventing the creation of new instances.
     * @return The existing singleton instance.
     * @throws ObjectStreamException 
     */
    protected Object readResolve() throws ObjectStreamException {
        return getInstance();
    }
    
    /**
     * Main entry point for the GoverLens application.
     * Uses SwingUtilities.invokeLater to ensure thread safety.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.getInstance();
            frame.setVisible(true);
            frame.showPanel(WELCOME);
        });
    }
}