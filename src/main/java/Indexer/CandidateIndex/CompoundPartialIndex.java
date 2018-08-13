package Indexer.CandidateIndex;

import Indexer.Factory.TableBaseProperties;
import Indexer.SubspaceClustering.MyData;
import Indexer.SubspaceClustering.MyVector;

import java.util.*;

public class CompoundPartialIndex implements IIndex {

    private HashMap<String, PartialIndex> predicateList;
    private double totalValue;
    private double totalWeight;
    private String tableName;

    public CompoundPartialIndex(String t){
        predicateList = new HashMap<>();
        totalWeight = 0.0f;
        totalValue = 0.0f;
        tableName = t;
    }

    public void addCompoundPredicate(PartialIndex p){
        predicateList.put(p.getColumnName(),p);
        totalValue = p.getValue();
    }

    public boolean isEmpty(){
        return predicateList.isEmpty();
    }

    @Override
    public String createIdxStatement(TableBaseProperties tp) {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            f.append(" AND ");
            f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        }
        f.append(");'");

        return "'CREATE INDEX ON "+tableName+"("+ getColumnName() +") where " + f.toString(); // ToDo: what table-name to use?;
    }

    @Override
    public String createIdxStatementWithId(int id, TableBaseProperties tp) {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            f.append(" AND ");
            f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        }
        f.append(");");

        return  "CREATE INDEX idx_" + id + " ON "+tableName+"("+ getColumnName() +") where " + f.toString(); // ToDo: what table-name to use?
    }

    @Override
    public String createSelectStatement(TableBaseProperties tp) {

        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            f.append(" AND ");
            f.append(((PartialIndex)pair.getValue()).getPredicate(tp));
        }
        f.append(");");

        return  "SELECT * FROM " + tableName + " WHERE " + f.toString();
    }

    public PartialIndex getPredicate(String name){
        return predicateList.get(name);
    }

    public PartialIndex getPredicateClone(String name){
        return new PartialIndex(predicateList.get(name));
    }

    @Override
    public String getColumnName() {
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        StringBuilder c = new StringBuilder(((PartialIndex)pair.getValue()).getColumnName());
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            c.append(", ").append(((PartialIndex)pair.getValue()).getColumnName());
        }
        return c.toString();
    }

    @Override
    public double getValue(){
        return this.totalValue;
    }

    @Override
    public double getWeight(){
        return this.totalWeight;
    }

    @Override
    public void setWeight(double w){
        this.totalWeight = w;
    }

    @Override
    public void resetValue() {
        totalValue = 0;
    }

    @Override
    public void incValue() {
        totalValue += 1;
    }

    public int isLeftOf(CompoundPartialIndex other, String col){
        return Double.compare(predicateList.get(col).getStart(), other.predicateList.get(col).getStart());
    }


    public boolean merge(CompoundPartialIndex other){
        // ToDo: Assert that other is similar to this
        for (String s : predicateList.keySet()) {
            if(!getPredicate(s).isOverlapping(other.getPredicate(s)))
                return false;
        }

        totalValue += other.getValue();
        for (String s : predicateList.keySet()) {
            getPredicate(s).merge(other.getPredicate(s));
        }

        return true;
    }

    public static List<List<IIndex>> merge(List<IIndex> intervals, String column) {
        if(intervals.size() < 2)
            return Collections.singletonList(intervals);


        intervals.sort((o1, o2) -> ((CompoundPartialIndex) o1).isLeftOf(((CompoundPartialIndex) o2), column));


        CompoundPartialIndex first = ((CompoundPartialIndex) intervals.get(0));
        double end = first.predicateList.get(column).getEnd();
        LinkedList<List<IIndex>> result = new LinkedList<>();
        LinkedList<IIndex> temp = new LinkedList<>();
        temp.add(first);
        for(int i = 1; i < intervals.size(); i++){
            CompoundPartialIndex current = ((CompoundPartialIndex) intervals.get(i));
            if(current.predicateList.get(column).getStart() <= end){
                end = Math.max(current.predicateList.get(column).getEnd(), end);
                temp.add(current);
            }else{
                result.add(temp);
                temp = new LinkedList<>();

                end = current.predicateList.get(column).getEnd();
            }
        }
        result.add(temp);
        return result;
    }

    public MyVector getVector(List<String> order){
        MyData[] data = new MyData[order.size()];

        for (int i = 0; i < order.size(); i++) {
            data[i] = predicateList.get(order.get(i)).getInterval();
        }

        return new MyVector(data);
    }
}
