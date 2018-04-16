package interval_tree.CandidateIndex;

import java.util.ArrayList;
import java.util.List;

public class CompoundPartialIndex implements IIndex {

    public static class Predicate {
        private List<PartialIndex> indexList;

        public Predicate(){
            indexList = new ArrayList<>();
        }

        public void addPartialIndex(PartialIndex p){
            indexList.add(p);
        }

        double getValue(){
            assert indexList.size() > 0;

            double f = 0.0;
            for (int i = 0; i < indexList.size(); i++) {
                f += indexList.get(i).getValue();
            }

            return f;
        }

        String getOrExpression(){
            assert indexList.size() > 0;

            StringBuilder f = new StringBuilder("(");
            f.append(indexList.get(0).getPredicate());
            for (int i = 1; i < indexList.size(); i++) {
                f.append(" OR ");
                f.append(indexList.get(i).getPredicate());
            }
            f.append(")");

            return f.toString();
        }

        String getColumnName(){
            assert indexList.size() > 0;

            return indexList.get(0).getColumnName();
        }

        public boolean isEmpty(){
            return indexList.isEmpty();
        }
    }


    private List<Predicate> predicateList;
    private double totalValue;
    private double totalWeight;

    public CompoundPartialIndex(){
        predicateList = new ArrayList<>();
        totalWeight = 0.0f;
        totalValue = 0.0f;
    }

    public void addCompoundPredicate(Predicate p){
        predicateList.add(p);
        totalValue += p.getValue();
    }

    public boolean isEmpty(){
        return predicateList.isEmpty();
    }

    @Override
    public String createIdxStatement() {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        f.append(predicateList.get(0).getOrExpression());
        for (int i = 1; i < predicateList.size(); i++) {
            f.append(" AND ");
            f.append(predicateList.get(i).getOrExpression());
        }
        f.append(");'");

        StringBuilder c = new StringBuilder(predicateList.get(0).getColumnName());
        for (int i = 1; i < predicateList.size(); i++) {
            c.append(", ").append(predicateList.get(i).getColumnName());
        }

        return "'CREATE INDEX ON TestTable("+ c.toString() +") where " + f.toString(); // ToDo: what table-name to use?;
    }

    @Override
    public String createIdxStatementWithId(int id) {
        assert predicateList.size() > 0;

        StringBuilder f = new StringBuilder("(");
        f.append(predicateList.get(0).getOrExpression());
        for (int i = 1; i < predicateList.size(); i++) {
            f.append(" AND ");
            f.append(predicateList.get(i).getOrExpression());
        }
        f.append(");");

        StringBuilder c = new StringBuilder(predicateList.get(0).getColumnName());
        for (int i = 1; i < predicateList.size(); i++) {
            c.append(", ").append(predicateList.get(i).getColumnName());
        }

        return  "CREATE INDEX idx_" + id + " ON TestTable("+ c.toString() +") where " + f.toString(); // ToDo: what table-name to use?
    }

    @Override
    public String getColumnName() {
        StringBuilder c = new StringBuilder(predicateList.get(0).getColumnName());
        for (int i = 1; i < predicateList.size(); i++) {
            c.append(", ").append(predicateList.get(i).getColumnName());
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
