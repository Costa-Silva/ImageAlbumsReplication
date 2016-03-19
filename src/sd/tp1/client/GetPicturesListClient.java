package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;
import java.util.List;

/**
 * Created by AntónioSilva on 18/03/2016.
 */
public class GetPicturesListClient {


    public static List<String> getPictures(Server server,String serverHost,String albumName){
        try{

            return server.getPicturesList(albumName);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return null;
    }
}
