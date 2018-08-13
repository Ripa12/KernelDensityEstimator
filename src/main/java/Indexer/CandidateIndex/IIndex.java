package Indexer.CandidateIndex;

import Indexer.Factory.TableBaseProperties;

public interface IIndex {
    String createIdxStatement(TableBaseProperties tp);
    String createIdxStatementWithId(int id, TableBaseProperties tp);

    String createSelectStatement(TableBaseProperties tp);

    String getColumnName();
    double getValue();
    double getWeight();
    void setWeight(double w);
    void resetValue();
    void incValue();

    default boolean isAPrefix(String other) {
        return other.startsWith(getColumnName());
    }

    default boolean containsPrefix(IIndex other) {
        return other.getColumnName().contains(getColumnName());
    }

//    boolean isAPrefix(String other);
}
