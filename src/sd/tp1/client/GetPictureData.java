package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;

/**
 * Created by paulo on 18/03/2016.
 */
public class GetPictureData {

    public static byte[] getPictureData(Server server,String serverHost,String albumName,String pictureName){
        try{

            return server.getPictureData(albumName,pictureName);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return null;

    }

}
