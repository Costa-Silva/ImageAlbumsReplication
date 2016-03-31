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

    public static Long getServerSize(WebTarget target){

        long size = target.path(PATH + "serverBytes")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Long.class);

        return size;
    }

}
