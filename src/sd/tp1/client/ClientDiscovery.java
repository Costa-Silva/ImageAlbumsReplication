package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;
import sd.tp1.gui.GalleryContentProvider;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AntónioSilva on 17/03/2016.
 */
public class ClientDiscovery {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;

    public static final String SVIDENTIFIER = "OPENBAR";

    private DatagramPacket datagramPacket;
    private MulticastSocket socket;
    private InetAddress address;
    private Map<String,Server> serverHashMap;


    public ClientDiscovery() {
        init();
    }

    public void init(){

        serverHashMap = new ConcurrentHashMap<>();
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
                socket.setSoTimeout(7000);
                while (true) {

                    sendMulticast();

                    hasTime = true;

                    while(hasTime){


                        try {
                            byte[] buffer = new byte[MAXBYTESBUFFER];

                            datagramPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(datagramPacket);

                            String newServerHost = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                    datagramPacket.getLength());

                            if (serverHashMap.get(newServerHost) == null) {
                                System.out.println("Got new response from server : " + newServerHost);
                                serverHashMap.put(newServerHost, getServer(newServerHost));

                            }
                        }catch (SocketTimeoutException e){
                            hasTime=false;
                            System.out.println("No connections");
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }




    public Map<String,Server> getServers(){

        return serverHashMap;
    }

    public static Server getServer(String serverHost){


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