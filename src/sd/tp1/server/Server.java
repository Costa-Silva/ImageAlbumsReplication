package sd.tp1.server;

import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.utils.HostInfo;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.xml.ws.Endpoint;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
@WebService
public class Server {
    public static final String TYPE = "WS";
    public static final String MAINPATH = "."+File.separator+"src"+File.separator;

    private File mainDirectory;

    public Server(){
        this(".");
    }

    protected Server(String pathname){
        super();
        this.mainDirectory= new File(MAINPATH);

    }


    @WebMethod
    public List<String> getAlbumList(){

        if (mainDirectory.isDirectory()) {

            List<String> albumList = new ArrayList<>();

            File[] files = mainDirectory.listFiles();

            for (File file: files) {
                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && file.isDirectory() ){
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

            File album = new File(mainDirectory.getAbsolutePath()+File.separator+albumName);

            if (album.exists()){

                File albumDir = new File(album.getAbsolutePath());

                File[] files = albumDir.listFiles();

                for (File file: files) {

                    if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && !file.isDirectory() ){
                        list.add(file.getName());

                    }


                }

            }

            return list;
        }
        return null;
    }

    @WebMethod
    public boolean uploadPicture(String albumName,String pictureName, byte[] pictureData){

        if (mainDirectory.isDirectory()){

            File album = new File(mainDirectory.getAbsolutePath()+File.separator+albumName);

            if (album.exists()) {



                File newPicture = new File(album.getAbsoluteFile()+ File.separator + pictureName);


                try {
                    Files.write(newPicture.toPath(),pictureData,StandardOpenOption.CREATE_NEW);
                    return newPicture.exists();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @WebMethod
    public boolean deletePicture(String albumName,String pictureName){

        if (mainDirectory.isDirectory() )  {

            File album = new File(mainDirectory.getAbsolutePath()+File.separator+albumName);

            if (album.exists()) {

                File picture = new File(album.getAbsolutePath()+File.separator+pictureName);


                File delpicture = new File(picture.getAbsolutePath().concat(".deleted"));

                System.out.println(delpicture.getAbsolutePath());


                            boolean success = picture.renameTo(delpicture);

                            return success;
            }
        }
        return true;
    }



    @WebMethod
    public byte[] getPictureData(String albumName,String picture) {
        byte[] array;
        if (mainDirectory.isDirectory()) {

            File album = new File(mainDirectory.getAbsolutePath()+File.separator+albumName);

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
                        }
                    }
                }
            }
        }

        return null;
    }

    @WebMethod
    public String createAlbum(String name){

        File album = new File(mainDirectory.getAbsolutePath()+File.separator+name);
        if(!album.exists()){
            album.mkdir();
            return album.getName();
        }
        return null;
    }

    @WebMethod
    public void deleteAlbum(String name){

        File album = new File(mainDirectory.getAbsolutePath()+File.separator+name);
        if(album.isDirectory()){
            File delAlbum = new File(album.getAbsolutePath().concat(".deleted"));
            album.renameTo(delAlbum);
        }
    }
    @WebMethod
    public long getserverSpace() {

        return mainDirectory.length();

    }


    public static void main(String args[]){

        String path = args.length > 0 ? args[0] : ".";
        Endpoint.publish("http://0.0.0.0:8080/FileServer", new Server(path));
        System.err.println("FileServer started");


        ServersUtils.startListening(TYPE);
    }



}
