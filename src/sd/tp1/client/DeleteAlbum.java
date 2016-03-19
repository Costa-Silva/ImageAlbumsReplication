package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;

/**
 * Created by paulo on 19/03/2016.
 */
public class DeleteAlbum {

    public static void deleteAlbum(String serverHost,String album){
        try{

            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));

            ServerService service = new ServerService(wsURL);

            Server server = service.getServerPort();

            System.out.println("Cliente a pedir a eliminacao do album " + album);

            server.deleteAlbum(album);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }

    }
}
