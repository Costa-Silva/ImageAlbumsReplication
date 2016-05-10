package sd.tp1.client;

import org.glassfish.jersey.client.ClientConfig;
import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AntónioSilva on 28/03/2016.
 */
public class DiscoveryClient {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String SVIDENTIFIER = "OPENBAR";

    private DatagramPacket datagramPacket;
    private MulticastSocket socket;
    private MulticastSocket reSendSocket;
    private InetAddress address;
    private Map<String,SharedGalleryClient> servers;
    private List<String> receivedHost;
    private boolean newHostFound;
    public DiscoveryClient() {
        init();
    }

    public void init(){
        servers = new ConcurrentHashMap<>();

        try {
            socket = new MulticastSocket();
            reSendSocket = new MulticastSocket();
        }catch (Exception e){
            System.out.println("Error creating Socket");
        }

    }

    public void sendMulticast() {


        try {
            address = InetAddress.getByName(MULTICASTIP);

            byte[] input = SVIDENTIFIER.getBytes();

            datagramPacket = new DatagramPacket(input, input.length);

            datagramPacket.setAddress(address);
            datagramPacket.setPort(PORT);

            socket.send(datagramPacket);

            System.out.println("Sent Multicast");


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void checkNewConnections(){

        new Thread(()->{

            try {
                boolean hasTime;
                while (true) {
                    socket.setSoTimeout(2000);
                    sendMulticast();
                    hasTime = true;
                    receivedHost = new ArrayList<>();
                    while(hasTime){


                        try {
                            byte[] buffer = new byte[MAXBYTESBUFFER];
                            datagramPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(datagramPacket);

                            String newServerResponse = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                    datagramPacket.getLength());
                            String newServerHost = newServerResponse.split("-")[0];
                            receivedHost.add(newServerHost);
                            if (servers.get(newServerHost) == null) {
                                System.out.println("Got new response from server : " + newServerHost);
                                if (newServerResponse.contains("REST")) {
                                    System.out.println("Set a password");

                                    Scanner in = new Scanner(System.in);
                                    String password = in.nextLine();
                                    in.close();
                                    SharedGalleryClientREST sharedGalleryClientREST = new SharedGalleryClientREST(getWebTarget(newServerHost),password);
                                    servers.put(newServerHost, sharedGalleryClientREST);
                                } else {
                                    SharedGalleryClientSOAP sharedGalleryClientSOAP = new SharedGalleryClientSOAP(getWebServiceServer(newServerHost));
                                    servers.put(newServerHost, sharedGalleryClientSOAP);

                                }
                                synchronized(this){
                                    newHostFound = true;
                                }
                            }

                        }catch (SocketTimeoutException e){
                            hasTime=false;
                            System.out.println("No connections");
                        }catch (Exception e){
                            System.out.println("Error");
                        }

                    }

                    for (Map.Entry<String,SharedGalleryClient> entry : servers.entrySet()){

                        if(!receivedHost.contains(entry.getKey())){
                            if (servers.remove(entry.getKey()).getType().equals("REST")){
                                reCheck(entry.getKey(),"REST");
                            }else
                                reCheck(entry.getKey(),"WS");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    public void reCheck(String hostname,String serverType) {


        new Thread(() -> {
            boolean handShake=false;
            for (int i=1; i<=3 && !handShake;i++) {
                try {

                    reSendSocket.setSoTimeout(2000);

                    address = InetAddress.getByName( (hostname.split(":")[0])   );
                    byte[] input = hostname.getBytes();
                    datagramPacket = new DatagramPacket(input, input.length);

                    datagramPacket.setAddress(address);
                    datagramPacket.setPort(PORT);

                    reSendSocket.send(datagramPacket);


                    System.out.println("Sent a recheck  with input " + hostname);


                    byte[] buffer = new byte[MAXBYTESBUFFER];

                    datagramPacket = new DatagramPacket(buffer, buffer.length);

                    reSendSocket.receive(datagramPacket);


                    String newServerHost = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                            datagramPacket.getLength());

                    String serverHost = newServerHost.split("-")[0];

                        if (newServerHost.split("-")[0].equals(hostname)) {

                        System.out.println("Received a recheck of " + serverHost);

                        if (serverType.equals("REST")) {
                            Scanner in = new Scanner(System.in);
                            String password = in.nextLine();
                            in.close();
                            SharedGalleryClientREST sharedGalleryClientREST = new SharedGalleryClientREST(getWebTarget(newServerHost),password);
                            servers.put(serverHost,sharedGalleryClientREST);
                        }else{
                            SharedGalleryClientSOAP sharedGalleryClientSOAP = new SharedGalleryClientSOAP(getWebServiceServer(newServerHost));
                            servers.put(serverHost, sharedGalleryClientSOAP);
                        }
                        handShake=true;
                        synchronized(this){
                            newHostFound = true;
                        }

                    }else{
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Host not found: " + hostname);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public synchronized boolean newHostFound(){

     if (newHostFound){
         newHostFound=false;
         return !newHostFound;
     }else
         return newHostFound;
    }

    public Map<String,SharedGalleryClient> getServers(){

        return servers;
    }

    public static WebTarget getWebTarget(String serverHost) {
        WebTarget target = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1");

            TrustManager[] trustAllCerts = {new InsecureClass.InsecureTrustManager()};
            sc.init(null,trustAllCerts,new SecureRandom());

            Client client = ClientBuilder.newBuilder()
                    .hostnameVerifier(new InsecureClass.InsecureHostnameVerifier())
                    .sslContext(sc)
                    .build();

            target = client.target(getBaseURI(serverHost));


        }catch (Exception e){
            System.out.print("Erro no client");
        }
        return  target;
    }

    private static URI getBaseURI(String serverHost) {
        return UriBuilder.fromUri("https://"+serverHost+"/").build();
    }


    public static Server getWebServiceServer(String serverHost){


        try {

            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));
            ServerService service = new ServerService(wsURL);

            return service.getServerPort();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return null;
    }



}
