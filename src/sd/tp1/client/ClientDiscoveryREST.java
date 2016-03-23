package sd.tp1.client;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ClientDiscoveryREST {


    public static WebTarget getWebTarget(){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(getBaseURI());
        return  target;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:9090/").build();
    }


}
