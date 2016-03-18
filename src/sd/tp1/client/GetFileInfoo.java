package sd.tp1.client;

import sd.tp1.client.ws.FileInfo;
import sd.tp1.client.ws.GetFileInfo;
import sd.tp1.client.ws.Server;
import sd.tp1.client.ws.ServerService;

import java.net.URL;

/**
 * Created by AntónioSilva on 17/03/2016.
 */
public class GetFileInfoo {


    public static void getInfoFile(String path, String serverHost){
        try{
            URL wsURL = new URL(String.format("http://%s/FileServer", serverHost));

            System.out.println(wsURL.toString());

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
    }

}
