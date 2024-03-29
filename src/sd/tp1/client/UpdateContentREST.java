package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by Antonio on 23/05/16.
 */
public class UpdateContentREST {
    private static String PATH = "/albums/";
    private static String content="askforcontent";
    private static String KEY = "key/";
    private static String SLASH= "/";


    public static boolean askForContent(WebTarget target, String objectid, String ip,String operation, String password ){
        String path = PATH+content+SLASH+objectid+SLASH+ip+SLASH+operation+SLASH+KEY+password;
        boolean result = target.path(path)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Boolean.class);
        return result;
    }
}
