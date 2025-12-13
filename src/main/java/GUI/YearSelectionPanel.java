package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * YearSelectionPanel allows the user to select a budget year.
 * Displays buttons for available years and allows navigation back to project selection.
 */
public class YearSelectionPanel extends JPanel {
    
    /** Reference to the main application frame (declared as final to fix EI_EXPOSE_REP2). */
    private final MainFrame mainFrame;
    
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
        
        // Δημιουργία του ημιδιάφανου λευκού container
        JPanel whiteContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Πολύ διάφανο λευκό φόντο με θολούρα
                g2d.setColor(new Color(255, 255, 255, 100)); // 100/255 = ~40% opacity
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Ελαφρύ border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
            }
        };
        whiteContainer.setLayout(new BoxLayout(whiteContainer, BoxLayout.Y_AXIS));
        whiteContainer.setOpaque(false);
        whiteContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
         whiteContainer.setMaximumSize(new Dimension(450, 600)); // Περιορισμός πλάτους
        // Τίτλος
        JLabel titleLabel = new JLabel("Επιλέξτε έτος ");
        JLabel title2Label = new JLabel("προυπολογισμού:");
        titleLabel.setFont(SECTION_TITLE_FONT);
        titleLabel.setForeground(Color.WHITE); // Λευκό για καλύτερη αντίθεση με διάφανο φόντο
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        title2Label.setFont(SECTION_TITLE_FONT);
        title2Label.setForeground(Color.WHITE); // Λευκό για καλύτερη αντίθεση με διάφανο φόντο
        title2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        whiteContainer.add(titleLabel);
        whiteContainer.add(title2Label);
        whiteContainer.add(Box.createVerticalStrut(60));
        
        /** year buttons */
        String[] years = {"2023", "2024", "2025"};
        for (String year : years) {
            JButton yearBtn = createYearButton(year);
            whiteContainer.add(yearBtn);
            whiteContainer.add(Box.createVerticalStrut(20));
        }
        
        centerPanel.add(whiteContainer);
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
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setMinimumSize(new Dimension(300, 70));
        btn.setMaximumSize(new Dimension(300, 70));
        btn.setBorder(new RoundedBorder(30));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hover effect για καλύτερη εμπειρία χρήστη
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 240, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        
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
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}