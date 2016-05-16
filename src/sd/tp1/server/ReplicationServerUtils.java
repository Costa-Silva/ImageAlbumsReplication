package sd.tp1.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.tp1.utils.Clock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Created by Antonio on 14/05/16.
 */
public class ReplicationServerUtils {
    public static final String REPLICAID= "replicaId";
    public static final String TIMESTAMP= "timestamps";
    public static final String FILENAME= "metadata.txt";
    public static final String DATA= "data";
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final String KNOWNHOSTS= "knownHosts";
    public static final int NONEXISTENCE = -1;


    public static void main(String[] args) throws Exception {


        JSONObject jsonObject = createFile();
        writeToFile(jsonObject);
    }


    public static void timestampRemove(JSONObject file,int id){
        ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).remove(id);
    }

    public static void timestampADD(JSONObject file,int id,Clock clock){
        LinkedHashMap<Object,Object> jSONconstructorTimestamp  = new LinkedHashMap<>();
        jSONconstructorTimestamp.put(CLOCK,clock.getClock());
        jSONconstructorTimestamp.put(REPLICA,clock.getReplica());
        jSONconstructorTimestamp.put(SHAREDBY,new JSONArray());

        JSONObject contentTimestamp = new JSONObject();
        contentTimestamp.put(id,new JSONObject(jSONconstructorTimestamp));
        ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).add(contentTimestamp);
    }

    public static JSONObject timestampgetJSONbyID(JSONObject file,int id){
        Iterator iterator= ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).iterator();
        while (iterator.hasNext()){
            JSONObject timestamp = (JSONObject) iterator.next();
            if(timestamp.get(id)!=null){
                return timestamp;
            }
        }
        return null;
    }

    public static void timestampChangeClock(JSONObject file,int id,Clock clock){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        jsonObject.put(CLOCK,clock.getClock());
        jsonObject.put(REPLICA,clock.getReplica());
    }

    public static JSONObject getTimeStamps(JSONObject file){
        return ((JSONObject) ((JSONObject)file.get(DATA)).get(TIMESTAMP));
    }

    public static void setTimeStamps(JSONObject file,JSONObject timestamp){
        ((JSONObject)file.get(DATA)).put(TIMESTAMP,timestamp);
    }

    public static Clock timestampGetClock(JSONObject file,int id){
        JSONObject jsonObject= timestampgetJSONbyID(file, id);
        return new Clock((int)jsonObject.get(CLOCK),(int)jsonObject.get(REPLICA));
    }


    public static void timestampAddSharedBy(JSONObject file,int id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        ((JSONArray)jsonObject.get(SHAREDBY)).add(sharedBy);
    }

    public static JSONArray timestampGetSharedBy(JSONObject file,int id){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return (JSONArray) jsonObject.get(SHAREDBY);
    }

    public static boolean timestampRemoveSharedBy(JSONObject file,int id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return ((JSONArray)jsonObject.get(SHAREDBY)).remove(sharedBy);
    }

    public static void addHost(JSONObject file, String ip){
        ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS)).add(ip);
    }

    public static void removeHost(JSONObject file, String ip){
        ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS)).remove(ip);
    }


    public static JSONObject createFile(){

        JSONObject file = new JSONObject();

        LinkedHashMap<Object,Object> jSONconstructorFile  = new LinkedHashMap<>();

        jSONconstructorFile.put(REPLICAID,UUID.randomUUID());
        jSONconstructorFile.put(KNOWNHOSTS,new JSONArray());

        jSONconstructorFile.put(TIMESTAMP,new JSONArray());

        JSONObject data = new JSONObject(new JSONObject(jSONconstructorFile));
        file.put(DATA,data);

        return file;
    }

    public static void writeToFile(JSONObject file){
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

    public static boolean metadataExistence(){
        return new File(FILENAME).exists();
    }

}
