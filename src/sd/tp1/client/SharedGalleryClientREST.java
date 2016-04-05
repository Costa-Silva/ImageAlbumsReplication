package sd.tp1.client;

import sd.tp1.gui.GalleryContentProvider;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AntónioSilva on 05/04/2016.
 */
public class SharedGalleryClientREST implements SharedGalleryClient {

    WebTarget target;

    public SharedGalleryClientREST(WebTarget target){
        this.target=target;
    }


    @Override
    public List<String> getListOfAlbums() {
        List<String> list = new ArrayList<>();


            List<String> listReceived = GetAlbumListREST.getAlbumList(target);
            if(listReceived!=null) {
                for (String album : listReceived) {
                    list.add(album);
                }
            }

            return list;
    }

    @Override
    public List<String> getListOfPictures(String albumName) {
        return null;
    }

    @Override
    public byte[] getPictureData(String albumName, String pictureName) {
        return new byte[0];
    }

    @Override
    public String createAlbum(String name) {
        return null;
    }

    @Override
    public void deleteAlbum(String albumName) {

    }

    @Override
    public String uploadPicture(String albumName, String pictureName, byte[] data) {
        return null;
    }

    @Override
    public boolean deletePicture(String albumName, String pictureName) {
        return false;
    }
}
