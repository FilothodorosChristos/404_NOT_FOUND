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
 * BudgetViewPanel displays budget data in a single table.
 * User can select between "Φορείς", "Έσοδα", or "Έξοδα".
 * Data is filtered by year and selection.
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

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JPanel tableSection;
    private JLabel sectionLabel;
    private JComboBox<String> typeCombo;

    /**
     * Constructs a BudgetViewPanel with the specified MainFrame reference.
     *
     * @param mainFrame the main application frame
     */
    
     @SuppressWarnings(value = "EI_EXPOSE_REP2")

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

        // Table section (initially empty, will be populated on selection)
        tableSection = new JPanel(new BorderLayout(5, 5));
        tableSection.setOpaque(false);
        tableSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NAVY_BLUE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        sectionLabel = new JLabel("Επιλέξτε κατηγορία");
        sectionLabel.setFont(SECTION_FONT);
        sectionLabel.setForeground(NAVY_BLUE);
        tableSection.add(sectionLabel, BorderLayout.NORTH);

        // Add components to content panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tableSection, BorderLayout.CENTER);

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
     * Creates the filter panel with type selection combo box.
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setOpaque(false);

        JLabel typeLabel = new JLabel("Κατηγορία:");
        typeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        typeCombo = new JComboBox<>(new String[] { "Φορείς", "Έσοδα", "Έξοδα" });
        typeCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(150, 30));

        JButton loadButton = new JButton("Φόρτωση Δεδομένων");
        loadButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loadButton.setBackground(NAVY_BLUE);
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);

        loadButton.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            loadDataBySelection(selectedType);
        });

        filterPanel.add(typeLabel);
        filterPanel.add(typeCombo);
        filterPanel.add(loadButton);

        return filterPanel;
    }

    /**
     * Creates and configures a table based on the selection.
     */
    private JScrollPane createTable(String selection) {
        String[] columnNames;

        if (selection.equals("Φορείς")) {
            columnNames = new String[] { "ID", "Foreas ID", "Year ID", "Type", "Name",
                    "Regular Budget (€)", "Public Inv Budget (€)", "Total (€)" };
        } else {
            columnNames = new String[] { "ID", "Year ID", "Type", "Name", "Amount (€)" };
        }

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        dataTable = new JTable(tableModel);
        styleTable(dataTable);

        // Set column widths based on selection
        if (selection.equals("Φορείς")) {
            dataTable.getColumnModel().getColumn(0).setPreferredWidth(40);
            dataTable.getColumnModel().getColumn(1).setPreferredWidth(70);
            dataTable.getColumnModel().getColumn(2).setPreferredWidth(70);
            dataTable.getColumnModel().getColumn(3).setPreferredWidth(80);
            dataTable.getColumnModel().getColumn(4).setPreferredWidth(200);
            dataTable.getColumnModel().getColumn(5).setPreferredWidth(130);
            dataTable.getColumnModel().getColumn(6).setPreferredWidth(150);
            dataTable.getColumnModel().getColumn(7).setPreferredWidth(110);
        } else {
            dataTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            dataTable.getColumnModel().getColumn(1).setPreferredWidth(70);
            dataTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            dataTable.getColumnModel().getColumn(3).setPreferredWidth(300);
            dataTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        }

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
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
     * Loads data with default selection "Φορείς".
     */
    private void loadData() {
        loadDataBySelection("Φορείς");
    }

    /**
     * Loads data based on user selection.
     */
    private void loadDataBySelection(String selection) {
        try {
            // Παίρνουμε το έτος ως int (π.χ. 2023)
            String yearStr = mainFrame.getSelectedYear();
            int year = Integer.parseInt(yearStr);

            System.out.println("=== DEBUG INFO ===");
            System.out.println("Selected Year String: " + yearStr);
            System.out.println("Selected Year Int: " + year);
            System.out.println("Selected Category: " + selection);

            // Update section label
            sectionLabel.setText(selection);

            // Remove old table if exists
            if (tableSection.getComponentCount() > 1) {
                tableSection.remove(1);
            }

            // Create new table based on selection
            JScrollPane scrollPane = createTable(selection);
            tableSection.add(scrollPane, BorderLayout.CENTER);

            // Load appropriate data
            if (selection.equals("Φορείς")) {
                loadForeisData(year);
            } else if (selection.equals("Έσοδα")) {
                loadCashFlowData(year, "Έσοδο");
            } else if (selection.equals("Έξοδα")) {
                loadCashFlowData(year, "Έξοδο");
            }

            // Refresh the panel
            tableSection.revalidate();
            tableSection.repaint();

        } catch (Exception e) {
            System.err.println("Σφάλμα κατά τη φόρτωση δεδομένων: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads CashFlow data into the table.
     */
    private void loadCashFlowData(int year, String type) {
        tableModel.setRowCount(0); // Clear existing data

        List<CashFlow> cashFlows = cashFlowService.getCashflows(year, type);

        System.out.println("CashFlow results (" + type + "): " + cashFlows.size() + " rows");

        for (CashFlow cf : cashFlows) {
            System.out.println("  - ID: " + cf.getId() + ", Name: " + cf.getName() + ", Amount: " + cf.getAmount());
            Object[] row = {
                    cf.getId(),
                    cf.getYearId(),
                    cf.getType(),
                    cf.getName(),
                    String.format("%.2f", cf.getAmount())
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Loads Foreis data into the table (all types).
     */
    private void loadForeisData(int year) {
        tableModel.setRowCount(0); // Clear existing data

        // Φορτώνουμε όλους τους φορείς (Κεντρική Διοίκηση, Υπουργείο, Αποκεντρωμένη
        // Διοίκηση)
        List<Foreis> foreisListCentered = foreisService.getForeisByYearAndType(year, "Κεντρική Διοίκηση");
        List<Foreis> foreisListDepartment = foreisService.getForeisByYearAndType(year, "Υπουργείο");
        List<Foreis> foreisListDecentered = foreisService.getForeisByYearAndType(year, "Αποκεντρωμένη Διοίκηση");

        System.out.println("Foreis results (Κεντρική Διοίκηση): " + foreisListCentered.size() + " rows");
        System.out.println("Foreis results (Υπουργείο): " + foreisListDepartment.size() + " rows");
        System.out.println("Foreis results (Αποκεντρωμένη Διοίκηση): " + foreisListDecentered.size() + " rows");

        // Προσθήκη όλων των φορέων
        for (Foreis f : foreisListCentered) {
            System.out.println("  - ID: " + f.getId() + ", Name: " + f.getName() + ", Total: " + f.getTotal());
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
            tableModel.addRow(row);
        }

        for (Foreis f : foreisListDepartment) {
            System.out.println("  - ID: " + f.getId() + ", Name: " + f.getName() + ", Total: " + f.getTotal());
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
            tableModel.addRow(row);
        }
        for (Foreis f : foreisListDecentered) {
            System.out.println("  - ID: " + f.getId() + ", Name: " + f.getName() + ", Total: " + f.getTotal());
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
            tableModel.addRow(row);
        }
    }
}
