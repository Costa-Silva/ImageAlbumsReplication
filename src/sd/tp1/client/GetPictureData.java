package sd.tp1.client;

import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;

/**
 * Created by paulo on 18/03/2016.
 */
public class GetPictureData {

    public static byte[] getPictureData(String serverHost,String albumName,String pictureName){
        try{
            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));

            System.out.println("GET PICTURE DATA de " + pictureName + " em " + albumName);

            ServerService service = new ServerService(wsURL);
            // FileServerImplWSService service = new FileServerImplWSService();
            // A invocação sem parâmetros aponta para a instância usada na
            // criação dos stubs através
            // da ferrament wsimport

            Server server = service.getServerPort();

            return server.getPictureData(albumName,pictureName);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return null;

    }

}
