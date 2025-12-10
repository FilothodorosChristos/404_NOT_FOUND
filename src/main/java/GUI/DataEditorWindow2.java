package GUI;

import dao.CashFlow;
import dao.Foreis;
import service.CashFlowService;
import service.ForeisService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Παράθυρο για εμφάνιση και επεξεργασία δεδομένων από τη βάση.
 * Υποστηρίζει τόσο CashFlows όσο και Foreis με δύο πίνακες (Έσοδα/Έξοδα).
 * Όλες οι λειτουργίες γίνονται μέσω των Service κλάσεων.
 * Οι αλλαγές αποθηκεύονται αυτόματα κατά την επεξεργασία.
 */
public class DataEditorWindow2 extends JFrame {

  private static final Color NAVY_BLUE = new Color(0, 0, 128);
  private static final Color LIGHT_BLUE = new Color(173, 216, 230);
  private static final Color LIGHT_GREEN = new Color(200, 230, 200);
  private static final Font TITLE_FONT = new Font("Tahoma", Font.BOLD, 18);
  private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);
  private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);

  private JTable incomeTable;
  private JTable expenseTable;
  private DefaultTableModel incomeTableModel;
  private DefaultTableModel expenseTableModel;
  private String dataType; // "cashflow" ή "foreis"
  private int selectedYear;

  private final CashFlowService cashFlowService;
  private final ForeisService foreisService;
  
  private BufferedImage backgroundImage;

  /**
   * Custom JPanel με background image
   */
  class BackgroundPanel extends JPanel {
    private BufferedImage image;
    
    public BackgroundPanel(BufferedImage img) {
      this.image = img;
      setLayout(new BorderLayout(10, 10));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (image != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
      }
    }
  }

  /**
   * Constructor - Δέχεται έτος και κατηγορία δεδομένων.
   *
   * @param year Το έτος που επέλεξε ο χρήστης (π.χ. 2023, 2024, 2025)
   * @param dataType "cashflow" ή "foreis"
   */
  public DataEditorWindow2(int year, String dataType) {
    this.selectedYear = year;
    this.dataType = dataType;
    this.cashFlowService = new CashFlowService();
    this.foreisService = new ForeisService();
    
    loadBackgroundImage();
    initializeUI();
    loadData();
  }

  /**
   * Φόρτωση background image
   */
  private void loadBackgroundImage() {
    try {
      // Προσπάθεια να βρει την εικόνα σε διάφορα paths
      String[] possiblePaths = {
        "BackroundPhoto.jpg",
        "src/BackroundPhoto.jpg",
        "resources/BackroundPhoto.jpg",
        "GUI/BackroundPhoto.jpg"
      };
      
      for (String path : possiblePaths) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
          backgroundImage = ImageIO.read(imageFile);
          System.out.println("Background image loaded from: " + path);
          return;
        }
      }
      
      System.out.println("Warning: BackroundPhoto.jpg not found in common locations");
      
    } catch (Exception e) {
      System.err.println("Error loading background image: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Αρχικοποίηση του UI.
   */
  private void initializeUI() {
    String windowTitle = "cashflow".equalsIgnoreCase(dataType)
            ? "Επεξεργασία Ταμειακών Ροών"
            : "Επεξεργασία Φορέων";

    setTitle(windowTitle);
    setSize(1400, 800);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    // Χρήση BackgroundPanel ως main panel
    BackgroundPanel mainPanel = new BackgroundPanel(backgroundImage);
    setContentPane(mainPanel);

    // Header Panel
    JPanel headerPanel = createHeaderPanel();
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Split Panel με δύο πίνακες
    JSplitPane splitPane = createSplitTablePanel();
    mainPanel.add(splitPane, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = createButtonPanel();
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Info Panel
    
  }

  /**
   * Δημιουργία header panel με τίτλο και πληροφορίες.
   */
  private JPanel createHeaderPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(0, 0, 128, 230)); // Semi-transparent
    panel.setPreferredSize(new Dimension(0, 80));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    // Τίτλος
    String title = "cashflow".equalsIgnoreCase(dataType)
            ? "Ταμειακές Ροές"
            : "Φορείς";
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
    titleLabel.setForeground(Color.WHITE);

    // Πληροφορίες
    String info = String.format("<html>Έτος: <b>%d</b> | Εμφάνιση: <b>Έσοδα & Έξοδα</b></html>",
            selectedYear);
    JLabel infoLabel = new JLabel(info);
    infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
    infoLabel.setForeground(LIGHT_BLUE);

    // Layout
    JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    textPanel.setOpaque(false);
    textPanel.add(titleLabel);
    textPanel.add(infoLabel);

    panel.add(textPanel, BorderLayout.WEST);

    return panel;
  }

  /**
   * Δημιουργία split panel με δύο πίνακες (Έσοδα & Έξοδα).
   */
  private JSplitPane createSplitTablePanel() {
    // Πίνακας Εσόδων
    JPanel incomePanel = createTablePanel("Έσοδα", "Έσοδο", true);
    
    // Πίνακας Εξόδων
    JPanel expensePanel = createTablePanel("Έξοδα", "Έξοδο", false);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, incomePanel, expensePanel);
    splitPane.setDividerLocation(350);
    splitPane.setResizeWeight(0.5);
    splitPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    splitPane.setOpaque(false);

    return splitPane;
  }

  /**
   * Δημιουργία table panel για Έσοδα ή Έξοδα.
   */
  private JPanel createTablePanel(String title, String type, boolean isIncome) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(isIncome ? LIGHT_GREEN : Color.WHITE),
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            TITLE_FONT,
            Color.WHITE
    ));

    // Δημιουργία table model ανάλογα με τον τύπο δεδομένων
    DefaultTableModel tableModel;
    JTable table;

    if ("cashflow".equalsIgnoreCase(dataType)) {
      String[] columnNames = {"ID", "Έτος", "Τύπος", "Όνομα", "Ποσό (€)"};
      tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          // Μόνο το όνομα (3) και το ποσό (4) είναι επεξεργάσιμα
          return column == 3 || column == 4;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
          if (columnIndex == 0 || columnIndex == 1) {
            return Integer.class;
          }
          if (columnIndex == 4) {
            return Double.class;
          }
          return String.class;
        }
      };
    } else {
      String[] columnNames = {"ID", "Foreas ID", "Έτος", "Τύπος", "Όνομα",
              "Τακτικός Π/Υ (€)", "Δημόσιες Επενδύσεις (€)", "Σύνολο (€)"};
      tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 4 || column == 5 || column == 6 || column == 7;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
          if (columnIndex <= 2) {
            return Integer.class;
          }
          if (columnIndex >= 5) {
            return Double.class;
          }
          return String.class;
        }
      };
    }

    table = new JTable(tableModel);
    table.setFont(TABLE_FONT);
    table.setRowHeight(25);
    table.getTableHeader().setFont(HEADER_FONT);
    table.getTableHeader().setBackground(isIncome ? LIGHT_GREEN : new Color(255, 200, 200));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setGridColor(Color.LIGHT_GRAY);
    table.setShowGrid(true);
    table.setOpaque(true);

    // Αυτόματη αποθήκευση κατά την επεξεργασία
    tableModel.addTableModelListener(e -> {
      if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
        int row = e.getFirstRow();
        if (row >= 0) {
          autoSaveRow(row, table, tableModel, type);
        }
      }
    });

    // Ρύθμιση πλάτους στηλών
    adjustColumnWidths(table);

    table.setSelectionBackground(new Color(184, 207, 229));
    table.setSelectionForeground(Color.BLACK);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Κουμπιά προσθήκης/διαγραφής για κάθε πίνακα
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    buttonPanel.setOpaque(false);

    JButton addButton = createMiniButton("➕ Προσθήκη", new Color(33, 150, 243));
    JButton deleteButton = createMiniButton("🗑️ Διαγραφή", new Color(244, 67, 54));

    addButton.addActionListener(e -> addNewRow(type));
    deleteButton.addActionListener(e -> deleteSelectedRow(table, tableModel, type));

    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    // Αποθήκευση αναφορών
    if (isIncome) {
      incomeTable = table;
      incomeTableModel = tableModel;
    } else {
      expenseTable = table;
      expenseTableModel = tableModel;
    }

    return panel;
  }

  /**
   * Ρύθμιση πλάτους στηλών ανάλογα με το περιεχόμενο.
   */
  private void adjustColumnWidths(JTable table) {
    TableColumnModel columnModel = table.getColumnModel();

    if ("cashflow".equalsIgnoreCase(dataType)) {
      columnModel.getColumn(0).setPreferredWidth(50);   // ID
      columnModel.getColumn(1).setPreferredWidth(70);   // Έτος
      columnModel.getColumn(2).setPreferredWidth(80);   // Τύπος
      columnModel.getColumn(3).setPreferredWidth(250);  // Όνομα
      columnModel.getColumn(4).setPreferredWidth(120);  // Ποσό
    } else {
      columnModel.getColumn(0).setPreferredWidth(40);   // ID
      columnModel.getColumn(1).setPreferredWidth(60);   // Foreas ID
      columnModel.getColumn(2).setPreferredWidth(60);   // Έτος
      columnModel.getColumn(3).setPreferredWidth(100);  // Τύπος
      columnModel.getColumn(4).setPreferredWidth(180);  // Όνομα
      columnModel.getColumn(5).setPreferredWidth(130);  // Τακτικός Π/Υ
      columnModel.getColumn(6).setPreferredWidth(130);  // Δημόσιες Επενδύσεις
      columnModel.getColumn(7).setPreferredWidth(130);  // Σύνολο
    }
  }

 

  /**
   * Δημιουργία button panel με λειτουργίες.
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
    panel.setBackground(new Color(240, 240, 240, 220)); // Semi-transparent
    panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

    JButton closeButton = createStyledButton("✖️ Κλείσιμο", new Color(96, 125, 139));

    closeButton.addActionListener(e -> {
      int result = JOptionPane.showConfirmDialog(
              this,
              "Είστε σίγουροι ότι θέλετε να κλείσετε;",
              "Επιβεβαίωση Κλεισίματος",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE
      );
      if (result == JOptionPane.YES_OPTION) {
        dispose();
      }
    });

    panel.add(closeButton);

    return panel;
  }

  /**
   * Δημιουργία styled button.
   */
  private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.BOLD, 13));
    button.setPreferredSize(new Dimension(170, 45));
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hover effect
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        button.setBackground(bgColor.brighter());
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setBackground(bgColor);
      }
    });

    return button;
  }

  /**
   * Δημιουργία mini button για τους πίνακες.
   */
 private JButton createMiniButton(String text, Color borderColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.BOLD, 11));
    button.setPreferredSize(new Dimension(120, 30));

    // ΔΙΑΦΑΝΟ ΛΕΥΚΟ BACKGROUND
    button.setBackground(new Color(255, 255, 255, 120));
    button.setOpaque(true);

    // ΛΕΥΚΑ ΓΡΑΜΜΑΤΑ
    button.setForeground(new Color(0,0,128));

   

    // ΧΩΡΙΣ hover αλλαγές
    button.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {}
        public void mouseExited(java.awt.event.MouseEvent evt) {}
        public void mousePressed(java.awt.event.MouseEvent evt) {}
        public void mouseReleased(java.awt.event.MouseEvent evt) {}
    });

    return button;
}
  /**
   * Φόρτωση δεδομένων από τη βάση μέσω των Service κλάσεων.
   */
  private void loadData() {
    incomeTableModel.setRowCount(0);
    expenseTableModel.setRowCount(0);

    try {
      if ("cashflow".equalsIgnoreCase(dataType)) {
        loadCashFlowData("Έσοδο", incomeTableModel);
        loadCashFlowData("Έξοδο", expenseTableModel);
      } else if ("foreis".equalsIgnoreCase(dataType)) {
        loadForeisData("Έσοδο", incomeTableModel);
        loadForeisData("Έξοδο", expenseTableModel);
      }

      updateStatistics();

    } catch (Exception e) {
      showError("Σφάλμα φόρτωσης δεδομένων: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Φόρτωση CashFlow δεδομένων μέσω CashFlowService.
   */
  private void loadCashFlowData(String type, DefaultTableModel tableModel) {
    List<CashFlow> cashFlows = cashFlowService.getCashflows(selectedYear, type);

    for (CashFlow cf : cashFlows) {
      Object[] row = {
              cf.getId(),
              cf.getYearId(),
              cf.getType(),
              cf.getName(),
              cf.getAmount()
      };
      tableModel.addRow(row);
    }
  }

  /**
   * Φόρτωση Foreis δεδομένων μέσω ForeisService.
   */
  private void loadForeisData(String type, DefaultTableModel tableModel) {
    List<Foreis> foreisList = foreisService.getForeisByYearAndType(selectedYear, type);

    for (Foreis f : foreisList) {
      Object[] row = {
              f.getId(),
              f.getForeasId(),
              f.getYearId(),
              f.getType(),
              f.getName(),
              f.getRegularBudget(),
              f.getPublicInvBudget(),
              f.getTotal()
      };
      tableModel.addRow(row);
    }
  }

  /**
   * Ενημέρωση στατιστικών.
   */
  private void updateStatistics() {
    int incomeCount = incomeTableModel.getRowCount();
    int expenseCount = expenseTableModel.getRowCount();
    double incomeSum = 0.0;
    double expenseSum = 0.0;

    if ("cashflow".equalsIgnoreCase(dataType)) {
      for (int i = 0; i < incomeCount; i++) {
        incomeSum += Double.parseDouble(incomeTableModel.getValueAt(i, 4).toString());
      }
      for (int i = 0; i < expenseCount; i++) {
        expenseSum += Double.parseDouble(expenseTableModel.getValueAt(i, 4).toString());
      }
    } else if ("foreis".equalsIgnoreCase(dataType)) {
      for (int i = 0; i < incomeCount; i++) {
        incomeSum += Double.parseDouble(incomeTableModel.getValueAt(i, 7).toString());
      }
      for (int i = 0; i < expenseCount; i++) {
        expenseSum += Double.parseDouble(expenseTableModel.getValueAt(i, 7).toString());
      }
    }

    String title = String.format("Επεξεργασία - Έσοδα: %d (%.2f €) | Έξοδα: %d (%.2f €)",
            incomeCount, incomeSum, expenseCount, expenseSum);
    setTitle(title);
  }

  /**
   * Αυτόματη αποθήκευση γραμμής κατά την επεξεργασία.
   */
  private void autoSaveRow(int row, JTable table, DefaultTableModel tableModel, String type) {
    try {
      if ("cashflow".equalsIgnoreCase(dataType)) {
        saveCashFlowRow(row, tableModel);
      } else if ("foreis".equalsIgnoreCase(dataType)) {
        saveForeisRow(row, tableModel);
      }
      updateStatistics();
    } catch (Exception e) {
      showError("Σφάλμα αυτόματης αποθήκευσης: " + e.getMessage());
      loadData(); // Επαναφόρτωση δεδομένων σε περίπτωση λάθους
    }
  }

  /**
   * Αποθήκευση γραμμής CashFlow μέσω CashFlowService.
   */
  private void saveCashFlowRow(int row, DefaultTableModel tableModel) {
    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    int yearId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
    String type = tableModel.getValueAt(row, 2).toString();
    String name = tableModel.getValueAt(row, 3).toString().trim();
    double amount = Double.parseDouble(tableModel.getValueAt(row, 4).toString());

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Το όνομα δεν μπορεί να είναι κενό!");
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Το ποσό δεν μπορεί να είναι αρνητικό!");
    }

    CashFlow cf = new CashFlow(id, yearId, type, name, amount);
    cashFlowService.updateCashflow(cf);
  }

  /**
   * Αποθήκευση γραμμής Foreis μέσω ForeisService.
   */
  private void saveForeisRow(int row, DefaultTableModel tableModel) {
    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    int foreasId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
    int yearId = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
    String type = tableModel.getValueAt(row, 3).toString();
    String name = tableModel.getValueAt(row, 4).toString().trim();
    double regularBudget = Double.parseDouble(tableModel.getValueAt(row, 5).toString());
    double publicInvBudget = Double.parseDouble(tableModel.getValueAt(row, 6).toString());
    double total = Double.parseDouble(tableModel.getValueAt(row, 7).toString());

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Το όνομα δεν μπορεί να είναι κενό!");
    }
    if (regularBudget < 0 || publicInvBudget < 0 || total < 0) {
      throw new IllegalArgumentException("Τα ποσά δεν μπορούν να είναι αρνητικά!");
    }

    Foreis f = new Foreis(id, foreasId, yearId, type, name,
            regularBudget, publicInvBudget, total);
    foreisService.updateForeis(f);
  }

  /**
   * Προσθήκη νέας γραμμής μέσω των Service κλάσεων.
   */
  private void addNewRow(String type) {
    try {
      if ("cashflow".equalsIgnoreCase(dataType)) {
        addNewCashFlow(type);
      } else if ("foreis".equalsIgnoreCase(dataType)) {
        addNewForeis(type);
      }
    } catch (Exception e) {
      showError("Σφάλμα προσθήκης: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Προσθήκη νέου CashFlow μέσω dialog.
   */
  private void addNewCashFlow(String type) {
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField nameField = new JTextField(20);
    JTextField amountField = new JTextField(20);

    panel.add(new JLabel("Όνομα:"));
    panel.add(nameField);
    panel.add(new JLabel("Ποσό (€):"));
    panel.add(amountField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Προσθήκη Νέας Ταμειακής Ροής - " + type,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
      String name = nameField.getText().trim();
      String amountStr = amountField.getText().trim();

      if (name.isEmpty() || amountStr.isEmpty()) {
        throw new IllegalArgumentException("Όλα τα πεδία είναι υποχρεωτικά!");
      }

      double amount = Double.parseDouble(amountStr);
      if (amount < 0) {
        throw new IllegalArgumentException("Το ποσό δεν μπορεί να είναι αρνητικό!");
      }

      CashFlow cf = new CashFlow(0, selectedYear, type, name, amount);
      cashFlowService.addCashflow(cf);

      showInfo("Η νέα εγγραφή προστέθηκε επιτυχώς!");
      loadData();
    }
  }

  /**
   * Προσθήκη νέου Foreis μέσω dialog.
   */
  private void addNewForeis(String type) {
    JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField foreasIdField = new JTextField(20);
    JTextField nameField = new JTextField(20);
    JTextField regularField = new JTextField(20);
    JTextField publicField = new JTextField(20);
    JTextField totalField = new JTextField(20);
    totalField.setEditable(false);
    totalField.setBackground(Color.LIGHT_GRAY);

    panel.add(new JLabel("Foreas ID:"));
    panel.add(foreasIdField);
    panel.add(new JLabel("Όνομα:"));
    panel.add(nameField);
    panel.add(new JLabel("Τακτικός Π/Υ (€):"));
    panel.add(regularField);
    panel.add(new JLabel("Δημόσιες Επενδύσεις (€):"));
    panel.add(publicField);
    panel.add(new JLabel("Σύνολο (€):"));
    panel.add(totalField);

    regularField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        calculateTotal(regularField, publicField, totalField);
      }
    });
    publicField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        calculateTotal(regularField, publicField, totalField);
      }
    });

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Προσθήκη Νέου Φορέα - " + type,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
      String foreasIdStr = foreasIdField.getText().trim();
      String name = nameField.getText().trim();
      String regularStr = regularField.getText().trim();
      String publicStr = publicField.getText().trim();

      if (foreasIdStr.isEmpty() || name.isEmpty() || regularStr.isEmpty() || publicStr.isEmpty()) {
        throw new IllegalArgumentException("Όλα τα πεδία είναι υποχρεωτικά!");
      }

      int foreasId = Integer.parseInt(foreasIdStr);
      double regular = Double.parseDouble(regularStr);
      double publicInv = Double.parseDouble(publicStr);
      double total = regular + publicInv;

      if (regular < 0 || publicInv < 0) {
        throw new IllegalArgumentException("Τα ποσά δεν μπορούν να είναι αρνητικά!");
      }

      Foreis f = new Foreis(0, foreasId, selectedYear, type, name,
              regular, publicInv, total);
      foreisService.addForeis(f);

      showInfo("Ο νέος φορέας προστέθηκε επιτυχώς!");
      loadData();
    }
  }

  /**
   * Υπολογισμός συνολικού ποσού για Foreis.
   */
  private void calculateTotal(JTextField regularField, JTextField publicField, JTextField totalField) {
    try {
      double regular = Double.parseDouble(regularField.getText().trim());
      double publicInv = Double.parseDouble(publicField.getText().trim());
      totalField.setText(String.format("%.2f", regular + publicInv));
    } catch (NumberFormatException e) {
      totalField.setText("0.00");
    }
  }

  /**
   * Διαγραφή επιλεγμένης γραμμής μέσω των Service κλάσεων.
   */
  private void deleteSelectedRow(JTable table, DefaultTableModel tableModel, String type) {
    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
      showWarning("Παρακαλώ επιλέξτε μια γραμμή για διαγραφή.");
      return;
    }

    String rowInfo;
    if ("cashflow".equalsIgnoreCase(dataType)) {
      String name = tableModel.getValueAt(selectedRow, 3).toString();
      double amount = Double.parseDouble(tableModel.getValueAt(selectedRow, 4).toString());
      rowInfo = String.format("%s (%.2f €)", name, amount);
    } else {
      String name = tableModel.getValueAt(selectedRow, 4).toString();
      double total = Double.parseDouble(tableModel.getValueAt(selectedRow, 7).toString());
      rowInfo = String.format("%s (%.2f €)", name, total);
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            String.format("Είστε σίγουροι ότι θέλετε να διαγράψετε:%n%n%s%n%n" +
                    "Αυτή η ενέργεια δεν μπορεί να αναιρεθεί!", rowInfo),
            "Επιβεβαίωση Διαγραφής",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

      if ("cashflow".equalsIgnoreCase(dataType)) {
        cashFlowService.deleteCashflow(id);
      } else if ("foreis".equalsIgnoreCase(dataType)) {
        foreisService.deleteForeis(id);
      }

      showInfo("Η εγγραφή διαγράφηκε επιτυχώς!");
      loadData();

    } catch (Exception e) {
      showError("Σφάλμα διαγραφής: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Helper methods για εμφάνιση μηνυμάτων.
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Σφάλμα", JOptionPane.ERROR_MESSAGE);
  }

  private void showInfo(String message) {
    JOptionPane.showMessageDialog(this, message, "Πληροφορία",
            JOptionPane.INFORMATION_MESSAGE);
  }

  private void showWarning(String message) {
    JOptionPane.showMessageDialog(this, message, "Προειδοποίηση",
            JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Test method - Παράδειγμα χρήσης.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // Αρχικοποίηση βάσης δεδομένων
      database.DatabaseSetup.setDatabase();
      database.DataImporter.importer();

      // Παράδειγμα: CashFlows για το 2023
      DataEditorWindow2 cashflowWindow = new DataEditorWindow2(2023, "cashflow");
      cashflowWindow.setVisible(true);
    });
  }
}