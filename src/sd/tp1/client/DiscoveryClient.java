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
    private MulticastSocket reSendSocket;
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
                    receivedHost = new ArrayList<String>();
                    while(hasTime){


                        try {
                            byte[] buffer = new byte[MAXBYTESBUFFER];
                            datagramPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(datagramPacket);

                            String newServerResponse = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                    datagramPacket.getLength());
                            String newServerHost = newServerResponse.split("-")[0];
                            receivedHost.add(newServerHost);
                            if (newServerResponse.contains("REST")){


                                if (serversRESTHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversRESTHashMap.put(newServerHost, getWebTarget(newServerHost));

                                }


                            }else{
                                if (serversWebServicesHashMap.get(newServerHost) == null) {
                                    System.out.println("Got new response from server : " + newServerHost);
                                    serversWebServicesHashMap.put(newServerHost, getWebServiceServer(newServerHost));

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
                            System.out.println("hey baby dont hurt me ws");
                            reCheck(entry.getKey(),"WS");
                        }
                    }

                    for (Map.Entry<String,WebTarget> entry : serversRESTHashMap.entrySet()){
                        if(!receivedHost.contains(entry.getKey())){
                            System.out.println("hey baby dont hurt me rest");
                            reCheck(entry.getKey(),"REST");
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

            try {

               // System.out.println("blocking for testing");
                //Thread.sleep(7000);


                reSendSocket.setSoTimeout(3000);

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



                if (newServerHost.split("-")[0].equals(hostname)) {

                    System.out.println("Received a recheck of " + newServerHost);

                }else{
                    throw new SocketTimeoutException();
                }


            } catch (SocketTimeoutException e) {
                if (serverType.equals("REST")) {
                    serversRESTHashMap.remove(hostname);
                }else{
                    serversWebServicesHashMap.remove(hostname);
                }
                System.out.println("Host not found: " + hostname);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        }


/*
    public void reCheck(String hostname,String serverType) throws IOException {
        System.out.println("recheck chamado");
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

                        resendSocket.setSoTimeout(3000);
                        //socket.setTimeToLive(10);

                        resendSocket.send(reSendpak);

                        System.out.println("Sent a recheck multicast with input " + hostname);


                        byte[] buffer = new byte[MAXBYTESBUFFER];

                        reSendpak = new DatagramPacket(buffer, buffer.length);

                        resendSocket.receive(reSendpak);

                       // System.out.println("passou");
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

                        System.out.println("Host not found: " + hostname);
                    } catch (Exception e) {
                        System.out.println("Erro no resocket");
                        e.printStackTrace();
                    }
                }
                go=false;
            }).start();
        }

    }
*/

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
