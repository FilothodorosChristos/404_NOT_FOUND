package GUI;

import static GUI.FinanceChartPanel.DataItem;

import dao.CashFlow;
import dao.Foreis;
import java.util.ArrayList;
import java.util.List;
import service.CashFlowService;
import service.ForeisService;

/**
 * Loads financial data from the database for a specific fiscal year.
 * Uses CashFlowService for revenues/expenses and ForeisService for agency data.
 */
public class GraphDataImporter {
    
  /** Fiscal year to load data for. */
  private final String year;
    
  /** Revenue items from database. */
  private final List<DataItem> revenues = new ArrayList<>();
    
  /** Expense items from database. */
  private final List<DataItem> expenses = new ArrayList<>();
    
  /** Agency budget items from database. */
  private final List<DataItem> agencies = new ArrayList<>();
    
  /** Service for cash flow data. */
  private final CashFlowService cashFlowService;
    
  /** Service for agency data. */
  private final ForeisService foreisService;
    
  /**
   * Creates a new importer for the given year.
   * 
   * @param year Fiscal year (e.g., "2023")
   */
  public GraphDataImporter(String year) {
    this.year = year;
    this.cashFlowService = new CashFlowService();
    this.foreisService = new ForeisService();
  }
    
  /**
   * Returns loaded revenue data.
   * 
   * @return Copy of revenues list
   */
  public List<DataItem> getRevenues() { 
    return new ArrayList<>(revenues); 
  }
    
  /**
   * Returns loaded expense data.
   * 
   * @return Copy of expenses list
   */
  public List<DataItem> getExpenses() { 
    return new ArrayList<>(expenses);
  }
    
  /**
   * Returns loaded agency data.
   * 
   * @return Copy of agencies list
   */
  public List<DataItem> getAgencies() { 
    return new ArrayList<>(agencies); 
  }

  /**
   * Loads all financial data from the database.
   * Fetches revenues, expenses, and agency budgets for the specified year.
   * Filters out zero/negative amounts automatically.
   */
  public void loadData() {
    try {
      int yearInt = Integer.parseInt(year);
            
      // Load revenues (Έσοδο)
      try {
        List<CashFlow> revenueData = cashFlowService.getCashflows(yearInt, "Έσοδο");
        for (CashFlow cf : revenueData) {
          if (cf.getAmount() > 0) {
            revenues.add(new DataItem(cf.getName(), cf.getAmount(), "Revenue"));
          }
        }
      } catch (Exception e) {
        System.err.println("Error loading revenue data for year " + year + ": " + e.getMessage());
        e.printStackTrace();
      }
            
      // Load expenses (Έξοδο)
      try {
        List<CashFlow> expenseData = cashFlowService.getCashflows(yearInt, "Έξοδο");
        for (CashFlow cf : expenseData) {
          if (cf.getAmount() > 0) {
            expenses.add(new DataItem(cf.getName(), cf.getAmount(), "Expense"));
          }
        }
      } catch (Exception e) {
        System.err.println("Error loading expense data for year " + year + ": " + e.getMessage());
        e.printStackTrace();
      }
            
      // Load agencies (Foreis) - all types
      try {
        List<Foreis> allForeis = new ArrayList<>();
                
        // Load all three types of agencies
        allForeis.addAll(foreisService.getForeisByYearAndType(yearInt, "Κεντρική Διοίκηση"));
        allForeis.addAll(foreisService.getForeisByYearAndType(yearInt, "Υπουργείο"));
        allForeis.addAll(foreisService.getForeisByYearAndType(yearInt, "Αποκεντρωμένη Διοίκηση"));
                
        for (Foreis f : allForeis) {
          if (f.getTotal() > 0) {
            agencies.add(new DataItem(f.getName(), f.getTotal(), "Agency"));
          }
        }
      } catch (Exception e) {
        System.err.println("Error loading agency data for year " + year + ": " + e.getMessage());
        e.printStackTrace();
      }
            
    } catch (NumberFormatException e) {
      System.err.println("Invalid year format: " + year);
      e.printStackTrace();
    }
  }
}