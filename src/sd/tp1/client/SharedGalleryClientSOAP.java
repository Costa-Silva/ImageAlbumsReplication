package sd.tp1.client;

import sd.tp1.client.ws.*;


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
    public boolean deleteAlbum(String albumName) {
        return DeleteAlbum.deleteAlbum(server,albumName);
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
    public byte[] getMetaData() {
        return sd.tp1.client.GetMetaData.getMetaData(server);
    }

    @Override
    public boolean checkAndAddSharedBy(String ip, String objectId) {
        return CheckAndAddSharedBy.checkAndAddSharedBy(server,ip,objectId);
    }

    @Override
    public boolean askForContent(String objctedId, String fullIp, String operation) {
        return UpdateContent.askForContent(server,objctedId,fullIp,operation);
    }

    @Override
    public String getExtension(String albumname, String pictureName) {
        return null;
    }

    @Override
    public boolean hasAlbum(String albumName) {
        return HasAlbum.hasAlbum(server,albumName);
    }
}
