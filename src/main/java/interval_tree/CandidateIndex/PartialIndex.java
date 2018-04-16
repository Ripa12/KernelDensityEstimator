package interval_tree.CandidateIndex;

public class PartialIndex extends AbstractIndex {

    private int start, end; // ToDo: maybe put Interval or Point from MyIntervalTree here

    public PartialIndex(double v, double w, String c, int s, int e){
        super(v, w, c);
        start = s;
        end = e;
    }

    @Override
    public String createIdxStatement() {
        String filter = "(" + columnNames + " > " + start + " AND " + columnNames + " < " + end + ");'";
        return "'CREATE INDEX ON TestTable("+ columnNames +") where " + filter; // ToDo: what table-name to use?
    }

    @Override
    public String createIdxStatementWithId(int id) {
        String filter = "(" + columnNames + " > " + start + " AND " + columnNames + " < " + end + ");";
        String res =  "CREATE INDEX idx_" + id + " ON TestTable("+ columnNames +") where " + filter; // ToDo: what table-name to use?
        return res;
    }

    public String getPredicate() {
        return "(" + columnNames + " > " + start + " AND " + columnNames + " < " + end + ")";
    }
}
