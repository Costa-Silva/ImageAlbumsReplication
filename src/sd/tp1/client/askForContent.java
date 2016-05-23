package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by Antonio on 23/05/16.
 */
public class AskForContent {

    public static boolean askForContent(Server server, String objectid, String ip){
        boolean result = server.askForContent(objectid,ip);
        return result;
    }

}
