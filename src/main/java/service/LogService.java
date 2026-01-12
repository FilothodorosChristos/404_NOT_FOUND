package service;

import dao.Log;
import dao.LogDao;
import java.util.List;

/**
 * Service κλάση για την διαχείριση των logs.
 */
public class LogService {

  private final LogDao logDao;
  private int defaultIndex = 335; // index καθορίζεται εδώ

  /**
   * Δημιουργεί ένα LogService με το δοσμένο LogDao.
   *
   * @param logDao το LogDao για πρόσβαση στα δεδομένα των logs
   */
  public LogService(LogDao logDao) {
    this.logDao = logDao;
  }

  /**
   * Ρυθμίζει το default index από το οποίο θα ξεκινούν τα logs.
   */
  public void setIndex(int index) {
    this.defaultIndex = index;
  }

  /**
   * Επιστρέφει όλα τα logs από το default index και μετά.
   */
  public List<Log> getLogsFrom() {
    return logDao.selectLog(defaultIndex);
  }
}
