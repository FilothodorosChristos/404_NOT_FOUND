package dao;

public class Log {
    private int id;
    private String tableName;
    private String operation;
    private Integer rowId;
    private String oldData;
    private String newData;
    private String timestamp;

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

    //getters
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
