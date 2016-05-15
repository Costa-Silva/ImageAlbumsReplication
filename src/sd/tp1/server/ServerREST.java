package sd.tp1.server;

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
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


    private static final String TYPE = "REST";
    private static final File KEYSTONE  = new File("./server.jks");
    private static OAuth20Service service;
    private static OAuth2AccessToken accessToken;

    private static final String APIKEY = "2ad0de2edda68b0";

    private static final String APISECRET = "18737726e52ee67e856c21cd7eac98e194cf0d26";

    public static void main(String[] args) throws Exception {

        //Initialize varibles
        String jkspass = "";
        String keypass = "";
        String srvpass = "";
        Scanner in = new Scanner(System.in);

        boolean success=false;
        int port = 8085;

        ResourceConfig config = new ResourceConfig();

        System.out.println("Set a server password");
        srvpass=in.nextLine();
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

        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        if (n>0){
            AlbumsResource albumsResource = new AlbumsResource(srvpass);
            config.register(albumsResource);
        }else{
            openConnection(in);
            AlbumsProxyResource albumsProxyResource = new AlbumsProxyResource(service,accessToken,srvpass);
            config.register(albumsProxyResource);
        }
        in.close();
        while (!success){
            try{
                URI baseUri = UriBuilder.fromUri("https://0.0.0.0/").port(port).build();
                HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config,sslContext);
                System.out.println("URI ATRIBUIDO: " + baseUri);
                success=true;
            }catch (ProcessingException e){
                port++;
            }
        }
        System.err.println("SSL REST Server ready... ");
        ServersUtils.startListening(TYPE,port);
    }


    private static void openConnection(Scanner in){
        service = new ServiceBuilder().apiKey(APIKEY).apiSecret(APISECRET)
                .build(ImgurApi.instance());

        // Obtain the Authorization URL
        System.out.println("A obter o Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Necessario dar permissao neste URL:");
        System.out.println(authorizationUrl);
        System.out.println("e copiar o codigo obtido para aqui:");
        System.out.print(">>");

        String code = in.nextLine();

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("A obter o Access Token!");
        accessToken = service.getAccessToken(code);
    }

}
