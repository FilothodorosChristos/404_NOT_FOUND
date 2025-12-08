package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import service.CashFlowService;
import service.ForeisService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BudgetViewPanel extends JPanel {
    
    private final MainFrame mainFrame;
    
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

    public BudgetViewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
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
     /** NEW: Load CashFlow data directly from CSV files */
    private List<Object[]> loadCashFlowDataFromCSV(int year, String type) {
        List<Object[]> data = new ArrayList<>();
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + 
                     "main" + File.separator + "resources" + File.separator + "data";
        File folder = new File(path);
        File[] files = folder.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".csv") && 
            (name.contains("esoda") || name.contains("exoda") || 
             name.contains("esodatest") || name.contains("exodatest")));
        
        if (files == null) return data;
        
        String yearStr = Integer.toString(year);
        String yearSuffix = yearStr.substring(2);
        String yearPrefix = "b" + yearSuffix;

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            if (!fileName.contains(yearPrefix)) continue;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                
                br.readLine(); // Skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("[;,]");
                    if (parts.length >= 4 && parts[0].trim().equals(yearStr)) {
                        String rowType = parts[1].trim();
                        if (!rowType.equalsIgnoreCase(type)) continue;
                        
                        String name = parts[2].trim();
                        double amount;
                        try {
                            amount = Double.parseDouble(parts[3].trim());
                            if (amount == 0) continue;
                            
                            // Add row directly to table data
                            data.add(new Object[]{
                                data.size() + 1,  // ID
                                year,             // Year ID
                                rowType,          // Type
                                name,             // Name
                                String.format("%.2f", amount)  // Amount
                            });
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading " + fileName + ": " + e.getMessage());
            }
        }
        return data;
    }

    /** NEW: Load Foreis data directly from CSV files */
    private List<Object[]> loadForeisDataFromCSV(int year, String type) {
        List<Object[]> data = new ArrayList<>();
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + 
                     "main" + File.separator + "resources" + File.separator + "data";
        File folder = new File(path);
        File[] files = folder.listFiles((dir, name) -> 
            name.toLowerCase().contains("foreis.csv"));
        
        if (files == null) return data;
        
        String yearStr = Integer.toString(year);
        String yearSuffix = yearStr.substring(2);
        String yearPrefix = "b" + yearSuffix;

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            if (!fileName.contains(yearPrefix)) continue;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                
                br.readLine(); // Skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("[;,]");
                    if (parts.length >= 7) {
                        try {
                            String name = parts[3].trim();
                            double total = Double.parseDouble(parts[6].trim());
                            if (total > 0) {
                                data.add(new Object[]{
                                    data.size() + 1,     // ID
                                    data.size() + 1,     // Foreas ID
                                    year,                // Year ID
                                    type,                // Type
                                    name,                // Name
                                    "0.00",              // Regular Budget (placeholder)
                                    "0.00",              // Public Inv Budget (placeholder)
                                    String.format("%.2f", total)  // Total
                                });
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading " + fileName + ": " + e.getMessage());
            }
        }
        return data;
    }

    /** UPDATED: Now uses direct CSV loading */
    private void loadData() {
        loadDataByType("income");
    }

    private void loadDataByType(String type) {
        try {
            int year = Integer.parseInt(mainFrame.getSelectedYear());
            
            System.out.println("=== LOADING DATA ===");
            System.out.println("Year: " + year + ", Type: " + type);
            
            // Clear tables
            cashFlowTableModel.setRowCount(0);
            foreisTableModel.setRowCount(0);
            
            // Load CashFlow data
            List<Object[]> cashFlowData = loadCashFlowDataFromCSV(year, type);
            for (Object[] row : cashFlowData) {
                cashFlowTableModel.addRow(row);
            }
            System.out.println("CashFlow rows loaded: " + cashFlowData.size());
            
            // Load Foreis data
            List<Object[]> foreisData = loadForeisDataFromCSV(year, type);
            for (Object[] row : foreisData) {
                foreisTableModel.addRow(row);
            }
            System.out.println("Foreis rows loaded: " + foreisData.size());
            
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
