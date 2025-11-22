package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GoverLensApp {
    
    // ========== CONSTANTS ==========
    private static final String LOGO_PATH = "GoverLensLogo.jpg";
    private static final String BACKGROUND_PATH = "BackroundPhoto.jpg";
    
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 800;
    
    private static final Color NAVY_BLUE = new Color(0, 0, 128);
    private static final Color TEXT_WHITE = Color.WHITE;
    
    private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 50);
    private static final Font SUBTITLE_FONT = new Font("Tahoma", Font.PLAIN, 24);
    private static final Font SECTION_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 30);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font YEAR_BUTTON_FONT = new Font("Tahoma", Font.BOLD, 28);
    
    private static final Dimension LARGE_BUTTON_SIZE = new Dimension(350, 90);
    private static final Dimension YEAR_BUTTON_SIZE = new Dimension(300, 70);
    private static final Dimension NAV_BUTTON_SIZE = new Dimension(150, 40);
    
    // ========== CACHED IMAGES ==========
    private Image logoImage;
    private Image backgroundImage;
    
    // ========== STATE ==========
    private String selectedYear;
    
    // ========== MAIN ==========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoverLensApp().createAndShowGUI());
    }
    
    // ========== GUI CREATION ==========
    private void createAndShowGUI() {
        loadImages();
        
        JFrame frame = createMainFrame();
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Add all panels
        mainPanel.add(createWelcomePanel(cardLayout, mainPanel), "welcome");
        mainPanel.add(createProjectSelectionPanel(cardLayout, mainPanel), "projectSelection");
        mainPanel.add(createYearSelectionPanel(cardLayout, mainPanel), "yearSelection");
        mainPanel.add(createActionSelectionPanel(cardLayout, mainPanel), "actionSelection");
        
        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "welcome");
    }
    
    // ========== IMAGE LOADING ==========
    private void loadImages() {
        try {
            logoImage = new ImageIcon(LOGO_PATH).getImage();
            backgroundImage = new ImageIcon(BACKGROUND_PATH).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            // Could use placeholder images or exit gracefully
        }
    }
    
    // ========== FRAME SETUP ==========
    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Καλώς Ήρθατε στην GoverLens.");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(logoImage);
        return frame;
    }
    
    // ========== PANEL 1: WELCOME ==========
    private JPanel createWelcomePanel(CardLayout layout, JPanel container) {
        JPanel panel = createLogoBackgroundPanel();
        
        // Center text
        JPanel centerPanel = createTransparentVerticalPanel();
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createCenteredLabel("Καλώς ήρθατε στην GoverLens! ", TITLE_FONT));
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom section
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
    
    // ========== PANEL 2: PROJECT SELECTION ==========
    private JPanel createProjectSelectionPanel(CardLayout layout, JPanel container) {
        JPanel panel = createLogoBackgroundPanel();
        
        // Title
        JPanel titlePanel = createTransparentVerticalPanel();
        titlePanel.add(Box.createVerticalStrut(40));
        titlePanel.add(createCenteredLabel("Καλώς ήρθατε στην GoverLens! ", TITLE_FONT));
        panel.add(titlePanel, BorderLayout.CENTER);
        
        // Main buttons
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
        
        // Navigation
        JButton prevButton = createNavigationButton("< Προηγούμενο", e -> layout.show(container, "welcome"));
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(buttonsPanel, BorderLayout.NORTH);
        southPanel.add(createBottomLeftPanel(prevButton), BorderLayout.SOUTH);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    // ========== PANEL 3: YEAR SELECTION ==========
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
    
    // ========== PANEL 4: ACTION SELECTION ==========
    private JPanel createActionSelectionPanel(CardLayout layout, JPanel container) {
        JPanel panel = new ImagePanel(backgroundImage);
        panel.setLayout(new BorderLayout());
        
        JLabel label = createCenteredLabel("Παρακαλώ επιλέξτε διαδικασία:", SECTION_TITLE_FONT, Color.WHITE);
        
        // Create action buttons
        JButton viewBtn = createStyledButton("Προβολή", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton editBtn = createStyledButton("Επεξεργασία", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton diagramBtn = createStyledButton("Διαγράμματα", LARGE_BUTTON_SIZE, BUTTON_FONT);
        JButton compareBtn = createStyledButton("Σύγκριση", LARGE_BUTTON_SIZE, BUTTON_FONT);
        
        // Add action listeners
        viewBtn.addActionListener(e -> handleAction("Προβολή"));
        editBtn.addActionListener(e -> handleAction("Επεξεργασία"));
        diagramBtn.addActionListener(e -> handleAction("Διαγράμματα"));
        compareBtn.addActionListener(e -> handleAction("Σύγκριση"));
        
        // Apply rounded borders
        for (JButton btn : new JButton[]{viewBtn, editBtn, diagramBtn, compareBtn}) {
            btn.setBorder(new RoundedBorder(30));
        }
        
        // Layout buttons in grid
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
        
        // Center panel with better spacing
        JPanel centerPanel = createTransparentVerticalPanel();
        centerPanel.add(Box.createVerticalStrut(150)); // Fixed space from top
        centerPanel.add(label);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonsGrid);
        centerPanel.add(Box.createVerticalGlue()); // Fill remaining space at bottom
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JButton prevButton = createNavigationButton("< Προηγούμενο", e -> layout.show(container, "yearSelection"));
        panel.add(createBottomLeftPanel(prevButton), BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ========== HELPER METHODS ==========
    
    private JPanel createLogoBackgroundPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw background
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Draw circular logo
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
    
    private JPanel createTransparentVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }
    
    private JPanel createTransparentHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        return panel;
    }
    
    private JLabel createCenteredLabel(String text, Font font) {
        return createCenteredLabel(text, font, TEXT_WHITE);
    }
    
    private JLabel createCenteredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
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
    
    private JButton createNavigationButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setPreferredSize(NAV_BUTTON_SIZE);
        button.setMinimumSize(NAV_BUTTON_SIZE);
        button.setMaximumSize(NAV_BUTTON_SIZE);
        button.addActionListener(action);
        return button;
    }
    
    private JPanel createBottomLeftPanel(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(button);
        return panel;
    }
    
    private void handleAction(String action) {
        String message = String.format("Επιλέξατε: %s%nΈτος: %s", action, selectedYear);
        JOptionPane.showMessageDialog(null, message, "Ενέργεια", JOptionPane.INFORMATION_MESSAGE);
        // Here you would implement actual functionality
    }
    
    // ========== INNER CLASSES ==========
    
    static class ImagePanel extends JPanel {
        private Image backgroundImage;
        
        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
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
    
    static class RoundedBorder extends AbstractBorder {
        private int radius;
        
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        
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