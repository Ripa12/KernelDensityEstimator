package Indexer.SubspaceClustering;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.VectorFieldTypeInformation;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.AbstractRelation;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.logging.Logging;

import java.util.ArrayList;
import java.util.List;

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

    public int getDimensionality(){
        return list.get(0).getDimensionality();
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
