package sd.tp1.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.tp1.client.DiscoveryClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import sd.tp1.client.SharedGalleryClient;
import sd.tp1.client.SharedGalleryClientREST;
import sd.tp1.client.SharedGalleryClientSOAP;
import sd.tp1.client.ws.Server;
import sd.tp1.utils.Clock;

import javax.ws.rs.client.WebTarget;

/**
 * Created by Antonio on 15/05/16.
 */
public class ReplicationServer {

    Map<String,String> serverIps;
    private String random= "random";
    private Map<String,Map<String,byte[]>> content;
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final String KNOWNHOSTS= "knownHosts";
    public static final String OBJECTID= "id";
    public static final String REMOVEOP= "REMOVED";
    public static final String CREATEOP= "CREATED";
    public static final String OPERATION ="operation";
    private JSONObject file;
    private Set<String> mytimeStampsSet;

    public ReplicationServer(){
        serverIps = new ConcurrentHashMap<>();
        content = new HashMap<>();
        mytimeStampsSet =new HashSet<>();
        initReplication();
    }

    public void initReplication(){

        new Thread(()->{

            try {
                if (ReplicationServerUtils.metadataExistence()){
                    //load metadata
                    //load from disk to memory
                    file = ServersUtils.getMetaData();
                    JSONArray array = ReplicationServerUtils.getTimeStamps(file);
                    Iterator it = array.iterator();

                    while (it.hasNext()){
                        mytimeStampsSet.add(((JSONObject) it.next()).toJSONString());
                    }
                    loadContentFromDisk();
                }else{
                    System.err.println("Waiting for possible connections");
                    Thread.sleep(5000); // any server discovered? no? create from scratch
                    int connectionsSize = serverIps.size();
                    System.err.println("Time up, got "+ connectionsSize +"connections");
                    if (connectionsSize>0){
                        //server discovered
                        String serverIp="";
                        for (String ip:serverIps.keySet()) {
                            serverIp=ip;
                            break;
                        }
                        file= fetch(serverIp,serverIps.get(serverIp));
                    } else{
                        //start new
                        file = ReplicationServerUtils.createFile();
                    }
                    ReplicationServerUtils.writeToFile(file);
                }
                System.out.println("content: " + content);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startReplicationTask(){

        new Thread(()->{

            while(true){
                try {
                    List<String> keys = new ArrayList<>(serverIps.size());
                    String serverIp = keys.get((new Random()).nextInt(serverIps.size()));

                    SharedGalleryClient sharedGalleryClient = getClient(serverIp,serverIps.get(serverIp));
                    JSONObject theirMetadata = sharedGalleryClient.getMetaData();
                    String theirReplica = "";

                    JSONObject myfile = ServersUtils.getMetaData();
                    ReplicationServerUtils.addHost(myfile,serverIp);
                    JSONArray timestamps = ReplicationServerUtils.getTimeStamps(theirMetadata);
                    Iterator iteratorTheirTimestamps = timestamps.iterator();

                    while (iteratorTheirTimestamps.hasNext()){

                        JSONObject timestamp = (JSONObject) iteratorTheirTimestamps.next();
                        String timestampStringID = timestamp.get(OBJECTID).toString();
                        String operation = ((JSONObject)timestamp.get(OPERATION)).toJSONString();
                        int clock = (int)timestamp.get(CLOCK);
                        String replica = timestamp.get(REPLICA).toString();
                        Clock clockObj = new Clock(clock,replica);
                        JSONArray sharedBy = (JSONArray) timestamp.get(SHAREDBY);
                        if (mytimeStampsSet.contains(timestampStringID)){
                            JSONObject myTimestamp = ReplicationServerUtils.timestampgetJSONbyID(myfile,timestampStringID);
                            String mytimestampStringID = myTimestamp.get(OBJECTID).toString();

                            if (timestampStringID.equals(mytimestampStringID)){
                                if ((int)timestamp.get(CLOCK)==(int)myTimestamp.get(CLOCK)){
                                    int result = timestamp.get(REPLICA).toString().compareTo(myTimestamp.get(REPLICA).toString());
                                    //  #timestamp's replicas -> b , mytimestamp's replicas ->a \\ result<0
                                    if (result<0){
                                        update(myfile,sharedBy,timestampStringID,operation,sharedGalleryClient,theirReplica,clockObj);
                                    }

                                }else if ((int)timestamp.get(CLOCK) > (int) myTimestamp.get(CLOCK)){
                                    update(myfile,sharedBy,timestampStringID,operation,sharedGalleryClient,theirReplica,clockObj);
                                }
                            }
                        }else{
                            if (operation.equals(CREATEOP)){
                                update(myfile,sharedBy,timestampStringID,operation,sharedGalleryClient,theirReplica,clockObj);
                            }else if (operation.equals(REMOVEOP)){
                                writeMetaData(myfile,timestampStringID,clockObj,sharedBy,REMOVEOP,theirReplica);
                            }
                        }
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void update(JSONObject myfile, JSONArray sharedBy, String timestampStringID,String operation,
                       SharedGalleryClient sharedGalleryClient,String theirReplica,Clock clockObj){
        String[] nameid = ReplicationServerUtils.getId(timestampStringID);
        if (nameid.length>1){
            if (operation.equals(CREATEOP)){
                byte[] aux = sharedGalleryClient.getPictureData(nameid[0],nameid[1]);
                content.get(nameid[0]).put(nameid[1],aux);
                ServersUtils.uploadPicture(nameid[0],nameid[1],aux);
                writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,theirReplica);
            }else if (operation.equals(REMOVEOP)){
                content.get(nameid[0]).remove(nameid[1]);
                if (ServersUtils.deletePicture(nameid[0],nameid[1])){
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,theirReplica);
                }
            }
        }else{
            if (operation.equals(CREATEOP)){
                content.put(nameid[0],new HashMap<>());
                if (ServersUtils.createAlbum(nameid[0])!=null)
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,theirReplica);

            }else if (operation.equals(REMOVEOP)){
                content.remove(nameid[0]);
                if (ServersUtils.deleteAlbum(nameid[0])) {
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,theirReplica);
                }
            }
        }
    }

    public void writeMetaData(JSONObject myfile,String timestampStringID,Clock clockObj,JSONArray sharedBy,
                              String operation,String theirReplica){

        ReplicationServerUtils.timestampSet(myfile,timestampStringID,clockObj,operation);

        ReplicationServerUtils.timestampSetSharedBy(myfile,timestampStringID,sharedBy,
                ReplicationServerUtils.getReplicaid(myfile));
        ReplicationServerUtils.timestampAddSharedBy(myfile,timestampStringID,theirReplica);
        ReplicationServerUtils.writeToFile(myfile);
    }




    public void loadContentFromDisk(){

        ServersUtils.getAlbumList().forEach(albumName->{
            HashMap<String,byte[]> imageContent = new HashMap<>();
            ServersUtils.getPicturesList(albumName).forEach(pictureName->{
                imageContent.put(pictureName,ServersUtils.getPictureData(albumName,pictureName));
            });
            content.put(albumName,imageContent);
        });
    }

    public void addServer(String newIp,String type){
        serverIps.putIfAbsent(newIp,type);
    }

    private JSONObject fetch(String ip, String type){

        SharedGalleryClient sharedGalleryClient = getClient(ip, type);
        sharedGalleryClient.getListOfAlbums().forEach(albumName->{

            HashMap<String,byte[]> imageContent = new HashMap<>();
            sharedGalleryClient.getListOfPictures(albumName).forEach(pictureName->{
                imageContent.put(pictureName,sharedGalleryClient.getPictureData(albumName,
                        pictureName));
            });
            content.put(albumName,imageContent);
        });

        return sharedGalleryClient.getMetaData();
    }

    private SharedGalleryClient getClient(String ip, String type){
        if (type.equals("REST")){
            WebTarget webTarget=  DiscoveryClient.getWebTarget(ip);
            return new SharedGalleryClientREST(webTarget,random);
        }else {
            Server server = DiscoveryClient.getWebServiceServer(ip);
            return new SharedGalleryClientSOAP(server);
        }
    }


}