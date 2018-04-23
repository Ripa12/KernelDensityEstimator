package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.data.type.VectorFieldTypeInformation;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.knn.KNNQuery;
import de.lmu.ifi.dbs.elki.database.query.range.RangeQuery;
import de.lmu.ifi.dbs.elki.database.query.rknn.RKNNQuery;
import de.lmu.ifi.dbs.elki.database.query.similarity.SimilarityQuery;
import de.lmu.ifi.dbs.elki.database.relation.AbstractRelation;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.similarityfunction.SimilarityFunction;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.rstar.RStarTree;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.rstar.RStarTreeFactory;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.result.ResultHierarchy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2018-04-21.
 */
public class MyRelation<v extends NumberVector> extends AbstractRelation implements Relation {

    private static final Logging LOG = Logging.getLogger(MyRelation.class);

    private final SimpleTypeInformation<v> type;

    //private RTree tree;
    private List<v> list;

    public MyRelation(int dim){
        this.list = new ArrayList<>();
        type = VectorFieldTypeInformation.typeRequest(NumberVector.class, dim, dim);
    }

    public void insert(v vec){
        list.add(vec);
    }

    @Override
    protected Logging getLogger() {
        return LOG;
    }

    @Override
    public Object get(DBIDRef dbidRef) {
        return list.get(dbidRef.internalGetIndex());
    }

    @Override
    public SimpleTypeInformation getDataTypeInformation() {
        return type;
    }

    @Override
    public DBIDs getDBIDs() {
        return null;
    }

    @Override
    public DBIDIter iterDBIDs() {
        return new MyIter();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public String getLongName() {
        return "MyRelation";
    }

    @Override
    public String getShortName() {
        return "MyRelation";
    }

    protected class MyIter implements DBIDIter{

        private int pos;

        public MyIter(){
            pos = 0;
        }

        @Override
        public boolean valid() {
            return (pos >= 0) && (pos < list.size());
        }

        @Override
        public DBIDIter advance() {
            pos++;
            return this;
        }

        @Override
        public int internalGetIndex() {
            return pos;
        }
    }
}
