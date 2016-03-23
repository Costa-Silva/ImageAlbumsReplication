package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 23/03/2016.
 */
public class GetPictureDataREST {

    private static String PATH = "/albums/";

    public static byte[] getPictureData(WebTarget target,String album, String pictureName){

       System.out.println(PATH+album+"/"+pictureName);
       byte[] data = target.path(PATH+album+"/"+pictureName)
                .request()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(byte[].class);

        return data;
    }

}
