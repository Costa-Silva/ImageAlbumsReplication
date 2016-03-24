package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;

/**
 * Created by paulo on 19/03/2016.
 */
public class DeleteAlbum {

    public static void deleteAlbum(Server server,String album){
        try{

            server.deleteAlbum(album);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }

    }
}
