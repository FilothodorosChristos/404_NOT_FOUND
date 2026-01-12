package gui;

import static gui.FinanceChartPanel.DataItem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.JPanel;

/**
 * Utility class responsible for rendering financial data as horizontal bar charts.
 * Uses a logarithmic scale to effectively visualize data with large magnitude differences,
 * making it easier to compare items that vary significantly in value.
 * 
 * <p>The renderer creates a custom JPanel that displays:
 * <ul>
 *   <li>Horizontal bars representing financial amounts</li>
 *   <li>Logarithmic axis with labeled tick marks</li>
 *   <li>Item numbers and values overlaid on or next to bars</li>
 *   <li>Color-coded bars based on item type (revenue/expense/agency)</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatic scaling based on data range</li>
 *   <li>Minimum visible bar width for very small values</li>
 *   <li>Smart text placement (inside or outside bars)</li>
 *   <li>Customizable color schemes via function parameters</li>
 * </ul>
 * 
 * @author YourName
 * @version 1.0
 * @see FinanceChartPanel
 */
public class ChartRenderer {
    
  /** Background color for chart elements (navy blue). */
  private static final Color NAVY_BLUE = new Color(0, 0, 128);
    
  /** Dark background color used for text contrast. */
  private static final Color DARK_BACKGROUND = NAVY_BLUE;
    
