package service;

import dao.LogDao;
import dao.Log;
import java.util.List;

public class LogService {

    private final LogDao logDao = new LogDao();

    /**
     * Επιστρέφει όλα τα logs από τη βάση δεδομένων.
     */
    public List<Log> getAllLogs() {
        return logDao.selectLog();
    }
}
