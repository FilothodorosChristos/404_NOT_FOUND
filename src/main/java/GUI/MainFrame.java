package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI application class for GoverLens.
 * Handles navigation between panels using CardLayout.
 * Manages shared data and resources across all panels.
 * Uses Singleton pattern to ensure only one instance exists.
 */
public class MainFrame extends JFrame {
    
    /** Singleton instance of MainFrame (volatile for thread safety). */
    private static volatile MainFrame instance;
    
    /** CardLayout for managing panel switching. */
    private CardLayout cardLayout;
    
    /** Main container panel holding all panels. */
    private JPanel mainPanel;
    
    /** Selected budget year by the user. */
    private String selectedYear;
    
    /** Cached logo image (Final for encapsulation). */
    private final Image logoImage;
    
    /** Cached background image (Final for encapsulation). */
    private final Image backgroundImage;
    
    /** Panel name constant for welcome screen. */
    public static final String WELCOME = "welcome";
    
    /** Panel name constant for project selection screen. */
    public static final String PROJECT_SELECTION = "projectSelection";
    
    /** Panel name constant for year selection screen. */
    public static final String YEAR_SELECTION = "yearSelection";
    
    /** Panel name constant for action selection screen. */
    public static final String ACTION_SELECTION = "actionSelection";

    public static final String FINANCE_CHART = "financeChart";
    
    /**
     * Private constructor for Singleton pattern.
     * Initializes images, frame settings, and all panels.
     */
    private MainFrame() {
        // Τα πεδία final πρέπει να αρχικοποιηθούν εδώ
        Image tempLogo = null;
        Image tempBackground = null;
        try {
            tempLogo = new ImageIcon("GoverLensLogo.jpg").getImage();
            tempBackground = new ImageIcon("BackroundPhoto.jpg").getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
        this.logoImage = tempLogo;
        this.backgroundImage = tempBackground;
        
        setupFrame();
        initializePanels();
    }
    
    /**
     * Returns the singleton instance of MainFrame.
     * Creates a new instance if one does not exist (Thread-safe using DCL).
     *
     * @return the singleton MainFrame instance
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            // Double-Checked Locking (DCL)
            synchronized (MainFrame.class) {
                if (instance == null) {
                    instance = new MainFrame();
                }
            }
        }
        return instance;
    }
    
    /**
     * Dummy method to replace the old loadImages(). Images are loaded directly in the constructor.
     */
    private void loadImages() {
        // Αυτή η μέθοδος δεν χρειάζεται πλέον, καθώς οι εικόνες φορτώνονται στον constructor.
        // Την αφήνουμε κενή για συμβατότητα, αν καλείται κάπου αλλού, αλλά ο κώδικας
        // του constructor πλέον διαχειρίζεται το φόρτωμα.
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
    // Δημιουργία νέου panel και προσθήκη του στο CardLayout
    FinanceChartPanel chartPanel = new FinanceChartPanel(selectedYear);
    // Χρησιμοποιούμε την σταθερά για το όνομα
    mainPanel.add(chartPanel, FINANCE_CHART); 
    showPanel(FINANCE_CHART);
    }

    /**
     * Initializes and adds all panel instances to the CardLayout.
     * Each panel is registered with its corresponding name constant.
     */
    private void initializePanels() {
        // ΣΗΜΑΝΤΙΚΗ ΣΗΜΕΙΩΣΗ: Για να διορθωθεί το σφάλμα EI_EXPOSE_REP2 στα panels,
        // τα πεδία 'mainFrame' μέσα στις κλάσεις WelcomePanel, ProjectSelectionPanel,
        // YearSelectionPanel και ActionSelectionPanel ΠΡΕΠΕΙ να δηλωθούν ως 'private final'.
        mainPanel.add(new WelcomePanel(this), WELCOME);
        mainPanel.add(new ProjectSelectionPanel(this), PROJECT_SELECTION);
        mainPanel.add(new YearSelectionPanel(this), YEAR_SELECTION);
        mainPanel.add(new ActionSelectionPanel(this), ACTION_SELECTION);
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
     * @return the logo Image object
     */
    public Image getLogoImage() { 
        return logoImage; 
    }
    
    /**
     * Returns the background image.
     *
     * @return the background Image object
     */
    public Image getBackgroundImage() { 
        return backgroundImage; 
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
     */
    protected Object readResolve() {
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