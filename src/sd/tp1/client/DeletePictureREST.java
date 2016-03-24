package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 24/03/2016.
 */
public class DeletePictureREST {


    private static String PATH = "/albums/";

    public static boolean deletePicture(WebTarget target, String albumName,String pictureName) {
        target.path("/albums/"+albumName+"/"+pictureName)
                .request()
                .delete();

        List<String> list = target.path(PATH+albumName)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(ArrayList.class);

        return !list.contains(pictureName);
    }
}
