package sd.tp1.client;


import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;
import sd.tp1.gui.GalleryContentProvider;


import java.net.URL;

import java.util.List;

/**
 * Created by AntónioSilva on 18/03/2016.
 */
public class GetAlbumList {



    public static List<String> getAlbums(String serverHost){
        try{

            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));


            ServerService service = new ServerService(wsURL);

            Server server = service.getServerPort();





            return server.getAlbumList();

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return null;
    }


}
