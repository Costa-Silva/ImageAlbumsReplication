package sd.tp1.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 24/03/2016.
 */
public class UploadPictureREST {

    private static String PATH = "/albums/";
    private static int OK = 200;
    public static boolean uploadPicture(WebTarget target, String albumName,String pictureName,byte[] pictureData){



        Response response = target.path(PATH+albumName+"/"+pictureName)
                .request()
                .post(Entity.entity(pictureData,MediaType.APPLICATION_OCTET_STREAM));

        return response.getStatus()==OK;
    }
}
