package sd.tp1.server;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ServerREST {

    public static void main(String[] args) throws Exception {

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(9090).build();

        ResourceConfig config = new ResourceConfig();

        config.register(AlbumsResource.class);

        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST Server ready... ");
    }



}
