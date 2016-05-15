package sd.tp1.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Antonio on 15/05/16.
 */
public class ReplicationServer {

    Map<String,String> serverIps;

    public ReplicationServer(){
        serverIps = new HashMap<>();
    }

    public void initReplication(){

        new Thread(()->{


        });

    }

    public void addServer(String newIp){
        serverIps.put(newIp,newIp);
        System.out.println(serverIps.toString());
    }


}
