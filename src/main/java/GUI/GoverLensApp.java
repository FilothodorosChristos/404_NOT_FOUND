package GUI;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
/**
 * Main GUI application class for GoverLens.
 * Handles navigation between screens using CardLayout.
 * Screens include: welcome, project selection, year selection, and action selection.
 * Logic of the original code remains unchanged. Only JavaDoc comments are added.
 */


public class GoverLensApp {
    
 /** File path to the logo image. */
    private static final String LOGO_PATH = "GoverLensLogo.jpg";

    /** File path to the background image. */
    private static final String BACKGROUND_PATH = "BackroundPhoto.jpg";

    /** Width of the main application window. */
    private static final int FRAME_WIDTH = 1000;

    /** Height of the main application window. */
    private static final int FRAME_HEIGHT = 800;

    /** Navy-blue color used in UI. */
    private static final Color NAVY_BLUE = new Color(0, 0, 128);

    /** White color for text. */
    private static final Color TEXT_WHITE = Color.WHITE;

    /** Font used for main titles. */
    private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 50);

    /** Font used for subtitles. */
    private static final Font SUBTITLE_FONT = new Font("Tahoma", Font.PLAIN, 24);

    /** Font used for section headers. */
    private static final Font SECTION_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 30);

    /** Font used for large buttons. */
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);

    /** Font used for year-selection buttons. */
    private static final Font YEAR_BUTTON_FONT = new Font("Tahoma", Font.BOLD, 28);

    /** Size of large action buttons. */
    private static final Dimension LARGE_BUTTON_SIZE = new Dimension(350, 90);

    /** Size of year buttons. */
    private static final Dimension YEAR_BUTTON_SIZE = new Dimension(300, 70);

    /** Size of navigation buttons. */
    private static final Dimension NAV_BUTTON_SIZE = new Dimension(150, 40);
    
    /** Cached logo image. */
    private Image logoImage;

    /** Cached background image. */
    private Image backgroundImage;
    
    /** Stores the selected budget year. */
    private String selectedYear;
    
      /**
     * Starts the GoverLens application.
     * SwingUtilities.invokeLater is used to ensure safe GUI creation.
     *
     * @param args command-line arguments (unused)
     */
    
       public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoverLensApp().createAndShowGUI());
    }
       /**
     * Creates and displays the main GUI window.
     * Loads images, initializes the main frame, creates all panels,
     * and registers them in a CardLayout container.
     */
    private void createAndShowGUI() {
        loadImages();

        JFrame frame = createMainFrame();
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        
        mainPanel.add(createWelcomePanel(cardLayout, mainPanel), "welcome");
        mainPanel.add(createProjectSelectionPanel(cardLayout, mainPanel), "projectSelection");
        mainPanel.add(createYearSelectionPanel(cardLayout, mainPanel), "yearSelection");
        mainPanel.add(createActionSelectionPanel(cardLayout, mainPanel), "actionSelection");
        
        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "welcome");
    }
    
     /**
     * Loads the logo and background images from disk.
     * Prints an error message if loading fails.
     */
    private void loadImages() {
        try {
            logoImage = new ImageIcon(LOGO_PATH).getImage();
            backgroundImage = new ImageIcon(BACKGROUND_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            // Could use placeholder images or exit gracefully
        }
    }
    
     /**
     * Creates the main application JFrame with predefined size,
     * close operation, position, and window icon.
     *
     * @return the configured JFrame
     */
    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Καλώς Ήρθατε στην GoverLens.");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(logoImage);
        return frame;
    }
    
     /**
 * Creates the welcome panel displayed when the application starts.
 * Shows the logo, welcome text, subtitle, and a "Next" button.
 *
 * @param layout the CardLayout managing panel switching
 * @param container the main container holding all panels
 * @return the configured welcome JPanel
 */
    private JPanel createWelcomePanel(CardLayout layout, JPanel container) {
        JPanel panel = createLogoBackgroundPanel();
        
        
        JPanel centerPanel = createTransparentVerticalPanel();
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createCenteredLabel("Καλώς ήρθατε στην GoverLens! ", TITLE_FONT));
        panel.add(centerPanel, BorderLayout.CENTER);
        
        
        JPanel bottomSection = createTransparentVerticalPanel();
        bottomSection.add(Box.createVerticalStrut(80));
        bottomSection.add(createCenteredLabel("________________________________________________________", SUBTITLE_FONT));
        bottomSection.add(Box.createVerticalStrut(40));
        bottomSection.add(createCenteredLabel("Περισσότερη διαφάνεια , λιγότερη δυσκολία!", SUBTITLE_FONT));
        bottomSection.add(Box.createVerticalStrut(10));
        bottomSection.add(createCenteredLabel("Μάθε που πηγαίνει κάθε ευρώ και επεξεργάσου τα δεδομένα σαν υπουργός .", SUBTITLE_FONT));
        bottomSection.add(Box.createVerticalStrut(80));
        
        JButton nextButton = createNavigationButton("Επόμενο >", e -> layout.show(container, "projectSelection"));
        
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setOpaque(false);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel nextButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButtonPanel.setOpaque(false);
        nextButtonPanel.add(nextButton);
        
        navigationPanel.add(nextButtonPanel, BorderLayout.EAST);
        
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setOpaque(false);
        southWrapper.add(bottomSection, BorderLayout.CENTER);
        southWrapper.add(navigationPanel, BorderLayout.SOUTH);
        
        panel.add(southWrapper, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
 * Creates the project selection panel.
 * Allows the user to continue an existing project or start a new simulation.
 *
 * @param layout the CardLayout managing panel switching
 * @param container the main container holding all panels
 * @return the configured project selection JPanel
 */
    private JPanel createProjectSelectionPanel(CardLayout layout, JPanel container) {
        JPanel panel = createLogoBackgroundPanel();
        
        
        JPanel titlePanel = createTransparentVerticalPanel();
        titlePanel.add(Box.createVerticalStrut(40));
        titlePanel.add(createCenteredLabel("Καλώς ήρθατε στην GoverLens! ", TITLE_FONT));
        panel.add(titlePanel, BorderLayout.CENTER);
        
        
        JButton oldProjectBtn = createStyledButton("Συνέχεια Προσομοίωσης", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton newProjectBtn = createStyledButton("Καινούργια Προσομοίωση", LARGE_BUTTON_SIZE, BUTTON_FONT);
        
        oldProjectBtn.addActionListener(e -> layout.show(container, "yearSelection"));
        newProjectBtn.addActionListener(e -> layout.show(container, "yearSelection"));
        
        JPanel buttonsPanel = createTransparentHorizontalPanel();
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(oldProjectBtn);
        buttonsPanel.add(Box.createHorizontalStrut(40));
        buttonsPanel.add(newProjectBtn);
        buttonsPanel.add(Box.createHorizontalGlue());
        
        
        JButton prevButton = createNavigationButton("< Προηγούμενο", e -> layout.show(container, "welcome"));
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(buttonsPanel, BorderLayout.NORTH);
        southPanel.add(createBottomLeftPanel(prevButton), BorderLayout.SOUTH);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
 * Creates the year selection panel.
 * Displays buttons for available years and allows navigation back to project selection.
 *
 * @param layout the CardLayout managing panel switching
 * @param container the main container holding all panels
 * @return the configured year selection JPanel
 */
    private JPanel createYearSelectionPanel(CardLayout layout, JPanel container) {
        JPanel panel = new ImagePanel(backgroundImage);
        panel.setLayout(new BorderLayout());
        
        JPanel centerPanel = createTransparentVerticalPanel();
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(createCenteredLabel("Επιλέξτε έτος προυπολογισμού:", SECTION_TITLE_FONT, Color.WHITE));
        centerPanel.add(Box.createVerticalStrut(30));
        
        String[] years = {"2023", "2024", "2025"};
        for (String year : years) {
            JButton yearBtn = createYearButton(year, layout, container);
            centerPanel.add(yearBtn);
            centerPanel.add(Box.createVerticalStrut(20));
        }
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JButton prevButton = createNavigationButton("< Προηγούμενο", e -> layout.show(container, "projectSelection"));
        panel.add(createBottomLeftPanel(prevButton), BorderLayout.SOUTH);
        
        return panel;
    }
    /**
 * Creates a year button for the year selection panel.
 * When clicked, sets the selected year and navigates to the action selection panel.
 *
 * @param year the year displayed on the button
 * @param layout the CardLayout managing panel switching
 * @param container the main container holding all panels
 * @return the configured year JButton
 */
    
    private JButton createYearButton(String year, CardLayout layout, JPanel container) {
        JButton btn = createStyledButton(year, YEAR_BUTTON_SIZE, YEAR_BUTTON_FONT);
        btn.setBorder(new RoundedBorder(30));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            selectedYear = year;
            System.out.println("Selected year: " + selectedYear);
            layout.show(container, "actionSelection");
        });
        return btn;
    }
    
    /**
 * Creates the action selection panel.
 * Allows the user to choose between viewing, editing, charts, or comparison.
 *
 * @param layout the CardLayout managing panel switching
 * @param container the main container holding all panels
 * @return the configured action selection JPanel
 */
    private JPanel createActionSelectionPanel(CardLayout layout, JPanel container) {
        JPanel panel = new ImagePanel(backgroundImage);
        panel.setLayout(new BorderLayout());
        
        JLabel label = createCenteredLabel("Παρακαλώ επιλέξτε διαδικασία:", SECTION_TITLE_FONT, Color.WHITE);
        
        
        JButton viewBtn = createStyledButton("Προβολή", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton editBtn = createStyledButton("Επεξεργασία", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton diagramBtn = createStyledButton("Διαγράμματα", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton compareBtn = createStyledButton("Σύγκριση", LARGE_BUTTON_SIZE, BUTTON_FONT);
        
        
        viewBtn.addActionListener(e -> handleAction("Προβολή"));
        editBtn.addActionListener(e -> handleAction("Επεξεργασία"));
        diagramBtn.addActionListener(e -> handleAction("Διαγράμματα"));
        compareBtn.addActionListener(e -> handleAction("Σύγκριση"));
        
        
        for (JButton btn : new JButton[]{viewBtn, editBtn, diagramBtn, compareBtn}) {
            btn.setBorder(new RoundedBorder(30));
        }
        
        
        JPanel buttonsGrid = new JPanel(new GridBagLayout());
        buttonsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        
        gbc.gridx = 0; gbc.gridy = 0;
        buttonsGrid.add(viewBtn, gbc);
        gbc.gridx = 1;
        buttonsGrid.add(editBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        buttonsGrid.add(diagramBtn, gbc);
        gbc.gridx = 1;
        buttonsGrid.add(compareBtn, gbc);
        
        
        JPanel centerPanel = createTransparentVerticalPanel();
        centerPanel.add(Box.createVerticalStrut(150)); 
        centerPanel.add(label);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonsGrid);
        centerPanel.add(Box.createVerticalGlue()); 
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JButton prevButton = createNavigationButton("< Προηγούμενο", e -> layout.show(container, "yearSelection"));
        panel.add(createBottomLeftPanel(prevButton), BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
 * Creates a panel with a background image and optionally draws a circular logo.
 *
 * @return the JPanel with background and logo
 */
    
    private JPanel createLogoBackgroundPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                
                if (logoImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int diameter = (int) (Math.min(getWidth(), getHeight()) * 0.4);
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 3;
                    
                    Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
                    g2.setClip(clip);
                    g2.drawImage(logoImage, x, y, diameter, diameter, this);
                    
                    g2.setClip(null);
                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x, y, diameter, diameter);
                    g2.dispose();
                }
            }
        };
    }
    

/**
 * Creates a transparent vertical JPanel using BoxLayout.
 *
 * @return the transparent vertical JPanel
 */
    
    private JPanel createTransparentVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }


    /**
 * Creates a transparent horizontal JPanel using BoxLayout.
 *
 * @return the transparent horizontal JPanel
 */
    
    private JPanel createTransparentHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        return panel;
    }
    
    /**
 * Creates a centered JLabel with a specific font and default white color.
 *
 * @param text the text to display
 * @param font the font to use
 * @return the JLabel configured with text, font, and alignment
 */

    private JLabel createCenteredLabel(String text, Font font) {
        return createCenteredLabel(text, font, TEXT_WHITE);
    }

    /**
 * Creates a centered JLabel with a specific font and color.
 *
 * @param text the text to display
 * @param font the font to use
 * @param color the text color
 * @return the JLabel configured with text, font, color, and center alignment
 */
    
    private JLabel createCenteredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
 * Creates a styled JButton with rounded borders, specified size, font, and alignment.
 *
 * @param text the text to display on the button
 * @param size the preferred button size
 * @param font the font to use (optional, default BUTTON_FONT if null)
 * @return the styled JButton
 */
    
    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton button = new JButton(text);
        button.setForeground(NAVY_BLUE);
        button.setBackground(Color.WHITE);
        button.setFont(font != null ? font : BUTTON_FONT);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setBorder(new RoundedBorder(15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    /**
 * Creates a navigation button with an action listener.
 *
 * @param text the text on the button
 * @param action the ActionListener triggered when clicked
 * @return the configured JButton
 */

    
    private JButton createNavigationButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setPreferredSize(NAV_BUTTON_SIZE);
        button.setMinimumSize(NAV_BUTTON_SIZE);
        button.setMaximumSize(NAV_BUTTON_SIZE);
        button.addActionListener(action);
        return button;
    }

    /**
 * Creates a panel aligned to the bottom left containing the specified button.
 *
 * @param button the JButton to add
 * @return the bottom-left aligned JPanel
 */
    
    private JPanel createBottomLeftPanel(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(button);
        return panel;
    }

    /**
 * Handles user action by displaying a message dialog.
 *
 * @param action the action string selected by the user
 */
    
    private void handleAction(String action) {
        String message = String.format("Επιλέξατε: %s%nΈτος: %s", action, selectedYear);
        JOptionPane.showMessageDialog(null, message, "Ενέργεια", JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    /**
 * A JPanel subclass that paints a background image stretched to fit the panel.
 */
   
    
    static class ImagePanel extends JPanel {
        private Image backgroundImage;

        /**
     * Constructs an ImagePanel with the specified background image.
     *
     * @param backgroundImage the Image to use as the panel's background
     */
        
        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            setLayout(new BorderLayout());
        }

        
    /**
     * Paints the background image onto the panel.
     *
     * @param g the Graphics context to use for painting
     */
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    /**
 * A custom border with rounded corners for Swing components.
 */
    
    static class RoundedBorder extends AbstractBorder {
        private int radius;

         /**
     * Constructs a RoundedBorder with the specified corner radius.
     *
     * @param radius the radius of the rounded corners
     */
        
        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        
    /**
     * Paints the rounded border around the specified component.
     *
     * @param c the component for which this border is being painted
     * @param g the Graphics context to use for painting
     * @param x the x position of the border
     * @param y the y position of the border
     * @param width the width of the border
     * @param height the height of the border
     */
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
