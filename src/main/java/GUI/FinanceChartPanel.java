package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * The FinanceChartPanel class is a JPanel that displays financial data 
 * (Revenues, Expenses, Agencies) using bar charts, loaded from CSV files.
 * It uses a JTabbedPane to switch between 'Revenues/Expenses' and 'Agencies' charts.
 */
public class FinanceChartPanel extends JPanel {
    
    /** The year for which data is loaded and displayed (e.g., "2025"). */
    private String year;
    
    /** List to hold revenue data items. */
    private List<DataItem> revenues = new ArrayList<>();
    
    /** List to hold expense data items. */
    private List<DataItem> expenses = new ArrayList<>();
    
    /** List to hold agency data items. */
    private List<DataItem> agencies = new ArrayList<>();
    
    /** Constant color for the background (Navy Blue). */
    private static final Color NAVY_BLUE = new Color(0, 0, 128); 

    /**
     * Constructor for FinanceChartPanel.
     * Loads financial data and sets up the user interface.
     *
     * @param year The year for which financial data will be displayed.
     */
    public FinanceChartPanel(String year) {
        this.year = year;
        
        setLayout(new BorderLayout());
        loadData();

        JPanel contentPanel = createContentPanel();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        
        /** UI settings for JTabbedPane transparency */
        UIManager.put("TabbedPane.contentAreaColor", new Color(0, 0, 0, 0));
        UIManager.put("TabbedPane.selectedBackground", new Color(200, 200, 200)); 
        
        /** Tab Titles (using Greek terms for consistency with the domain) */
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
                /** Use only NAVY_BLUE color for the background */
                g.setColor(NAVY_BLUE); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
    
    /**
     * Loads financial data from CSV files located in the 'src/main/resources/data' folder.
     * Data is filtered by file name and the specified year.
     */
    private void loadData() {
        File folder = new File("src/main/resources/data"); 
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return;
        
        String yearSuffix = year.substring(2); 
        String yearPrefix = "b" + yearSuffix; 

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                /** Skip header */
                br.readLine(); 
                String line;
                
                /** 1. REVENUE/EXPENSE DATA */
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
                            } catch (NumberFormatException e) {
                                /** Ignore line with wrong amount format */
                            }
                        }
                    }
                } 
                
                /** 2. AGENCY DATA (BXXForeis.csv) */
                else if (fileName.contains("foreis.csv") && fileName.contains(yearPrefix)) { 
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("[;,]"); 
                        
                        /** Check for at least 7 columns (0 through 6) */
                        if (parts.length >= 7) { 
                            try {
                                /** Use Index 3 (Name) and Index 6 (Total) */
                                String name = parts[3].trim();      
                                double amount = Double.parseDouble(parts[6].trim()); 
                                
                                if (amount > 0) {
                                    agencies.add(new DataItem(name, amount, "Agency"));
                                }
                            } catch (NumberFormatException e) {
                                /** Ignore line if amount is not a number */
                            } catch (ArrayIndexOutOfBoundsException e) {
                                /** Ignore if line is too short */
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading file " + fileName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Creates the JPanel for Revenues and Expenses chart. 
     * Uses LINEAR scaling with a minimum visible bar width of 10 pixels.
     *
     * @return The JPanel containing the Revenue/Expense chart.
     */
    private JPanel createRevenueExpensePanel() {
        List<DataItem> allItems = new ArrayList<>();
        allItems.addAll(revenues);
        allItems.addAll(expenses);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        final int BAR_HEIGHT = 25;
        final int BAR_SPACING = 5;
        int preferredHeight = allItems.size() * (BAR_HEIGHT + BAR_SPACING) + 60; 

        JPanel barPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, preferredHeight); 
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                if (allItems.isEmpty()) return;

                /** The background is drawn by the parent component (createContentPanel) */

                int width = getWidth();
                int height = getHeight();
                int paddingX = 80; 
                int paddingY = 20;
                int graphWidth = width - 2 * paddingX;
                
                /** Max amount for LINEAR scaling */
                double max = allItems.stream().mapToDouble(d -> d.amount).max().orElse(1);
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                
                g2.setColor(Color.WHITE); 
                /** Vertical axis (Zero line) */
                g2.drawLine(paddingX, paddingY, paddingX, height - paddingY); 
                
                int currentY = paddingY;
                /** Minimum visible bar width (10 PIXELS) */
                final int MIN_VISIBLE_BAR_WIDTH = 10; 
                
                for (int i = 0; i < allItems.size(); i++) {
                    DataItem item = allItems.get(i);
                    
                    /** LINEAR SCALING */
                    int barWidth = (int) ((item.amount / max) * graphWidth);
                    
                    /** MINIMUM VISIBLE WIDTH */
                    if (item.amount > 0 && barWidth < MIN_VISIBLE_BAR_WIDTH) { 
                        barWidth = MIN_VISIBLE_BAR_WIDTH; 
                    }

                    /** Choose color (green for Revenue, red for Expense) */
                    if (item.type.equals("Revenue")) {
                        int revenueIndex = revenues.indexOf(item); 
                        g2.setColor(pastelGreen(revenueIndex, revenues.size()));
                    } else if (item.type.equals("Expense")) {
                        int expenseIndex = expenses.indexOf(item); 
                        g2.setColor(pastelRed(expenseIndex, expenses.size()));
                    }
                    
                    /** Fill the bar */
                    g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);

                    /** Add white border */
                    g2.setColor(Color.WHITE);
                    g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT); 
                    
                    /** Reset color for text */
                    g2.setColor(Color.WHITE); 
                    String numberStr = String.valueOf(i + 1);
                    
                    /** Numbering left of the axis */
                    g2.drawString(numberStr, 
                                  paddingX - g2.getFontMetrics().stringWidth(numberStr) - 5, 
                                  currentY + BAR_HEIGHT - 8);
                    
                    String valueStr = String.format("%,.0f €", item.amount); 
                    
                    /** Value placement (inside or outside the bar) */
                    if (barWidth > g2.getFontMetrics().stringWidth(valueStr) + 5 && barWidth > MIN_VISIBLE_BAR_WIDTH + 1) {
                        g2.setColor(Color.WHITE); 
                        g2.drawString(valueStr, paddingX + 5, currentY + BAR_HEIGHT - 8);
                    } else {
                        g2.setColor(Color.WHITE); 
                        g2.drawString(valueStr, paddingX + barWidth + 5, currentY + BAR_HEIGHT - 8);
                    }
                    
                    currentY += BAR_HEIGHT + BAR_SPACING;
                }
            }
        };
        
        /** Ensure barPanel is transparent to see the NAVY_BLUE background */
        barPanel.setOpaque(false); 
        
        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        /** Ensure scroll pane is transparent */
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel legendPanel = createLegendPanel(revenues, expenses);
        
        /** Ensure Legend panel has the same background color */
        legendPanel.setOpaque(true); 
        legendPanel.setBackground(NAVY_BLUE); 
        
        JScrollPane legendScrollPane = new JScrollPane(legendPanel);
        /** Ensure scroll pane for the legend also has the same background */
        legendScrollPane.setOpaque(true);
        legendScrollPane.getViewport().setOpaque(true);
        legendScrollPane.setBackground(NAVY_BLUE); 
        legendScrollPane.getViewport().setBackground(NAVY_BLUE); 
        
        panel.add(legendScrollPane, BorderLayout.EAST);
        return panel;
    }
    
    /**
     * Creates the legend panel showing the index, name, and total for each Revenue and Expense item.
     */
    private JPanel createLegendPanel(List<DataItem> revenues, List<DataItem> expenses) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        int currentNumber = 1;

        /** Revenue Title and Total */
        JLabel revenueTitle = new JLabel("<html><font color='white'><b>Revenues (€" + String.format("%,.0f", revenues.stream().mapToDouble(d -> d.amount).sum()) + ")</b></font></html>");
        revenueTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(revenueTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        /** List all Revenues */
        for (int i = 0; i < revenues.size(); i++) {
            DataItem item = revenues.get(i);
            JLabel label = new JLabel((currentNumber++) + ": " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(pastelGreen(i, revenues.size())); 
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        legendPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        /** Expense Title and Total */
        JLabel expenseTitle = new JLabel("<html><font color='white'><b>Expenses (€" + String.format("%,.0f", expenses.stream().mapToDouble(d -> d.amount).sum()) + ")</b></font></html>");
        expenseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(expenseTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        /** List all Expenses */
        for (int i = 0; i < expenses.size(); i++) {
            DataItem item = expenses.get(i);
            JLabel label = new JLabel((currentNumber++) + ": " + item.name + " (" + String.format("%,.0f €", item.amount) + ")");
            label.setForeground(pastelRed(i, expenses.size())); 
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(label);
        }
        return legendPanel;
    }
    
    /**
     * Creates the Agencies panel as a horizontal bar chart. 
     * Uses a HYBRID scaling approach (Linear for high values, Logarithmic for low values)
     * to show better differentiation in small amounts while preserving overall proportionality.
     */
    private JPanel createAgencyPanel() {
        if (agencies.isEmpty()) {
            /** If no data is found, display a simple message */
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

        JPanel barPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                /** Height depends on the number of agencies */
                return new Dimension(700, preferredHeight); 
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                if (agencies.isEmpty()) return;
                
                /** The background is drawn by the parent component (createContentPanel) */

                int width = getWidth();
                int height = getHeight();
                int paddingX = 20; 
                int paddingY = 20;
                int graphWidth = width - 2 * paddingX;
                
                /** Calculate max amount */
                double max = agencies.stream().mapToDouble(d -> d.amount).max().orElse(1);
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                
                int currentY = paddingY;
                
                /** --- HYBRID SCALING PARAMETERS --- */
                /** Threshold ratio (e.g., 5% of the max value) */
                final double THRESHOLD_RATIO = 0.05; 
                final double THRESHOLD_AMOUNT = max * THRESHOLD_RATIO; 
                
                /** 20% of the bar width is reserved for the logarithmic part (small values) */
                final double LOG_WIDTH_RATIO = 0.20; 
                final int LOG_WIDTH = (int) (graphWidth * LOG_WIDTH_RATIO);
                
                /** The remaining 80% is for the linear part (large values) */
                final int LINEAR_WIDTH = graphWidth - LOG_WIDTH;
                /** --- END HYBRID SCALING PARAMETERS --- */

                /** Find max amount in the log scale range (0 to THRESHOLD_AMOUNT) */
                double maxLogAmount = agencies.stream()
                                            .mapToDouble(d -> d.amount)
                                            .filter(amount -> amount < THRESHOLD_AMOUNT)
                                            .max()
                                            .orElse(0); 

                
                for (int i = 0; i < agencies.size(); i++) {
                    DataItem item = agencies.get(i);
                    int barWidth = 0;
                    
                    if (item.amount > 0) {
                        if (item.amount >= THRESHOLD_AMOUNT) {
                            /** 1. Linear Scale for Large Values */
                            /** Calculate linear scale above the threshold */
                            double linearPart = (item.amount - THRESHOLD_AMOUNT) / (max - THRESHOLD_AMOUNT);
                            barWidth = (int) (LINEAR_WIDTH * linearPart) + LOG_WIDTH;
                            
                        } else {
                            /** 2. Logarithmic Scale for Small Values (to show differentiation) */
                            if (maxLogAmount > 0) {
                                /** Use logarithmic function to differentiate small values */
                                double logBase = Math.E; 
                                double logPart = Math.log(item.amount + logBase) - Math.log(logBase);
                                double logMaxPart = Math.log(THRESHOLD_AMOUNT + logBase) - Math.log(logBase);

                                barWidth = (int) (LOG_WIDTH * (logPart / logMaxPart));
                            } else {
                                /** If all values are very small, use minimum visible width (10px) */
                                barWidth = 10;
                            }
                        }
                    }
                    
                    /** Minimum width check (if amount > 0 and bar disappeared due to rounding) */
                    if (item.amount > 0 && barWidth < 10) { 
                        barWidth = 10; 
                    }
                    
                    /** Coloring */
                    g2.setColor(pastelGreen(i, agencies.size()));
                    
                    /** Draw the horizontal bar (fill) */
                    g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
                    /** Add white border */
                    g2.setColor(Color.WHITE);
                    g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
                    /** --- Display Name & Amount INSIDE the Bar --- */
                    
                    g2.setColor(Color.WHITE);
                    String valueStr = String.format("%s (%,.0f €)", item.name, item.amount);
                    int textWidth = g2.getFontMetrics().stringWidth(valueStr);
                    
                    /** Value placement (inside or outside the bar) */
                    if (barWidth > textWidth + 10) { 
                        g2.setColor(Color.WHITE); 
                        g2.drawString(valueStr, paddingX + 5, currentY + BAR_HEIGHT - 8);
                    } else {
                        /** If it doesn't fit, place it next to the bar */
                        g2.setColor(Color.WHITE); 
                        g2.drawString(valueStr, paddingX + barWidth + 5, currentY + BAR_HEIGHT - 8);
                    }
                    
                    currentY += BAR_HEIGHT + BAR_SPACING;
                }
            }
        };
        
        JPanel panel = new JPanel(new BorderLayout());
        /** Ensure barPanel is transparent */
        barPanel.setOpaque(false); 
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(barPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        /** Ensure scroll pane is transparent */
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        
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
        float saturation = 0.4f + 0.4f * i / Math.max(total - 1, 1); 
        float brightness = 0.7f; 
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
        float saturation = 0.4f + 0.4f * i / Math.max(total - 1, 1); 
        float brightness = 0.7f; 
        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * A simple static class to hold financial data items.
     */
    static class DataItem {
        /** The name of the item (e.g., category, agency name). */
        String name;
        /** The monetary amount. */
        double amount;
        /** The type of item (e.g., "Revenue", "Expense", "Agency"). */
        String type;
        
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
    }
}