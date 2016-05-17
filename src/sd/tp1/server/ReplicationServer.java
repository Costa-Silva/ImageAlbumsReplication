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

import javax.ws.rs.client.WebTarget;

/**
 * Created by Antonio on 15/05/16.
 */
public class ReplicationServer {

    Map<String,String> serverIps;
    private String random= "random";
    private Map<String,Map<String,byte[]>> content;
    public static final String DATA= "data";
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final String KNOWNHOSTS= "knownHosts";
    public static final String OBJECTID= "id";

    private JSONObject file;

    public ReplicationServer(){
        serverIps = new ConcurrentHashMap<>();
        content = new HashMap<>();
        initReplication();
    }

    public void initReplication(){

        new Thread(()->{

            try {
                if (ReplicationServerUtils.metadataExistence()){
                    //load metadata
                    //load from disk to memory
                    file = ServersUtils.getMetaData();
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
                    JSONArray timestamps = ReplicationServerUtils.getTimeStamps(theirMetadata);
                    Iterator iteratorTheirTimestamps = timestamps.iterator();

                    JSONObject myfile = ServersUtils.getMetaData();
                    JSONArray myTimestamps = ReplicationServerUtils.getTimeStamps(myfile);
                    String myReplica ="";// GET REPLICA

                    while (iteratorTheirTimestamps.hasNext()){

                        JSONObject timestamp = (JSONObject) iteratorTheirTimestamps.next();

                        Iterator iteratorMyTimestamps = myTimestamps.iterator();

                        while (iteratorMyTimestamps.hasNext()){
                            JSONObject myTimestamp = (JSONObject) iteratorMyTimestamps.next();
                            boolean exist = false;

                            String timestampStringID = timestamp.get(OBJECTID).toString();
                            String mytimestampStringID = myTimestamp.get(OBJECTID).toString();

                            if (timestampStringID.equals(mytimestampStringID)){
                                exist=true;
                                if ((int)timestamp.get(CLOCK)==(int)myTimestamp.get(CLOCK)){

                                    int result = timestamp.get(REPLICA).toString().compareTo(myTimestamp.get(REPLICA).toString());

                                    // lexicographically order have priority

                                    if (result<0){
                                        //pede content
                                        //atualiza dados
                                        String[] nameid = getId(timestampStringID);
                                        if (nameid.length>1){
                                            if ("operation create".isEmpty()){
                                                byte[] aux = sharedGalleryClient.getPictureData(nameid[0],nameid[1]);
                                                content.get(nameid[0]).put(nameid[1],aux);
                                            }else if ("operation remove".isEmpty()){
                                                content.get(nameid[0]).remove(nameid[1]);
                                            }
                                        }else{
                                            if ("operation create".isEmpty()){
                                                if (sharedGalleryClient.createAlbum(nameid[0]).equals(nameid[0]))
                                                content.put(nameid[0],new HashMap<>());
                                            }else if ("operation remove".isEmpty()){
                                                content.remove(nameid[0]);
                                            }
                                        }
                                    }

                                }else if ( (int)timestamp.get(CLOCK) > (int) myTimestamp.get(CLOCK) ){
                                    //pede content
                                    //atualiza dados
                                }
                            }

                            if (!exist){
                                //pede content
                                //cria dados
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

    public String buildNewId(String albumName,String pictureName){

        String defaultAlbum = "Album:"+albumName;
        String defaultPicture = "Picture:";
        return pictureName.equals("") ? defaultAlbum : defaultAlbum+"|"+defaultPicture+pictureName;
    }

    public String[] getId(String id){
        if (id.contains("Picture:")){
            return id.split("Album:")[0].split("Picture:") ;
        }else{
            return id.split("Album:");
        }
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