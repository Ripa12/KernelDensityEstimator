package Indexer.DBMS;

import Indexer.CandidateIndex.IIndex;
import Indexer.Factory.TableBaseProperties;
import Indexer.Logger.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static Indexer.Utility.getOutputDirectory;

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
//        for(;indexIDs > -1; indexIDs--){
//            stmt.execute("drop INDEX idx_" + indexIDs);
//        }

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

//                System.out.println("Weight: " + weight + "\t unit: kB");// + " | " + idx.getColumnName() + " : " + idx_name);
            }

            dropHypotheticalIndexes();
        }

        public void checkUtility(List<? extends IIndex> items, String sourcePath, double min_sup, TableBaseProperties tp) throws SQLException {
            System.out.println("-- Check Utility of Indexes --");

//            HashMap<IIndex, String> id = new HashMap<>();
            HashMap<String, IIndex> id = new HashMap<>();

            for(IIndex idx : items) {

                idx.resetValue();

                String sql = "SELECT * from hypopg_create_index(" + idx.createIdxStatement(tp) +");";

                ResultSet rs = stmt.executeQuery(sql);

                rs.next();
                String idx_name = rs.getString(1);

                id.put(idx_name, idx);

            }


            int totalQueries = 0;
            try(BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {
                for (String line; (line = br.readLine()) != null; ) {
                    totalQueries += 1;
                    checkUtility(line, items, id, tp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            checkUtility(items, id, tp);

            double finalTotalQueries = ((double) totalQueries);
            items.removeIf((Predicate<IIndex>) iIndex -> iIndex.getValue() / finalTotalQueries < min_sup);

            dropHypotheticalIndexes();
        }

        private void checkUtility(String line, List<? extends IIndex> items, Map<String , IIndex> id, TableBaseProperties tp) throws SQLException {
//            for (int i = items.size() - 1; i >= 0; i--) {

//                String sql = "EXPLAIN " + items.get(i).createSelectStatement(tp);
                String sql = "EXPLAIN " + line;


                ResultSet rs = stmt.executeQuery(sql);

//                Pattern regex = Pattern.compile("\\(<.*?>\\)");


                boolean isUsed = false;
                while (rs.next() && !isUsed){
                    String temp = rs.getString(1);
//                    Matcher regexMatcher = regex.matcher(temp);
//                    if(regexMatcher.find()) {
                        String idString = StringUtils.substringBetween(temp, "<", ">");

//                    if(temp.contains("<" + id.get(items.get(i)) + ">")){
                        if (id.containsKey(idString)) {
                            isUsed = true;
                            id.get(idString).incValue();
                        }
//                    }
                }

                if(!isUsed){
                    //Debug
//                    System.out.println(items.get(i).createIdxStatement(tp) + " is not used!");
//
//                    items.remove(i);
                }
//            }
        }

        public void buildCandidateIndexes(List<? extends IIndex> items, TableBaseProperties tp) throws SQLException {

            System.out.println("-- Build Indexes --");
            for(IIndex idx : items) {
                indexIDs++;
                stmt.execute(idx.createIdxStatementWithId(indexIDs, tp));
                System.out.println(idx.createIdxStatementWithId(indexIDs, tp));
            }

        }

        public void testIndexes(String filename) throws SQLException {
            System.out.println("-- Test Indexes --");

            Logger.getInstance().setTimer("QueryBatchTime");

            try(BufferedReader br = new BufferedReader(new FileReader(getOutputDirectory() + filename))) {

//                int c = 0;
                for (String line; (line = br.readLine()) != null; ) {
//                    outputResult(stmt.executeQuery("explain analyze " + line));
                    stmt.executeQuery(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Logger.getInstance().stopTimer("QueryBatchTime");
        }


        public void dropAllIndexes(TableBaseProperties tp){
            for (String s : tp.getTableNames()) {

                String stat = "DO\n" +
                        "$$BEGIN\n" +
                        "   EXECUTE (\n" +
                        "   SELECT 'DROP INDEX ' || string_agg(indexrelid::regclass::text, ', ')\n" +
                        "   FROM   pg_index  i\n" +
                        "   LEFT   JOIN pg_depend d ON d.objid = i.indexrelid\n" +
                        "                          AND d.deptype = 'i'\n" +
                        "   WHERE  i.indrelid = '\"" + s + "\"'::regclass  -- possibly schema-qualified\n" +
                        "   AND    d.objid IS NULL                                -- no internal dependency\n" +
                        "   );\n" +
                        "END$$;";

                try {
                    stmt.execute(stat); // ToDo: Sometimes throws an error when no current indexes to delete exists (though there might be another reason)
                } catch (SQLException e) {
//                    e.printStackTrace(); // ToDo: Ignoring exception for the moment!!!
                }
            }
        }


        private void dropHypotheticalIndexes() throws SQLException {
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
