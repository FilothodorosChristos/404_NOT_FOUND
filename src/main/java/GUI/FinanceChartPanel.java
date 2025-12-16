package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

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
    
    /** The fiscal year for which data is displayed */
    private final String year;
    
    /** List of revenue items for the fiscal year */
    private final List<DataItem> revenues;
    
    /** List of expense items for the fiscal year */
    private final List<DataItem> expenses;
    
    /** List of agency budget allocations for the fiscal year */
    private final List<DataItem> agencies;
    
    /** Primary background color for the panel (dark blue: #14192D) */
    private static final Color DARK_BACKGROUND = new Color(20, 25, 45); 
        
    /** Color used for text elements throughout the panel */
    private static final Color TEXT_COLOR = Color.WHITE;

    /**
     * Private constructor to enforce factory method pattern.
     * 
     * @param year The fiscal year for the budget data
     * @param revenues List of revenue items
     * @param expenses List of expense items
     * @param agencies List of agency budget items
     */
    private FinanceChartPanel(String year, List<DataItem> revenues, List<DataItem> expenses, List<DataItem> agencies) {
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
        
        DataImporter2 importer = new DataImporter2(year);
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
        // TOP PANEL: Back button + Title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(true);
        topPanel.setBackground(DARK_BACKGROUND);
        
        
        JButton backButton = new JButton("← Πίσω");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(75, 150, 225));
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setMaximumSize(new Dimension(100, 35));
        
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
                backButton.setBackground(new Color(99, 170, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(75, 150, 225));
            }
        });
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(backButton);
        buttonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        /** Title label */
        JLabel mainHeader = new JLabel("Προϋπολογισμός (" + year + ")", SwingConstants.CENTER);
        mainHeader.setFont(new Font("Arial", Font.BOLD, 28));
        mainHeader.setForeground(TEXT_COLOR);
        mainHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        topPanel.add(buttonWrapper);
        topPanel.add(mainHeader);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create custom tab buttons panel with better styling
        JPanel tabButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        tabButtonPanel.setBackground(DARK_BACKGROUND);
        tabButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton revenueExpenseButton = new JButton("Έσοδα/Έξοδα");
        JButton agenciesButton = new JButton("Φορείς");
        
        // Style buttons to match back button
        for (JButton btn : new JButton[]{revenueExpenseButton, agenciesButton}) {
            btn.setFont(new Font("Arial", Font.BOLD, 15));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(75, 150, 225));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(180, 40));
        }
        
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
        
        // Set initial selected state
        revenueExpenseButton.setBackground(new Color(99, 170, 255));
        
        // Button actions
        revenueExpenseButton.addActionListener(e -> {
            cardLayout.show(contentCardPanel, "REVENUE_EXPENSE");
            revenueExpenseButton.setBackground(new Color(99, 170, 255));
            agenciesButton.setBackground(new Color(75, 150, 225));
        });
        
        agenciesButton.addActionListener(e -> {
            cardLayout.show(contentCardPanel, "AGENCIES");
            agenciesButton.setBackground(new Color(99, 170, 255));
            revenueExpenseButton.setBackground(new Color(75, 150, 225));
        });
        
        // Add hover effects
        revenueExpenseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                revenueExpenseButton.setBackground(new Color(99, 170, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (agenciesButton.getBackground().equals(new Color(99, 170, 255))) {
                    revenueExpenseButton.setBackground(new Color(75, 150, 225));
                }
            }
        });
        
        agenciesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                agenciesButton.setBackground(new Color(99, 170, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (revenueExpenseButton.getBackground().equals(new Color(99, 170, 255))) {
                    agenciesButton.setBackground(new Color(75, 150, 225));
                }
            }
        });
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(DARK_BACKGROUND);
        mainContentPanel.add(tabButtonPanel, BorderLayout.NORTH);
        mainContentPanel.add(contentCardPanel, BorderLayout.CENTER);
        
        add(mainContentPanel, BorderLayout.CENTER);
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
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel legendPanel = createLegendPanel(sortedRevenues, sortedExpenses);
        legendPanel.setOpaque(true); 
        legendPanel.setBackground(DARK_BACKGROUND); 
        
        JScrollPane legendScrollPane = new JScrollPane(legendPanel);
        legendScrollPane.getViewport().setBackground(DARK_BACKGROUND); 
        legendScrollPane.setPreferredSize(new Dimension(350, 0));
        legendScrollPane.setBorder(null);
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
            JLabel msgLabel = new JLabel("No Agency data found for the year " + year, SwingConstants.CENTER);
            msgLabel.setFont(new Font("Arial", Font.BOLD, 20));
            msgLabel.setForeground(TEXT_COLOR); 
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
            return String.format("%,.0f Τρισ. €", value / 1_000_000_000_000.0);
        } else if (value >= 1_000_000_000.0) {
            return String.format("%,.0f Δισ. €", value / 1_000_000_000.0);
        } else if (value >= 1_000_000.0) {
            return String.format("%,.0f Εκατ. €", value / 1_000_000.0);
        } else if (value >= 1_000.0) {
            return String.format("%,.0f Χιλ. €", value / 1_000.0);
        } else {
            return String.format("%,.0f €", value);
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
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        legendPanel.setBackground(DARK_BACKGROUND);

        double totalRevenue = revenues.stream().mapToDouble(d -> d.amount).sum();
        double totalExpense = expenses.stream().mapToDouble(d -> d.amount).sum();
        double balance = totalRevenue - totalExpense;

        // TOTAL RESULTS SECTION - More professional styling
        JLabel mainTitle = new JLabel("ΣΥΝΟΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 16));
        mainTitle.setForeground(new Color(99, 170, 255));
        mainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(mainTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Revenue total with better formatting
        JLabel revenueTitle = new JLabel(String.format("▶ Έσοδα: €%,.0f", totalRevenue));
        revenueTitle.setFont(new Font("Arial", Font.BOLD, 13));
        revenueTitle.setForeground(new Color(120, 180, 255));
        revenueTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueTitle);
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Expense total with better formatting
        JLabel expenseTitle = new JLabel(String.format("▶ Έξοδα: €%,.0f", totalExpense));
        expenseTitle.setFont(new Font("Arial", Font.BOLD, 13));
        expenseTitle.setForeground(new Color(255, 140, 200));
        expenseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseTitle);
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Balance with color coding and dynamic label
        String balanceLabel;
        Color balanceColor;
        
        if (balance > 0) {
            balanceLabel = String.format("▶ Πλεόνασμα: €%,.0f", balance);
            balanceColor = new Color(100, 255, 150); // Green
        } else if (balance < 0) {
            balanceLabel = String.format("▶ Έλλειμμα: €%,.0f", Math.abs(balance));
            balanceColor = new Color(255, 100, 100); // Red
        } else {
            balanceLabel = "▶ Ισοσκελισμένος";
            balanceColor = new Color(200, 200, 200); // Gray
        }
        
        JLabel balanceLabelComponent = new JLabel(balanceLabel);
        balanceLabelComponent.setFont(new Font("Arial", Font.BOLD, 13));
        balanceLabelComponent.setForeground(balanceColor);
        balanceLabelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(balanceLabelComponent);
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Separator line
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(new Color(75, 150, 225));
        separator1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        legendPanel.add(separator1);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Revenue group
        JLabel revenueGroupTitle = new JLabel("I. Έσοδα (Revenues)");
        revenueGroupTitle.setFont(new Font("Arial", Font.BOLD, 14));
        revenueGroupTitle.setForeground(new Color(99, 170, 255));
        revenueGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueGroupTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        for (int i = 0; i < sortedRevenues.size(); i++) {
            DataItem item = sortedRevenues.get(i);
            Color itemColor = revenueGradient(i, sortedRevenues.size());

            JLabel label = new JLabel(String.format("%d. %s (€%,.0f)", i + 1, item.name, item.amount));
            label.setForeground(itemColor); 
            label.setFont(new Font("Arial", Font.PLAIN, 11));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
            legendPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Separator line
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(new Color(255, 105, 180));
        separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        legendPanel.add(separator2);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
       
        // Expense group
        JLabel expenseGroupTitle = new JLabel("II. Έξοδα (Expenses)");
        expenseGroupTitle.setFont(new Font("Arial", Font.BOLD, 14));
        expenseGroupTitle.setForeground(new Color(255, 140, 200));
        expenseGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseGroupTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        int offset = sortedRevenues.size(); 
        
        for (int i = 0; i < sortedExpenses.size(); i++) {
            DataItem item = sortedExpenses.get(i);
            Color itemColor = expenseGradient(i, sortedExpenses.size()); 

            JLabel label = new JLabel(String.format("%d. %s (€%,.0f)", i + offset + 1, item.name, item.amount));
            label.setForeground(itemColor); 
            label.setFont(new Font("Arial", Font.PLAIN, 11));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
            legendPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        return legendPanel;
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
        float hue = 0.6f; 
        
        float saturation = 0.8f; 
        float brightness = 0.7f; 
        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * Generates a gradient color for revenue bars using HSB color model.
     * Creates a blue gradient that transitions from lighter to darker shades
     * based on the item's position in the sorted list.
     * 
     * @param i The index of the revenue item in the list
     * @param total The total number of revenue items
     * @return A Color object with calculated HSB values for gradient effect
     */
    private Color revenueGradient(int i, int total) {
        float hue = 0.6f; 
        float saturation = 0.8f - 0.2f * i / Math.max(total - 1, 1); 
        float brightness = 0.9f - 0.3f * i / Math.max(total - 1, 1); 
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    /**
     * Generates a gradient color for expense bars using HSB color model.
     * Creates a pink/magenta gradient that transitions from lighter to darker shades
     * based on the item's position in the sorted list.
     * 
     * @param i The index of the expense item in the list
     * @param total The total number of expense items
     * @return A Color object with calculated HSB values for gradient effect
     */
    private Color expenseGradient(int i, int total) {
        float hue = 0.9f; 
        float saturation = 0.9f - 0.2f * i / Math.max(total - 1, 1); 
        float brightness = 0.9f - 0.3f * i / Math.max(total - 1, 1); 
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    /**
     * Data transfer object representing a single financial item.
     * Encapsulates the name, monetary amount, and type (revenue/expense/agency)
     * of a budget line item.
     */
    public static class DataItem {
        /** The name/description of the financial item */
        final String name;
        
        /** The monetary amount in euros */
        final double amount;
        
        /** The type of item: "revenue", "expense", or "agency" */
        final String type;
        
        /**
         * Constructs a new DataItem.
         * 
         * @param name The name or description of the item
         * @param amount The monetary value in euros
         * @param type The category: "revenue", "expense", or "agency"
         */
        public DataItem(String name, double amount, String type) {
            this.name = name;
            this.amount = amount;
            this.type = type;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataItem dataItem = (DataItem) o;
            return Double.compare(dataItem.amount, amount) == 0 && Objects.equals(name, dataItem.name) && Objects.equals(type, dataItem.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, amount, type);
        }
    }
}