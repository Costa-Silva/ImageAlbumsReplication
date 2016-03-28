package sd.tp1.server;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd.tp1.utils.HostInfo;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ServerREST {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String MYIDENTIFIER = "OPENBAR";


    public static void main(String[] args) throws Exception {

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(8080).build();

        ResourceConfig config = new ResourceConfig();

        config.register(AlbumsResource.class);

        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST Server ready... ");


        ServersUtils.startListening();
    }

}
