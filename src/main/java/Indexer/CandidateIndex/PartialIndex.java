package Indexer.CandidateIndex;

import Indexer.Factory.TableBaseProperties;
import Indexer.SubspaceClustering.MyData;
import Indexer.SubspaceClustering.MyInterval;

public class PartialIndex extends AbstractIndex {

    public enum ConditionType{
        INTERVAL,
        LESS_THAN,
        GREATER_THAN,
        POINT
    }

    private double start, end;
    private ConditionType conditionType;

    public PartialIndex(double v, double w, String t, String c, double s, double e){
        super(v, w, t, c);
        start = s;
        end = e;

        conditionType = ConditionType.INTERVAL;
    }

    public PartialIndex(double v, double w, String t, String c, double s, ConditionType conditionType){
        super(v, w, t, c);
        start = s;
        end = s;

        this.conditionType = conditionType;
    }

    public PartialIndex(PartialIndex other){
        super(other.getValue(), other.getWeight(), other.tableName, other.columnNames);
        start = other.start;
        end = other.end;

        this.conditionType = other.conditionType;
    }

    @Override
    public String createIdxStatement(TableBaseProperties tp) {

        Number start = tp.getCorrectType(tableName, columnNames, this.start);
        Number end = tp.getCorrectType(tableName, columnNames, this.end);

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
    public String createIdxStatementWithId(int id, TableBaseProperties tp) {

        Number start = tp.getCorrectType(tableName, columnNames, this.start);
        Number end = tp.getCorrectType(tableName, columnNames, this.end);

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

    String getPredicate(TableBaseProperties tp) {

        Number start = tp.getCorrectType(tableName, columnNames, this.start);
        Number end = tp.getCorrectType(tableName, columnNames, this.end);

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
    //[1 8] x
    //[2 4] y

    //[2 4] x
    //[1 8] y


    boolean isOverlapping(PartialIndex other){
        // x1 <= y2 && y1 <= x2
        return other.start <= end && start <= other.end;
    }

    void merge(PartialIndex other){
        this.setValue(getValue() + other.getValue());
        this.start = Math.min(other.start, start);
        this.end = Math.max(other.end, end);
    }

    double getStart(){
        return start;
    }

    double getEnd(){
        return end;
    }

    MyData getInterval(){
        return new MyInterval(start, end);
    }
}
