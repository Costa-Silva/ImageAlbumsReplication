package sd.tp1.server;

import com.sun.xml.internal.bind.v2.model.core.ID;
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
    public static final String OBJECTID= "id";
    public static final String OPERATION ="operation";

    public static void main(String[] args) throws Exception {


        //JSONObject jsonObject = createFile();
        //timestampADD(jsonObject,"alu",new Clock(1,"ss"),OPERATION);
        JSONArray test=getTimeStamps(ServersUtils.getJsonFromFile(new byte[0]));
        System.out.println(test);
        //System.out.println(getTimeStamps(jsonObject));

        // writeToFile(jsonObject);
    }

    public static void timestampRemove(JSONObject file,String id){

        ((JSONArray) ((JSONObject)file.get(DATA)).get(TIMESTAMP)).remove(timestampgetJSONbyID(file,id));
    }

    public static void timestampChangeOperation(JSONObject file, String id, String op){
        JSONObject timestamp =  timestampgetJSONbyID(file,id);

        timestamp.put(OPERATION,op);
    }

    private static Map<Object,Object> timestampConstrutor(String id, Clock clock,String operation){
        Map<Object,Object> jSONconstructorTimestamp  = new LinkedHashMap<>();
        jSONconstructorTimestamp.put(OBJECTID,id);
        jSONconstructorTimestamp.put(CLOCK,clock.getClock());
        jSONconstructorTimestamp.put(REPLICA,clock.getReplica());
        jSONconstructorTimestamp.put(OPERATION,operation);
        jSONconstructorTimestamp.put(SHAREDBY,new JSONArray());
        return jSONconstructorTimestamp;
    }


    public static JSONObject newTimestamp(JSONObject file,String id,String myReplica, String operation){

        JSONObject newTimestamp = new JSONObject(timestampConstrutor(id, new Clock(0,myReplica),operation));
        ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).add(newTimestamp);


        return  newTimestamp;
    }

    public static String getTimestampID(JSONObject timestamp){
        return timestamp.get(OBJECTID).toString();
    }


    public static JSONObject timestampSet(JSONObject file, String id,Clock clock,String operation){

        JSONArray array = (JSONArray) ((JSONObject)file.get(DATA)).get(TIMESTAMP);

        JSONObject newTimeStamp = new JSONObject(timestampConstrutor(id,clock,operation));

        for (int i = 0; i < array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            if (getTimestampID(jsonObject).equals(id)){
                array.set (i,newTimeStamp);
                break;
            }
        }
        ((JSONObject)file.get(DATA)).put(TIMESTAMP,array);

        return newTimeStamp;
    }


    public static JSONObject timestampADD(JSONObject file,String id,Clock clock,String operation){

       JSONObject jsonObject = new JSONObject(timestampConstrutor(id,clock,operation));
         ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).add(jsonObject);
        return jsonObject;
    }


    public static JSONObject timestampgetJSONbyID(JSONObject file,String id){
        Iterator iterator= ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).iterator();
        while (iterator.hasNext()){
            JSONObject timestamp = (JSONObject) iterator.next();
            if(timestamp.get(OBJECTID).equals(id)){
                return timestamp;
            }
        }
        return new JSONObject();
    }

    public static void timestampChangeClock(JSONObject file,String id,Clock clock){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        jsonObject.put(CLOCK,clock.getClock());
        jsonObject.put(REPLICA,clock.getReplica());
    }

    public static JSONArray getTimeStamps(JSONObject file){
        return (JSONArray) ((JSONObject)file.get(DATA)).get(TIMESTAMP);
    }

    public static void addHost(JSONObject file, String ip){
        ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS)).add(ip);
    }

    public static boolean hasHost(JSONObject file, String ip){
        JSONArray knowhosts = ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS));

        Iterator iterator = knowhosts.iterator();

        while (iterator.hasNext()){
            String host = (String)iterator.next();
            if (host.equals(ip)){
                return true;
            }
        }
        return false;
    }

    public static void timestampADDJSON(JSONObject file, JSONObject newtimestamp){
        ((JSONArray)((JSONObject)file.get(DATA)).get(TIMESTAMP)).add(newtimestamp);
    }

    public static int hasSharedByPosition(JSONArray sharedBy,String ip){

        for (int i = 0; i < sharedBy.size() ; i++) {
            String testIp =sharedBy.get(i).toString();

            if (ip.equals(testIp)){
                return i;
            }
        }
            return -1;
    }

    public static void setTimeStamps(JSONObject file,JSONArray timestamps){
        ((JSONObject)file.get(DATA)).put(TIMESTAMP,timestamps);
    }

    public static Clock timestampGetClock(JSONObject file,String id){
        JSONObject jsonObject= timestampgetJSONbyID(file, id);
        Clock clock = new Clock(Integer.parseInt(jsonObject.get(CLOCK).toString()),jsonObject.get(REPLICA).toString());
        return clock;
    }

    public static void timestampAddSharedBy(JSONObject file,String id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        ((JSONArray)jsonObject.get(SHAREDBY)).add(sharedBy);
    }

    public static JSONArray timestampGetSharedBy(JSONObject file,String id){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return (JSONArray) jsonObject.get(SHAREDBY);
    }


    public static void timestampSetSharedBy(JSONObject file, String id, JSONArray array, String myIP){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        jsonObject.put(SHAREDBY,array);
        timestampRemoveSharedBy(file,id,myIP);
    }

    public static boolean timestampRemoveSharedBy(JSONObject file,String id,String sharedBy){
        JSONObject jsonObject = timestampgetJSONbyID(file, id);
        return ((JSONArray)jsonObject.get(SHAREDBY)).remove(sharedBy);
    }

    public static JSONArray getKnownHosts(JSONObject file){
        return ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS));
    }
    public static void removeHost(JSONObject file, String ip){
        ((JSONArray)((JSONObject)file.get(DATA)).get(KNOWNHOSTS)).remove(ip);
    }

    public static String getReplicaid(JSONObject file){
        return (String) ((JSONObject)file.get(DATA)).get(REPLICAID);
    }

    public static void setReplicaid(JSONObject file, UUID replica){
        ((JSONObject)file.get(DATA)).put(REPLICAID,replica.toString());
    }

    public static JSONObject createFile(){
        JSONObject file = new JSONObject();
        Map<Object,Object> jSONconstructorFile  = new LinkedHashMap<>();
        jSONconstructorFile.put(REPLICAID,UUID.randomUUID().toString());
        jSONconstructorFile.put(KNOWNHOSTS,new JSONArray());
        jSONconstructorFile.put(TIMESTAMP,new JSONArray());
        JSONObject json = new JSONObject(jSONconstructorFile);
        JSONObject data = new JSONObject(json);
        file.put(DATA,data);

        return file;
    }

    public static void writeToFile(JSONObject file){
        try {
            FileWriter fileWriter = new FileWriter(FILENAME);
            fileWriter.write(file.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean metadataExistence(){
        return new File(FILENAME).exists();
    }

    public static String buildNewId(String albumName,String pictureName){

        String defaultAlbum = "Album:"+albumName;
        String defaultPicture = "Picture:";
        return pictureName.isEmpty() ? defaultAlbum : defaultAlbum+"|"+defaultPicture+pictureName;
    }

    public static String[] getId(String id){

        if (id.contains("Picture:")){
            return id.split("Album:")[0].split("|Picture:") ;
        }else{
            String[] result = new String[1];
            result[0] = id.split("Album:")[1];

            return result;
        }
    }
}
