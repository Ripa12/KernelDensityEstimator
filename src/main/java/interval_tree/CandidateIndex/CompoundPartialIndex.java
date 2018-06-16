package interval_tree.CandidateIndex;

import interval_tree.FrequentPatternMining.SupportCount.TableProperties;

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
    public String createIdxStatement(TableProperties tp) {
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
    public String createIdxStatementWithId(int id, TableProperties tp) {
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

    private PartialIndex getPredicate(String name){
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
}
