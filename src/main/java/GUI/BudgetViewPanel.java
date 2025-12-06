package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import service.CashFlowService;
import service.ForeisService;
import dao.CashFlow;
import dao.Foreis;

/**
 * BudgetViewPanel displays budget data in two tables:
 * one for CashFlow and one for Foreis.
 * Data is filtered by year and type selected by the user.
 */
public class BudgetViewPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private final CashFlowService cashFlowService;
    private final ForeisService foreisService;
    
    private static final Color NAVY_BLUE = new Color(0, 0, 128);
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);
    private static final Font TITLE_FONT = new Font("Tahoma", Font.BOLD, 24);
    private static final Font SECTION_FONT = new Font("Tahoma", Font.BOLD, 18);
    private static final Font TABLE_HEADER_FONT = new Font("Tahoma", Font.BOLD, 12);
    private static final Font TABLE_FONT = new Font("Tahoma", Font.PLAIN, 11);
    
    private JTable cashFlowTable;
    private JTable foreisTable;
    private DefaultTableModel cashFlowTableModel;
    private DefaultTableModel foreisTableModel;
    
    /**
     * Constructs a BudgetViewPanel with the specified MainFrame reference.
     *
     * @param mainFrame the main application frame
     */
    public BudgetViewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cashFlowService = new CashFlowService();
        this.foreisService = new ForeisService();
        setLayout(new BorderLayout());
        createUI();
        loadData();
    }
    
    /**
     * Creates the user interface components.
     */
    private void createUI() {
        // Background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainFrame.getBackgroundImage() != null) {
                    g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        // Main content panel with white background
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Προβολή Προϋπολογισμού - Έτος " + mainFrame.getSelectedYear());
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(NAVY_BLUE);
        titlePanel.add(titleLabel);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        
        // Tables panel (split into two sections)
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.setOpaque(false);
        
        // CashFlow table section
        JPanel cashFlowSection = createTableSection("Ταμειακές Ροές (CashFlow)", createCashFlowTable());
        tablesPanel.add(cashFlowSection);
        
        // Foreis table section
        JPanel foreisSection = createTableSection("Φορείς (Foreis)", createForeisTable());
        tablesPanel.add(foreisSection);
        
        // Add components to content panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        
        // Previous button
        JButton prevButton = new JButton("< Προηγούμενο");
        prevButton.setPreferredSize(new Dimension(150, 40));
        prevButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        prevButton.addActionListener(e -> mainFrame.showPanel(MainFrame.ACTION_SELECTION));
        
        JPanel prevButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prevButtonPanel.setOpaque(false);
        prevButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        prevButtonPanel.add(prevButton);
        
        contentPanel.add(prevButtonPanel, BorderLayout.SOUTH);
        
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        add(backgroundPanel);
    }
    
    /**
     * Creates the filter panel with type selection combo boxes.
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel("Τύπος:");
        typeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"income", "expense"});
        typeCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(150, 30));
        
        JButton loadButton = new JButton("Φόρτωση Δεδομένων");
        loadButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loadButton.setBackground(NAVY_BLUE);
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        
        loadButton.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            loadDataByType(selectedType);
        });
        
        filterPanel.add(typeLabel);
        filterPanel.add(typeCombo);
        filterPanel.add(loadButton);
        
        return filterPanel;
    }
    
    /**
     * Creates a table section with title and scrollable table.
     */
    private JPanel createTableSection(String title, JScrollPane tableScrollPane) {
        JPanel section = new JPanel(new BorderLayout(5, 5));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NAVY_BLUE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(SECTION_FONT);
        sectionLabel.setForeground(NAVY_BLUE);
        
        section.add(sectionLabel, BorderLayout.NORTH);
        section.add(tableScrollPane, BorderLayout.CENTER);
        
        return section;
    }
    
    /**
     * Creates and configures the CashFlow table.
     */
    private JScrollPane createCashFlowTable() {
        String[] columnNames = {"ID", "Year ID", "Type", "Name", "Amount (€)"};
        cashFlowTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        cashFlowTable = new JTable(cashFlowTableModel);
        styleTable(cashFlowTable);
        
        // Set column widths
        cashFlowTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        cashFlowTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        cashFlowTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cashFlowTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        cashFlowTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(cashFlowTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        return scrollPane;
    }
    
    /**
     * Creates and configures the Foreis table.
     */
    private JScrollPane createForeisTable() {
        String[] columnNames = {"ID", "Foreas ID", "Year ID", "Type", "Name", 
                                "Regular Budget (€)", "Public Inv Budget (€)", "Total (€)"};
        foreisTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        foreisTable = new JTable(foreisTableModel);
        styleTable(foreisTable);
        
        // Set column widths
        foreisTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        foreisTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        foreisTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        foreisTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        foreisTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        foreisTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        foreisTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        foreisTable.getColumnModel().getColumn(7).setPreferredWidth(110);
        
        JScrollPane scrollPane = new JScrollPane(foreisTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        return scrollPane;
    }
    
    /**
     * Applies consistent styling to a table.
     */
    private void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(25);
        table.setGridColor(LIGHT_BLUE);
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(NAVY_BLUE);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(NAVY_BLUE);
        header.setForeground(Color.WHITE);
    }
    
    /**
     * Loads data for both tables with default type "income".
     */
    private void loadData() {
        loadDataByType("income");
    }
    
    /**
     * Loads data for both tables based on selected type.
     */
    private void loadDataByType(String type) {
        try {
            String yearStr = mainFrame.getSelectedYear();
            if (yearStr == null) {
                JOptionPane.showMessageDialog(this, 
                    "Δεν έχει επιλεγεί έτος!", 
                    "Σφάλμα", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int year = Integer.parseInt(yearStr);
            
            // Load CashFlow data
            loadCashFlowData(year, type);
            
            // Load Foreis data
            loadForeisData(year, type);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Μη έγκυρο έτος: " + mainFrame.getSelectedYear(), 
                "Σφάλμα", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Σφάλμα κατά τη φόρτωση δεδομένων: " + e.getMessage(), 
                "Σφάλμα", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Loads CashFlow data into the table.
     */
    private void loadCashFlowData(int year, String type) {
        cashFlowTableModel.setRowCount(0); // Clear existing data
        
        List<CashFlow> cashFlows = cashFlowService.getCashflows(year, type);
        
        for (CashFlow cf : cashFlows) {
            Object[] row = {
                cf.getId(),
                cf.getYearId(),
                cf.getType(),
                cf.getName(),
                String.format("%.2f", cf.getAmount())
            };
            cashFlowTableModel.addRow(row);
        }
        
        if (cashFlows.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Δεν βρέθηκαν δεδομένα CashFlow για έτος " + year + " και τύπο " + type, 
                "Πληροφορία", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Loads Foreis data into the table.
     */
    private void loadForeisData(int year, String type) {
        foreisTableModel.setRowCount(0); // Clear existing data
        
        List<Foreis> foreisList = foreisService.getForeisByYearAndType(year, type);
        
        for (Foreis f : foreisList) {
            Object[] row = {
                f.getId(),
                f.getForeasId(),
                f.getYearId(),
                f.getType(),
                f.getName(),
                String.format("%.2f", f.getRegularBudget()),
                String.format("%.2f", f.getPublicInvBudget()),
                String.format("%.2f", f.getTotal())
            };
            foreisTableModel.addRow(row);
        }
        
        if (foreisList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Δεν βρέθηκαν δεδομένα Foreis για έτος " + year + " και τύπο " + type, 
                "Πληροφορία", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}