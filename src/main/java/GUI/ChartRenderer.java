package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import static GUI.FinanceChartPanel.DataItem;

/**
 * Utility class responsible for rendering the actual bar charts, using 
 * logarithmic scale for magnitude comparison.
 */
public class ChartRenderer {
    
    private static final Color NAVY_BLUE = new Color(0, 0, 128); 
    private static final Color DARK_BACKGROUND = NAVY_BLUE;
    private static final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final int BAR_HEIGHT = 25;
    private static final int BAR_SPACING = 5;

    public static JPanel createBarChartPanel(
            List<DataItem> allItems, 
            BiFunction<Integer, Integer, Color> revenueColorFunction, 
            BiFunction<Integer, Integer, Color> expenseColorFunction,
            Function<Double, String> formatter) {

        if (allItems.isEmpty()) return new JPanel();

        double minAmount = allItems.stream().mapToDouble(d -> d.amount).filter(a -> a > 0).min().orElse(1.0);
        double maxAmount = allItems.stream().mapToDouble(d -> d.amount).max().orElse(1.0);
        
        final double LOG_MIN = Math.log10(Math.max(1.0, minAmount));
        final double LOG_MAX = Math.log10(Math.max(1.0, maxAmount));
        final double LOG_RANGE = LOG_MAX - LOG_MIN;
        
        int preferredHeight = allItems.size() * (BAR_HEIGHT + BAR_SPACING) + 60; 

        return new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, preferredHeight + 25); 
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int width = getWidth();
                final int paddingY = 20;
                final int paddingX = allItems.stream().anyMatch(d -> d.type.equals("Revenue") || d.type.equals("Expense")) ? 80 : 20;
                final int axisMarginBottom = 30; 
                final int MIN_VISIBLE_BAR_WIDTH = 5;
                
                int graphWidth = width - paddingX - 20; 
                int chartBottomY = getHeight() - axisMarginBottom;
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(DETAIL_FONT);
                
                g2.setColor(new Color(100, 100, 100)); 
                g2.drawLine(paddingX, chartBottomY, width - 20, chartBottomY);
                g2.setColor(Color.LIGHT_GRAY);
                
                for (double logVal = Math.ceil(LOG_MIN); logVal <= LOG_MAX; logVal++) {
                    double ratio = (logVal - LOG_MIN) / LOG_RANGE;
                    int x = paddingX + (int) (ratio * graphWidth);
                    
                    if (x > paddingX) { 
                        double value = Math.pow(10, logVal);
                        String label = formatter.apply(value); 
                        
                        g2.drawLine(x, chartBottomY, x, chartBottomY + 5);
                        g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, chartBottomY + 20);
                    }
                }

                int currentY = paddingY;
                for (int i = 0; i < allItems.size(); i++) {
                    DataItem item = allItems.get(i);
                    int barWidth = 0;

                    if (item.amount > 0) {
                        double logValue = Math.log10(item.amount);
                        double ratio = (logValue - LOG_MIN) / LOG_RANGE;
                        barWidth = (int) (ratio * graphWidth);
                        if (barWidth < MIN_VISIBLE_BAR_WIDTH) barWidth = MIN_VISIBLE_BAR_WIDTH; 
                    }

                    if (item.type.equals("Revenue") || item.type.equals("Agency")) {
                        g2.setColor(revenueColorFunction.apply(i, allItems.size()));
                    } else if (item.type.equals("Expense") && expenseColorFunction != null) {
                        g2.setColor(expenseColorFunction.apply(i, allItems.size()));
                    } else {
                         g2.setColor(Color.WHITE); 
                    }
                    
                    g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
                    g2.setColor(new Color(255, 255, 255, 150)); 
                    g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT); 

                    g2.setColor(Color.WHITE); 
                    
                    if (paddingX > 50) {
                         String numberStr = String.valueOf(i + 1);
                         g2.drawString(numberStr, 
                                 paddingX - g2.getFontMetrics().stringWidth(numberStr) - 5, 
                                 currentY + BAR_HEIGHT - 8);
                    }

                    String valueStr;
                    if (item.type.equals("Agency")) {
                         valueStr = String.format("%s (%,.0f €)", item.name, item.amount);
                    } else {
                         valueStr = String.format("%,.0f €", item.amount);
                    }
                    
                    int textWidth = g2.getFontMetrics().stringWidth(valueStr);

                    if (barWidth > textWidth + 10) {
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
    }
}