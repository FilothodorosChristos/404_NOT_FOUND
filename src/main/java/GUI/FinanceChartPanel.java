package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets; 
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * The FinanceChartPanel class is a JPanel that displays financial data 
 * (Revenues, Expenses, Agencies) using bar charts, loaded from CSV files.
 * It uses a JTabbedPane to switch between 'Revenues/Expenses' and 'Agencies' charts, both using Logarithmic Scale 
 * to handle large discrepancies in data magnitude.
 */
public class FinanceChartPanel extends JPanel {
    
    
    /** The year for which data is loaded and displayed (e.g., "2025"). */
    private final String year;
    
    /** List to hold revenue data items. */
    private final List<DataItem> revenues = new ArrayList<>();
    
    /** List to hold expense data items. */
    private final List<DataItem> expenses = new ArrayList<>();
    
    /** List to hold agency data items. */
    private final List<DataItem> agencies = new ArrayList<>();
    
    /** Constant color for the background (Navy Blue). */
    private static final Color NAVY_BLUE = new Color(0, 0, 128); 
    
    private static final Color DARK_BACKGROUND = NAVY_BLUE; 
    private static final Color ACCENT_COLOR = new Color(255, 180, 0); 
    
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 12);

    /**
     * Private constructor for FinanceChartPanel.
     * * @param year The year for which financial data will be displayed.
     */
    private FinanceChartPanel(String year) {
        this.year = year;
        setLayout(new BorderLayout());
    }

    /**
     * Factory method to create an initialized FinanceChartPanel safely.
     * @param year The year for which financial data will be displayed.
     * @return The fully initialized FinanceChartPanel.
     */
    public static FinanceChartPanel createPanel(String year) {
        
        FinanceChartPanel panel = new FinanceChartPanel(year);
        
        
        panel.loadData();
        panel.initializeUI();
        
        return panel;
    }
    
    /**
     * Sets up the user interface components (JTabbedPane, charts, etc.).
     */
    private void initializeUI() {
        JPanel contentPanel = createContentPanel();
        
        
        JLabel mainHeader = new JLabel("Προϋπολογισμός (" + year + ")", SwingConstants.CENTER);
        mainHeader.setFont(new Font("Arial", Font.BOLD, 28));
        mainHeader.setForeground(ACCENT_COLOR);
        mainHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        contentPanel.add(mainHeader, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setForeground(Color.WHITE); 
        tabbedPane.setBackground(DARK_BACKGROUND);
        
        
        
        tabbedPane.add("Έσοδα/Έξοδα", createRevenueExpensePanel());
        tabbedPane.add("Φορείς", createAgencyPanel()); 
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    

    
    /**
     * Creates a JPanel that acts as the main background, setting the Navy Blue color.
     *
     * @return The JPanel with the Navy Blue background.
     */
    private JPanel createContentPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(DARK_BACKGROUND); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
    
    /**
     * Loads financial data from CSV files located in the 'src/main/resources/data' folder.
     * Data is filtered by file name and the specified year.
     */
    private void loadData() {
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data";
        File folder = new File(path);
        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return;
        
        String yearSuffix = year.substring(2); 
        String yearPrefix = "b" + yearSuffix; 

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) { 
                
                br.readLine(); 
                String line;
                
                
                if ((fileName.contains("esoda.csv") || fileName.contains("exoda.csv") || fileName.contains("esodatest.csv") || fileName.contains("exodatest.csv")) && fileName.contains(yearPrefix)) {
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("[;,]"); 
                        if (parts.length >= 4 && parts[0].trim().equals(year)) { 
                            String type = parts[1].trim();
                            String name = parts[2].trim();
                            
                            try {
                                double amount = Double.parseDouble(parts[3].trim());
                                if (amount == 0) continue;
                                if (type.equalsIgnoreCase("Revenue") || type.equalsIgnoreCase("Έσοδο"))
                                    revenues.add(new DataItem(name, amount, "Revenue"));
                                else if (type.equalsIgnoreCase("Expense") || type.equalsIgnoreCase("Έξοδο"))
                                    expenses.add(new DataItem(name, amount, "Expense"));
                            } catch (NumberFormatException ignored) {
                                
                            }
                        }
                    }
                } 
                
                
                else if (fileName.contains("foreis.csv") && fileName.contains(yearPrefix)) { 
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("[;,]"); 
                        
                        
                        if (parts.length >= 7) { 
                            try {
                                
                                String name = parts[3].trim(); 
                                double amount = Double.parseDouble(parts[6].trim()); 
                                
                                if (amount > 0) {
                                    agencies.add(new DataItem(name, amount, "Agency"));
                                }
                            } catch (NumberFormatException ignored) {
                                
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                                
                            }
                        }
                    }
                }
            
            } catch (IOException e) { 
                System.err.println("Error reading file " + fileName + ": " + e.getMessage());
                
                e.printStackTrace(); 
            }
        }
    }
    
    /**
     * Creates the JPanel for Revenues and Expenses chart. 
     * The data is sorted within groups (Revenues, then Expenses) and displayed using logarithmic scaling.
     *
     * @return The JPanel containing the Revenue/Expense chart.
     */
    private JPanel createRevenueExpensePanel() {
        
        /** 1. Sort Revenues separately (descending)*/
        List<DataItem> sortedRevenues = revenues.stream()
                                             .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                             .toList();
        
        /**2. Sort Expenses separately (descending)*/
        List<DataItem> sortedExpenses = expenses.stream()
                                             .sorted(Comparator.comparingDouble((DataItem d) -> d.amount).reversed()) 
                                             .toList();
        
        /**3. Combine: Revenues (sorted) + Expenses (sorted). This list determines the bar order*/
        List<DataItem> allItems = Stream.concat(sortedRevenues.stream(), sortedExpenses.stream())
                                             .toList();


        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        /**Calculate Logarithmic boundaries (using the combined list)*/
        double minAmount = allItems.stream().mapToDouble(d -> d.amount).filter(a -> a > 0).min().orElse(1.0);
        double maxAmount = allItems.stream().mapToDouble(d -> d.amount).max().orElse(1.0);
        
        
        final double LOG_MIN = Math.log10(Math.max(1.0, minAmount));
        final double LOG_MAX = Math.log10(Math.max(1.0, maxAmount));
        final double LOG_RANGE = LOG_MAX - LOG_MIN;

        
        final int BAR_HEIGHT = 25;
        final int BAR_SPACING = 5;
        int preferredHeight = allItems.size() * (BAR_HEIGHT + BAR_SPACING) + 60; 

        JPanel barPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
               
                return new Dimension(700, preferredHeight + 25); 
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                if (allItems.isEmpty()) return;

                
                super.paintComponent(g);

                int width = getWidth();
                final int paddingY = 20;
                final int paddingX = 80;
                final int axisMarginBottom = 30; 
                
                
                int graphWidth = width - paddingX - 20; 
                
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(DETAIL_FONT);
                
                g2.setColor(Color.WHITE); 
                
                int chartBottomY = getHeight() - axisMarginBottom;

                
                g2.setColor(new Color(100, 100, 100)); 
                g2.drawLine(paddingX, chartBottomY, width - 20, chartBottomY);
                
                
                g2.setColor(Color.LIGHT_GRAY);
                
                for (double logVal = Math.ceil(LOG_MIN); logVal <= LOG_MAX; logVal++) {
                    
                    double ratio = (logVal - LOG_MIN) / LOG_RANGE;
                    int x = paddingX + (int) (ratio * graphWidth);
                    
                    if (x > paddingX) { 
                        double value = Math.pow(10, logVal);
                        String label = formatValueForAxis(value); 

                        
                        g2.drawLine(x, chartBottomY, x, chartBottomY + 5);
                        
                        
                        g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, chartBottomY + 20);
                    }
                }

                int currentY = paddingY;
                final int MIN_VISIBLE_BAR_WIDTH = 5; 
                
                
                for (int i = 0; i < allItems.size(); i++) {
                    DataItem item = allItems.get(i);
                    
                    int barWidth = 0;
                    if (item.amount > 0) {
                        double logValue = Math.log10(item.amount);
                        double ratio = (logValue - LOG_MIN) / LOG_RANGE;
                        barWidth = (int) (ratio * graphWidth);
                    }
                    
                    
                    if (item.amount > 0 && barWidth < MIN_VISIBLE_BAR_WIDTH) { 
                        barWidth = MIN_VISIBLE_BAR_WIDTH; 
                    }

                    
                    
                    int originalIndex;
                    int totalListSize;
                    
                    if (item.type.equals("Revenue")) {
                        originalIndex = sortedRevenues.indexOf(item); 
                        totalListSize = sortedRevenues.size();
                        g2.setColor(pastelGreen(originalIndex, totalListSize));
                    } else { 
                        originalIndex = sortedExpenses.indexOf(item); 
                        totalListSize = sortedExpenses.size();
                        g2.setColor(pastelRed(originalIndex, totalListSize));
                    }
                    
                    
                    g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);

                    
                    g2.setColor(new Color(255, 255, 255, 150)); 
                    g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT); 
                    
                    
                    
                    
                    
                    
                    g2.setColor(Color.WHITE); 
                    
                    String numberStr = String.valueOf(i + 1);
                    
                    
                    g2.drawString(numberStr, 
                            paddingX - g2.getFontMetrics().stringWidth(numberStr) - 5, 
                            currentY + BAR_HEIGHT - 8);
                    
                    
                    String valueStr = String.format("%,.0f €", item.amount); 
                    
                    
                    
                    
                    
                    if (barWidth > g2.getFontMetrics().stringWidth(valueStr) + 5) {
                        g2.setColor(DARK_BACKGROUND); 
                        g2.drawString(valueStr, paddingX + 5, currentY + BAR_HEIGHT - 8);
                    } else {
                        g2.setColor(Color.WHITE); 
                        if (barWidth > 0) {
                             g2.drawString(valueStr, paddingX + barWidth + 5, currentY + BAR_HEIGHT - 8);
                        }
                    }
                    
                    currentY += BAR_HEIGHT + BAR_SPACING;
                }
            }
        };
        
        
        barPanel.setOpaque(true); 
        barPanel.setBackground(DARK_BACKGROUND);
        
        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        
        scrollPane.setOpaque(true); 
        scrollPane.getViewport().setOpaque(true); 
        scrollPane.getViewport().setBackground(DARK_BACKGROUND); 
        scrollPane.setBackground(DARK_BACKGROUND); 
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel legendPanel = createLegendPanel(sortedRevenues, sortedExpenses);
        
        
        legendPanel.setOpaque(true); 
        legendPanel.setBackground(DARK_BACKGROUND); 
        
        JScrollPane legendScrollPane = new JScrollPane(legendPanel);
        
        legendScrollPane.setOpaque(true);
        legendScrollPane.getViewport().setOpaque(true);
        legendScrollPane.setBackground(DARK_BACKGROUND);
        legendScrollPane.getViewport().setBackground(DARK_BACKGROUND); 
        
        panel.add(legendScrollPane, BorderLayout.EAST);
        return panel;
    }
    
    /**
     * Helper method to format large monetary values for the logarithmic axis.
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
     * Creates the legend panel showing the index, name, and total for each Revenue and Expense item.
     * It displays all Revenues first (sorted) and then all Expenses (sorted), matching the chart order.
     */
    private JPanel createLegendPanel(List<DataItem> sortedRevenues, List<DataItem> sortedExpenses) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        legendPanel.setBackground(DARK_BACKGROUND);

        
        double totalRevenue = revenues.stream().mapToDouble(d -> d.amount).sum();
        double totalExpense = expenses.stream().mapToDouble(d -> d.amount).sum();

       
        JLabel mainTitle = new JLabel("<html><font color='#" + Integer.toHexString(ACCENT_COLOR.getRGB()).substring(2) + "'><b>ΣΥΝΟΛΙΚΑ ΑΠΟΤΕΛΕΣΜΑΤΑ</b></font></html>");
        mainTitle.setFont(TITLE_FONT);
        mainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(mainTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        
        JLabel revenueTitle = new JLabel("<html><font color='white'>▶ Revenues: <b>€" + String.format("%,.0f", totalRevenue) + "</b></font></html>");
        revenueTitle.setFont(DETAIL_FONT);
        revenueTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueTitle);
        
        JLabel expenseTitle = new JLabel("<html><font color='white'>▶ Expenses: <b>€" + String.format("%,.0f", totalExpense) + "</b></font></html>");
        expenseTitle.setFont(DETAIL_FONT);
        expenseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseTitle);
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        JLabel revenueGroupTitle = new JLabel("<html><font color='#90EE90'><b>I. Έσοδα (Revenues)</b></font></html>");
        revenueGroupTitle.setFont(DETAIL_FONT);
        revenueGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueGroupTitle);
        
        for (int i = 0; i < sortedRevenues.size(); i++) {
            DataItem item = sortedRevenues.get(i);
            Color itemColor = pastelGreen(i, sortedRevenues.size());

            
            JLabel label = new JLabel((i + 1) + ". " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(itemColor); 
            label.setFont(DETAIL_FONT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
       
        JLabel expenseGroupTitle = new JLabel("<html><font color='#FF8C8C'><b>II. Έξοδα (Expenses)</b></font></html>");
        expenseGroupTitle.setFont(DETAIL_FONT);
        expenseGroupTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseGroupTitle);

        int offset = sortedRevenues.size(); 
        
        for (int i = 0; i < sortedExpenses.size(); i++) {
            DataItem item = sortedExpenses.get(i);
            Color itemColor = pastelRed(i, sortedExpenses.size()); 

            
            JLabel label = new JLabel((i + offset + 1) + ". " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(itemColor); 
            label.setFont(DETAIL_FONT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        
        return legendPanel;
    }
    
    
    /**
     * Creates the Agencies panel as a horizontal bar chart. 
     * Uses LOGARITHMIC scaling for datasets spanning multiple orders of magnitude.
     */
    private JPanel createAgencyPanel() {
        if (agencies.isEmpty()) {
            
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setOpaque(false);
            JLabel msgLabel = new JLabel("No Agency data found for the year " + year, SwingConstants.CENTER);
            msgLabel.setFont(new Font("Arial", Font.BOLD, 20));
            msgLabel.setForeground(Color.WHITE);
            emptyPanel.add(msgLabel, BorderLayout.CENTER);
            return emptyPanel;
        }

        final int BAR_HEIGHT = 25;
        final int BAR_SPACING = 5;
        int preferredHeight = agencies.size() * (BAR_HEIGHT + BAR_SPACING) + 60; 

        
        double minAmount = agencies.stream().mapToDouble(d -> d.amount).filter(a -> a > 0).min().orElse(1.0);
        double maxAmount = agencies.stream().mapToDouble(d -> d.amount).max().orElse(1.0);
        
     
        final double LOG_MIN = Math.log10(Math.max(1.0, minAmount));
        final double LOG_MAX = Math.log10(Math.max(1.0, maxAmount));
        final double LOG_RANGE = LOG_MAX - LOG_MIN;


        JPanel barPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                
                return new Dimension(700, preferredHeight + 25); 
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                if (agencies.isEmpty()) return;
                
                
                super.paintComponent(g);

                int width = getWidth();
                final int paddingX = 20; 
                final int paddingY = 20;
                final int axisMarginBottom = 30; 
                
                
                int graphWidth = width - paddingX - 20; 
                
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                
                int currentY = paddingY;

                int chartBottomY = getHeight() - axisMarginBottom;

                
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(paddingX, chartBottomY, width - 20, chartBottomY);
                
                
                g2.setColor(Color.WHITE);
                
                for (double logVal = Math.ceil(LOG_MIN); logVal <= LOG_MAX; logVal++) {
                    
                    double ratio = (logVal - LOG_MIN) / LOG_RANGE;
                    int x = paddingX + (int) (ratio * graphWidth);
                    
                    if (x > paddingX) { 
                        
                        double value = Math.pow(10, logVal); 
                        String label = formatValueForAxis(value);
                        
                        // Tick Mark
                        g2.drawLine(x, chartBottomY, x, chartBottomY + 5);
                        
                        // Label
                        g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, chartBottomY + 20);
                    }
                }
                
                
                final int MIN_VISIBLE_BAR_WIDTH = 5; 

                for (int i = 0; i < agencies.size(); i++) {
                    DataItem item = agencies.get(i);
                    int barWidth = 0;
                    
                    if (item.amount > 0) {
                        
                        double logValue = Math.log10(item.amount);
                        
                        
                        double ratio = (logValue - LOG_MIN) / LOG_RANGE;
                        barWidth = (int) (ratio * graphWidth);
                    }
                    
                    
                    
                    
                    
                    
                    if (item.amount > 0 && barWidth < MIN_VISIBLE_BAR_WIDTH) { 
                        barWidth = MIN_VISIBLE_BAR_WIDTH; 
                    }
                    
                    
                    
                    
                    
                    
                    g2.setColor(pastelGreen(i, agencies.size()));
                    
                    
                    
                    
                    
                    
                    g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
                    
                    
                    
                    
                    
                    g2.setColor(Color.WHITE);
                    g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    g2.setColor(Color.WHITE);
                    String valueStr = String.format("%s (%,.0f €)", item.name, item.amount);
                    int textWidth = g2.getFontMetrics().stringWidth(valueStr);
                    
                    
                    
                    
                    
                    /**  Display label: Inside the bar if it fits, otherwise to the right*/
                    if (barWidth > textWidth + 10) { 
                        g2.setColor(NAVY_BLUE); /** Dark color inside the bar*/
                        g2.drawString(valueStr, paddingX + 5, currentY + BAR_HEIGHT - 8);
                    } else {
                        
                        g2.setColor(Color.WHITE); /** White color outside the bar*/
                        g2.drawString(valueStr, paddingX + barWidth + 5, currentY + BAR_HEIGHT - 8);
                    }
                    
                    currentY += BAR_HEIGHT + BAR_SPACING;
                }
            }
        };
        
        JPanel panel = new JPanel(new BorderLayout());
        
        
        barPanel.setOpaque(true); 
        barPanel.setBackground(NAVY_BLUE); 
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        
        
        scrollPane.setOpaque(true); 
        scrollPane.getViewport().setOpaque(true); 
        scrollPane.getViewport().setBackground(NAVY_BLUE);
        scrollPane.setBackground(NAVY_BLUE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Generates a distinct pastel green color based on the item index 
     * for visual differentiation between bars.
     * @param i The current item index.
     * @param total The total number of items.
     * @return A pastel green color.
     */
    private Color pastelGreen(int i, int total) {
        float hue = 0.33f; 
        float saturation = 0.5f + 0.3f * i / Math.max(total - 1, 1); 
        float brightness = 0.85f; 
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    /**
     * Generates a distinct pastel red color based on the item index 
     * for visual differentiation between bars.
     * @param i The current item index.
     * @param total The total number of items.
     * @return A pastel red color.
     */
    private Color pastelRed(int i, int total) {
        float hue = 0f; 
        float saturation = 0.5f + 0.3f * i / Math.max(total - 1, 1); 
        float brightness = 0.85f; 
        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * A simple static class to hold financial data items.
     */
    static class DataItem {
        /** The name of the item (e.g., category, agency name). */
        final String name;
        /** The monetary amount. */
        final double amount;
        /** The type of item (e.g., "Revenue", "Expense", "Agency"). */
        final String type;
        
        /**
         * Constructor for DataItem.
         * @param name The name of the item.
         * @param amount The monetary amount.
         * @param type The type of item.
         */
        DataItem(String name, double amount, String type) {
            this.name = name;
            this.amount = amount;
            this.type = type;
        }
        
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataItem dataItem = (DataItem) o;
            /**Comparison for indexOf/identity purposes*/
            return Double.compare(dataItem.amount, amount) == 0 && Objects.equals(name, dataItem.name) && Objects.equals(type, dataItem.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, amount, type);
        }
    }
}