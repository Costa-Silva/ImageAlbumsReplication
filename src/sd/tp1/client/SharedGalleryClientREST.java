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
    private static final String TYPE= "REST";
    WebTarget target;

    public SharedGalleryClientREST(WebTarget target){
        this.target=target;
    }


    @Override
    public List<String> getListOfAlbums() {
        return GetAlbumListREST.getAlbumList(target);
    }

    @Override
    public List<String> getListOfPictures(String albumName) {
        return GetPicturesListREST.getPicturesList(target,albumName);
    }

    @Override
    public byte[] getPictureData(String albumName, String pictureName) {
        return GetPictureDataREST.getPictureData(target,albumName,pictureName);
    }

    @Override
    public String createAlbum(String name) {
        return CreateAlbumREST.createAlbum(target,name);
    }

    @Override
    public void deleteAlbum(String albumName) {
        DeleteAlbumREST.deleteAlbum(target,albumName);
    }

    @Override
    public boolean uploadPicture(String albumName, String pictureName, byte[] data) {
        return UploadPictureREST.uploadPicture(target,albumName,pictureName,data);
    }

    @Override
    public boolean deletePicture(String albumName, String pictureName) {
        return DeletePictureREST.deletePicture(target,albumName,pictureName);
    }

    @Override
    public long getServerSize() {
        return ServerSizeREST.getServerSize(target);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
