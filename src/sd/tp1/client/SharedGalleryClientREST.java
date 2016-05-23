package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import java.util.List;

/**
 * Created by AntónioSilva on 05/04/2016.
 */
public class SharedGalleryClientREST implements SharedGalleryClient {
    private static final String TYPE= "REST";
    WebTarget target;
    String password;

    public SharedGalleryClientREST(WebTarget target,String password){
        this.target=target;
        this.password=password;
    }


    @Override
    public List<String> getListOfAlbums() {
        return GetAlbumListREST.getAlbumList(target,password);
    }

    @Override
    public List<String> getListOfPictures(String albumName) {
        return GetPicturesListREST.getPicturesList(target,albumName,password);
    }

    @Override
    public byte[] getPictureData(String albumName, String pictureName) {
        return GetPictureDataREST.getPictureData(target,albumName,pictureName,password);
    }

    @Override
    public String createAlbum(String name) {
        return CreateAlbumREST.createAlbum(target,name,password);
    }

    @Override
    public void deleteAlbum(String albumName) {
        DeleteAlbumREST.deleteAlbum(target,albumName,password);
    }

    @Override
    public boolean uploadPicture(String albumName, String pictureName, byte[] data) {
        return UploadPictureREST.uploadPicture(target,albumName,pictureName,data,password);
    }

    @Override
    public boolean deletePicture(String albumName, String pictureName) {
        return DeletePictureREST.deletePicture(target,albumName,pictureName,password);
    }

    @Override
    public long getServerSize() {
        return ServerSizeREST.getServerSize(target,password);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public byte[] getMetaData() {
        return GetMetaDataRest.getMetaData(target,password);
    }

    @Override
    public boolean checkAndAddSharedBy(String ip, String objectId) {
        return CheckAndAddSharedByREST.checkAndAddSharedBy(target,ip,objectId,password);
    }

    @Override
    public boolean askForContent(String objctedId, String fullIp) {
        return UpdateContentREST.askForContent(target,objctedId,fullIp,password);
    }


}
