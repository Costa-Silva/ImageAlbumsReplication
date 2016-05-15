package sd.tp1.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.tp1.utils.Clock;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Created by Antonio on 14/05/16.
 */
public class ReplicationServerUtils {
    public static final String REPLICAID= "replicaId";
    public static final String TOTALSERVERS= "totalServers";
    public static final String TIMESTAMP= "timestamps";
    public static final String FILENAME= "metadata.txt";
    public static final String DATA= "data";
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final int NONEXISTENCE = -1;

    public static void main(String[] args) throws Exception {
        JSONObject file = createFile();
        timestampRemove(file,NONEXISTENCE);
        timestampADD(file,1,new Clock(200,5));
        replicaIdSet(file,0);
        replaceServers(file,2);

        //timestampADD(file,4834);

        //Object obj = parser.parse(new FileReader("c:\\test.json")); JSONObject jsonObject = (JSONObject) obj;
        writeToFile(file);
    }

    private static void timestampRemove(JSONObject file,int id){
        ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).remove(id);
    }

    private static void timestampADD(JSONObject file,int id,Clock clock){

        LinkedHashMap<Object,Object> jSONconstructorTimestamp  = new LinkedHashMap<>();
        jSONconstructorTimestamp.put(CLOCK,clock.getClock());
        jSONconstructorTimestamp.put(REPLICA,clock.getReplica());
        jSONconstructorTimestamp.put(SHAREDBY,new JSONArray());
        JSONObject contentTimestamp = new JSONObject(new JSONObject(jSONconstructorTimestamp));
        ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).put(id,contentTimestamp);


        //(JSONObject) ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).put(id,)

    }

    private static JSONObject timestampgetJSONbyID(JSONObject file,int id){
        return (JSONObject) ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).get(id);
    }

    private static void replicaIdSet(JSONObject file,int newId){
        ((JSONObject)file.get(DATA)).put(REPLICAID,newId);
    }

    private static void timestampChangeClock(JSONObject file,int id,Clock clock){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        jsonObject.put(CLOCK,clock.getClock());
        jsonObject.put(REPLICA,clock.getReplica());
    }

    private static Clock timestampGetClock(JSONObject file,int id){
        JSONObject jsonObject= timestampgetJSONbyID(file, id);
        return new Clock((int)jsonObject.get(CLOCK),(int)jsonObject.get(REPLICA));
    }


    private static void timestampAddSharedBy(JSONObject file,int id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        ((JSONArray)jsonObject.get(SHAREDBY)).add(sharedBy);
    }

    private static JSONArray timestampGetSharedBy(JSONObject file,int id){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return (JSONArray) jsonObject.get(SHAREDBY);
    }

    private static boolean timestampRemoveSharedBy(JSONObject file,int id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return ((JSONArray)jsonObject.get(SHAREDBY)).remove(sharedBy);
    }

    private static void replaceServers(JSONObject file, int serverTotals){
        ((JSONObject)file.get(DATA)).put(TOTALSERVERS,serverTotals);
    }

    private static int getServers(JSONObject file){
        return (int) ((JSONObject)file.get(DATA)).get(TOTALSERVERS);
    }

    private static JSONObject createFile(){

        JSONObject file = new JSONObject();


        LinkedHashMap<Object,Object> jSONconstructorFile  = new LinkedHashMap<>();
        LinkedHashMap<Object,Object> timeStamp = new LinkedHashMap<>();

        jSONconstructorFile.put(REPLICAID,NONEXISTENCE);
        jSONconstructorFile.put(TOTALSERVERS,NONEXISTENCE);
        jSONconstructorFile.put(TIMESTAMP,new JSONObject(timeStamp));


        JSONObject data = new JSONObject(new JSONObject(jSONconstructorFile));
        file.put(DATA,data);

        timestampADD(file,NONEXISTENCE,new Clock(NONEXISTENCE,NONEXISTENCE));

        return file;
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
