package sd.tp1.client;

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
    String uploadPicture(String albumName, String pictureName, byte[] data);
    boolean deletePicture(String albumName,String pictureName);

}
