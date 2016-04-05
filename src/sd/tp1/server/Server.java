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
        return ServersUtils.getAlbumList();
    }

    @WebMethod
    public List<String> getPicturesList(String albumName){

        return ServersUtils.getPicturesList(albumName);
    }

    @WebMethod
    public boolean uploadPicture(String albumName,String pictureName, byte[] pictureData){

        return ServersUtils.uploadPicture(albumName,pictureName,pictureData);
    }

    @WebMethod
    public boolean deletePicture(String albumName,String pictureName){

        return ServersUtils.deletePicture(albumName,pictureName);
    }

    @WebMethod
    public byte[] getPictureData(String albumName,String picture) {

        return ServersUtils.getPictureData(albumName,picture);
    }

    @WebMethod
    public String createAlbum(String albumName){

        return ServersUtils.createAlbum(albumName);
    }

    @WebMethod
    public boolean deleteAlbum(String name){

        return ServersUtils.deleteAlbum(name);
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
