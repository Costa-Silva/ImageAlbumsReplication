package sd.tp1.server;

import sd.tp1.client.ws.ObjectFactory;

/**
 * Created by paulo on 14/05/2016.
 */
public class ImgurPicture {

    private String id;
    private String picName;
    private String albumId;



    public ImgurPicture(String id, String picName, String albumId){
        this.id=id;
        this.picName=picName;
        this.albumId=albumId;
    }

    public String getId() {
        return id;
    }

    public String getPicName() {
        return picName;
    }

    public String getAlbumId() {
        return albumId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ImgurPicture){
            return ((ImgurPicture) obj).getId().equals(id);
        }
        return false;
    }
}
