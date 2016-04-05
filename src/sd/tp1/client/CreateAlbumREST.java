package sd.tp1.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 23/03/2016.
 */
public class CreateAlbumREST {

    private static String PATH = "/albums/";
    private static int OK = 200;
    public static String createAlbum(WebTarget target,String albumName){

        Response response = target.path(PATH+albumName)
                .request()
                .post(Entity.entity(albumName,MediaType.APPLICATION_JSON));

       if (response.getStatus()==OK){
           return albumName;
       }
        return null;
    }

}
