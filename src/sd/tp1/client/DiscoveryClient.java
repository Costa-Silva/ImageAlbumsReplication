package sd.tp1.client;

import org.glassfish.jersey.client.ClientConfig;
import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private InetAddress address;
    private Map<String,Server> serversWebServicesHashMap;
    private Map<String,WebTarget> serversRESTHashMap;
    private List<String> receivedHost;

    private List<String> recheckhosts;

    public DiscoveryClient() {
        init();
    }

    public void init(){

        serversWebServicesHashMap = new ConcurrentHashMap<>();
        serversRESTHashMap = new ConcurrentHashMap<>();
        recheckhosts = new ArrayList<String>();

        receivedHost = new ArrayList<String>();

        try {
            socket = new MulticastSocket();
        }catch (Exception e){
            System.out.println("Error creating Socket");
        }

    }

    public void sendMulticast() {


        try {
            address = InetAddress.getByName(MULTICASTIP);

            //IOexception

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
                    socket.setSoTimeout(7000);
                    sendMulticast();
                    hasTime = true;

                    while(hasTime){


                        try {
                            byte[] buffer = new byte[MAXBYTESBUFFER];

                            datagramPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(datagramPacket);

                            String newServerResponse = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                    datagramPacket.getLength());
                            String newServerHost = newServerResponse.split("-")[0];

                            if (newServerResponse.contains("REST")){


                                if (serversRESTHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversRESTHashMap.put(newServerHost, getWebTarget(newServerHost));
                                    receivedHost.add(newServerHost);
                                }


                            }else{
                                if (serversWebServicesHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversWebServicesHashMap.put(newServerHost, getWebServiceServer(newServerHost));
                                    receivedHost.add(newServerHost);
                                }
                            }




                        }catch (SocketTimeoutException e){
                            hasTime=false;
                            System.out.println("No connections");
                        }catch (Exception e){
                            System.out.println("Outro erro");
                        }

                    }

                    for (Map.Entry<String,Server> entry : serversWebServicesHashMap.entrySet()){

                        if(!receivedHost.contains(entry.getKey())){
                            reCheck(entry.getKey(),"WS");
                        }
                    }

                    for (Map.Entry<String,WebTarget> entry : serversRESTHashMap.entrySet()){

                        if(!receivedHost.contains(entry.getKey())){
                            reCheck(entry.getKey(),"REST");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }






    public void reCheck(String hostname,String serverType){
        if(!recheckhosts.contains(hostname)) {
            recheckhosts.add(hostname);
            new Thread(() -> {
                boolean go = true;
                while (go) {
                    try {
                        address = InetAddress.getByName(MULTICASTIP);

                        //IOexception

                        byte[] input = hostname.getBytes();

                        DatagramPacket reSendpak = new DatagramPacket(input, input.length);

                        reSendpak.setAddress(address);
                        reSendpak.setPort(PORT);

                        socket.setSoTimeout(5000);
                        //socket.setTimeToLive(10);

                        socket.send(reSendpak);

                        System.out.println("Sent a recheck multicast with input " + hostname);


                        byte[] buffer = new byte[MAXBYTESBUFFER];

                        reSendpak = new DatagramPacket(buffer, buffer.length);

                        socket.receive(reSendpak);

                        String newServerHost = new String(reSendpak.getData(), reSendpak.getOffset(),
                                reSendpak.getLength());

                        if (newServerHost.equals(hostname)) {

                            System.out.println("Received a recheck of " + newServerHost);

                            if (serverType.equals("REST")) {

                                if (serversRESTHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversRESTHashMap.put(newServerHost, getWebTarget(newServerHost));
                                    recheckhosts.remove(hostname);
                                    go = false;
                                } else {
                                    throw new SocketTimeoutException();
                                }
                            }else{
                                if (serversWebServicesHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversWebServicesHashMap.put(newServerHost, getWebServiceServer(newServerHost));
                                    recheckhosts.remove(hostname);
                                    go = false;
                                } else {
                                    throw new SocketTimeoutException();
                                }
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        if (serverType.equals("REST")) {
                            serversRESTHashMap.remove(hostname);
                        }else{
                            serversWebServicesHashMap.remove(hostname);
                        }

                        System.out.println("host not found" + hostname);
                    } catch (Exception e) {
                        System.out.println("Erro no resocket");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    public Map<String,WebTarget> getRESTServers(){

        return serversRESTHashMap;
    }

    public Map<String,Server> getWebServicesServers(){

        return serversWebServicesHashMap;
    }

    public static WebTarget getWebTarget(String serverHost){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(getBaseURI(serverHost));

        return  target;
    }

    private static URI getBaseURI(String serverHost) {
        return UriBuilder.fromUri("http://"+serverHost+"/").build();
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
