package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import dao.Log;
import dao.LogDao;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * LogViewerPanel displays the history of all changes made by users.
 * Shows log entries from the database in a sortable table format.
 */
public class LogViewerPanel extends JPanel {
    
    /** Reference to the main application frame. */
    private final MainFrame mainFrame;
    
    /** Year to return to when going back */
    private final int returnYear;
    
    /** Data type to return to when going back */
    private final String returnDataType;
    
    /** Navy-blue color used in UI. */
    private static final Color NAVY_BLUE = new Color(0, 0, 128);
    
    /** Font used for section headers. */
    private static final Font TITLE_FONT = new Font("Tahoma", Font.BOLD, 28);
    
    /** Font used for table headers. */
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    
    /** Font used for table content. */
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);
    
    /** Table model for displaying log data. */
    private DefaultTableModel tableModel;
    
    /** The JTable component. */
    private JTable logTable;
    
    /** DAO for accessing log data. */
    private final LogDao logDao;
    
    /**
     * Constructs a LogViewerPanel with the specified MainFrame reference.
     *
     * @param mainFrame the main application frame
     * @param year the year to return to when going back
     * @param dataType the data type to return to when going back
     */

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Necessary for GUI communication")

    public LogViewerPanel(MainFrame mainFrame, int year, String dataType) {
        this.mainFrame = mainFrame;
        this.returnYear = year;
        this.returnDataType = dataType;
        this.logDao = new LogDao();
        setLayout(new BorderLayout());
        createUI();
        loadLogData();
    }
    
    /**
     * Creates and initializes the user interface components.
     */
    private void createUI() {
        /** Background panel with image */
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainFrame.getBackgroundImage() != null) {
                    g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        /** Main content panel */
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        /** Title panel */
        JPanel titlePanel = createTitlePanel();
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        
        /** Table panel with semi-transparent background */
        JPanel tableContainer = createTablePanel();
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        
        /** Bottom panel with buttons */
        JPanel bottomPanel = createBottomPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        add(backgroundPanel);
    }
    
    /**
     * Creates the title panel with header text.
     *
     * @return the configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel titleLabel = new JLabel("Ιστορικό Αλλαγών");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Creates the table panel with semi-transparent white background.
     *
     * @return the configured table panel
     */
    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent white background
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Light border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table with column names
        String[] columnNames = {"ID", "Πίνακας", "Λειτουργία", "ID Εγγραφής", "Παλιά Δεδομένα", "Νέα Δεδομένα", "Χρόνος"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        logTable = new JTable(tableModel);
        logTable.setFont(TABLE_FONT);
        logTable.setRowHeight(30);
        logTable.setSelectionBackground(new Color(173, 216, 230));
        logTable.setSelectionForeground(Color.BLACK);
        logTable.setGridColor(new Color(220, 220, 220));
        logTable.setShowGrid(true);
        
        // Configure table header
        JTableHeader header = logTable.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(NAVY_BLUE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        logTable.setRowSorter(sorter);
        
        // Set column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        logTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Πίνακας
        logTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Λειτουργία
        logTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // ID Εγγραφής
        logTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Παλιά Δεδομένα
        logTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Νέα Δεδομένα
        logTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Χρόνος
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        return tableContainer;
    }
    
    /**
     * Creates the bottom panel with navigation and action buttons.
     *
     * @return the configured bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Previous button (left side)
        JButton prevButton = createStyledButton("< Προηγούμενο");
        prevButton.addActionListener(e -> {
            // Return to DataEditorPanel with saved parameters
            mainFrame.showDataEditorPanel(returnYear, returnDataType);
        });
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(prevButton);
        
        // Refresh button (right side)
        JButton refreshButton = createStyledButton("Ανανέωση");
        refreshButton.addActionListener(e -> {
            loadLogData();
            JOptionPane.showMessageDialog(
                this,
                "Τα δεδομένα ανανεώθηκαν επιτυχώς!",
                "Ανανέωση",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshButton);
        
        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(rightPanel, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    /**
     * Creates a styled button with consistent appearance.
     *
     * @param text the button text
     * @return the configured button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(NAVY_BLUE);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(new RoundedBorder(20));
        button.setFocusPainted(false);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 240, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    /**
     * Loads log data from the database and populates the table.
     */
    private void loadLogData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            List<Log> logs = logDao.selectLog();
            
            System.out.println("Logs found: " + logs.size()); // Debug
            
            if (logs.isEmpty()) {
                // Don't show message, just leave table empty
                System.out.println("No logs found in database.");
                return;
            }
            
            // Add each log entry to the table
            for (Log log : logs) {
                Object[] row = {
                    log.getId(),
                    log.getTableName() != null ? log.getTableName() : "N/A",
                    log.getOperation() != null ? log.getOperation() : "N/A",
                    log.getRowId() != null ? log.getRowId() : "N/A",
                    truncateText(log.getOldData(), 50),
                    truncateText(log.getNewData(), 50),
                    log.getTimestamp() != null ? log.getTimestamp() : "N/A"
                };
                tableModel.addRow(row);
            }
            
            System.out.println("Logs loaded successfully!");
            
        } catch (Exception e) {
            System.err.println("Error loading logs: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(
                this,
                "Σφάλμα κατά τη φόρτωση των δεδομένων:\n" + e.getMessage(),
                "Σφάλμα",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Truncates text to a maximum length and adds ellipsis if needed.
     *
     * @param text the text to truncate
     * @param maxLength the maximum length
     * @return the truncated text
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
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
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}