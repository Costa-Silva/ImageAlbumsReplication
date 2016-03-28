package sd.tp1.server;

import sd.tp1.utils.HostInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Antonio on 28/03/16.
 */
public class ServersUtils {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String MYIDENTIFIER = "OPENBAR";


    public static void startListening(){

        try {
            InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
            MulticastSocket socket = new MulticastSocket(PORT); //IOexception

            socket.joinGroup(address);

            System.out.println("Listening on "+MULTICASTIP+":"+PORT);

            while (true){

                byte[] buffer = new byte[MAXBYTESBUFFER];

                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);

                socket.receive(datagramPacket);

                HostInfo hostInfo = new HostInfo(datagramPacket.getAddress(),datagramPacket.getPort());


                String mensage = new String(datagramPacket.getData(),datagramPacket.getOffset(),
                        datagramPacket.getLength());


                if (mensage.equals(MYIDENTIFIER) || mensage.equals(InetAddress.getLocalHost().getHostAddress()+":8080")){

                    System.out.println("Sending my info to : "+hostInfo.getAddress()+":"+hostInfo.getPort());

                    String myinfo= InetAddress.getLocalHost().getHostAddress()+":8080" ;
                    buffer = myinfo.getBytes();

                    datagramPacket = new DatagramPacket(buffer,buffer.length);

                    datagramPacket.setAddress(hostInfo.getAddress());
                    datagramPacket.setPort(hostInfo.getPort());
                    socket.send(datagramPacket);

                }



            }



        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
