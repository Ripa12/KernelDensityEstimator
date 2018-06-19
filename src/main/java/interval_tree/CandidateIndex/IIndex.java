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

    default boolean containsPrefix(IIndex other) {
        return other.getColumnName().contains(getColumnName());
    }

//    boolean isAPrefix(String other);
}
