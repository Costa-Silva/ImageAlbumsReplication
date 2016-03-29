package sd.tp1.client;

import sd.tp1.client.ws.Server;

import java.util.List;

/**
 * Created by Antonio on 29/03/16.
 */
public class ServerSize {

    public static long getServerSize(Server server){
        try{

            return server.getserverSpace();

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return Integer.MAX_VALUE;
    }

}