  /** Font used for labels, values, and axis markers. */
  private static final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 12);
    
  /** Height of each individual bar in pixels. */
  private static final int BAR_HEIGHT = 25;
    
  /** Vertical spacing between bars in pixels. */
  private static final int BAR_SPACING = 5;

  /**
   * Creates a JPanel containing a horizontal bar chart with logarithmic scaling.
   * 
   * <p>The chart uses a logarithmic scale on the x-axis to handle data with large
   * magnitude differences. This allows effective visualization of datasets where
   * values can range from millions to trillions.
   * 
   * <p>Bar colors are determined by calling the provided color functions with the
   * item's index and total count. Revenue and agency items use the revenue color
   * function, while expense items use the expense color function if provided.
   * 
   * <p>Text labels are intelligently placed:
   * <ul>
   *   <li>Inside the bar (dark text) if the bar is wide enough</li>
   *   <li>Outside the bar (white text) if the bar is too narrow</li>
   * </ul>
   * 
   * @param allItems List of financial data items to visualize
   * @param revenueColorFunction Function that generates colors for revenue/agency bars.
   *                            Takes (item_index, total_count) and returns a Color
   * @param expenseColorFunction Function that generates colors for expense bars.
   *                            Takes (item_index, total_count) and returns a Color.
   *                            Can be null if no expense items are present
   * @param formatter Function to format numeric values for axis labels.
   *                 Takes a double value and returns a formatted string
   * @return A JPanel containing the rendered bar chart. Returns an empty panel
   *         if the input list is empty
   * 
   * @throws NullPointerException if allItems, revenueColorFunction, or formatter is null
   */
  public static JPanel createBarChartPanel(
            List<DataItem> allItems, 
            BiFunction<Integer, Integer, Color> revenueColorFunction, 
            BiFunction<Integer, Integer, Color> expenseColorFunction,
            Function<Double, String> formatter) {

    if (allItems.isEmpty()) {
      return new JPanel();
    }
    // Calculate logarithmic range for scaling
    double minAmount = allItems
        .stream().mapToDouble(d -> d.amount).filter(a -> a > 0).min().orElse(1.0);
    double maxAmount = allItems.stream().mapToDouble(d -> d.amount).max().orElse(1.0);
        
    final double logmin = Math.log10(Math.max(1.0, minAmount));
    final double logmax = Math.log10(Math.max(1.0, maxAmount));
    final double logrange = logmax - logmin;
        
    // Calculate required panel height based on number of items
    int preferredHeight = allItems.size() * (BAR_HEIGHT + BAR_SPACING) + 60; 

    return new JPanel() {
        @Override
            public Dimension getPreferredSize() {
            return new Dimension(700, preferredHeight + 25); 
        }
            
        /**
         * Custom paint method that renders the bar chart with logarithmic scaling.
         * 
         * <p>Rendering steps:
         * <ol>
         *   <li>Draw horizontal axis line</li>
         *   <li>Draw logarithmic tick marks and labels</li>
         *   <li>Render bars with calculated widths</li>
         *   <li>Add item numbers and value labels</li>
         * </ol>
         * 
         * @param g Graphics context for drawing
         */
        @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            final int paddingY = 20;
                
            // Use larger left padding for revenue/expense charts to accommodate item numbers
            final int paddingX = allItems.stream()
                .anyMatch(d -> d.type.equals("Revenue") || d.type.equals("Expense")) ? 80 : 20;
            final int axisMarginBottom = 30; 

            final int minvisiblebarwidth = 5; 
                
            int chartBottomY = getHeight() - axisMarginBottom;
                
            Graphics2D g2 = (Graphics2D) g;
 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(DETAIL_FONT);
 
            // Draw horizontal axis line
            g2.setColor(new Color(100, 100, 100)); 
            int graphWidth = width - paddingX - 20;   
            g2.drawLine(paddingX, chartBottomY, width - 20, chartBottomY);
            g2.setColor(Color.LIGHT_GRAY);
                
            // Draw logarithmic scale tick marks and labels
            for (double logVal = Math.ceil(logmin); logVal <= logmax; logVal++) {
            double ratio = (logVal - logmin) / logrange;
            int x = paddingX + (int) (ratio * graphWidth);
                    
            if (x > paddingX) { 
                double value = Math.pow(10, logVal);
                String label = formatter.apply(value); 
                        
                // Draw tick mark
                g2.drawLine(x, chartBottomY, x, chartBottomY + 5);
                        
                // Draw label centered under tick
                g2.drawString(label,
                         x - g2.getFontMetrics().stringWidth(label) / 2, chartBottomY + 20);
            }
            }

            // Render bars for each data item
            int currentY = paddingY;
            for (int i = 0; i < allItems.size(); i++) {
            DataItem item = allItems.get(i);
            int barWidth = 0;

            // Calculate bar width using logarithmic scale
            if (item.amount > 0) {
                double logValue = Math.log10(item.amount);
                double ratio = (logValue - logmin) / logrange;
                barWidth = (int) (ratio * graphWidth);
                        
            // Ensure minimum visibility for very small values
            if (barWidth < minvisiblebarwidth) { 
                barWidth = minvisiblebarwidth; 
            }
            }

            // Set bar color based on item type
            if (item.type.equals("Revenue") || item.type.equals("Agency")) {
                g2.setColor(revenueColorFunction.apply(i, allItems.size()));
            } else if (item.type.equals("Expense") && expenseColorFunction != null) {
                g2.setColor(expenseColorFunction.apply(i, allItems.size()));
            } else {
                g2.setColor(Color.WHITE); 
            }
                    
            // Draw filled bar
            g2.fillRect(paddingX, currentY, barWidth, BAR_HEIGHT);
                    
            // Draw bar outline for definition
            g2.setColor(new Color(255, 255, 255, 150)); 
            g2.drawRect(paddingX, currentY, barWidth, BAR_HEIGHT); 

            g2.setColor(Color.WHITE); 
                    
            // Draw item number on the left (if space allows)
            if (paddingX > 50) {
                String numberStr = String.valueOf(i + 1);
                g2.drawString(numberStr, 
                                 paddingX - g2.getFontMetrics().stringWidth(numberStr) - 5, 
                                 currentY + BAR_HEIGHT - 8);
            }

            // Format value string (include name for agencies)
            String valueStr;
            if (item.type.equals("Agency")) {
                valueStr = String.format("%s (%,.0f €)", item.name, item.amount);
            } else {
                valueStr = String.format("%,.0f €", item.amount);
            }
                    
            int textWidth = g2.getFontMetrics().stringWidth(valueStr);

            // Smart text placement: inside bar if it fits, otherwise outside
            if (barWidth > textWidth + 10) {
                // Text inside bar (dark color for contrast)
                g2.setColor(DARK_BACKGROUND); 
                g2.drawString(valueStr, paddingX + 5, currentY + BAR_HEIGHT - 8);
            } else {
                // Text outside bar (white color)
                g2.setColor(Color.WHITE); 
                if (barWidth > 0) {
                g2.drawString(valueStr, paddingX + barWidth + 5,
                                 currentY + BAR_HEIGHT - 8);
                }
            }
                    
            currentY += BAR_HEIGHT + BAR_SPACING;
            }
        }
        };
  }
}