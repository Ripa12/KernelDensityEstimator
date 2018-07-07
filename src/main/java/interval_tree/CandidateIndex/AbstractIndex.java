package interval_tree.CandidateIndex;

import interval_tree.Factory.TableBaseProperties;

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

//    public final boolean isAPrefix(String other) {
//        return other.startsWith(columnNames);
//    }

    @Override
    public String createSelectStatement(TableBaseProperties tp){

        String[] columns = columnNames.split(",");

        StringBuilder builder = new StringBuilder();

        builder.append(columns[0] + " = " + tp.getCorrectType(tableName, columns[0], 0));
        for (int i = 1; i < columns.length; i++) {
            builder.append(" AND ");
            builder.append(columns[i] + " = " + tp.getCorrectType(tableName, columns[0], 0));
        }
        builder.append(";");


        return "SELECT * FROM " + tableName + " WHERE " + builder.toString();
    }

    @Override
    public String getColumnName(){
        return this.columnNames;
    }

    @Override
    public double getValue(){
        return value;
    }
    void setValue(double v){
        this.value = v;
    }

    @Override
    public double getWeight(){
        return weight;
    }

    @Override
    public void setWeight(double w){
        this.weight = w;
    }

    @Override
    public void resetValue(){
        value = 0;
    }

    @Override
    public void incValue(){
        value += 1;
    }
}
