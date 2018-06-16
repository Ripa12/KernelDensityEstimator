package interval_tree.CandidateIndex;

import interval_tree.FrequentPatternMining.SupportCount.TableProperties;

public interface IIndex {
    String createIdxStatement(TableProperties tp);
    String createIdxStatementWithId(int id, TableProperties tp);

    String getColumnName();
    double getValue();
    double getWeight();
    void setWeight(double w);

    default boolean isAPrefix(String other) {
        return other.startsWith(getColumnName());
    }

//    boolean isAPrefix(String other);
}
