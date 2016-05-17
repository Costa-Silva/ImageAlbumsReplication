package sd.tp1.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.tp1.utils.HostInfo;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Antonio on 28/03/16.
 */
public class ServersUtils {
    public static final List<String> EXTENSIONS = Arrays.asList(new String[] { "tiff", "gif", "jpg", "jpeg", "png" });
    public static final String MULTICASTIP = "224.0.0.1";
    public static final int PORT = 5555;
    public static final int MAXBYTESBUFFER = 65536;
    public static final String MYIDENTIFIER = "OPENBAR";
    public static final String SERVERSIDENTIFIER = "OPENBARSV";
    public static final String MAINSOURCE = "."+File.separator+"src"+File.separator;
    public static final File mainDirectory = new File(MAINSOURCE);
    public static final String METADATAPATH = "metadata.txt";


    public static void startListening(String serverType,int port){

        try {
            sendingMyInfo(port,serverType);
            ReplicationServer replicationServer = new ReplicationServer();
            InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
            MulticastSocket socket = new MulticastSocket(PORT); //IOexception

            socket.joinGroup(address);

            System.out.println("Listening on "+MULTICASTIP+":"+PORT);

            while (true){

                byte[] buffer = new byte[MAXBYTESBUFFER];
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
                socket.receive(datagramPacket);
                HostInfo hostInfo = new HostInfo(datagramPacket.getAddress(),datagramPacket.getPort());
                String message = new String(datagramPacket.getData(),datagramPacket.getOffset(),
                        datagramPacket.getLength());
                if (message.equals(MYIDENTIFIER) || message.equals(InetAddress.getLocalHost().getHostAddress()+":"+port)){

                    System.out.println("Sending my info to : "+hostInfo.getAddress()+":"+hostInfo.getPort());
                    String myinfo= InetAddress.getLocalHost().getHostAddress()+":"+port+"-"+serverType ;

                    buffer = myinfo.getBytes();
                    datagramPacket = new DatagramPacket(buffer,buffer.length);

                    datagramPacket.setAddress(hostInfo.getAddress());
                    datagramPacket.setPort(hostInfo.getPort());
                    socket.send(datagramPacket);
                }else if (message.contains(SERVERSIDENTIFIER)){
                    String myip= InetAddress.getLocalHost().getHostAddress()+":"+port ;
                    String ip = message.split("-")[1];
                    if (!myip.equals(ip)){
                        String type;

                        if (message.contains("REST")){
                            type= "REST";
                        }else{
                            type="SOAP";
                        }
                        replicationServer.addServer(ip,type);
                    }
                }

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendingMyInfo(int port,String type){
        new Thread(()->{
            try {
                InetAddress address = InetAddress.getByName(MULTICASTIP); //unknownHostException
                MulticastSocket socket = new MulticastSocket(); //IOexception
                socket.joinGroup(address);
                String myinfo= SERVERSIDENTIFIER+"_"+type+"-"+InetAddress.getLocalHost().getHostAddress()+":"+port ;
                byte[] buffer = myinfo.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
                datagramPacket.setAddress(address);
                datagramPacket.setPort(PORT);
                while (true){
                    socket.send(datagramPacket);
                    System.out.println("Sent Multicast");
                    Thread.sleep(3000);
                }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static boolean deletePicture(String albumName,String pictureName) {

        boolean success = false;
        if (correctMainDirectory()) {

            File album = new File(MAINSOURCE + albumName);

            if (album.exists() && album.isDirectory()) {

                System.out.println("album " + album.getAbsolutePath());

                File[] pictures = album.listFiles();

                for (File picture : pictures) {
                    if (picture.getName().equals(pictureName)) {

                        System.out.println("picture " + picture.getAbsolutePath());

                        File pict = new File(album.getAbsolutePath() + File.separator + pictureName.concat(".deleted"));

                        success = picture.renameTo(pict);
                        System.out.println("renamed to " + pict.getAbsolutePath() + "  " + success);
                    }
                }
            }
        }
        return success;
    }




    public static boolean deleteAlbum(String albumName){

        boolean success = false;
        if (correctMainDirectory()){

            File album = new File(MAINSOURCE+albumName);
            if(album.isDirectory() && album.exists()){
                File delAlbum = new File(album.getAbsolutePath().concat(".deleted"));
                success= album.renameTo(delAlbum);

            }
        }
        return success;
    }

    public static String createAlbum(String albumName){

        if (correctMainDirectory()){
            File album = new File(MAINSOURCE+albumName);
            if(!album.exists()){
                album.mkdir();
                return album.getName();
            }
        }
        return null;
    }



    public static boolean uploadPicture(String albumName,String pictureName,byte[] pictureData){

        boolean success=false;
        if (correctMainDirectory()){

            File album = new File(MAINSOURCE+albumName);
            if (album.exists() && album.isDirectory()) {
                File newPicture = new File(album.getAbsolutePath()+ File.separator + pictureName);

                try {
                    Files.write(newPicture.toPath(),pictureData, StandardOpenOption.CREATE_NEW);
                    success = newPicture.exists();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }


    public static byte[] getPictureData(String albumName, String picture){

        if (correctMainDirectory()) {
            File album = new File(MAINSOURCE + albumName);
            byte[] array;
            if (album.exists() && album.isDirectory()) {

                File albumDir = new File(album.getAbsolutePath());
                File[] files = albumDir.listFiles();

                for (File file : files) {
                    if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && file.getName().equals(picture) && checkExtension(file)) {

                        try {
                            RandomAccessFile f = new RandomAccessFile(file, "r");
                            array = new byte[(int) f.length()];

                            f.readFully(array);
                            f.close();
                            return array;

                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<String> getPicturesList(String albumName){

        List<String> list = new ArrayList<>();
        if (correctMainDirectory()){

            File album = new File(MAINSOURCE+albumName);

            if (album.exists() && album.isDirectory()) {

                File albumDir = new File(album.getAbsolutePath());

                File[] files = albumDir.listFiles();

                for (File file : files) {

                    if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && !file.isDirectory() && checkExtension(file)) {
                        list.add(file.getName());
                    }
                }
            }
        }
        return  list;
    }



    public static List<String> getAlbumList(){

        List<String> albums = new ArrayList<>();
        if (correctMainDirectory()){

            File[] files = mainDirectory.listFiles();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && file.isDirectory()  ){

                    albums.add(file.getName());
                }

            }
        }
        return albums;
    }

    private static boolean correctMainDirectory(){

        return mainDirectory.exists() && mainDirectory.isDirectory();
    }

    private static boolean checkExtension(File f){
        String filename = f.getName();
        int i = filename.lastIndexOf('.');
        String ext = i < 0 ? "" : filename.substring(i + 1).toLowerCase();
        return f.isFile() && !filename.startsWith(".") && EXTENSIONS.contains(ext);
    }


    public static JSONObject getMetaData(){
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(METADATAPATH));
        } catch (ParseException |IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
