package sd.tp1.client;

import sd.tp1.gui.GalleryContentProvider;

import java.util.List;

/**
 * Created by AntónioSilva on 05/04/2016.
 */
public interface SharedGalleryClient {

    List<String> getListOfAlbums();
    List<String> getListOfPictures(String albumName);
    byte[] getPictureData(String albumName,String pictureName);
}
