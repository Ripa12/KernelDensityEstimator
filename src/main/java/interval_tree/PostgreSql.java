package interval_tree;

import java.sql.*;
import java.util.List;

public class PostgreSql {

    //ToDo: Start server
    // pg_ctl -D /usr/local/var/postgres start

    //ToDo: Restore database
    // http://www.postgresqltutorial.com/load-postgresql-sample-database/

    //ToDo: Download database
    // http://www.postgresqltutorial.com/postgresql-sample-database/

    Connection c = null;
    Statement stmt = null;
    public PostgreSql(){
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/testDB?currentSchema=mgd",
                            "ripa12", "password");

            stmt = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            stmt.close();
            c.close();
            System.out.println( "Hello World!" );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

        public void estimateWeights(List<CandidateIndex> items) throws SQLException {

            for(CandidateIndex idx : items) {
                String sql = "SELECT * from hypopg_create_index(" + idx.createIdxStatement() +");";

                ResultSet rs = stmt.executeQuery(sql);
                //outputResult(rs);

                rs.beforeFirst();
                rs.next();
                rs = stmt.executeQuery("SELECT pg_size_pretty(hypopg_relation_size(" + rs.getString(1) + ")) FROM hypopg()");

                rs.next();
                int weight = Integer.valueOf(rs.getString(0));

                String unit = rs.getString(1);

                if(unit.equals("mB")){
                    weight *= 1024;
                }

                idx.setWeight(weight);

                System.out.println("Weight: " + weight + "\t unit: " + unit);
            }

            reset();
        }

        public void reset() throws SQLException {
            stmt.executeQuery("SELECT hypopg_reset()");
        }

//        private static void testPartialIndex(Statement stmt) throws SQLException {
//            //stmt.execute("DROP EXTENSION hypopg;");
//            //stmt.execute("CREATE EXTENSION hypopg SCHEMA mgd;");
//
//            String indexName = "testIdx";
//
//
//            String filter = "(B > 1000 AND B < 5000);";
//            String tableName = "TestTable";
//            String partialIndex2 = tableName+"(B) where " + filter;
//            String partialIndex1 = "'CREATE INDEX ON "+ partialIndex2 +"'";
//            String sql = "SELECT * from hypopg_create_index(" + partialIndex1 + ");";
//
//            ResultSet rs = stmt.executeQuery(sql);
//            outputResult(rs);
//
//            rs.beforeFirst(); rs.next();
//            outputResult(stmt.executeQuery("SELECT pg_size_pretty(hypopg_relation_size(" + rs.getString(1) + ")) FROM hypopg()"));
//            // 112 000 Partial idx (estimation)
//
////        stmt.execute("drop INDEX " + indexName);
////        stmt.executeQuery("SELECT hypopg_reset()");
//
//            //stmt.execute(partialIndex3);
//
//            //stmt.execute("drop INDEX " + indexName);
//            stmt.execute("CREATE INDEX " + indexName + " ON "+tableName+"(B)");
////        stmt.execute("CREATE INDEX " + indexName + " ON "+tableName+"(B) where " + filter);
//
//            outputResult(stmt.executeQuery("SELECT pg_size_pretty(pg_relation_size('"+indexName+"'))"));
////        pg_size_pretty(pg_relation_size('"+indexName+"')
//            // 106 KB Partial idx
//            // 475 KB Full idx
//
//
//            String query = "SELECT * from "+tableName+" where (B = 1500);";
////        String query = "SELECT * from "+tableName;
//            //outputResult(stmt.executeQuery(query));
//            outputResult(stmt.executeQuery("explain " + query));
//            outputResult(stmt.executeQuery("explain analyze " + query));
//
//
//            stmt.execute("drop INDEX " + indexName);
//            stmt.executeQuery("SELECT hypopg_reset()");
//        }
//    ---------


//        private static void outputResult(ResultSet rs) throws SQLException {
//            System.out.println("---------");
//
//            ResultSetMetaData rsmd = rs.getMetaData();
//            System.out.println("querying SELECT * FROM XXX");
//            int columnsNumber = rsmd.getColumnCount();
//            while (rs.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) System.out.print(",  ");
//                    String columnValue = rs.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }
//            System.out.println("---------");
//        }
}
