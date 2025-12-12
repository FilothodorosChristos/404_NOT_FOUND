package service;

import dao.Foreis;
import dao.ForeisDao;
import java.util.List;
import util.ValidationUtils;

/**
 * Service layer για την οντότητα {@link Foreis}.
 * Περιέχει επιχειρησιακή λογική και validation κανόνες
 * πριν την επικοινωνία με το {@link ForeisDao}.
 *
 * <p>Οι βασικοί κανόνες που εφαρμόζονται:
 * <ul>
 *   <li>Έλεγχος έτους (2023–2025)</li>
 *   <li>Έλεγχος θετικών/μη αρνητικών αριθμητικών πεδίων</li>
 *   <li>Έλεγχος ότι το total = regular_budget + public_inv_budget</li>
 *   <li>Έλεγχος επιτρεπτής μεταβολής συνολικού ποσού (±45%)</li>
 * </ul>
 */
public class ForeisService {

  private final ForeisDao foreisDao = new ForeisDao();
 
  /**
   * Προσθέτει νέο Foreis στη βάση δεδομένων.
   * Πραγματοποιεί όλους τους απαραίτητους ελέγχους:
   * <ul>
   *   <li>Έτος εντός ορίων</li>
   *   <li>Θετικό foreas_id</li>
   *   <li>Μη αρνητικά ποσά</li>
   *   <li>Ορθότητα total (regular + public_inv)</li>
   * </ul>
   *
   * @param foreis το αντικείμενο προς εισαγωγή
   * @throws IllegalArgumentException αν κάποιος κανόνας αποτύχει
   */
  public void addForeis(Foreis foreis) {
    if (foreis == null) {
      throw new IllegalArgumentException("Foreis cannot be null");
    }
      
    ValidationUtils.validateYear(foreis.getYearId());
    ValidationUtils.validatePositiveId(foreis.getForeasId(), "Foreas ID");
    ValidationUtils.validateNonNegative(foreis.getRegularBudget());
    ValidationUtils.validateNonNegative(foreis.getPublicInvBudget());
    ValidationUtils.validateNonNegative(foreis.getTotal());

    double expectedTotal = foreis.getRegularBudget() + foreis.getPublicInvBudget();
    if (Math.abs(expectedTotal - foreis.getTotal()) > 1.0) {
      throw new IllegalArgumentException(
             "Το total πρέπει να ισούται με regular_budget + public_inv_budget"
            );
    }

    foreisDao.addForeis(foreis);
  }
  /**
   * Ενημερώνει υπάρχον Foreis με βάση το ID.
   * Πραγματοποιεί:
   * <ul>
   *   <li>Έλεγχο ύπαρξης εγγραφής</li>
   *   <li>Έλεγχο έτους</li>
   *   <li>Έλεγχο θετικών/μη αρνητικών τιμών</li>
   *   <li>Έλεγχο ορθότητας total</li>
   *   <li>Έλεγχο επιτρεπτής μεταβολής συνολικού ποσού (±45%)</li>
   * </ul> 
   *    
   * @param foreis το αντικείμενο με ενημερωμένα δεδομένα
   * @throws IllegalArgumentException αν κάποιος κανόνας αποτύχει
   */

  public void updateForeis(Foreis foreis) {
    if (foreis == null || foreis.getId() == 0) {
      throw new IllegalArgumentException("Foreis or ID cannot be null/0");
    }
     
    ValidationUtils.validatePositiveId(foreis.getId(), "Foreis");
    ValidationUtils.validateYear(foreis.getYearId());
    ValidationUtils.validatePositiveId(foreis.getForeasId(), "Foreas ID");
    ValidationUtils.validateNonNegative(foreis.getRegularBudget());
    ValidationUtils.validateNonNegative(foreis.getPublicInvBudget());
    ValidationUtils.validateNonNegative(foreis.getTotal());

    double expectedTotal = foreis.getRegularBudget() + foreis.getPublicInvBudget();
    if (Math.abs(expectedTotal - foreis.getTotal()) > 1.0) {
      throw new IllegalArgumentException(
                "Το total πρέπει να ισούται με regular_budget + public_inv_budget"
            );
    }

    Foreis existing = foreisDao.selectForeisById(foreis.getId());
    if (existing == null) {
      throw new IllegalArgumentException("Foreis with ID " + foreis.getId() + " not found");
    }

    ValidationUtils.validateAmountChange(existing.getTotal(), foreis.getTotal());

    foreisDao.updateForeis(foreis);
  }
  
  /**
   * Διαγράφει Foreis με βάση το ID.
   *
   * @param id το μοναδικό αναγνωριστικό
   * @throws IllegalArgumentException αν το ID δεν είναι θετικό
   */

  public void deleteForeis(int id) {
    if (id == 0) {
      throw new IllegalArgumentException("ID cannot be 0");
    }
    ValidationUtils.validatePositiveId(id, "Foreis");
    foreisDao.deleteForeis(id);
  }
  
  /**
   * Επιστρέφει όλους τους Foreis για συγκεκριμένο έτος και τύπο.
   *
   * @param year το έτος (2023–2025)
   * @param type ο τύπος (π.χ. "Τακτικός", "ΠΔΕ")
   * @return λίστα με Foreis
   * @throws IllegalArgumentException αν το έτος ή ο τύπος δεν είναι έγκυρα
   */

  public List<Foreis> getForeisByYearAndType(int year, String type) {
    if (year == 0) {
      throw new IllegalArgumentException("Year cannot be 0");
    }
    ValidationUtils.validateYear(year);
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Type cannot be null or empty");
    }
    return foreisDao.selectForeis(year, type);
  }
}

