package sd.tp1.server;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd.tp1.utils.HostInfo;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ServerREST {


    public static final String TYPE = "REST";

    public static void main(String[] args) throws Exception {

        boolean success=false;
        int port = 8080;

        ResourceConfig config = new ResourceConfig();

        config.register(AlbumsResource.class);




        while (!success){

            try{
                URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
                System.out.println(baseUri);
                HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
                success=true;
            }catch (ProcessingException e){
                port++;
            }



        }



        System.err.println("REST Server ready... ");


        ServersUtils.startListening(TYPE);
    }

}
