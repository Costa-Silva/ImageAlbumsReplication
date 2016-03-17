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
        if (args.length != 2) {
            System.out.println("Use: java GetFileInfo server_endpoint path");
            System.exit(0);
        }
        String serverHost = args[0];
        String path = args[1];

        try {
            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));

            ServerService service = new ServerService(wsURL);
            // FileServerImplWSService service = new FileServerImplWSService();
            // A invocação sem parâmetros aponta para a instância usada na
            // criação dos stubs através
            // da ferrament wsimport

            Server server = service.getServerPort();
            FileInfo info = server.getFileInfo(path);
            System.out.println("Name : " + info.getName() + "\nLength: " + info.getLength() + "\nDate modified: "
                    + info.getModified() + "\nisFile : " + info.isIsFile());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }



        //search4Servers();
    }


    public static void search4Servers(){

        try {
            InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
            MulticastSocket socket = new MulticastSocket(); //IOexception

            byte[] input = new String(SVIDENTIFIER).getBytes();

            DatagramPacket datagramPacket = new DatagramPacket(input,input.length);

            datagramPacket.setAddress(address);
            datagramPacket.setPort(PORT);

            socket.send(datagramPacket);

            System.out.println("Sent");

            byte[] buffer = new byte[MAXBYTESBUFFER];

            datagramPacket = new DatagramPacket(buffer,buffer.length);

            socket.receive(datagramPacket);


            String mensage = new String(datagramPacket.getData(),datagramPacket.getOffset(),
                    datagramPacket.getLength());

            System.out.println(mensage);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}