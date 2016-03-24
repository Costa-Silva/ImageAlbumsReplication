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

    public static String createAlbum(WebTarget target,String albumName){

        Response album = target.path("/albums/"+albumName)
                .request()
                .post(Entity.entity(albumName,MediaType.APPLICATION_JSON));

        List<String> list = target.path("/albums")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(ArrayList.class);


        if(list.contains(albumName)) {
            return list.get(list.indexOf(albumName));
        }else return null;
    }

}
