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

import javax.ws.rs.ProcessingException;
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
    public static final String OBJECTID= "id";
    public static final String REMOVEOP= "REMOVED";
    public static final String CREATEOP= "CREATED";
    public static final String OPERATION ="operation";
    public static final String REST ="REST";
    public static final String SOAP ="SOAP";
    public static final int PARCIALREPLICATION = 2 ;
    private static Map<String,String> toReplicate;
    private boolean initialized;
    private JSONObject file;
    private String myFullIp;
    private String myReplica;

    public ReplicationServer(String myFullIp){
        serverIps = new ConcurrentHashMap<>();
        content = new HashMap<>();
        toReplicate= new HashMap<>();
        this.myFullIp=myFullIp;
        initialized =false;
        initReplication();
    }

    public void initReplication(){

        new Thread(()->{

            try {
                if (ReplicationServerUtils.metadataExistence()){
                    //load metadata
                    //load from disk to memory
                    file = ServersUtils.getJsonFromFile(new byte[0]);
                    loadContentFromDisk(file);
                }else{
                    System.err.println("Waiting for possible connections");
                    Thread.sleep(5000); // any server discovered? no? create from scratch
                    int connectionsSize = serverIps.size();
                    System.err.println("Time up, got "+ connectionsSize +" connections.");

                    file = ReplicationServerUtils.createFile();
                    if (hasContent()){
                        loadContentFromDisk(file);
                    }

                    if (connectionsSize>0){
                        //server discovered
                        String serverIp="";
                        for (String ip:serverIps.keySet()) {
                            serverIp=ip;
                            break;
                        }
                        ReplicationServerUtils.addHost(file,buildIP(serverIp,serverIps.get(serverIp)));
                    }
                    myReplica= ReplicationServerUtils.getReplicaid(file);
                    ReplicationServerUtils.writeToFile(file);
                }
                initialized=true;
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

                    //checkUnreplicaredContent();

                    if (serverIps.size()>0){
                        System.out.println("STARTING REPLICATION TASK");
                        List<String> keys = new ArrayList<>(serverIps.keySet());
                        String serverIp = keys.get((new Random()).nextInt(serverIps.size()));
                        SharedGalleryClient sharedGalleryClient = getClient(serverIp,serverIps.get(serverIp));
                        JSONObject theirMetadata = new JSONObject();
                        boolean x =false;
                        while (!x) {
                            try {
                                theirMetadata = ServersUtils.getJsonFromFile(sharedGalleryClient.getMetaData());
                                x = true;
                            } catch (ProcessingException e) {
                                x = false;
                            }
                        }
                        String fullServerIp= buildIP(serverIp,serverIps.get(serverIp));

                        file = ServersUtils.getJsonFromFile(new byte[0]);

                        if (!ReplicationServerUtils.hasHost(file,fullServerIp)) {
                            ReplicationServerUtils.addHost(file,fullServerIp);
                            ReplicationServerUtils.writeToFile(file);
                            System.out.println("Added to my known hosts: "+ serverIp);
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

                            JSONObject myTimestamp = ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID);
                            if (myTimestamp.size()>0){

                                if (( Integer.parseInt(timestamp.get(CLOCK).toString()))==Integer.parseInt(myTimestamp.get(CLOCK).toString())){
                                    int result = replica.compareTo(myTimestamp.get(REPLICA).toString());
                                    //  #timestamp's replicas -> b , mytimestamp's replicas ->a \\ result<0
                                    if (result<0){
                                        update(timestampStringID,operation,sharedGalleryClient,clockObj);
                                    }

                                }else if (Integer.parseInt(timestamp.get(CLOCK).toString()) > Integer.parseInt(myTimestamp.get(CLOCK).toString())){
                                    update(timestampStringID,operation,sharedGalleryClient,clockObj);
                                }
                                JSONArray mySharedby = sharedByAux(sharedBy,timestampStringID,replica,file);
                                //doReplication(mySharedby,timestampStringID,serverIp,operation);

                            }
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



    private void doReplication(JSONArray sharedby,String objectId,String hisSv,String operation) {
        int size = sharedby.size() +1;
        if (PARCIALREPLICATION>size){

            String bestmatch = serverReplicaToReplicate(sharedby);
            String myreplica = ReplicationServerUtils.getReplicaid(file);

            if (bestmatch.equals(myreplica)){
                System.out.println("I'm the chosen one");
                int toReplicate = PARCIALREPLICATION-size;
                List<String> keys = new ArrayList<>(serverIps.keySet());
                keys.remove(hisSv);
                int i =0;
                while (i<toReplicate){
                    if (keys.size()>0) {
                        try{
                            int index = new Random().nextInt(keys.size());
                            String serverIp = keys.remove(index);
                            SharedGalleryClient sharedGalleryClient = getClient(serverIp, serverIps.get(serverIp));
                            sharedGalleryClient.askForContent(objectId,myFullIp,operation);
                            System.out.println("Replicated to "+serverIp);
                        }catch (ProcessingException e){
                            i--;
                        }
                    }else{
                        System.out.println("Not enought servers to replicate this content");
                        break;
                    }
                    i++;
                }
            }
        }
    }


    private String serverReplicaToReplicate(JSONArray sharedby){
        Iterator iterator = sharedby.iterator();
        String bestMatch=ReplicationServerUtils.getReplicaid(file) ;
        while (iterator.hasNext()){
            String testReplica = iterator.next().toString();
            int result = testReplica.compareTo(bestMatch);
            if (result<0){
                bestMatch= testReplica;
            }
        }
        return bestMatch;
    }


    public JSONArray sharedByAux(JSONArray sharedBy,String timestampStringID, String replica,JSONObject file ){
        Iterator iterator = sharedBy.iterator();
        JSONArray mySharedBy = ReplicationServerUtils.timestampGetSharedBy(file,timestampStringID);

        if (ReplicationServerUtils.hasSharedByPosition(mySharedBy,replica)<0) {
            mySharedBy.add(replica);
        }

        while (iterator.hasNext()){
            String newReplica= (String)iterator.next();
            if (!newReplica.equals(myReplica)) {
                if (ReplicationServerUtils.hasSharedByPosition(mySharedBy, newReplica) < 0) {
                    mySharedBy.add(newReplica);
                }
            }
        }
        JSONObject jsonObject = ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID);
        jsonObject.put(SHAREDBY,mySharedBy);
        ReplicationServerUtils.writeToFile(file);

        return mySharedBy;
    }


    public  String buildIP(String ip,String type){

        return type.equals(REST) ? ip+"-"+REST  : ip+"-"+SOAP;
    }

    public void update(String timestampStringID,String operation,
                       SharedGalleryClient sharedGalleryClient,Clock clockObj){
        String[] nameid = ReplicationServerUtils.getId(timestampStringID);


        if (nameid.length>1){
            if (operation.equals(CREATEOP)){
                byte[] aux = sharedGalleryClient.getPictureData(nameid[0],nameid[1]);
                content.get(nameid[0]).put(nameid[1],aux);
                ServersUtils.uploadPicture(nameid[0],nameid[1],aux);
                writeMetaData(timestampStringID,clockObj,operation,sharedGalleryClient);
            }else if (operation.equals(REMOVEOP)){
                content.get(nameid[0]).remove(nameid[1]);
                if (ServersUtils.deletePicture(nameid[0],nameid[1])){
                    writeMetaData(timestampStringID,clockObj,operation,sharedGalleryClient);
                }
            }
        }else{
            if (operation.equals(CREATEOP)){
                content.put(nameid[0],new HashMap<>());
                if (ServersUtils.hasAlbum(nameid[0]) || ServersUtils.createAlbum(nameid[0])!=null)
                    writeMetaData(timestampStringID,clockObj,operation,sharedGalleryClient);

            }else if (operation.equals(REMOVEOP)){
                content.remove(nameid[0]);
                if (ServersUtils.deleteAlbum(nameid[0])) {
                    writeMetaData(timestampStringID,clockObj,operation,sharedGalleryClient);
                }
            }
        }
    }

    public void writeMetaData(String timestampStringID,Clock clockObj,
                              String operation,SharedGalleryClient sharedGalleryClient){

        //notify another server to let him known that he can count with me :)
        sharedGalleryClient.checkAndAddSharedBy(myReplica,timestampStringID);

        System.out.println("my file "+file.toJSONString());
        System.out.println("timestamp "+ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID).toJSONString());


        if (ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID).size()>0){
            ReplicationServerUtils.timestampSet(file,timestampStringID,clockObj,operation);
        }else{
            ReplicationServerUtils.timestampADD(file,timestampStringID,clockObj,operation);
        }
        ReplicationServerUtils.writeToFile(file);
        System.out.println("Updated:"+ timestampStringID);
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
            ReplicationServerUtils.timestampADDJSON(file,albumTimestampJson);
            ServersUtils.getPicturesList(albumName).forEach(pictureName->{
                imageContent.put(pictureName,ServersUtils.getPictureData(albumName,pictureName));
                JSONObject newTimestampJson = ReplicationServerUtils.newTimestamp(file,ReplicationServerUtils.buildNewId(albumName,pictureName),ReplicationServerUtils.getReplicaid(file),CREATEOP);
                ReplicationServerUtils.timestampADDJSON(file,newTimestampJson);
            });
            content.put(albumName,imageContent);
        });
        Iterator hostsIterator = ReplicationServerUtils.getKnownHosts(file).iterator();
        while (hostsIterator.hasNext()){
            String[] identifiers = ((String)hostsIterator.next()).split("-");
            serverIps.put(identifiers[0],identifiers[1]);
        }

        ReplicationServerUtils.writeToFile(file);
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

    public boolean allowedToSendInfo(){
        return initialized;
    }


    private void keepAlive(){
        int timeout = 4000;
        new Thread(()->{
            while (true){
                try {
                    for (String ipToCheck :serverIps.keySet()) {
                        //you there?
                        //checking if return any info
                        String type = serverIps.get(ipToCheck);
                        SharedGalleryClient sharedGalleryClient = getClient(ipToCheck,type);
                        try{
                            sharedGalleryClient.getServerSize();
                        }catch (ProcessingException e){
                            System.out.println("Lost connection with: "+ipToCheck);
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
                try{
                    if(sharedGalleryClient.getServerSize()>=0){
                        System.out.println(ipToCheck+" is back!");
                        return;
                    }
                }catch (ProcessingException e){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            serverIps.remove(ipToCheck);
            ReplicationServerUtils.removeHost(file,ipToCheck);
            ReplicationServerUtils.writeToFile(file);
        }).start();
    }

    public static void addNewContent(String objectid,String fullip,String operation){
        String fullinfo = objectid+"/"+fullip;
        if (!toReplicate.containsKey(fullinfo))
            toReplicate.put(fullinfo,operation);
    }

    private void checkUnreplicaredContent() {

        while (toReplicate.size()>0){

            for (String fullinfo:toReplicate.keySet()) {
                String[] objIdAndIp= fullinfo.split("/");
                String objectId= objIdAndIp[0];
                String fullip= objIdAndIp[1];
                String ip = fullip.split("-")[0];
                String type = fullip.split("-")[1];
                SharedGalleryClient sharedGalleryClient = getClient(ip,type);
                String operation= toReplicate.get(fullinfo);
                update(objectId,operation,sharedGalleryClient,new Clock(0,myReplica));
            }

        }

    }
}
