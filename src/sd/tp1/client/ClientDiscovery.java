package sd.tp1.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

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
}