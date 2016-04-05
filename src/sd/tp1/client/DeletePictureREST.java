package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 24/03/2016.
 */
public class DeletePictureREST {


    private static String PATH = "/albums/";
    private static int OK = 200;
    public static boolean deletePicture(WebTarget target, String albumName,String pictureName) {

       Response response = target.path(PATH+albumName+"/"+pictureName)
                .request()
                .delete();

        if (response.getStatus()==OK){
            return true;
        }
        return false;
    }
}
