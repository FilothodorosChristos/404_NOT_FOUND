package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The FinanceChartPanel class is a JPanel that displays financial data 
 * (Revenues, Expenses, Agencies) using bar charts, loaded from a DataImporter.
 */
public class FinanceChartPanel extends JPanel {
    
    private final String year;
    private final List<DataItem> revenues;
    private final List<DataItem> expenses;
    private final List<DataItem> agencies;
    
    
    // Dark Blue: #14192D
    private static final Color DARK_BACKGROUND = new Color(20, 25, 45); 
   
    private static final Color AGENCY_BAR_COLOR = new Color(75, 150, 225); 
    
    private static final Color TEXT_COLOR = Color.WHITE; 
    
    private static final Color EXPENSE_COLOR = new Color(255, 105, 180); 
    
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 12);

    private FinanceChartPanel(String year, List<DataItem> revenues, List<DataItem> expenses, List<DataItem> agencies) {
        this.year = year;
        this.revenues = revenues;
        this.expenses = expenses;
        this.agencies = agencies;
        setLayout(new BorderLayout());
    }

    public static FinanceChartPanel createPanel(String year) {
        
        DataImporter importer = new DataImporter(year);
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
    
    private void initializeUI() {
        JPanel contentPanel = createContentPanel();
        
        // ✅ TOP PANEL: Back button + Title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(true);
        topPanel.setBackground(DARK_BACKGROUND);
        
        // Back Button
        JButton backButton = new JButton("← Πίσω");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(75, 150, 225));
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
        buttonWrapper.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        /**title */
        JLabel mainHeader = new JLabel("Προϋπολογισμός (" + year + ")", SwingConstants.CENTER);
        mainHeader.setFont(new Font("Arial", Font.BOLD, 28));
        mainHeader.setForeground(TEXT_COLOR);
        mainHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        topPanel.add(buttonWrapper);
        topPanel.add(mainHeader);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBackground(DARK_BACKGROUND);
        
        tabbedPane.add("Έσοδα/Έξοδα", createRevenueExpensePanel());
        tabbedPane.add("Φορείς", createAgencyPanel()); 
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createContentPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(DARK_BACKGROUND); // Σκούρο Φόντο
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
    
    private JPanel createRevenueExpensePanel() {
        List<DataItem> sortedRevenues = revenues.stream()
                                               .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                               .toList();
        
        List<DataItem> sortedExpenses = expenses.stream()
                                               .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                               .toList();
        
        List<DataItem> allItems = Stream.concat(sortedRevenues.stream(), sortedExpenses.stream()).toList();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

      
        JPanel barPanel = ChartRenderer.createBarChartPanel(allItems, 
                                                            this::revenueGradient, 
                                                            this::expenseGradient, 
                                                            this::formatValueForAxis);
        
        barPanel.setOpaque(true); 
        barPanel.setBackground(DARK_BACKGROUND); 
        
        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollPane.getViewport().setBackground(DARK_BACKGROUND); 
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel legendPanel = createLegendPanel(sortedRevenues, sortedExpenses);
        legendPanel.setOpaque(true); 
        legendPanel.setBackground(DARK_BACKGROUND); 
        
        JScrollPane legendScrollPane = new JScrollPane(legendPanel);
        legendScrollPane.getViewport().setBackground(DARK_BACKGROUND); 
        panel.add(legendScrollPane, BorderLayout.EAST);

        return panel;
    }
    
    private JPanel createAgencyPanel() {
        if (agencies.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setOpaque(false);
            JLabel msgLabel = new JLabel("No Agency data found for the year " + year, SwingConstants.CENTER);
            msgLabel.setFont(new Font("Arial", Font.BOLD, 20));
            msgLabel.setForeground(TEXT_COLOR); 
            emptyPanel.add(msgLabel, BorderLayout.CENTER);
            return emptyPanel;
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        
        JPanel barPanel = ChartRenderer.createBarChartPanel(agencies, 
                                                            this::agencyBarGradient, 
                                                            null, 
                                                            this::formatValueForAxis);
        
        barPanel.setOpaque(true); 
        barPanel.setBackground(DARK_BACKGROUND); 
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollPane.getViewport().setOpaque(true); 
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
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
    
    private JPanel createLegendPanel(List<DataItem> sortedRevenues, List<DataItem> sortedExpenses) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        legendPanel.setBackground(DARK_BACKGROUND);

        double totalRevenue = revenues.stream().mapToDouble(d -> d.amount).sum();
        double totalExpense = expenses.stream().mapToDouble(d -> d.amount).sum();

        // ΣΥΝΟΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ
        JLabel mainTitle = new JLabel("<html><font color='#" + Integer.toHexString(TEXT_COLOR.getRGB()).substring(2) + "'><b>ΣΥΝΟΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ</b></font></html>");
        mainTitle.setFont(TITLE_FONT);
        mainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(mainTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Σύνολο Εσόδων & Εξόδων
        JLabel revenueTitle = new JLabel("<html><font color='#" + Integer.toHexString(TEXT_COLOR.getRGB()).substring(2) + "'>▶ Revenues: <b>€" + String.format("%,.0f", totalRevenue) + "</b></font></html>");
        revenueTitle.setFont(DETAIL_FONT);
        revenueTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueTitle);
        
        JLabel expenseTitle = new JLabel("<html><font color='#" + Integer.toHexString(TEXT_COLOR.getRGB()).substring(2) + "'>▶ Expenses: <b>€" + String.format("%,.0f", totalExpense) + "</b></font></html>");
        expenseTitle.setFont(DETAIL_FONT);
        expenseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseTitle);
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Λίστα Εσόδων
        JLabel revenueGroupTitle = new JLabel("<html><font color='#" + Integer.toHexString(AGENCY_BAR_COLOR.getRGB()).substring(2) + "'><b>I. Έσοδα (Revenues)</b></font></html>");
        revenueGroupTitle.setFont(DETAIL_FONT);
        revenueGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueGroupTitle);
        
        for (int i = 0; i < sortedRevenues.size(); i++) {
            DataItem item = sortedRevenues.get(i);
            Color itemColor = revenueGradient(i, sortedRevenues.size());

            JLabel label = new JLabel((i + 1) + ". " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(itemColor); 
            label.setFont(DETAIL_FONT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
       
        JLabel expenseGroupTitle = new JLabel("<html><font color='#" + Integer.toHexString(EXPENSE_COLOR.getRGB()).substring(2) + "'><b>II. Έξοδα (Expenses)</b></font></html>");
        expenseGroupTitle.setFont(DETAIL_FONT);
        expenseGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseGroupTitle);

        int offset = sortedRevenues.size(); 
        
        for (int i = 0; i < sortedExpenses.size(); i++) {
            DataItem item = sortedExpenses.get(i);
            Color itemColor = expenseGradient(i, sortedExpenses.size()); 

            JLabel label = new JLabel((i + offset + 1) + ". " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(itemColor); 
            label.setFont(DETAIL_FONT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        
        return legendPanel;
    }
    
    
    private Color agencyBarGradient(int i, int total) {
        float hue = 0.6f; // Μπλε
        
        float saturation = 0.8f; 
        float brightness = 0.7f; 
        return Color.getHSBColor(hue, saturation, brightness);
    }

    
    private Color revenueGradient(int i, int total) {
        float hue = 0.6f; 
        float saturation = 0.8f - 0.2f * i / Math.max(total - 1, 1); 
        float brightness = 0.9f - 0.3f * i / Math.max(total - 1, 1); 
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
   
    private Color expenseGradient(int i, int total) {
        float hue = 0.9f; 
        float saturation = 0.9f - 0.2f * i / Math.max(total - 1, 1); 
        float brightness = 0.9f - 0.3f * i / Math.max(total - 1, 1); 
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    public static class DataItem {
        final String name;
        final double amount;
        final String type;
        
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