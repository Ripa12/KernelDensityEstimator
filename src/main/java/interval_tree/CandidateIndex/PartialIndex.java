package interval_tree.CandidateIndex;

public class PartialIndex extends AbstractIndex {

    private int start, end;

    public PartialIndex(double v, double w, String t, String c, int s, int e){
        super(v, w, t, c);
        start = s;
        end = e;
    }

    @Override
    public String createIdxStatement() {
        String filter = "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ");'";
        return "'CREATE INDEX ON "+ tableName +"("+ columnNames +") where " + filter; // ToDo: what table-name to use?
    }

    @Override
    public String createIdxStatementWithId(int id) {
        String filter = "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ");";
        String res =  "CREATE INDEX idx_" + id + " ON "+ tableName +"("+ columnNames +") where " + filter; // ToDo: what table-name to use?
        return res;
    }

    public String getPredicate() {
        return "(" + columnNames + " >= " + start + " AND " + columnNames + " <= " + end + ")";
    }
}
