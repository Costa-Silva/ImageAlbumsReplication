package sd.tp1.client;

import org.glassfish.jersey.client.ClientConfig;
import sd.tp1.client.ws.Server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class ClientDiscoveryREST {
    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;

    public static final String SVIDENTIFIER = "OPENBAR";

    private DatagramPacket datagramPacket;
    private MulticastSocket socket;
    private InetAddress address;
    private Map<String,WebTarget> serverHashMap;
    private Map<String,WebTarget> receivedHost;
    private List<String> recheckhosts;

    public ClientDiscoveryREST() {
        init();
    }

    public void init(){

        serverHashMap = new ConcurrentHashMap<>();
        recheckhosts = new ArrayList<String>();
        try {
            socket = new MulticastSocket();
        }catch (Exception e){
            System.out.println("Error creating Socket");
        }

    }



    public void checkNewConnections(){

        new Thread(()->{

            try {
                boolean hasTime;
                while (true) {
                    socket.setSoTimeout(7000);
                    sendMulticast();
                    receivedHost = new ConcurrentHashMap<String, WebTarget>();
                    hasTime = true;

                    while(hasTime){


                        try {
                            byte[] buffer = new byte[MAXBYTESBUFFER];

                            datagramPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(datagramPacket);

                            String newServerHost = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                    datagramPacket.getLength());
                            System.out.println("Tou a por no receivedhost " + newServerHost);
                            receivedHost.put(newServerHost,getWebTarget(newServerHost));

                            if (serverHashMap.get(newServerHost) == null) {
                                System.out.println("Got new response from server : " + newServerHost);
                                serverHashMap.put(newServerHost, getWebTarget(newServerHost));
                            }
                        }catch (SocketTimeoutException e){
                            hasTime=false;
                            System.out.println("No connections");
                        }catch (Exception e){
                            System.out.println("Outro erro");
                        }

                    }
                    for (Map.Entry<String,WebTarget> entry : serverHashMap.entrySet()){

                        if(!receivedHost.containsKey(entry.getKey())){
                            reCheck(entry.getKey());
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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


    public void reCheck(String hostname){
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

                            if (serverHashMap.get(newServerHost) == null) {
                                System.out.println("Got new response from server : " + newServerHost);
                                serverHashMap.put(newServerHost, getWebTarget(newServerHost));
                                recheckhosts.remove(hostname);
                                go=false;
                            }
                        } else {
                            throw new SocketTimeoutException();
                        }

                    } catch (SocketTimeoutException e) {
                        serverHashMap.remove(hostname);
                        System.out.println("host not found" + hostname);
                    } catch (Exception e) {
                        System.out.println("Erro no resocket");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    public Map<String,WebTarget> getServers(){

        return serverHashMap;
    }




    public static WebTarget getWebTarget(String serverHost){
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(getBaseURI(serverHost));
        System.out.println(target.getUri());

        return  target;
    }

    private static URI getBaseURI(String serverHost) {
        return UriBuilder.fromUri("http://"+serverHost+"/").build();
    }


}
