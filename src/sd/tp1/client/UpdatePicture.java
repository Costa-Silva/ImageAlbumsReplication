package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
public class UpdatePicture {

    public static boolean updatePicture(Server server, byte[] pictureData, String albumName, String pictureName){
        try{

            return server.updatePicture(albumName,pictureName,pictureData);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return false;

    }
}
