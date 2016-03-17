package sd.tp1.client;

import sd.tp1.client.ws.FileInfo;
import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.io.IOException;
import java.net.*;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
public class Client {


    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;

    public static final String SVIDENTIFIER = "OPENBAR";



    public static void main(String args[]) {
        if (args.length == 1 ) {

        String path = args[0];


            try {
                InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
                MulticastSocket socket = new MulticastSocket(); //IOexception

                byte[] input = new String(SVIDENTIFIER).getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(input,input.length);

                datagramPacket.setAddress(address);
                datagramPacket.setPort(PORT);

                socket.send(datagramPacket);

                System.out.println("Sent Multicast");

                byte[] buffer = new byte[MAXBYTESBUFFER];

                datagramPacket = new DatagramPacket(buffer,buffer.length);

                socket.receive(datagramPacket);



                String mensage = new String(datagramPacket.getData(),datagramPacket.getOffset(),
                        datagramPacket.getLength());


                GetFileInfoo.getInfoFile(path,mensage);


        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }


        }else {

            System.out.println("Use: java GetFileInfo path");
            System.exit(0);
        }
    }









}