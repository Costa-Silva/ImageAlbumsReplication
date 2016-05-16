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

                    Iterator iterator = timestamps.iterator();

                    while (iterator.hasNext()){

                        JSONObject timestamp = (JSONObject) iterator.next();




                    }


                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        });
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