package dao;

/**
 * Αντικείμενο που αναπαριστά μια εγγραφή στον πίνακα logs της βάσης δεδομένων.
 */
public class Log {
  private int id;
  private String tableName;
  private String operation;
  private Integer rowId;
  private String oldData;
  private String newData;
  private String timestamp;
  
  /**
   * Δημιουργεί ένα αντικείμενο Log με τα δοσμένα πεδία.
   * 
   * @param id το αναγνωριστικό του log
   * @param tableName το όνομα του πίνακα
   * @param operation η λειτουργία (INSERT, UPDATE, DELETE)
   * @param rowId το αναγνωριστικό της εγγραφής που επηρεάστηκε
   * @param oldData τα παλιά δεδομένα (για UPDATE/DELETE)
   * @param newData τα νέα δεδομένα (για INSERT/UPDATE)
   * @param timestamp η χρονική σήμανση της εγγραφής
   */

  public Log(int id, String tableName, String operation, Integer rowId,
                    String oldData, String newData, String timestamp) {
    this.id = id;
    this.tableName = tableName;
    this.operation = operation;
    this.rowId = rowId;
    this.oldData = oldData;
    this.newData = newData;
    this.timestamp = timestamp;
  }

  // getters
  public int getId() {
    return this.id;
  }

  public String getTableName() {
    return this.tableName;
  }

  public String getOperation() {
    return this.operation;
  }

  public Integer getRowId() {
    return this.rowId;
  }

  public String getOldData() {
    return this.oldData;
  }

  public String getNewData() {
    return this.newData;
  }

  public String getTimestamp() {
    return this.timestamp;
  }
}
