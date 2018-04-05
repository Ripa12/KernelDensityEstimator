package interval_tree.CandidateIndex;

public class FullIndex extends AbstractIndex {

    public FullIndex(double v, double w, String c){
        super(v, w, c);
    }

    @Override
    public String createIdxStatement() {

        return "'CREATE INDEX ON TestTable("+ columnNames +");'"; // ToDo: what table-name to use?
    }

    @Override
    public String createIdxStatementWithId(int id) {
        String res =  "CREATE INDEX idx_" + id + " ON TestTable("+ columnNames +");"; // ToDo: what table-name to use?
        return res;
    }
}
