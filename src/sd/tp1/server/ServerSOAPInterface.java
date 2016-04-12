package sd.tp1.server;

import javax.jws.WebMethod;
import java.util.List;

/**
 * Created by paulo on 10/04/2016.
 */
public interface ServerSOAPInterface {

    /**
     * Returns the list of albums in the server
     * @return List of Albums
     */
    public List<String> getAlbumList();

    /**
     * Returns the list of pictures for the given album.
     * @param albumName Album that contains the pictures
     * @return List of pictures
     */
    public List<String> getPicturesList(String albumName);

    /**
     * Add new picture to an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @param pictureData Picture data
     * @return  <code>true</code>if added, <code>false</code> otherwise
     */
    public boolean uploadPicture(String albumName,String pictureName, byte[] pictureData);

    /**
     * Delete a picture from an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @return <code>true</code> if deleted, <code>false</code> otherwise
     */
    public boolean deletePicture(String albumName,String pictureName);

    /**
     * Returns the contents of picture in album
     * @param albumName Album name
     * @param picture Picture name
     * @return  Contents of picture
     */
    public byte[] getPictureData(String albumName,String picture);

    /**
     * Create a new album
     * @param albumName Album name
     * @return Album name
     */
    public String createAlbum(String albumName);

    /**
     * Delete an album
     * @param name  Album name
     * @return <code>true</code> if deleted, <code>false</code>otherwise
     */
    public boolean deleteAlbum(String name);

    /**
     * Returns the occupied space
     * @return Occupied space
     */
    public long getserverSpace();
}