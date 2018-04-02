package interval_tree;

public abstract class CandidateIndex {

    private double value;
    private double weight;

    String columnNames;

    public CandidateIndex(double v, double w, String c){
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
}
