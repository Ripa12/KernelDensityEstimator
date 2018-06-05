package interval_tree.CandidateIndex;

import java.util.*;

public class CompoundPartialIndex implements IIndex {

    // ToDo: Really no need for this class. Partial Indexes are enough in CompositeClass.
//    public static class Predicate {
//        private List<PartialIndex> indexList;
//
//        public Predicate(){
//            indexList = new ArrayList<>();
//        }
//
//        public void addPartialIndex(PartialIndex p){
//            indexList.add(p);
//        }
//
//        double getValue(){
//            return indexList.get(0).getValue();
//        }
//
//        String getOrExpression(){
//            assert indexList.size() > 0;
//
//            StringBuilder f = new StringBuilder("(");
//            f.append(indexList.get(0).getPredicate());
//            for (int i = 1; i < indexList.size(); i++) {
//                f.append(" OR ");
//                f.append(indexList.get(i).getPredicate());
//            }
//            f.append(")");
//
//            return f.toString();
//        }
//
//        String getColumnName(){
//            assert indexList.size() > 0;
//
//            return indexList.get(0).getColumnName();
//        }
//
//        public boolean isEmpty(){
//            return indexList.isEmpty();
//        }
//    }


//    private List<Predicate> predicateList;
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
    public String createIdxStatement() {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        f.append(((PartialIndex)pair.getValue()).getPredicate());
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            f.append(" AND ");
            f.append(((PartialIndex)pair.getValue()).getPredicate());
        }
        f.append(");");

        return "'CREATE INDEX ON "+tableName+"("+ getColumnName() +") where " + f.toString(); // ToDo: what table-name to use?;
    }

    @Override
    public String createIdxStatementWithId(int id) {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        Iterator it = predicateList.entrySet().iterator();
        Map.Entry pair = (Map.Entry)it.next();

        f.append(((PartialIndex)pair.getValue()).getPredicate());
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();

            f.append(" AND ");
            f.append(((PartialIndex)pair.getValue()).getPredicate());
        }
        f.append(");");

        return  "CREATE INDEX idx_" + id + " ON "+tableName+"("+ getColumnName() +") where " + f.toString(); // ToDo: what table-name to use?
    }

    public PartialIndex getPredicate(String name){
        return predicateList.get(name);
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
}
