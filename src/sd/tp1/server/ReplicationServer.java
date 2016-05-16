package sd.tp1.server;

import org.json.simple.JSONObject;
import sd.tp1.client.DiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String random= "random";
    private Map<String,Map<String,byte[]>> content;
    private boolean first;
    public ReplicationServer(){
        serverIps = new ConcurrentHashMap<>();
        first = true;
        content = new HashMap<>();
    }

    public void initReplication(String ip, String type){

        new Thread(()->{
            JSONObject file = fetch(ip,type);
            System.out.println("content: " + content);

            JSONObject ourMetadata = ReplicationServerUtils.createFile();

            ReplicationServerUtils.setTimeStamps(ourMetadata,ReplicationServerUtils.getTimeStamps(file));

        }).start();
    }

    public void addServer(String newIp,String type){
        serverIps.put(newIp,type);
        if (first){
            first = false;
            initReplication(newIp,type);
        }
    }


    private JSONObject fetch(String ip, String type){
        SharedGalleryClient sharedGalleryClient;
        if (type.equals("REST")){
            WebTarget webTarget=  DiscoveryClient.getWebTarget(ip);
            sharedGalleryClient = new SharedGalleryClientREST(webTarget,random);
        }else {
            Server server = DiscoveryClient.getWebServiceServer(ip);
            sharedGalleryClient = new SharedGalleryClientSOAP(server);
        }


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
}