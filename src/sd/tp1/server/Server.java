package sd.tp1.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
public class Server {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;

    public static void main(String args[]){
        startListeningServer();
    }

    public static void startListeningServer(){

        try {
            InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
            MulticastSocket socket = new MulticastSocket(PORT); //IOexception

            socket.joinGroup(address);

            System.out.println("Listening on "+MULTICASTIP+":"+PORT);

          //  while (true){

                byte[] buffer = new byte[MAXBYTESBUFFER];

                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);

                socket.receive(datagramPacket);



            String mensage = new String(datagramPacket.getData(),datagramPacket.getOffset(),
                    datagramPacket.getLength());

            System.out.println("Someone Connected\n"+mensage);
            System.out.println("Sending my info");

            //}



        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
