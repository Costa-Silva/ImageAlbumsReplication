package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * Created by paulo on 24/05/2016.
 */
public class HasAlbumREST {

    private static String PATH = "/albums/";
    private static String KEY = "key/";

    public static boolean hasAlbum(WebTarget target,String albumName,String password){
        return target.path(PATH+"hasAlbum/"+albumName+"/"+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Boolean.class);
    }
}
