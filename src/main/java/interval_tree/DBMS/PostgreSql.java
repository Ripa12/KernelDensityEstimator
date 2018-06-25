package interval_tree.DBMS;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.Factory.TableBaseProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
//        c = DriverManager
//                .getConnection("jdbc:postgresql://localhost:5432/testDB?currentSchema=mgd",
//                        "postgres", "postgres");

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

        public void estimateWeights(List<? extends IIndex> items, TableBaseProperties tp) throws SQLException {
            System.out.println("-- Estimate Indexes --");

//            outputResult(stmt.executeQuery("SELECT * FROM \"UCI_CBM\""));

            for(IIndex idx : items) {
                String sql = "SELECT * from hypopg_create_index(" + idx.createIdxStatement(tp) +");";

                ResultSet rs = stmt.executeQuery(sql);

                rs.next();
                String idx_name = rs.getString(1);
                rs = stmt.executeQuery("SELECT pg_size_pretty(hypopg_relation_size(" + idx_name + ")) FROM hypopg()");

                rs.next();
                String[] arr = rs.getString(1).split(" ");
                int weight = Integer.valueOf(arr[0]);

                if(arr[1].equals("MB")){
//                    weight *= 1024 * 1024;
                    weight *= 1024;
                }
                else if(arr[1].equals("kB")){
//                    weight *= 1024;
                }
                else if(arr[1].equals("bytes")){
                    weight /= 1024;
                }
                idx.setWeight(weight);

                System.out.println("Weight: " + weight + "\t unit: kB");
            }

            reset();
        }

        public void buildCandidateIndexes(List<? extends IIndex> items, TableBaseProperties tp) throws SQLException {

            System.out.println("-- Build Indexes --");
            for(IIndex idx : items) {
                indexIDs++;
                stmt.execute(idx.createIdxStatementWithId(indexIDs, tp));
                System.out.println(idx.createIdxStatementWithId(indexIDs, tp));
            }

        }

        public void testIndexes(String sourcePath) throws SQLException {
            System.out.println("-- Test Indexes --");

            long queryStartTime = System.nanoTime();

            try(BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {

                int c = 0;
                for (String line; (line = br.readLine()) != null; ) {
                    stmt.executeQuery(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
