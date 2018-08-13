package Indexer.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;

public interface  MyData {
    double getLow();
    double getHigh();

    boolean isContained(CLIQUEInterval interval);
    boolean isContained(double min, double max);
    boolean contains(double min, double max);
}
