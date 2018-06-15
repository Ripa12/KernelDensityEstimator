package interval_tree.CandidateIndex;

public class PartialIndex extends AbstractIndex {

    public enum ConditionType{
        INTERVAL,
        LESS_THAN,
        GREATER_THAN,
        POINT
    }

    private Number start, end;
    private ConditionType conditionType;

    public PartialIndex(double v, double w, String t, String c, Number s, Number e){
        super(v, w, t, c);
        start = s;
        end = e;

        conditionType = ConditionType.INTERVAL;
    }

    public PartialIndex(double v, double w, String t, String c, Number s, ConditionType conditionType){
        super(v, w, t, c);
        start = s;
        end = s;

        this.conditionType = conditionType;
    }

    @Override
    public String createIdxStatement() {
        switch (conditionType) {
            case INTERVAL: {

                String filter = "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ");'";
                return "'CREATE INDEX ON "+ tableName +"("+ columnNames +") where " + filter;
            }
            case POINT: {
                String filter = "(" + columnNames + " = " + start + ");'";
                return "'CREATE INDEX ON "+ tableName +"("+ columnNames +") where " + filter;
            }
            case LESS_THAN: {
                String filter = "(" + columnNames + " <= " + end + ");'";
                return "'CREATE INDEX ON "+ tableName +"("+ columnNames +") where " + filter;
            }
            case GREATER_THAN: {
                String filter = "(" + columnNames + " >= " + start + ");'";
                return "'CREATE INDEX ON "+ tableName +"("+ columnNames +") where " + filter;
            }
            default:
                return null;
        }
    }

    @Override
    public String createIdxStatementWithId(int id) {
        switch (conditionType) {
            case INTERVAL: {
                String filter = "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ");";
                String res = "CREATE INDEX idx_" + id + " ON " + tableName + "(" + columnNames + ") where " + filter;
                return res;
            }
            case POINT: {
                String filter = "(" + columnNames + " = " + end + ");";
                String res = "CREATE INDEX idx_" + id + " ON " + tableName + "(" + columnNames + ") where " + filter;
                return res;
            }
            case LESS_THAN: {
                String filter = "(" + columnNames + " <= " + end + ");";
                String res = "CREATE INDEX idx_" + id + " ON " + tableName + "(" + columnNames + ") where " + filter;
                return res;
            }
            case GREATER_THAN: {
                String filter = "(" + columnNames + " >= " + start + ");";
                String res = "CREATE INDEX idx_" + id + " ON " + tableName + "(" + columnNames + ") where " + filter;
                return res;
            }
            default:
                return null;
        }
    }

    String getPredicate() {

        switch (conditionType) {
            case INTERVAL: {
                return "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ")";
            }
            case POINT: {
                return "(" + columnNames + " = " + start + ")";
            }
            case LESS_THAN: {
                return "(" + columnNames + " <= " + end + ")";
            }
            case GREATER_THAN: {
                return "(" + columnNames + " >= " + start + ")";
            }
            default:
                return null;
        }
    }

    // 1 2
    //[3 8] x
    //[2 4] y

    boolean isOverlapping(PartialIndex other){
        // x1 <= y2 && y1 <= x2
        return other.start.doubleValue() <= end.doubleValue() && start.doubleValue() <= other.end.doubleValue();
    }
}
