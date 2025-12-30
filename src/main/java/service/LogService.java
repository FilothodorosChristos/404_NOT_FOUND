
package service;

import dao.Log;
import dao.LogDao;
import java.util.List;

public class LogService {

    private final LogDao logDao;
    private final int defaultIndex = 335; // index καθορίζεται εδώ

    public LogService(LogDao logDao) {
        this.logDao = logDao;
    }

    /**
     * Επιστρέφει όλα τα logs από το default index και μετά.
     */
    public List<Log> getLogsFrom() {
        return logDao.selectLog(defaultIndex);
    }
}
