package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;
import sd.tp1.gui.GalleryContentProvider;

import java.net.URL;
import java.util.List;

/**
 * Created by paulo on 19/03/2016.
 */
public class CreateAlbum {


        public static String createAlbum(Server server, String album){
            try{

                return server.createAlbum(album);

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
            return null;
        }




}
