package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.List;
import service.CashFlowService;
import service.ForeisService;
import dao.CashFlow;
import dao.Foreis;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import util.PdfExporter;
import java.util.ArrayList;
import java.io.File;

/**
 * BudgetViewPanel displays budget data in a single table.
 * User can select between "Φορείς", "Έσοδα", or "Έξοδα".
 * Data is filtered by year and selection.
 */
public class BudgetViewPanel extends JPanel {

  private final MainFrame mainFrame;
  private final CashFlowService cashFlowService;
  private final ForeisService foreisService;
  private static final Color DARK_BG = new Color(15, 23, 42);
  private static final Color DARKER_BG = new Color(8, 15, 30);
  private static final Color CARD_BG = new Color(30, 41, 59);
  private static final Color ACCENT_BLUE = new Color(37, 99, 235);
  private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
  private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
  private static final Color BORDER_COLOR = new Color(51, 65, 85);
  private static final Color TABLE_HEADER_BG = new Color(30, 41, 59);
  private static final Color TABLE_ROW_ALT = new Color(20, 30, 48);
  private static final Color SUCCESS_GREEN = new Color(16, 185, 129);
  
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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Necessary for GUI communication")

    public BudgetViewPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.cashFlowService = new CashFlowService();
    this.foreisService = new ForeisService();
    setLayout(new BorderLayout());
    setBackground(DARK_BG);
    createUI();
    loadData();
  }

  /**
   * Creates the user interface components.
   */
  private void createUI() {
    // Main content panel
    JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
    contentPanel.setBackground(DARK_BG);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

    // Title panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(DARK_BG);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
    JLabel titleLabel = new JLabel("Προβολή Προϋπολογισμού - Έτος " + mainFrame.getSelectedYear());
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titleLabel.setForeground(TEXT_PRIMARY);
    headerPanel.add(titleLabel, BorderLayout.NORTH);

    // Filter panel
    JPanel filterPanel = createFilterPanel();
    headerPanel.add(filterPanel, BorderLayout.CENTER);

    // Table section
    tableSection = new JPanel(new BorderLayout(0, 10));
    tableSection.setBackground(DARKER_BG);
    tableSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

    sectionLabel = new JLabel("Επιλέξτε κατηγορία");
    sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    sectionLabel.setForeground(TEXT_PRIMARY);
    tableSection.add(sectionLabel, BorderLayout.NORTH);

    contentPanel.add(headerPanel, BorderLayout.NORTH);
    contentPanel.add(tableSection, BorderLayout.CENTER);

    // Previous button
    JButton prevButton = new JButton("← Προηγούμενο");
    prevButton.setUI(new BasicButtonUI());
    prevButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    prevButton.setPreferredSize(new Dimension(160, 38));
    prevButton.setBackground(ACCENT_BLUE);
    prevButton.setForeground(Color.WHITE);
    prevButton.setOpaque(true);
    prevButton.setBorder(BorderFactory.createEmptyBorder());
    prevButton.setFocusPainted(false);
    prevButton.addActionListener(e -> mainFrame.showPanel(MainFrame.ACTION_SELECTION));

    JPanel prevButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    prevButtonPanel.setBackground(DARK_BG);
    prevButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    prevButtonPanel.add(prevButton);

    contentPanel.add(prevButtonPanel, BorderLayout.SOUTH);

    add(contentPanel);
  }

  /**
   * Creates the filter panel with type selection combo box.
   */
  private JPanel createFilterPanel() {
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
    filterPanel.setBackground(DARK_BG);

    JLabel typeLabel = new JLabel("Κατηγορία:");
    typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    typeLabel.setForeground(TEXT_SECONDARY);

    typeCombo = new JComboBox<>(new String[] { "Φορείς", "Έσοδα", "Έξοδα" });
    styleComboBox(typeCombo);

    JButton loadButton = new JButton("ΦΟΡΤΩΣΗ ΔΕΔΟΜΕΝΩΝ");
    loadButton.setUI(new BasicButtonUI());
    loadButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    loadButton.setPreferredSize(new Dimension(200, 38));
    loadButton.setBackground(ACCENT_BLUE);
    loadButton.setForeground(Color.WHITE);
    loadButton.setOpaque(true);
    loadButton.setBorder(BorderFactory.createEmptyBorder());
    loadButton.setFocusPainted(false);
    loadButton.addActionListener(e -> {
      String selectedType = (String) typeCombo.getSelectedItem();
      loadDataBySelection(selectedType);
    });
    // Στο createFilterPanel() μετά το loadButton:

    JButton exportPdfButton = new JButton("ΕΞΑΓΩΓΗ PDF");
    exportPdfButton.setUI(new BasicButtonUI());
    exportPdfButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    exportPdfButton.setPreferredSize(new Dimension(180, 38));
    exportPdfButton.setBackground(SUCCESS_GREEN);
    exportPdfButton.setForeground(Color.WHITE);
    exportPdfButton.setOpaque(true);
    exportPdfButton.setBorder(BorderFactory.createEmptyBorder());
    exportPdfButton.setFocusPainted(false);
    exportPdfButton.addActionListener(e -> exportCurrentDataToPdf());

    filterPanel.add(exportPdfButton);

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
      columnNames = new String[] { "ID", "ID Φορέα", "Έτος", "Τύπος", "Όνομα",
        "Τακτικός Π/Υ (€)", "Π/Υ Δημ. Επενδύσεων (€)", "Σύνολο (€)" };
    } else {
      columnNames = new String[] { "ID", "Έτος", "Τύπος", "Όνομα", "Ποσό (€)" };
    }

    tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
            public boolean isCellEditable(int row, int column) {
            return false;
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
    scrollPane.getViewport().setBackground(DARKER_BG);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    return scrollPane;
    }

  /**
   * Applies consistent styling to a table.
   */
  private void styleTable(JTable table) {
    table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    table.setRowHeight(45);
    table.setBackground(DARKER_BG);
    table.setForeground(TEXT_PRIMARY);
    table.setGridColor(BORDER_COLOR);
    table.setFillsViewportHeight(true);

    JTableHeader header = table.getTableHeader();
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
          @Override
        public Component getTableCellRendererComponent(JTable t,
                Object v, boolean isS, boolean hasF, int r, int c) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
            label.setBackground(TABLE_HEADER_BG);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
            label.setOpaque(true);
            return label;
        }
        });

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                 boolean isS, boolean hasF, int r, int c) {
              Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
            setForeground(TEXT_PRIMARY);
            setBackground(r % 2 == 0 ? DARKER_BG : TABLE_ROW_ALT);
            setHorizontalAlignment(c == 0 ? LEFT : (c >= 5 || (c == 4 && t.getColumnCount() == 5)) ? RIGHT : LEFT);
            return comp;
          }
        });
  }

  private void styleComboBox(JComboBox<?> combo) {
    combo.setUI(new BasicComboBoxUI() {
        @Override
            protected JButton createArrowButton() {
            JButton b = super.createArrowButton();
            b.setBackground(CARD_BG);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            return b;
        }

        @Override
            protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            return popup;
        }
        });
    combo.setBackground(TABLE_ROW_ALT);
    combo.setForeground(TABLE_ROW_ALT);
    combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    combo.setPreferredSize(new Dimension(140, 38));
        
    combo.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
             Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value,
             index, isSelected, cellHasFocus);
            l.setForeground(Color.WHITE);
            l.setBackground(isSelected ? ACCENT_BLUE : TABLE_ROW_ALT);
            l.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return l;
            }
        });
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
    tableModel.setRowCount(0);

    List<CashFlow> cashFlows = cashFlowService.getCashflows(year, type);

    for (CashFlow cf : cashFlows) {
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
    tableModel.setRowCount(0);

    // Φορτώνουμε όλους τους φορείς (Κεντρική Διοίκηση, Υπουργείο, Αποκεντρωμένη
    // Διοίκηση)
    List<Foreis> foreisListCentered = foreisService.getForeisByYearAndType(year,
         "Κεντρική Διοίκηση");
    List<Foreis> foreisListDepartment = foreisService.getForeisByYearAndType(year, "Υπουργείο");
    List<Foreis> foreisListDecentered = foreisService.getForeisByYearAndType(year,
         "Αποκεντρωμένη Διοίκηση");

    // Addition of Foreis
    for (Foreis f : foreisListCentered) {
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

  private void exportCurrentDataToPdf() {
    try {
      String selectedType = (String) typeCombo.getSelectedItem();
      String yearStr = mainFrame.getSelectedYear();
      int year = Integer.parseInt(yearStr);

      // File chooser για επιλογή τοποθεσίας
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Αποθήκευση PDF");
      fileChooser.setSelectedFile(new File("GoverLens_" + selectedType + "_" + year + ".pdf"));
        
      int userSelection = fileChooser.showSaveDialog(this);
        
      if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String path = fileToSave.getAbsolutePath();
            
        // Εξασφάλιση .pdf extension
        if (!path.toLowerCase().endsWith(".pdf")) {
          path += ".pdf";
        }

        if ("Φορείς".equals(selectedType)) {
          List<Foreis> allForeis = new ArrayList<>();
          allForeis.addAll(foreisService.getForeisByYearAndType(year, "Κεντρική Διοίκηση"));
          allForeis.addAll(foreisService.getForeisByYearAndType(year, "Υπουργείο"));
          allForeis.addAll(foreisService.getForeisByYearAndType(year, "Αποκεντρωμένη Διοίκηση"));
                
          PdfExporter.exportForeisToPdf(allForeis, year, "Όλοι οι Φορείς", path);
        } else if ("Έσοδα".equals(selectedType)) {
          List<CashFlow> cashFlows = cashFlowService.getCashflows(year, "Έσοδο");
          PdfExporter.exportCashFlowToPdf(cashFlows, year, "Έσοδα", path);
        } else if ("Έξοδα".equals(selectedType)) {
          List<CashFlow> cashFlows = cashFlowService.getCashflows(year, "Έξοδο");
          PdfExporter.exportCashFlowToPdf(cashFlows, year, "Έξοδα", path);
        }

        JOptionPane.showMessageDialog(this,
                "Το PDF δημιουργήθηκε επιτυχώς!\n" + path,
                "Επιτυχία",
                JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
            "Σφάλμα κατά τη δημιουργία του PDF:\n" + ex.getMessage(),
            "Σφάλμα",
            JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
}
