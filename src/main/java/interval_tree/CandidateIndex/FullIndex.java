package interval_tree.CandidateIndex;

public class FullIndex extends AbstractIndex {

    public FullIndex(double v, double w, String t, String c){
        super(v, w, t, c);
    }

    @Override
    public String createIdxStatement() {

        return "'CREATE INDEX ON "+ tableName +"("+ columnNames +");'"; // ToDo: what table-name to use?
    }

    @Override
    public String createIdxStatementWithId(int id) {
        String res =  "CREATE INDEX idx_" + id + " ON "+ tableName +"("+ columnNames +");"; // ToDo: what table-name to use?
        return res;
    }
}
