package sd.tp1.server;

import sd.tp1.utils.HostInfo;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
@WebService
public class Server {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String MYIDENTIFIER = "OPENBAR";

    private File basePath;

    public Server(){
        this(".");
    }

    protected Server(String pathname){
        super();
        this.basePath= new File(pathname);
    }


    @WebMethod
    public FileInfo getFileInfo(String path) throws InfoNotFoundException {
        File f = new File(basePath, path);
        if (f.exists())
            return new FileInfo(f.getName(), f.length(), new Date(f.lastModified()), f.isFile());
        else
            throw new InfoNotFoundException("File not found :" + path);
    }






    public static void main(String args[]){

        String path = args.length > 0 ? args[0] : ".";
        Endpoint.publish("http://0.0.0.0:8080/FileServer", new Server(path));
        System.err.println("FileServer started");






        //startListeningServer();
    }







    private static void startListeningServer(){

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


            System.out.println("Sending my info to : "+hostInfo.getAddress()+":"+hostInfo.getPort());

            if (mensage.equals(MYIDENTIFIER)){

                buffer = new String(InetAddress.getLocalHost().getHostAddress()).getBytes();

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
