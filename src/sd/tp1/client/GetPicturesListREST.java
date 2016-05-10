package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class GetPicturesListREST {

    private static String PATH = "/albums/";
    private static String KEY = "key/";


    public static List<String> getPicturesList(WebTarget target,String albumName,String password){

        List<String> list = target.path(PATH+albumName+"/"+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(ArrayList.class);

        return list;
    }


}
