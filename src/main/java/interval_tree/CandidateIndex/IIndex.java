package interval_tree.CandidateIndex;

public interface IIndex {
    String createIdxStatement();
    String createIdxStatementWithId(int id);

    String getColumnName();
    double getValue();
    double getWeight();
    void setWeight(double w);

    default boolean isAPrefix(String other) {
        return other.startsWith(getColumnName());
    }

//    boolean isAPrefix(String other);
}
