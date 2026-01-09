package GUI;

import java.awt.*;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import javax.swing.*;

/**
 * A JPanel component that visualizes financial data through bar charts.
 * Displays government budget information including revenues, expenses, and agencies
 * for a specified fiscal year. Data is loaded via DataImporter2 and presented
 * in a tabbed interface with interactive visualizations.
 * 
 * @author YourName
 * @version 1.0
 */
public class FinanceChartPanel extends JPanel {
    
  /** The fiscal year for which data is displayed. */
  private final String year;
    
  /** List of revenue items for the fiscal year. */
  private final List<DataItem> revenues;
    
  /** List of expense items for the fiscal year.*/
  private final List<DataItem> expenses;
    
  /** List of agency budget allocations for the fiscal year.*/
  private final List<DataItem> agencies;
    
  /** Primary background color matching the preview screen. */
  private static final Color DARK_BACKGROUND = new Color(26, 32, 46);
    
  /** Secondary background for cards and panels.*/
  private static final Color CARD_BACKGROUND = new Color(35, 42, 58);
        
  /** Color used for text elements throughout the panel. */
  private static final Color TEXT_COLOR = new Color(220, 225, 235);
    
  /** Accent color for interactive elements.*/
  private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    
  /** Hover color for buttons.*/
  private static final Color ACCENT_HOVER = new Color(96, 165, 250);

  /**
   * Private constructor to enforce factory method pattern.
   * 
   * @param year The fiscal year for the budget data
   * @param revenues List of revenue items
   * @param expenses List of expense items
   * @param agencies List of agency budget items
   */
  private FinanceChartPanel(String year,
      List<DataItem> revenues, List<DataItem> expenses, List<DataItem> agencies) {
    this.year = year;
    this.revenues = revenues;
    this.expenses = expenses;
    this.agencies = agencies;
    setLayout(new BorderLayout());
    setBackground(DARK_BACKGROUND);
  }

  /**
   * Factory method to create a fully initialized FinanceChartPanel.
   * Loads data for the specified year using DataImporter2 and constructs
   * the complete UI with all visualizations.
   * 
   * @param year The fiscal year to display (e.g., "2023")
   * @return A fully configured FinanceChartPanel ready for display
   */
  public static FinanceChartPanel createPanel(String year) {
        
    GraphDataImporter importer = new GraphDataImporter(year);
    importer.loadData();
        
    FinanceChartPanel panel = new FinanceChartPanel(
            year, 
            importer.getRevenues(), 
            importer.getExpenses(), 
            importer.getAgencies()
        );
        
    panel.initializeUI();
        
    return panel;
  }
    
