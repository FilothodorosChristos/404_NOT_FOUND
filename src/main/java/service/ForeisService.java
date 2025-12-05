package service;

import dao.ForeisDao;
import dao.Foreis;
import java.util.List;

public class ForeisService {

    private final ForeisDao foreisDao = new ForeisDao();

    public void addForeis(Foreis foreis) {
        if (foreis == null) {
            throw new IllegalArgumentException("Foreis cannot be null");
        }
        if (foreis.getYearId() == 0) {
            throw new IllegalArgumentException("Year cannot be null or 0");
        }

        foreisDao.addForeis(foreis);
    }

    public void updateForeis(Foreis foreis) {
        if (foreis == null || foreis.getId() == 0) {
            throw new IllegalArgumentException("Foreis or ID cannot be null/0");
        }

        foreisDao.updateForeis(foreis);
    }

    public void deleteForeis(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("ID cannot be 0");
        }
        foreisDao.deleteForeis(id);
    }

    public List<Foreis> getForeisByYearAndType(int year, String type) {
        if (year == 0) {
            throw new IllegalArgumentException("Year cannot be 0");
        }
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        return foreisDao.selectForeis(year, type);
    }
}
