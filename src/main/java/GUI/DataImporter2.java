package GUI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static GUI.FinanceChartPanel.DataItem;

/**
 * Handles the loading and parsing of financial data from CSV files.
 * This class reads budget data (revenues, expenses, and agency allocations) 
 * for a specified fiscal year from CSV files located in the resources/data directory.
 * 
 * <p>The importer supports three types of data files:
 * <ul>
 *   <li>Revenue files (esoda.csv, esodatest.csv)</li>
 *   <li>Expense files (exoda.csv, exodatest.csv)</li>
 *   <li>Agency allocation files (foreis.csv)</li>
 * </ul>
 * 
 * <p>All files must be named with the year suffix (e.g., b23 for year 2023)
 * and use either semicolon or comma as field delimiters.
 * 
 * @author YourName
 * @version 1.0
 * @see FinanceChartPanel.DataItem
 */
public class DataImporter2 {
    
    /** The fiscal year for which data should be loaded (e.g., "2023") */
    private final String year;
    
    /** List of revenue items loaded from CSV files */
    private final List<DataItem> revenues = new ArrayList<>();
    
    /** List of expense items loaded from CSV files */
    private final List<DataItem> expenses = new ArrayList<>();
    
    /** List of agency budget allocation items loaded from CSV files */
    private final List<DataItem> agencies = new ArrayList<>();
    
    /**
     * Constructs a new DataImporter2 for the specified fiscal year.
     * 
     * @param year The fiscal year to load data for (e.g., "2023")
     */
    public DataImporter2(String year) {
        this.year = year;
    }
    
    /**
     * Returns the list of revenue items that have been loaded.
     * 
     * @return An unmodifiable view of the revenues list
     */
    public List<DataItem> getRevenues() { 
        return new ArrayList<>(revenues); 
    }
    
    /**
     * Returns the list of expense items that have been loaded.
     * 
     * @return An unmodifiable view of the expenses list
     */
    public List<DataItem> getExpenses() { 
        return new ArrayList<>(expenses);
    }
    
    /**
     * Returns the list of agency allocation items that have been loaded.
     * 
     * @return An unmodifiable view of the agencies list
     */
    public List<DataItem> getAgencies() { 
        return new ArrayList<>(agencies); 
    }

    /**
     * Loads financial data from CSV files in the resources/data directory.
     * 
     * <p>This method searches for CSV files matching the fiscal year suffix
     * and parses them according to their type (revenue, expense, or agency).
     * Files must follow these naming conventions:
     * <ul>
     *   <li>Revenue/Expense files: esoda[test]_bYY.csv or exoda[test]_bYY.csv</li>
     *   <li>Agency files: foreis_bYY.csv</li>
     * </ul>
     * 
     * <p>CSV Format Requirements:
     * <ul>
     *   <li><b>Revenue/Expense files:</b> year, type, name, amount</li>
     *   <li><b>Agency files:</b> multiple fields with name at index 3 and amount at index 6</li>
     * </ul>
     * 
     * <p>The method automatically:
     * <ul>
     *   <li>Skips header rows</li>
     *   <li>Handles both semicolon and comma delimiters</li>
     *   <li>Filters out zero or negative amounts</li>
     *   <li>Supports both English and Greek type labels</li>
     *   <li>Uses UTF-8 encoding for proper character handling</li>
     * </ul>
     * 
     * <p>Errors during file reading are logged to System.err but do not stop
     * the loading process for other files.
     * 
     * @throws RuntimeException if the resources/data directory does not exist
     */
    public void loadData() {
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator 
                    + "main" + File.separator + "resources" + File.separator + "data";
        File folder = new File(path);
        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return;
        
        // Extract year suffix (e.g., "23" from "2023")
        String yearSuffix = year.substring(2); 
        String yearPrefix = "b" + yearSuffix; 

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) { 
                
                // Skip header row
                br.readLine(); 
                String line;
                
                // Process revenue and expense files
                if ((fileName.contains("esoda.csv") || fileName.contains("exoda.csv") 
                        || fileName.contains("esodatest.csv") || fileName.contains("exodatest.csv")) 
                        && fileName.contains(yearPrefix)) {
                    
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("[;,]"); 
                        
                        // Validate format: year, type, name, amount
                        if (parts.length >= 4 && parts[0].trim().equals(year)) { 
                            String type = parts[1].trim();
                            String name = parts[2].trim();
                            
                            try {
                                double amount = Double.parseDouble(parts[3].trim());
                                
                                // Skip zero amounts
                                if (amount == 0) continue;
                                
                                // Categorize by type (supports English and Greek)
                                if (type.equalsIgnoreCase("Revenue") || type.equalsIgnoreCase("Έσοδο"))
                                    revenues.add(new DataItem(name, amount, "Revenue"));
                                else if (type.equalsIgnoreCase("Expense") || type.equalsIgnoreCase("Έξοδο"))
                                    expenses.add(new DataItem(name, amount, "Expense"));
                            } catch (NumberFormatException ignored) { 
                                // Skip malformed numeric values
                            }
                        }
                    }
                } 
                // Process agency allocation files
                else if (fileName.contains("foreis.csv") && fileName.contains(yearPrefix)) { 
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("[;,]"); 
                        
                        // Validate format: at least 7 fields required
                        if (parts.length >= 7) { 
                            try {
                                // Agency name at index 3, amount at index 6
                                String name = parts[3].trim(); 
                                double amount = Double.parseDouble(parts[6].trim()); 
                                
                                // Only add positive amounts
                                if (amount > 0) {
                                    agencies.add(new DataItem(name, amount, "Agency"));
                                }
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) { 
                                // Skip malformed rows
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
}