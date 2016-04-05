package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 24/03/2016.
 */
public class DeleteAlbumREST {
    private static int OK = 200;
    private static String PATH = "/albums/";
    public static boolean deleteAlbum(WebTarget target, String albumName) {
       Response response= target.path(PATH+albumName).request().delete();


        if (response.getStatus()==OK){
            return true;
        }
        return false;
    }
}
