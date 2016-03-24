package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class UploadPicture {

    public static boolean uploadPicture(Server server, byte[] pictureData, String albumName, String pictureName){
        try{

            return server.uploadPicture(albumName,pictureName,pictureData);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return false;

    }
}
