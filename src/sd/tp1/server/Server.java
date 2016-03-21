package sd.tp1.server;

import sd.tp1.SharedGalleryContentProvider;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.utils.HostInfo;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
@WebService
public class Server {

    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String MYIDENTIFIER = "OPENBAR";

    private File mainDirectory;

    public Server(){
        this(".");
    }

    protected Server(String pathname){
        super();
        this.mainDirectory= new File(pathname);
    }


    @WebMethod
    public List<String> getAlbumList(){

        if (mainDirectory.isDirectory()) {

           List<String> albumList = new ArrayList<>();

            File[] files = mainDirectory.listFiles();

            for (File file: files) {
                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") ){
                    albumList.add(file.getName());

                }

            }

            return  albumList;
        }
        return null;

    }

    @WebMethod
    public List<String> getPicturesList(String albumName){



        if (mainDirectory.isDirectory()) {

            List<String> list = new ArrayList<>();

            File album = new File(albumName);

            if (album.exists()){

                File albumDir = new File(album.getAbsolutePath());

                File[] files = albumDir.listFiles();

                for (File file: files) {

                    if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") ){
                        list.add(file.getName());

                    }


                }

            }

            return list;
        }
        return null;
    }

    @WebMethod
    public byte[] getPictureData(String albumName,String picture) {
        byte[] array;
        if (mainDirectory.isDirectory()) {

            File album = new File(albumName);

            if (album.exists()) {

                File albumDir = new File(album.getAbsolutePath());

                File[] files = albumDir.listFiles();

                for (File file : files) {
                    if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && file.getName().equals(picture)) {

                        try {
                            RandomAccessFile f = new RandomAccessFile(file, "r");
                            array = new byte[(int) f.length()];

                            f.readFully(array);
                            return array;

                        } catch (Exception e) {
                            //System.out.println("DEU ERRO");
                        }
                    }
                }
            }
        }

        return null;
    }

    @WebMethod
    public String createAlbum(String name){
        SharedAlbum sharedAlbum = new SharedAlbum(name);
        File album = new File(sharedAlbum.getName());
        if(!album.exists()){
            album.mkdir();
            return album.getName();
        }
        return null;
    }

    @WebMethod
    public void deleteAlbum(String name){

        File album = new File(name);
        if(album.isDirectory()){
        File delAlbum = new File(album.getName().concat(".deleted"));
        album.renameTo(delAlbum);
        }
    }


    public static void main(String args[]){

        String path = args.length > 0 ? args[0] : ".";
        Endpoint.publish("http://0.0.0.0:8080/FileServer", new Server(path));
        System.err.println("FileServer started");






        startListening();
    }







    private static void startListening(){

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


    /**
     * Represents a shared album.
     */
    static class SharedAlbum implements GalleryContentProvider.Album {
        final String name;

        SharedAlbum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }


}
