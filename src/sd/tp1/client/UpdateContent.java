package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by Antonio on 23/05/16.
 */
public class UpdateContent {
    public static boolean askForContent(Server server, String objectid, String ip,String operation){
        boolean result = server.askForContent(objectid,ip,operation);
        return result;
    }
}
