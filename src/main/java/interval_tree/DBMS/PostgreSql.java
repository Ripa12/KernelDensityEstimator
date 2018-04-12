package interval_tree.DBMS;

import interval_tree.CandidateIndex.AbstractIndex;

import java.sql.*;
import java.util.List;

public class PostgreSql {

    //ToDo: Start server
    // pg_ctl -D /usr/local/var/postgres start

    //ToDo: Restore database
    // http://www.postgresqltutorial.com/load-postgresql-sample-database/

    //ToDo: Download database
    // http://www.postgresqltutorial.com/postgresql-sample-database/

    boolean hasBeenClosed;
    int indexIDs;

    Connection c;
    Statement stmt;
    public PostgreSql() throws SQLException, ClassNotFoundException {
        indexIDs = -1;

        Class.forName("org.postgresql.Driver");
        c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB?currentSchema=mgd",
                            "ripa12", "password");

        stmt = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

        hasBeenClosed = false;
        System.out.println( "Initialize PostGreSql" );
    }

    public void close() throws SQLException{
        for(;indexIDs > -1; indexIDs--){
            stmt.execute("drop INDEX idx_" + indexIDs);
        }

        stmt.close();
        c.close();
        hasBeenClosed = true;
    }

    @Override
    protected void finalize() throws Throwable {
        if(!hasBeenClosed) {
            System.err.println("PostGreSql fail to close properly!");
            close();
        }

        super.finalize();
    }

        public void estimateWeights(List<AbstractIndex> items) throws SQLException {
            System.out.println("-- Estimate Indexes --");

            for(AbstractIndex idx : items) {
                String sql = "SELECT * from hypopg_create_index(" + idx.createIdxStatement() +");";

                ResultSet rs = stmt.executeQuery(sql);

                rs.next();
                String idx_name = rs.getString(1);
                rs = stmt.executeQuery("SELECT pg_size_pretty(hypopg_relation_size(" + idx_name + ")) FROM hypopg()");

                rs.next();
                String[] arr = rs.getString(1).split(" ");
                int weight = Integer.valueOf(arr[0]);

                if(arr[1].equals("MB")){
                    weight *= 1024 * 1024;
                }
                else if(arr[1].equals("kB")){
                    weight *= 1024;
                }

                idx.setWeight(weight);

                System.out.println("Weight: " + weight + "\t unit: Bytes");
            }

            reset();
        }

        public void buildCandidateIndexes(List<AbstractIndex> items) throws SQLException {

            System.out.println("-- Build Indexes --");
            for(AbstractIndex idx : items) {
                indexIDs++;
                stmt.execute(idx.createIdxStatementWithId(indexIDs));
                System.out.println(idx.createIdxStatementWithId(indexIDs));
            }

        }

        public void testIndexes(String queries) throws SQLException {
            System.out.println("-- Test Indexes --");

            long queryStartTime = System.nanoTime();

            String[] arr = queries.split(";");
            for(int c = 0; c < arr.length - 1; c++){
                stmt.executeQuery(arr[c]);
                //System.out.println("Current query: " + c);
            }

            long queryEndTime = System.nanoTime() - queryStartTime;

            System.out.println("Total query time: " + queryEndTime/1000000000.0);
        }

        public void reset() throws SQLException {
            stmt.executeQuery("SELECT hypopg_reset()");
        }

        private static void outputResult(ResultSet rs) throws SQLException {
            System.out.println("---------");

            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("querying SELECT * FROM XXX");
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
            System.out.println("---------");
        }
}
