package interval_tree;

public class PartialIndex extends CandidateIndex{

    private int start, end; // ToDo: maybe put Interval or Point from MyIntervalTree here

    public PartialIndex(double v, double w, String c, int s, int e){
        super(v, w, c);
        start = s;
        end = e;
    }

    @Override
    public String createIdxStatement() {
        String filter = "(" + columnNames + " >" + start + " AND " + columnNames + " < " + end + ");'";
        return "'CREATE INDEX ON TestTable("+ columnNames +") where " + filter; // ToDo: what table-name to use?
    }
}
