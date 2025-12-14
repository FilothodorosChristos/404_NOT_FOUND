package GUI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static GUI.FinanceChartPanel.DataItem;

/**
 * Handles the loading and parsing of financial data from CSV files.
 */
public class DataImporter {
    
    private final String year;
    private final List<DataItem> revenues = new ArrayList<>();
    private final List<DataItem> expenses = new ArrayList<>();
    private final List<DataItem> agencies = new ArrayList<>();
    
    public DataImporter(String year) {
        this.year = year;
    }
    
    public List<DataItem> getRevenues() { return revenues; }
    public List<DataItem> getExpenses() { return expenses; }
    public List<DataItem> getAgencies() { return agencies; }

    public void loadData() {
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
                            } catch (NumberFormatException ignored) { }
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
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) { }
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