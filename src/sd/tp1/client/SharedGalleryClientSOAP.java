package sd.tp1.client;

import org.json.simple.JSONObject;
import sd.tp1.client.ws.*;
import sd.tp1.client.ws.GetMetaData;


import java.util.List;

/**
 * Created by AntónioSilva on 05/04/2016.
 */
public class SharedGalleryClientSOAP implements SharedGalleryClient {
    private static final String TYPE= "SOAP";
    Server server;

    public SharedGalleryClientSOAP(Server server){
            this.server = server;
    }

    @Override
    public List<String> getListOfAlbums() {
        return GetAlbumList.getAlbums(server);
    }

    @Override
    public List<String> getListOfPictures(String albumName) {
        return GetPicturesList.getPictures(server,albumName);
    }

    @Override
    public byte[] getPictureData(String albumName, String pictureName) {
        return GetPictureData.getPictureData(server,albumName,pictureName);
    }

    @Override
    public String createAlbum(String name) {
        return CreateAlbum.createAlbum(server,name);
    }

    @Override
    public void deleteAlbum(String albumName) {
        DeleteAlbum.deleteAlbum(server,albumName);
    }

    @Override
    public boolean uploadPicture(String albumName, String pictureName, byte[] data) {
        return UploadPicture.uploadPicture(server,data,albumName,pictureName);
    }

    @Override
    public boolean deletePicture(String albumName, String pictureName) {
        return DeletePicture.deletePicture(server,albumName,pictureName);
    }

    @Override
    public long getServerSize() {
        return ServerSize.getServerSize(server);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JSONObject getMetaData() {
        return sd.tp1.client.GetMetaData.getMetaData(server);
    }
}
