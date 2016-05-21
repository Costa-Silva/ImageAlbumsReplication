package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by AntónioSilva on 21/05/2016.
 */
public class CheckAndAddSharedBy {

    public static boolean checkAndAddSharedBy(Server server, String ip, String objectid){

        boolean result = server.checkAndAddSharedBy(ip,objectid);

        return result;
    }
}
