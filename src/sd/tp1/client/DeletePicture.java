package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by AntónioSilva on 24/03/2016.
 */
public class DeletePicture {

    public static boolean deletePicture(Server server,String album,String picture){
        try{

            return server.deletePicture(album,picture);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return false;
    }



}
