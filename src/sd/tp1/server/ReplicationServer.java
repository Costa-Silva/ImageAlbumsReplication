package sd.tp1.server;

import com.sun.deploy.net.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.tp1.SharedGallery;
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
    private static Map<String,String> ipTranslator;
    private boolean initialized;
    private JSONObject file;
    private String myFullIp;
    private String myReplica;

    public ReplicationServer(String myFullIp){
        serverIps = new ConcurrentHashMap<>();
        content = new HashMap<>();
        toReplicate= new ConcurrentHashMap<>();
        ipTranslator = new HashMap<>();
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
                    loadContentFromDisk(file,true);
                }else{
                    System.err.println("Waiting for possible connections");
                    Thread.sleep(5000); // any server discovered? no? create from scratch
                    int connectionsSize = serverIps.size();
                    System.err.println("Time up, got "+ connectionsSize +" connections.");

                    file = ReplicationServerUtils.createFile();
                    if (hasContent()){
                        loadContentFromDisk(file,false);
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
                    ReplicationServerUtils.writeToFile(file);
                }
                initialized=true;
                myReplica= ReplicationServerUtils.getReplicaid(file);
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

                    checkUnreplicaredContent();

                    if (serverIps.size()>0) {
                        System.out.println("STARTING REPLICATION TASK");

                        List<String> keys = new ArrayList<>(serverIps.keySet());
                        String serverIp = keys.get((new Random()).nextInt(serverIps.size()));
                        SharedGalleryClient sharedGalleryClient = getClient(serverIp, serverIps.get(serverIp));
                        JSONObject theirMetadata = new JSONObject();

                        Iterator mytimestamp = ReplicationServerUtils.getTimeStamps(file).iterator();
                        List<String> myObjectIds = new ArrayList<String>();

                        boolean gotconnection = false;
                        try {
                            theirMetadata = ServersUtils.getJsonFromFile(sharedGalleryClient.getMetaData());
                            gotconnection = true;
                        } catch (ProcessingException e) {
                            gotconnection = false;
                        }

                        if (gotconnection){

                            while (mytimestamp.hasNext()) {
                                String objectid = ReplicationServerUtils.getTimestampID((JSONObject) mytimestamp.next());
                                myObjectIds.add(objectid);
                            }

                            String otherServerReplica = ReplicationServerUtils.getReplicaid(theirMetadata);

                            String fullServerIp = buildIP(serverIp, serverIps.get(serverIp));

                            ipTranslator.put(fullServerIp, ReplicationServerUtils.getReplicaid(theirMetadata));

                            file = ServersUtils.getJsonFromFile(new byte[0]);

                            if (!ReplicationServerUtils.hasHost(file, fullServerIp)) {
                                ReplicationServerUtils.addHost(file, fullServerIp);
                                ReplicationServerUtils.writeToFile(file);
                                System.out.println("Added to my known hosts: " + serverIp);
                            }

                            JSONArray timestamps = ReplicationServerUtils.getTimeStamps(theirMetadata);
                            Iterator iteratorTheirTimestamps = timestamps.iterator();

                            while (iteratorTheirTimestamps.hasNext()) {

                                JSONObject timestamp = (JSONObject) iteratorTheirTimestamps.next();
                                String timestampStringID = timestamp.get(OBJECTID).toString();
                                myObjectIds.remove(timestampStringID);
                                String operation = timestamp.get(OPERATION).toString();
                                int clock = toIntExact((long) timestamp.get(CLOCK));

                                String replica = timestamp.get(REPLICA).toString();
                                Clock clockObj = new Clock(clock, replica);
                                JSONArray sharedBy = (JSONArray) timestamp.get(SHAREDBY);

                                JSONObject myTimestamp = ReplicationServerUtils.timestampgetJSONbyID(file, timestampStringID);
                                if (myTimestamp.size() > 0) {

                                    if ((Integer.parseInt(timestamp.get(CLOCK).toString())) == Integer.parseInt(myTimestamp.get(CLOCK).toString())) {
                                        int result = replica.compareTo(myTimestamp.get(REPLICA).toString());
                                        //  #timestamp's replicas -> b , mytimestamp's replicas ->a \\ result<0
                                        if (result < 0) {
                                            update(timestampStringID, operation, sharedGalleryClient, clockObj);
                                        }

                                    } else if (Integer.parseInt(timestamp.get(CLOCK).toString()) > Integer.parseInt(myTimestamp.get(CLOCK).toString())) {
                                        update(timestampStringID, operation, sharedGalleryClient, clockObj);
                                    }
                                    JSONArray mySharedby = sharedByAux(sharedBy, timestampStringID, otherServerReplica, file, sharedGalleryClient);
                                    doReplication(mySharedby, timestampStringID, serverIp, operation);
                                }
                            }
                            if (myObjectIds.size() > 0) {
                                checkAndReplicateReplicas(myObjectIds);
                            }
                            Thread.sleep(15000);
                        }else{
                            System.out.println("Couldn't connect to server");
                            continue;
                        }
                    }else {
                        System.out.println("No servers found to replicate");
                        Thread.sleep(15000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void checkAndReplicateReplicas(List<String> myObjectIds){
        JSONArray timestamps = ReplicationServerUtils.getTimeStamps(file);
        System.out.println("Check and replicate activated size:" +myObjectIds.size());
        while (myObjectIds.size()>0){
            String objectId= myObjectIds.remove(0);
            Iterator iterator =timestamps.iterator();
            while (iterator.hasNext()){
                JSONObject thisTimestamp = (JSONObject) iterator.next();
                String thisobj = ReplicationServerUtils.getTimestampID(thisTimestamp);
                if (thisobj.equals(objectId)){
                    JSONArray thisSharedBy =  (JSONArray) thisTimestamp.get(SHAREDBY);
                    String operation = thisTimestamp.get(OPERATION).toString();
                    doReplication(thisSharedBy,objectId,"",operation);
                    break;
                }
            }
        }
    }

    private void doReplication(JSONArray sharedby,String objectId,String hisSv,String operation) {
        int size = sharedby.size() +1;
        if (PARCIALREPLICATION>size){
            String bestmatch = serverReplicaToReplicate(sharedby);
            if (bestmatch.equals(myReplica)){
                System.out.println("I'm the chosen one for "+ objectId);
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
        String bestMatch=myReplica;
        while (iterator.hasNext()){
            String testReplica = iterator.next().toString();
            int result = testReplica.compareTo(bestMatch);
            if (result<0){
                bestMatch= testReplica;
            }
        }
        return bestMatch;
    }


    public JSONArray sharedByAux(JSONArray sharedBy,String timestampStringID, String replica,JSONObject file, SharedGalleryClient sharedGalleryClient){

        Iterator iterator = sharedBy.iterator();
        JSONArray mySharedBy = ReplicationServerUtils.timestampGetSharedBy(file,timestampStringID);

        if (ReplicationServerUtils.hasSharedByPosition(mySharedBy,replica)<0) {
            mySharedBy.add(replica);
        }

        while (iterator.hasNext()){
            String newReplica= iterator.next().toString();
            if (!newReplica.equals(myReplica)) {
                if (ReplicationServerUtils.hasSharedByPosition(mySharedBy, newReplica) < 0) {
                    mySharedBy.add(newReplica);
                }
            }
        }
        JSONObject jsonObject = ReplicationServerUtils.timestampgetJSONbyID(file,timestampStringID);
        jsonObject.put(SHAREDBY,mySharedBy);


        //notify another server to let him known that he can count with me :)
        sharedGalleryClient.checkAndAddSharedBy(myReplica,timestampStringID);

        ReplicationServerUtils.writeToFile(file);

        return mySharedBy;
    }


    public  String buildIP(String ip,String type){

        return type.equals(REST) ? ip+"-"+REST  : ip+"-"+SOAP;
    }

    public void update(String timestampStringID,String operation,
                       SharedGalleryClient sharedGalleryClient,Clock clockObj){

        String[] nameid = ReplicationServerUtils.getId(timestampStringID);

        String album="";
        String pict="";
        if (nameid.length>1){
        album=nameid[0];
        pict= nameid[1];
            String extension="";
            extension = sharedGalleryClient.getExtension(album, pict);
            pict+=extension;

        }else{
            album=nameid[0];
        }

        String[] ip = myFullIp.split("-");
        SharedGalleryClient mysharedGalleryClient = getClient(ip[0],ip[1]);

        if (nameid.length>1){
            if (operation.equals(CREATEOP)) {
                byte[] aux = sharedGalleryClient.getPictureData(album, pict);

                if (!content.containsKey(album)){
                    mysharedGalleryClient.createAlbum(album);
                    content.put(album,new HashMap<>());
                }
                content.get(album).put(pict,aux);
                mysharedGalleryClient.uploadPicture(album,pict,aux);
                writeMetaData(timestampStringID,clockObj,operation);
            }else if (operation.equals(REMOVEOP)){
                if(content.containsKey(album)) {
                    if (mysharedGalleryClient.deletePicture(album, pict)) {
                        writeMetaData(timestampStringID, clockObj, operation);
                        content.get(album).remove(pict);
                    }
                }
            }
        }else{
            if (operation.equals(CREATEOP)){

                if (mysharedGalleryClient.createAlbum(album)!=null){
                    writeMetaData(timestampStringID,clockObj,operation);
                    content.put(album,new HashMap<>());
                }

            }else if (operation.equals(REMOVEOP)){

                if (mysharedGalleryClient.deleteAlbum(album)) {
                    content.remove(album);
                    writeMetaData(timestampStringID,clockObj,operation);
                }
            }
        }
    }

    public void writeMetaData(String timestampStringID,Clock clockObj,
                              String operation){

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


    public void loadContentFromDisk(JSONObject file,boolean hadContent){

        String[] ip = myFullIp.split("-");
        SharedGalleryClient sharedGalleryClient = getClient(ip[0],ip[1]);


        sharedGalleryClient.getListOfAlbums().forEach(albumName->{
            HashMap<String,byte[]> imageContent = new HashMap<>();
            if (!hadContent)
            ReplicationServerUtils.newTimestamp(file,ReplicationServerUtils.buildNewId(albumName,""),ReplicationServerUtils.getReplicaid(file),CREATEOP);
            sharedGalleryClient.getListOfPictures(albumName).forEach(pictureName->{
                imageContent.put(pictureName,sharedGalleryClient.getPictureData(albumName,pictureName));
                if (!hadContent)
                ReplicationServerUtils.newTimestamp(file,ReplicationServerUtils.buildNewId(albumName,pictureName),ReplicationServerUtils.getReplicaid(file),CREATEOP);
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



                            keepAliveRecheck(ipToCheck,type,sharedGalleryClient);
                        }
                    }
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void keepAliveRecheck(String ipToCheck,String type,SharedGalleryClient sharedGalleryClient){

        new Thread(()->{
            int maxRetrys = 1;
            for (int i = 0; i < maxRetrys ; i++) {
                try{
                    if(sharedGalleryClient.getServerSize()>=0){
                        System.out.println(ipToCheck+" is back!");
                        serverIps.put(ipToCheck,type);
                        return;
                    }
                }catch (ProcessingException e){
                }
            }
            serverIps.remove(ipToCheck);

            String fullip = buildIP(ipToCheck,type);
            String replicatoRemove = ipTranslator.get(fullip);

            ReplicationServerUtils.removeHost(file,fullip);
            removeFromAllSharedBy(replicatoRemove);
            ReplicationServerUtils.writeToFile(file);
        }).start();
    }

    private void removeFromAllSharedBy(String replicatoremove){
        JSONArray timestamps = ReplicationServerUtils.getTimeStamps(file);

        Iterator timestampsIterator = timestamps.iterator();
        while (timestampsIterator.hasNext()){
            JSONObject thisTimestamp = (JSONObject) timestampsIterator.next();
            JSONArray thisSharedBy = (JSONArray) thisTimestamp.get(SHAREDBY);
            int size = thisSharedBy.size();
            for (int i = 0; i < size ; i++) {
                String replica= thisSharedBy.get(i).toString();
                if (replica.equals(replicatoremove)){
                    thisSharedBy.remove(i);
                    System.out.println("removed "+replica);
                    break;
                }
            }
        }
    }


    public static void addNewContent(String objectid,String fullip,String operation){
        String fullinfo = objectid+"/"+fullip;
        if (!toReplicate.containsKey(fullinfo))
            toReplicate.put(fullinfo,operation);
        System.out.println("Added remote content"+ fullinfo);
    }

    private void checkUnreplicaredContent() {
        for (String fullinfo:toReplicate.keySet()) {
            String[] objIdAndIp= fullinfo.split("/");
            String objectId= objIdAndIp[0];
            String fullip= objIdAndIp[1];
            String ip = fullip.split("-")[0];
            String type = fullip.split("-")[1];
            SharedGalleryClient sharedGalleryClient = getClient(ip,type);
            String operation= toReplicate.get(fullinfo);
            update(objectId,operation,sharedGalleryClient,new Clock(0,myReplica));
            toReplicate.remove(fullinfo);
        }
    }
}
