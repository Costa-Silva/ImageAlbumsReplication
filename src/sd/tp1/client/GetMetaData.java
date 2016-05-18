package sd.tp1.client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sd.tp1.client.ws.Server;


/**
 * Created by Antonio on 16/05/16.
 */
public class GetMetaData {


    public static byte[] getMetaData(Server server){
        try{
            JSONParser parser = new JSONParser();
          // return server.getMetaData();

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return null;
    }

}
