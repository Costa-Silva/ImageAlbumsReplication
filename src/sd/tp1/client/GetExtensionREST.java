package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


/**
 * Created by Antonio on 24/05/16.
 */
public class GetExtensionREST {
    private static String PATH = "/albums/extension/";
    private static String KEY = "key/";

    public static String getExtension(WebTarget target,String albumName, String pictureName, String password ){

       String extension = target.path(PATH+albumName+"/"+pictureName+"/"+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);

        return extension;
    }
}
