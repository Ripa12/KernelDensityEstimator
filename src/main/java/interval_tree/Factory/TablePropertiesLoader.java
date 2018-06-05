package interval_tree.Factory;

import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class TablePropertiesLoader {

     void loadTableProperty(String fname) throws IOException, ParseException {
        String sourcePath = "data/testdata/unittests/" + fname;
        if (SystemUtils.IS_OS_WINDOWS) {
            sourcePath = sourcePath.replaceFirst("/", "//");
        }

        Object obj = new JSONParser().parse(new FileReader(sourcePath));
        JSONObject jo = (JSONObject) obj;

        // getting firstName and lastName
        String table_name = (String) jo.get("TABLE_NAME");
        String column_labels = (String) jo.get("lastName");

//        System.out.println(firstName);
//        System.out.println(lastName);

        // getting age
        long age = (long) jo.get("age");
        System.out.println(age);

        // getting address
        Map address = ((Map)jo.get("address"));

        // iterating address Map
        Iterator<Map.Entry> itr1 = address.entrySet().iterator();
        while (itr1.hasNext()) {
            Map.Entry pair = itr1.next();
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }

        // getting phoneNumbers
        JSONArray ja = (JSONArray) jo.get("phoneNumbers");

        // iterating phoneNumbers
        Iterator itr2 = ja.iterator();

        while (itr2.hasNext())
        {
            itr1 = ((Map) itr2.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                System.out.println(pair.getKey() + " : " + pair.getValue());
            }
        }
    }
}
