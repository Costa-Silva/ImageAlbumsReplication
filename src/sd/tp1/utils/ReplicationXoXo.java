package sd.tp1.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Created by Antonio on 14/05/16.
 */
public class ReplicationXoXo {
    public static final String REPLICAID= "replica_id";
    public static final String TOTALSERVERS= "total_servers";
    public static final String TIMESTAMP= "timestamps";
    public static final String FILENAME= "metadata.txt";
    public static final String DATA= "data";
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final int NONEXISTENCE = -1;

    public static void main(String[] args) throws Exception {
        JSONObject file = new JSONObject();
        JSONObject data = new JSONObject();

        createFile(file,data);
        replaceServers(file,6);
        replaceOrAddTimestamp(file,9898,new Clock(8,26));
        replace(file,9898,new Clock(8,26));
    }

    private static void replaceOrAddTimestamp(JSONObject file,int id ,Clock clock){

        JSONObject data= new JSONObject();
        JSONArray sharedBy = new JSONArray();

        LinkedHashMap<Object,Object> jSONCONSTRUCTOR  = new LinkedHashMap<>();

        jSONCONSTRUCTOR.put(CLOCK,clock.getClock());
        jSONCONSTRUCTOR.put(REPLICA,clock.getReplica());
        sharedBy.add("INES+PAULO <3");
        jSONCONSTRUCTOR.put(SHAREDBY,sharedBy);
        JSONObject content = new JSONObject(new JSONObject(jSONCONSTRUCTOR));

        data.put(id,content);
        ((JSONObject)file.get(DATA)).put(TIMESTAMP,data);

        writeToFile(file);

    }


    private static void replace(JSONObject file,int id ,Clock clock){

        JSONObject json = (JSONObject) ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).get(id);

        json.put(CLOCK,clock.getClock());
        json.put(REPLICA,clock.getReplica());
        ((JSONArray)json.get(SHAREDBY)).add("tutut");
         writeToFile(file);

    }




    private static void replaceServers(JSONObject file, int serverTotals){

        ((JSONObject)file.get(DATA)).put(TOTALSERVERS,serverTotals);

        writeToFile(file);
    }

    private static int getServers(JSONObject file){
        return (int) ((JSONObject)file.get(DATA)).get(TOTALSERVERS);
    }

    private static void createFile(JSONObject file,JSONObject data){

        file.put(DATA,data);
        data.put(REPLICAID,NONEXISTENCE);
        data.put(TOTALSERVERS,NONEXISTENCE);
        data.put(TIMESTAMP,new JSONArray());
        writeToFile(file);
    }

    private static void writeToFile(JSONObject file){
        try {
            FileWriter fileWriter = new FileWriter(FILENAME);
            ObjectMapper mapper = new ObjectMapper();
            fileWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(file));
            fileWriter.flush();

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
