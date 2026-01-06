package service;

import dao.Log;
import dao.LogDao;
import java.util.List;

public class LogService {

  private final LogDao logDao;
  private int defaultIndex = 335; // index καθορίζεται εδώ

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
