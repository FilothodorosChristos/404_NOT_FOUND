package gui;

import dao.CashFlow;
import dao.Foreis;
import dto.CashFlowCompareDto;
import dto.ForeasCompareDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import service.CashFlowService;
import service.ForeisService;

/**
 * Service for comparing budget data between two years.
 */
public class ComparisonService {
    
  private CashFlowService cashFlowService;
  private ForeisService foreisService;
  /**
   * Service for comparing budget data between two years.
   */

  public ComparisonService() {
    this.cashFlowService = new CashFlowService();
    this.foreisService = new ForeisService();
  }
    
  /**
   * Compares cash flows between two years.
   * * @param year1 First year to compare
   * 
   * @param year2 Second year to compare
   * @param type Type of cash flow ("Έσοδο" or "Έξοδο")
   * @return List of comparison DTOs
   */
  public List<CashFlowCompareDto> compareCashFlows(int year1, int year2, String type) {
    List<CashFlow> cashFlowsYear1 = cashFlowService.getCashflows(year1, type);
    List<CashFlow> cashFlowsYear2 = cashFlowService.getCashflows(year2, type);
        
    Map<String, CashFlow> mapYear1 = new HashMap<>();
    Map<String, CashFlow> mapYear2 = new HashMap<>();
        
    for (CashFlow cf : cashFlowsYear1) {
      mapYear1.put(cf.getName(), cf);
    }
        
    for (CashFlow cf : cashFlowsYear2) {
      mapYear2.put(cf.getName(), cf);
    }
        
    List<CashFlowCompareDto> results = new ArrayList<>();
        
 
    for (Map.Entry<String, CashFlow> entry : mapYear1.entrySet()) {
      String name = entry.getKey();
      if (!mapYear2.containsKey(name)) {
        CashFlow cf = entry.getValue();
        results.add(new CashFlowCompareDto(
                    name,
                    cf.getAmount(),
                    0.0,
                    false,
                    true
                ));
      }
    }
        

    for (Map.Entry<String, CashFlow> entry : mapYear2.entrySet()) {
      String name = entry.getKey();
      CashFlow cfYear2 = entry.getValue();
      if (mapYear1.containsKey(name)) {
        CashFlow cfYear1 = mapYear1.get(name);
        results.add(new CashFlowCompareDto(
                    name,
                    cfYear1.getAmount(),
                    cfYear2.getAmount(),
                    false,
                    false
                ));
      } else {
        results.add(new CashFlowCompareDto(
                    name,
                    0.0,
                    cfYear2.getAmount(),
                    true,
                    false
                ));
      }
    }
        
    return results;
  }
    
  /**
   * Compares foreis (organizations) between two years.
   * * @param year1 First year to compare
   * 
   * @param year2 Second year to compare
   * @return List of comparison DTOs
   */
  public List<ForeasCompareDto> compareForeis(int year1, int year2) {
        
    List<Foreis> foreisYear1 = new ArrayList<>();
    foreisYear1.addAll(foreisService.getForeisByYearAndType(year1, "Κεντρική Διοίκηση"));
    foreisYear1.addAll(foreisService.getForeisByYearAndType(year1, "Υπουργείο"));
    foreisYear1.addAll(foreisService.getForeisByYearAndType(year1, "Αποκεντρωμένη Διοίκηση"));
        
    List<Foreis> foreisYear2 = new ArrayList<>();
    foreisYear2.addAll(foreisService.getForeisByYearAndType(year2, "Κεντρική Διοίκηση"));
    foreisYear2.addAll(foreisService.getForeisByYearAndType(year2, "Υπουργείο"));
    foreisYear2.addAll(foreisService.getForeisByYearAndType(year2, "Αποκεντρωμένη Διοίκηση"));
        
    Map<Integer, Foreis> mapYear1 = new HashMap<>();
    Map<Integer, Foreis> mapYear2 = new HashMap<>();
        
    for (Foreis f : foreisYear1) {
      mapYear1.put(f.getForeasId(), f);
    }
        
    for (Foreis f : foreisYear2) {
      mapYear2.put(f.getForeasId(), f);
    }
        
    List<ForeasCompareDto> results = new ArrayList<>();
        
      
    for (Map.Entry<Integer, Foreis> entry : mapYear1.entrySet()) {
      Integer id = entry.getKey();
      Foreis f1 = entry.getValue();
      Foreis f2 = mapYear2.get(id);
            
      if (f2 != null) {
                
        ForeasCompareDto dto = createCompareDto(f1, f2);
        results.add(dto);
      } else {
                
        ForeasCompareDto dto = createCompareDto(f1, null);
        results.add(dto);
      }
    }
        
       
    for (Map.Entry<Integer, Foreis> entry : mapYear2.entrySet()) {
      Integer id = entry.getKey();
      if (!mapYear1.containsKey(id)) {
        Foreis f2 = entry.getValue();
        ForeasCompareDto dto = createCompareDto(null, f2);
        results.add(dto);
      }
    }
        
    return results;
  }
    
  /**
   * Creates a ForeasCompareDto from two Foreis objects.
   */
  private ForeasCompareDto createCompareDto(Foreis year1, Foreis year2) {
    ForeasCompareDto dto = new ForeasCompareDto();
        
    if (year1 != null) {
      dto.setForeasId(year1.getForeasId());
      dto.setName(year1.getName());
      dto.setRegularYear1(year1.getRegularBudget());
      dto.setPublicInvYear1(year1.getPublicInvBudget());
      dto.setTotalYear1(year1.getTotal());
    }
        
    if (year2 != null) {
      if (year1 == null) {
        dto.setForeasId(year2.getForeasId());
        dto.setName(year2.getName());
      }
      dto.setRegularYear2(year2.getRegularBudget());
      dto.setPublicInvYear2(year2.getPublicInvBudget());
      dto.setTotalYear2(year2.getTotal());
    }
        
        
    dto.setRegularDiff(dto.getRegularYear2() - dto.getRegularYear1());
    dto.setPublicInvDiff(dto.getPublicInvYear2() - dto.getPublicInvYear1());
    dto.setTotalDiff(dto.getTotalYear2() - dto.getTotalYear1());
        
        
    if (dto.getRegularYear1() != 0) {
      dto.setRegularPercentChange((dto.getRegularDiff() / dto.getRegularYear1()) * 100);
    }
        
    if (dto.getPublicInvYear1() != 0) {
      dto.setPublicInvPercentChange((dto.getPublicInvDiff() / dto.getPublicInvYear1()) * 100);
    }
        
    if (dto.getTotalYear1() != 0) {
      dto.setTotalPercentChange((dto.getTotalDiff() / dto.getTotalYear1()) * 100);
    }
        
    return dto;
  }
}