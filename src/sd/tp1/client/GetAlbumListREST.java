package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class GetAlbumListREST {


    public static List<String> getAlbumList(WebTarget target){

        List<String> list = target.path("/albums")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(ArrayList.class);

        return list;
    }



}
