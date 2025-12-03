package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * YearSelectionPanel allows the user to select a budget year.
 * Displays buttons for available years and allows navigation back to project selection.
 */
public class YearSelectionPanel extends JPanel {
    
    /** Reference to the main application frame. */
    private MainFrame mainFrame;
    
    /** Navy-blue color used in UI. */
    private static final Color NAVY_BLUE = new Color(0, 0, 128);
    
    /** Font used for section headers. */
    private static final Font SECTION_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 30);
    
    /** Font used for year-selection buttons. */
    private static final Font YEAR_BUTTON_FONT = new Font("Tahoma", Font.BOLD, 28);
    
    /**
     * Constructs a YearSelectionPanel with the specified MainFrame reference.
     *
     * @param mainFrame the main application frame
     */
    public YearSelectionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        createUI();
    }
    
    /**
     * Creates and initializes the user interface components.
     * Sets up the background, title, year buttons, and navigation.
     */
    private void createUI() {
        // Background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            /**
             * Paints the background image.
             *
             * @param g the Graphics context
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainFrame.getBackgroundImage() != null) {
                    g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
       /**center panel */
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalGlue());
        
        JLabel titleLabel = new JLabel("Επιλέξτε έτος προυπολογισμού:");
        titleLabel.setFont(SECTION_TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        
        /** year buttons */
        String[] years = {"2023", "2024", "2025"};
        for (String year : years) {
            JButton yearBtn = createYearButton(year);
            centerPanel.add(yearBtn);
            centerPanel.add(Box.createVerticalStrut(20));
        }
        
        centerPanel.add(Box.createVerticalGlue());
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        
        /** previous button */
        JButton prevButton = new JButton("< Προηγούμενο");
        prevButton.setPreferredSize(new Dimension(150, 40));
        prevButton.addActionListener(e -> mainFrame.showPanel(MainFrame.PROJECT_SELECTION));
        
        JPanel prevButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prevButtonPanel.setOpaque(false);
        prevButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        prevButtonPanel.add(prevButton);
        
        backgroundPanel.add(prevButtonPanel, BorderLayout.SOUTH);
        add(backgroundPanel);
    }
    
    /**
     * Creates a year button for the year selection panel.
     * When clicked, sets the selected year and navigates to the action selection panel.
     *
     * @param year the year displayed on the button
     * @return the configured year JButton
     */
    private JButton createYearButton(String year) {
        JButton btn = new JButton(year);
        btn.setForeground(NAVY_BLUE);
        btn.setBackground(Color.WHITE);
        btn.setFont(YEAR_BUTTON_FONT);
        btn.setPreferredSize(new Dimension(300, 70));
        btn.setMinimumSize(new Dimension(300, 70));
        btn.setMaximumSize(new Dimension(300, 70));
        btn.setBorder(new RoundedBorder(30));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addActionListener(e -> {
            mainFrame.setSelectedYear(year);
            mainFrame.showPanel(MainFrame.ACTION_SELECTION);
        });
        
        return btn;
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