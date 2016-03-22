package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;
import sd.tp1.gui.GalleryContentProvider;

import java.io.IOException;
import java.net.*;
import java.util.*;

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
    private Map<String,String> servers;
    private InetAddress address;
    public ClientDiscovery() {
        init();
    }

    public void init(){

        servers = new HashMap<>();

    }


    public void sendMulticast() {


        try {
            socket = new MulticastSocket();
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
        public void receiveConnections() {



            new Thread(()->{

                try {

                    while (true) {

                        byte[] buffer = new byte[MAXBYTESBUFFER];

                        datagramPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(datagramPacket);


                        String newServerHost = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                                datagramPacket.getLength());

                        if (servers.get(newServerHost)==null){
                            System.out.println("Got new response from server : "+newServerHost);
                            servers.put(newServerHost,newServerHost);

                        }

                    }

                } catch (Exception e) {}

            }).start();



    }

    public void checkNewConnections(){

        new Thread(()->{

            try {
                while (true) {
                    sendMulticast();
                    receiveConnections();
                    Thread.sleep(6000);
                    socket.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }




    public Map<String,String> getServers(){

        return servers;
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