  /**
   * Initializes and assembles all UI components including the top panel
   * with back button and title, and the tabbed pane containing revenue/expense
   * and agency visualizations.
   */
  private void initializeUI() {
    // TOP PANEL: Professional header with back button
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(true);
    topPanel.setBackground(DARK_BACKGROUND);
    topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        
    // Back button - positioned at top-left (matching BudgetViewPanel style)
    JButton backButton = new JButton("← Προηγούμενο");
    backButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    backButton.setPreferredSize(new Dimension(160, 38));
    backButton.setBackground(new Color(37, 99, 235)); // ACCENT_BLUE
    backButton.setForeground(Color.WHITE);
    backButton.setOpaque(true);
    backButton.setBorder(BorderFactory.createEmptyBorder());
    backButton.setFocusPainted(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
    backButton.addActionListener(e -> {
      Container parent = this.getParent();
      while (parent != null && !(parent instanceof MainFrame)) {
        parent = parent.getParent();
      }
      if (parent instanceof MainFrame) {
        ((MainFrame) parent).showPanel(MainFrame.ACTION_SELECTION);
      }
    });
        
    backButton.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            backButton.setBackground(new Color(59, 130, 246)); // Lighter blue on hover
        }
            
        @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            backButton.setBackground(new Color(37, 99, 235));
        }
        });
        
    // Professional title - full width centered
    JLabel mainHeader = new JLabel("Προβολή Προϋπολογισμού - Έτος " + year, SwingConstants.CENTER);
    mainHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
    mainHeader.setForeground(new Color(248, 250, 252)); // TEXT_PRIMARY
        
    // Wrapper for back button
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    leftPanel.setBackground(DARK_BACKGROUND);
    leftPanel.add(backButton);
        
    topPanel.add(leftPanel, BorderLayout.WEST);
    topPanel.add(mainHeader, BorderLayout.CENTER);
        
    add(topPanel, BorderLayout.NORTH);
        
    // Create modern tab buttons panel
    JPanel tabButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    tabButtonPanel.setBackground(DARK_BACKGROUND);
    tabButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
    JButton revenueExpenseButton = createTabButton("Έσοδα/Έξοδα", true);
    JButton agenciesButton = createTabButton("Φορείς", false);
        
    // Remove spacing between buttons for seamless look
    tabButtonPanel.add(revenueExpenseButton);
    tabButtonPanel.add(agenciesButton);
        
    // Create card panel for switching content
    JPanel contentCardPanel = new JPanel(new CardLayout());
    contentCardPanel.setBackground(DARK_BACKGROUND);
        
    JPanel revenueExpensePanel = createRevenueExpensePanel();
    JPanel agenciesPanel = createAgencyPanel();
        
    contentCardPanel.add(revenueExpensePanel, "REVENUE_EXPENSE");
    contentCardPanel.add(agenciesPanel, "AGENCIES");
        
    CardLayout cardLayout = (CardLayout) contentCardPanel.getLayout();
        
    // Button actions with active state management
    revenueExpenseButton.addActionListener(e -> {
      cardLayout.show(contentCardPanel, "REVENUE_EXPENSE");
      setActiveTab(revenueExpenseButton, agenciesButton);
    });
        
    agenciesButton.addActionListener(e -> {
      cardLayout.show(contentCardPanel, "AGENCIES");
      setActiveTab(agenciesButton, revenueExpenseButton);
    });
        
    // Create main content panel
    JPanel mainContentPanel = new JPanel(new BorderLayout());
    mainContentPanel.setBackground(DARK_BACKGROUND);
    mainContentPanel.add(tabButtonPanel, BorderLayout.NORTH);
    mainContentPanel.add(contentCardPanel, BorderLayout.CENTER);
        
    add(mainContentPanel, BorderLayout.CENTER);
  }
    
  /**
   * Creates a modern tab button with consistent styling (matching BudgetViewPanel).
   */
  private JButton createTabButton(String text, boolean isActive) {
    JButton button = new JButton(text);
    button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    button.setFont(new Font("Segoe UI", Font.BOLD, 13));
    button.setPreferredSize(new Dimension(180, 38));
    button.setBackground(isActive ? new Color(37, 99, 235) : new Color(30, 41, 59));
    button.setForeground(Color.WHITE);
    button.setOpaque(true);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            if (button.getBackground().equals(new Color(30, 41, 59))) {
            button.setBackground(new Color(45, 55, 72));
            }
        }
            
        @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            if (!button.getBackground().equals(new Color(37, 99, 235))) {
            button.setBackground(new Color(30, 41, 59));
            }
        }
    });
        
    return button;
  }
    
  /**
   * Sets the active tab styling (matching BudgetViewPanel).
   */
  private void setActiveTab(JButton activeButton, JButton inactiveButton) {
    activeButton.setBackground(new Color(37, 99, 235)); // ACCENT_BLUE
    inactiveButton.setBackground(new Color(30, 41, 59)); // CARD_BG
  }
    
  /**
   * Creates the revenue and expense visualization panel.
   * Displays sorted bar charts for both revenues and expenses, along with
   * a legend panel showing totals and itemized breakdowns.
   * 
   * @return A JPanel containing the complete revenue/expense visualization
   */
  private JPanel createRevenueExpensePanel() {
    List<DataItem> sortedRevenues = revenues.stream()
                                               .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                               .toList();
        
    List<DataItem> sortedExpenses = expenses.stream()
                                               .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                               .toList();
        
        List<DataItem> allItems = Stream.concat(sortedRevenues.stream(), sortedExpenses.stream()).toList();

    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(true);
    panel.setBackground(DARK_BACKGROUND);

    JPanel barPanel = ChartRenderer.createBarChartPanel(allItems, 
                                                            this::revenueGradient, 
                                                            this::expenseGradient, 
                                                            this::formatValueForAxis);
        
    barPanel.setOpaque(true); 
    barPanel.setBackground(DARK_BACKGROUND); 
        
    JScrollPane scrollPane = new JScrollPane(barPanel);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
    scrollPane.getViewport().setBackground(DARK_BACKGROUND); 
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    panel.add(scrollPane, BorderLayout.CENTER);
        
    JPanel legendPanel = createLegendPanel(sortedRevenues, sortedExpenses);
    legendPanel.setOpaque(true); 
    legendPanel.setBackground(CARD_BACKGROUND); 
        
    JScrollPane legendScrollPane = new JScrollPane(legendPanel);
    legendScrollPane.getViewport().setBackground(CARD_BACKGROUND); 
    legendScrollPane.setPreferredSize(new Dimension(380, 0));
    legendScrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 60, 80)));
    legendScrollPane.getVerticalScrollBar().setUnitIncrement(20);
    panel.add(legendScrollPane, BorderLayout.EAST);

    return panel;
  }
    
  /**
   * Creates the agency budget visualization panel.
   * Displays a bar chart showing budget allocations for different government agencies.
   * Returns an empty state panel if no agency data is available.
   * 
   * @return A JPanel containing the agency budget visualization or empty state
   */
  private JPanel createAgencyPanel() {
    if (agencies.isEmpty()) {
      JPanel emptyPanel = new JPanel(new BorderLayout());
      emptyPanel.setOpaque(true);
      emptyPanel.setBackground(DARK_BACKGROUND);
      JLabel msgLabel = new JLabel("Δεν βρέθηκαν δεδομένα φορέων για το έτος " + year,
          SwingConstants.CENTER);
      msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
      msgLabel.setForeground(new Color(150, 160, 180)); 
      emptyPanel.add(msgLabel, BorderLayout.CENTER);
      return emptyPanel;
    }

    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(true);
    panel.setBackground(DARK_BACKGROUND);

    JPanel barPanel = ChartRenderer.createBarChartPanel(agencies, 
                                                            this::agencyBarGradient, 
                                                            null, 
                                                            this::formatValueForAxis);
        
    barPanel.setOpaque(true); 
    barPanel.setBackground(DARK_BACKGROUND); 

    JScrollPane scrollPane = new JScrollPane(barPanel);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
    scrollPane.getViewport().setOpaque(true); 
    scrollPane.getViewport().setBackground(DARK_BACKGROUND);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    panel.add(scrollPane, BorderLayout.CENTER);
        
    return panel;
  }
    
  /**
   * Formats numerical values for axis labels with appropriate Greek units.
   * Converts large numbers into readable format using Greek abbreviations:
   * Τρισ. (Trillion), Δισ. (Billion), Εκατ. (Million), Χιλ. (Thousand).
   * 
   * @param value The numerical value to format
   * @return A formatted string with Greek unit notation
   */
  private String formatValueForAxis(double value) {
    if (value >= 1_000_000_000_000.0) {
      return String.format("%.1f Τρισ. €", value / 1_000_000_000_000.0);
    } else if (value >= 1_000_000_000.0) {
      return String.format("%.1f Δισ. €", value / 1_000_000_000.0);
    } else if (value >= 1_000_000.0) {
      return String.format("%.0f Εκατ. €", value / 1_000_000.0);
    } else if (value >= 1_000.0) {
      return String.format("%.0f Χιλ. €", value / 1_000.0);
    } else {
      return String.format("%.0f €", value);
    }
  }
    
  /**
   * Creates the legend panel showing total results and itemized breakdowns.
   * Displays aggregate totals for revenues and expenses, followed by detailed
   * lists of each revenue and expense item with their respective amounts.
   * 
   * @param sortedRevenues List of revenue items sorted by amount (descending)
   * @param sortedExpenses List of expense items sorted by amount (descending)
   * @return A JPanel containing the formatted legend with all financial details
   */
  private JPanel createLegendPanel(List<DataItem> sortedRevenues, List<DataItem> sortedExpenses) {
    JPanel legendPanel = new JPanel();
    legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
    legendPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
    legendPanel.setBackground(CARD_BACKGROUND);

    double totalRevenue = revenues.stream().mapToDouble(d -> d.amount).sum();
    double totalExpense = expenses.stream().mapToDouble(d -> d.amount).sum();
    double balance = totalRevenue - totalExpense;

    // TOTAL RESULTS SECTION
    JLabel mainTitle = new JLabel("ΣΥΝΟΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ");
    mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
    mainTitle.setForeground(new Color(150, 160, 180));
    mainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    legendPanel.add(mainTitle);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
    // Revenue total
    JPanel revenuePanel = createSummaryRow("Έσοδα", totalRevenue, new Color(34, 197, 94));
    legendPanel.add(revenuePanel);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
    // Expense total
    JPanel expensePanel = createSummaryRow("Έξοδα", totalExpense, new Color(239, 68, 68));
    legendPanel.add(expensePanel);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
    // Balance
    String balanceLabel;
    Color balanceColor;
        
    if (balance > 0) {
      balanceLabel = "Πλεόνασμα";
      balanceColor = new Color(34, 197, 94);
    } else if (balance < 0) {
      balanceLabel = "Έλλειμμα";
      balanceColor = new Color(239, 68, 68);
    } else {
      balanceLabel = "Ισοσκελισμένος";
      balanceColor = new Color(150, 160, 180);
    }
        
    JPanel balancePanel = createSummaryRow(balanceLabel, Math.abs(balance), balanceColor);
    legendPanel.add(balancePanel);
        
    legendPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
    // Separator line
    JSeparator separator1 = new JSeparator();
    separator1.setForeground(new Color(50, 60, 80));
    separator1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    legendPanel.add(separator1);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Revenue items
    JLabel revenueGroupTitle = new JLabel("ΕΣΟΔΑ");
    revenueGroupTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
    revenueGroupTitle.setForeground(new Color(150, 160, 180));
    revenueGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    legendPanel.add(revenueGroupTitle);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
    for (int i = 0; i < sortedRevenues.size(); i++) {
      DataItem item = sortedRevenues.get(i);
      JPanel itemPanel = createItemRow(item.name, item.amount, new Color(34, 197, 94));
      legendPanel.add(itemPanel);
      legendPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
        
    legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
    // Separator line
    JSeparator separator2 = new JSeparator();
    separator2.setForeground(new Color(50, 60, 80));
    separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    legendPanel.add(separator2);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 20)));
       
    // Expense items
    JLabel expenseGroupTitle = new JLabel("ΕΞΟΔΑ");
    expenseGroupTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
    expenseGroupTitle.setForeground(new Color(150, 160, 180));
    expenseGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    legendPanel.add(expenseGroupTitle);
    legendPanel.add(Box.createRigidArea(new Dimension(0, 12)));

    for (int i = 0; i < sortedExpenses.size(); i++) {
      DataItem item = sortedExpenses.get(i);
      JPanel itemPanel = createItemRow(item.name, item.amount, new Color(239, 68, 68));
      legendPanel.add(itemPanel);
      legendPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
        
    return legendPanel;
  }
    
  /**
   * Creates a summary row for totals display.
   */
  private JPanel createSummaryRow(String label, double amount, Color accentColor) {
    JPanel panel = new JPanel(new BorderLayout(5, 0));
    panel.setOpaque(false);
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
    JLabel nameLabel = new JLabel(label);
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLabel.setForeground(TEXT_COLOR);
        
    JLabel amountLabel = new JLabel(formatValueForDisplay(amount));
    amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    amountLabel.setForeground(accentColor);
        
    panel.add(nameLabel, BorderLayout.WEST);
    panel.add(amountLabel, BorderLayout.EAST);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    return panel;
  }
    
  /**
   * Creates an item row for individual entries.
   */
  private JPanel createItemRow(String name, double amount, Color accentColor) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    JLabel nameLabel = new JLabel(name);
    nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    nameLabel.setForeground(TEXT_COLOR);
    nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    JLabel amountLabel = new JLabel(formatValueForDisplay(amount));
    amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    amountLabel.setForeground(accentColor);
    amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
    panel.add(nameLabel);
    panel.add(amountLabel);
        
    return panel;
  }
    
  /**
   * Formats values for display in legend with proper units.
   */
  private String formatValueForDisplay(double value) {
    if (value >= 1_000_000_000_000.0) {
      return String.format("€%.2f Τρισ.", value / 1_000_000_000_000.0);
    } else if (value >= 1_000_000_000.0) {
      return String.format("€%.2f Δισ.", value / 1_000_000_000.0);
    } else if (value >= 1_000_000.0) {
      return String.format("€%.0f Εκατ.", value / 1_000_000.0);
    } else if (value >= 1_000.0) {
      return String.format("€%.0f Χιλ.", value / 1_000.0);
    } else {
      return String.format("€%.2f", value);
    }
  }
    
  /**
   * Generates a color for agency bars using HSB color model.
   * All agency bars use a consistent blue tone.
   * 
   * @param i The index of the item (currently unused, all bars same color)
   * @param total The total number of items
   * @return A Color object for the agency bar
   */
  private Color agencyBarGradient(int i, int total) {
    return ACCENT_COLOR;
  }

  /**
   * Generates a gradient color for revenue bars using HSB color model.
   * Creates a green gradient for revenue items.
   * 
   * @param i The index of the revenue item in the list
   * @param total The total number of revenue items
   * @return A Color object with calculated HSB values for gradient effect.
   */
  private Color revenueGradient(int i, int total) {
    float hue = 0.33f; // Green
    float saturation = 0.7f - 0.1f * i / Math.max(total - 1, 1); 
    float brightness = 0.75f - 0.15f * i / Math.max(total - 1, 1); 
    return Color.getHSBColor(hue, saturation, brightness);
  }
    
  /**
   * Generates a gradient color for expense bars using HSB color model.
   * Creates a red gradient for expense items.
   * 
   * @param i The index of the expense item in the list
   * @param total The total number of expense items
   * @return A Color object with calculated HSB values for gradient effect.
   */
  private Color expenseGradient(int i, int total) {
    float hue = 0.0f; // Red
    float saturation = 0.75f - 0.15f * i / Math.max(total - 1, 1); 
    float brightness = 0.9f - 0.2f * i / Math.max(total - 1, 1); 
    return Color.getHSBColor(hue, saturation, brightness);
  }
    
  /**
   * Data transfer object representing a single financial item.
   * Encapsulates the name, monetary amount, and type (revenue/expense/agency)
   * of a budget line item.
   */
  public static class DataItem {
    /** The name/description of the financial item. */
    final String name;
        
    /** The monetary amount in euros. */
    final double amount;
        
    /** The type of item: "revenue", "expense", or "agency". */
    final String type;
        
    /**
     * Constructs a new DataItem.
     * 
     * @param name The name or description of the item.
     * @param amount The monetary value in euros.
     * @param type The category: "revenue", "expense", or "agency".
     */
    public DataItem(String name, double amount, String type) {
      this.name = name;
      this.amount = amount;
      this.type = type;
    }
        
    @Override
        public boolean equals(Object o) {
      if (this == o) {
        return true;
      } 
      if (o == null || getClass() != o.getClass()) {
        return false; 
      }
      DataItem dataItem = (DataItem) o;
      return Double.compare(dataItem.amount, amount) == 0 && Objects.equals(name,
         dataItem.name) && Objects.equals(type, dataItem.type);
    }

    @Override
        public int hashCode() {
      return Objects.hash(name, amount, type);
    }
  }
}