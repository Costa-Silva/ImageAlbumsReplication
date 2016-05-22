package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 21/05/2016.
 */
public class CheckAndAddSharedByREST {

    private static String PATH = "/albums/";
    private static String SHAREDBY="checkAndaddSharedby";
    private static String KEY = "key/";
    private static String SLASH= "/";

    public static boolean checkAndAddSharedBy(WebTarget target, String ip, String objectid, String password ){

        String path = PATH+SHAREDBY+SLASH+ip+SLASH+objectid+SLASH+KEY+password;
        System.out.println("path:"+path);
        Long result = target.path(path)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Long.class);


        return result==1;
    }
}
