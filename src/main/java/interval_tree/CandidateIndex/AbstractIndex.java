package interval_tree.CandidateIndex;

public abstract class AbstractIndex implements IIndex{

    private double value;
    private double weight;

    String tableName;
    String columnNames;

    public AbstractIndex(double v, double w, String t, String c){
        this.value = v;
        this.weight = w;
        this.tableName = t;
        this.columnNames = c;
    }

    public final boolean isAPrefix(String other) {
        return other.startsWith(columnNames);
    }

    @Override
    public String getColumnName(){
        return this.columnNames;
    }

    @Override
    public double getValue(){
        return value;
    }

    @Override
    public double getWeight(){
        return weight;
    }

    @Override
    public void setWeight(double w){
        this.weight = w;
    }
}
