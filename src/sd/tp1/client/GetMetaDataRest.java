package sd.tp1.client;

import org.json.simple.JSONObject;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 16/05/16.
 */
public class GetMetaDataRest {

    private static String PATH = "albums/metadata/";
    private static String KEY = "key/";


    public static JSONObject getMetaData(WebTarget target, String password){

        JSONObject file = target.path(PATH+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(JSONObject.class);

        return file;
    }


}
