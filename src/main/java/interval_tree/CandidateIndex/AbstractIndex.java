package interval_tree.CandidateIndex;

public abstract class AbstractIndex {

    private double value;
    private double weight;

    String columnNames;

    public AbstractIndex(double v, double w, String c){
        this.value = v;
        this.weight = w;
        this.columnNames = c;
    }

    public double getValue(){
        return value;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double w){
        this.weight = w;
    }

    public abstract String createIdxStatement();
    public abstract String createIdxStatementWithId(int id);
}
