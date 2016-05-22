package sd.tp1.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.tp1.client.DiscoveryClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static java.lang.Math.toIntExact;
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
    public static final String DATA= "data";
    public static final String REST ="REST";
    public static final String SOAP ="SOAP";
    private JSONObject file;
    private Set<String> mytimeStampsSet;
    private String myFullIp;

    public ReplicationServer(String myFullIp){
        serverIps = new ConcurrentHashMap<>();
        content = new HashMap<>();
        mytimeStampsSet =new HashSet<>();
        this.myFullIp=myFullIp;
        initReplication();
    }

    public void initReplication(){

        new Thread(()->{

            try {
                if (ReplicationServerUtils.metadataExistence()){
                    //load metadata
                    //load from disk to memory
                    file = ServersUtils.getJsonFromFile(new byte[0]);
                    JSONArray array = ReplicationServerUtils.getTimeStamps(file);

                    Iterator it = array.iterator();
                    while (it.hasNext()){
                        mytimeStampsSet.add(ReplicationServerUtils.getTimestampID((JSONObject) it.next()));
                    }
                    loadContentFromDisk(file);
                }else{
                    System.err.println("Waiting for possible connections");
                    Thread.sleep(5000); // any server discovered? no? create from scratch
                    int connectionsSize = serverIps.size();
                    System.err.println("Time up, got "+ connectionsSize +" connections.");
                    if (connectionsSize>0){
                        //server discovered
                        String serverIp="";
                        for (String ip:serverIps.keySet()) {
                            serverIp=ip;
                            break;
                        }
                        file = ReplicationServerUtils.createFile();

                        JSONObject theirMetadata = fetch(serverIp,serverIps.get(serverIp));

                        JSONArray timeStamps = ReplicationServerUtils.getTimeStamps(theirMetadata);

                        if (hasContent()){
                            loadContentFromDisk(file);
                        }
                        Iterator iterator = timeStamps.iterator();
                        while (iterator.hasNext()){
                            JSONObject timestampjson =  (JSONObject) iterator.next();

                            if (!mytimeStampsSet.contains(timestampjson.get(OBJECTID).toString()))
                                ReplicationServerUtils.timestampADDJSON(file,timestampjson);
                        }


                        ReplicationServerUtils.addHost(file,buildIP(serverIp,serverIps.get(serverIp)));


                    } else{
                        //start new
                        file = ReplicationServerUtils.createFile();
                        if (hasContent()){
                            loadContentFromDisk(file);
                        }
                    }

                    ReplicationServerUtils.writeToFile(file);
                }
                startReplicationTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startReplicationTask(){
        keepAlive();
        new Thread(()->{
            while(true){
                try {
                    if (serverIps.size()>0){
                        System.out.println("STARTING REPLICATION TASK");
                        List<String> keys = new ArrayList<>(serverIps.keySet());
                        String serverIp = keys.get((new Random()).nextInt(serverIps.size()));

                        SharedGalleryClient sharedGalleryClient = getClient(serverIp,serverIps.get(serverIp));
                        JSONObject theirMetadata = ServersUtils.getJsonFromFile(sharedGalleryClient.getMetaData());
                        String fullServerIp= buildIP(serverIp,serverIps.get(serverIp));

                        if (!ReplicationServerUtils.hasHost(file,fullServerIp)) {
                            ReplicationServerUtils.addHost(file,fullServerIp);
                            ReplicationServerUtils.writeToFile(file);
                            System.out.println("added to my hosts: "+ serverIp);
                        }

                        JSONArray timestamps = ReplicationServerUtils.getTimeStamps(theirMetadata);
                        Iterator iteratorTheirTimestamps = timestamps.iterator();

                        while (iteratorTheirTimestamps.hasNext()){

                            JSONObject timestamp = (JSONObject) iteratorTheirTimestamps.next();
                            String timestampStringID = timestamp.get(OBJECTID).toString();
                            String operation = timestamp.get(OPERATION).toString();
                            int clock = toIntExact((long)timestamp.get(CLOCK))  ;

                            String replica = timestamp.get(REPLICA).toString();
                            Clock clockObj = new Clock(clock,replica);
                            JSONArray sharedBy = (JSONArray) timestamp.get(SHAREDBY);


                            Iterator iterator= sharedBy.iterator();
                            while (iterator.hasNext()){
                                System.out.println("sharedbuy: "+iterator.next());
                            }


                            if (mytimeStampsSet.contains(timestampStringID)){
                                JSONObject myTimestamp = ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID);
                                String mytimestampStringID = myTimestamp.get(OBJECTID).toString();

                                if (timestampStringID.equals(mytimestampStringID)){


                                    if (( Integer.parseInt(timestamp.get(CLOCK).toString()))==Integer.parseInt(myTimestamp.get(CLOCK).toString())){
                                        int result = timestamp.get(REPLICA).toString().compareTo(myTimestamp.get(REPLICA).toString());
                                        //  #timestamp's replicas -> b , mytimestamp's replicas ->a \\ result<0
                                        if (result<0){
                                            update(file,sharedBy,timestampStringID,operation,sharedGalleryClient,fullServerIp,clockObj);
                                        }

                                    }else if (Integer.parseInt(timestamp.get(CLOCK).toString()) > Integer.parseInt(myTimestamp.get(CLOCK).toString())){
                                        update(file,sharedBy,timestampStringID,operation,sharedGalleryClient,fullServerIp,clockObj);
                                    }
                                }
                            }else{
                                mytimeStampsSet.add(timestampStringID);
                                if (operation.equals(CREATEOP)){
                                    update(file,sharedBy,timestampStringID,operation,sharedGalleryClient,fullServerIp,clockObj);
                                }else if (operation.equals(REMOVEOP)){
                                    writeMetaData(file,timestampStringID,clockObj,sharedBy,REMOVEOP,fullServerIp,sharedGalleryClient);
                                }
                            }

                            int index = ReplicationServerUtils.hasSharedByPosition(sharedBy,fullServerIp);
                            if (index>=0){
                                sharedBy.set(index,fullServerIp);
                            }else{
                                sharedBy.add(fullServerIp);
                            }
                            JSONObject jsonObject = ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID);
                            jsonObject.put(SHAREDBY,sharedBy);
                            ReplicationServerUtils.writeToFile(file);
                        }
                    }else {
                        System.out.println("No servers found to replicate");
                    }
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public  String buildIP(String ip,String type){

        return type.equals(REST) ? ip+"-"+REST  : ip+"-"+SOAP;
    }

    public void update(JSONObject myfile, JSONArray sharedBy, String timestampStringID,String operation,
                       SharedGalleryClient sharedGalleryClient,String serverIp,Clock clockObj){
        String[] nameid = ReplicationServerUtils.getId(timestampStringID);


        if (nameid.length>1){
            if (operation.equals(CREATEOP)){
                byte[] aux = sharedGalleryClient.getPictureData(nameid[0],nameid[1]);
                content.get(nameid[0]).put(nameid[1],aux);
                ServersUtils.uploadPicture(nameid[0],nameid[1],aux);
                writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,serverIp,sharedGalleryClient);
            }else if (operation.equals(REMOVEOP)){
                content.get(nameid[0]).remove(nameid[1]);
                if (ServersUtils.deletePicture(nameid[0],nameid[1])){
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,serverIp,sharedGalleryClient);
                }
            }
        }else{
            if (operation.equals(CREATEOP)){
                content.put(nameid[0],new HashMap<>());
                if (ServersUtils.hasAlbum(nameid[0]) || ServersUtils.createAlbum(nameid[0])!=null)
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,serverIp,sharedGalleryClient);

            }else if (operation.equals(REMOVEOP)){
                content.remove(nameid[0]);
                if (ServersUtils.deleteAlbum(nameid[0])) {
                    writeMetaData(myfile,timestampStringID,clockObj,sharedBy,operation,serverIp,sharedGalleryClient);
                }
            }
        }
    }

    public void writeMetaData(JSONObject myfile,String timestampStringID,Clock clockObj,JSONArray sharedBy,
                              String operation,String hostIp, SharedGalleryClient sharedGalleryClient){



        //notify another server to let him known that he can count with me :)
       sharedGalleryClient.checkAndAddSharedBy(myFullIp,timestampStringID);

        if (ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID).size()>0){
            ReplicationServerUtils.timestampSet(file,timestampStringID,clockObj,operation);
        }else{
            ReplicationServerUtils.timestampADD(file,timestampStringID,clockObj,operation);
        }

        System.out.println("Updated:"+ timestampStringID);
        System.out.println("posUpd: "+file);
    }

    public boolean hasContent(){
        if (ServersUtils.getAlbumList().size()>0)
            return true;

        return false;
    }


    public void loadContentFromDisk(JSONObject file){

        ServersUtils.getAlbumList().forEach(albumName->{
            HashMap<String,byte[]> imageContent = new HashMap<>();
            JSONObject albumTimestampJson = ReplicationServerUtils.newTimestamp(file,ReplicationServerUtils.buildNewId(albumName,""),ReplicationServerUtils.getReplicaid(file),CREATEOP);
            mytimeStampsSet.add(ReplicationServerUtils.getTimestampID(albumTimestampJson));
            ServersUtils.getPicturesList(albumName).forEach(pictureName->{
                imageContent.put(pictureName,ServersUtils.getPictureData(albumName,pictureName));
                JSONObject newTimestampJson = ReplicationServerUtils.newTimestamp(file,ReplicationServerUtils.buildNewId(albumName,pictureName),ReplicationServerUtils.getReplicaid(file),CREATEOP);
                mytimeStampsSet.add(ReplicationServerUtils.getTimestampID(newTimestampJson));

            });
            content.put(albumName,imageContent);
        });
        Iterator hostsIterator = ReplicationServerUtils.getKnownHosts(file).iterator();
        while (hostsIterator.hasNext()){
            String[] identifiers = ((String)hostsIterator.next()).split("-");
            serverIps.put(identifiers[0],identifiers[1]);
        }
        System.out.println("Loaded Content size: " +content.size());
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

        return ServersUtils.getJsonFromFile(sharedGalleryClient.getMetaData());
    }

    private SharedGalleryClient getClient(String ip, String type){
        if (type.equals(REST)){
            WebTarget webTarget=  DiscoveryClient.getWebTarget(ip);
            return new SharedGalleryClientREST(webTarget,random);
        }else {
            Server server = DiscoveryClient.getWebServiceServer(ip);
            return new SharedGalleryClientSOAP(server);
        }
    }

    private void keepAlive(){
        int timeout = 6000;
        new Thread(()->{
            while (true){
                try {
                    for (String ipToCheck :serverIps.keySet()) {
                        //you there?
                        //checking if return any info
                        String type = serverIps.get(ipToCheck);
                        SharedGalleryClient sharedGalleryClient = getClient(ipToCheck,type);
                        if(!(sharedGalleryClient.getServerSize()>=0)){
                            serverIps.remove(ipToCheck);

                            //remove from known hosts

                            JSONObject myfile = ServersUtils.getJsonFromFile(new byte[0]);
                            keepAliveRecheck(ipToCheck,sharedGalleryClient);
                        }
                    }
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void keepAliveRecheck(String ipToCheck,SharedGalleryClient sharedGalleryClient){


        new Thread(()->{
            int maxRetrys = 2;
            for (int i = 0; i < maxRetrys ; i++) {
                if(sharedGalleryClient.getServerSize()>=0){
                    String type = sharedGalleryClient.getType().equals(REST) ? REST : SOAP;
                    serverIps.put(ipToCheck,type);
                    break;
                }
            }
            ReplicationServerUtils.removeHost(file,ipToCheck);
            ReplicationServerUtils.writeToFile(file);
        }).start();
    }


}