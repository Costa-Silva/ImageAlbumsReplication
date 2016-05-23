package sd.tp1.client;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by AntónioSilva on 05/04/2016.
 */
public interface SharedGalleryClient {

    List<String> getListOfAlbums();
    List<String> getListOfPictures(String albumName);
    byte[] getPictureData(String albumName,String pictureName);
    String createAlbum(String name);
    void deleteAlbum(String albumName);
    boolean uploadPicture(String albumName, String pictureName, byte[] data);
    boolean deletePicture(String albumName,String pictureName);
    long getServerSize();
    String getType();
    byte[] getMetaData();
    boolean checkAndAddSharedBy(String ip,String objectId);
    boolean askForContent(String objctedId, String fullIp,String operation);
}
