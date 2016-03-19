package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.io.IOException;
import java.net.*;

/**
 * Created by AntónioSilva on 17/03/2016.
 */
public class ClientDiscovery {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;

    public static final String SVIDENTIFIER = "OPENBAR";

    public static String searchServer() {

        InetAddress address = null; //unknownHostException
        try {
            address = InetAddress.getByName(MULTICASTIP);

            MulticastSocket socket = new MulticastSocket(); //IOexception

            byte[] input = new String(SVIDENTIFIER).getBytes();

            DatagramPacket datagramPacket = new DatagramPacket(input, input.length);

            datagramPacket.setAddress(address);
            datagramPacket.setPort(PORT);

            socket.send(datagramPacket);

            System.out.println("Sent Multicast");

            byte[] buffer = new byte[MAXBYTESBUFFER];

            datagramPacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(datagramPacket);


            String serverHost = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                    datagramPacket.getLength());


            return serverHost;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
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