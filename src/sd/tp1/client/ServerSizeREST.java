package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 29/03/16.
 */
public class ServerSizeREST {

    private static String PATH = "/albums/";
    private static String KEY = "key/";

    public static Long getServerSize(WebTarget target,String password){

        long size = target.path(PATH + "serverBytes"+"/"+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Long.class);

        return size;
    }

}
