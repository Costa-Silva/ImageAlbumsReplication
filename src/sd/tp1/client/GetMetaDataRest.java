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


    public static byte[] getMetaData(WebTarget target, String password){

        byte[] file = target.path(PATH+KEY+password)
                .request()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(byte[].class);

        return file;
    }
}