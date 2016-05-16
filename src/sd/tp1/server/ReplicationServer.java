package sd.tp1.server;

import sd.tp1.client.DiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            if (type.equals("REST")){
                WebTarget webTarget=  DiscoveryClient.getWebTarget(ip);

                SharedGalleryClientREST sharedGalleryClientREST = new SharedGalleryClientREST(webTarget,random);


                sharedGalleryClientREST.getListOfAlbums().forEach(albumName->{

                    HashMap<String,byte[]> imageContent = new HashMap<>();
                    sharedGalleryClientREST.getListOfPictures(albumName).forEach(pictureName->{
                        imageContent.put(pictureName,sharedGalleryClientREST.getPictureData(albumName,
                                pictureName));
                    });
                    content.put(albumName,imageContent);
                });

            }else{
                Server server = DiscoveryClient.getWebServiceServer(ip);
                SharedGalleryClientSOAP sharedGalleryClientSOAP = new SharedGalleryClientSOAP(server);

                sharedGalleryClientSOAP.getListOfAlbums().forEach(albumName->{

                    HashMap<String,byte[]> imageContent = new HashMap<>();

                    sharedGalleryClientSOAP.getListOfPictures(albumName).forEach(pictureName->{

                        imageContent.put(pictureName,sharedGalleryClientSOAP.getPictureData(albumName,
                                pictureName));
                    });
                    content.put(albumName,imageContent);
                });

            }
        }).start();
    }

    public void addServer(String newIp,String type){
        serverIps.put(newIp,type);
        if (first){
            first = false;
            initReplication(newIp,type);
        }
    }


}
