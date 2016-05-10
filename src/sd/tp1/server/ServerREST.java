package sd.tp1.server;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.lang.String;
import sd.tp1.utils.HostInfo;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ServerREST {


    public static final String TYPE = "REST";
    public static final File KEYSTONE  = new File("./server.jks");


    public static void main(String[] args) throws Exception {

        boolean success=false;
        int port = 8080;

        ResourceConfig config = new ResourceConfig();

        config.register(AlbumsResource.class);




        while (!success){

            try{

                //Initialize varibles
                String jkspass = "";
                String keypass = "";
                String srvpass = "";
                Scanner in = new Scanner(System.in);

                URI baseUri = UriBuilder.fromUri("https://0.0.0.0/").port(port).build();
                System.out.println(baseUri);

                System.out.println("JKS pass?");
                jkspass=in.nextLine();
                System.out.println("KEY pass?");
                keypass=in.nextLine();

                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                KeyStore keyStore = KeyStore.getInstance("JKS");

                try(InputStream is = new FileInputStream(KEYSTONE)){

                    keyStore.load(is,jkspass.toCharArray());
                }

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                kmf.init(keyStore,keypass.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                sslContext.init(kmf.getKeyManagers(),tmf.getTrustManagers(),new SecureRandom());

                System.out.println("Set a server password");
                srvpass=in.nextLine();
                in.close();
                HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config,sslContext);
                success=true;


            }catch (ProcessingException e){
                port++;
            }



        }



        System.err.println("SSL REST Server ready... ");


        ServersUtils.startListening(TYPE);
    }

}
