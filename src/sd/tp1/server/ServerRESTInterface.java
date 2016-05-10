package sd.tp1.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by paulo on 10/04/2016.
 */
public interface ServerRESTInterface {
    /**
     * Returns occupied space
     * @return Occupied space via JSON
     */
    @GET
    @Path("/serverBytes/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace(@PathParam("password")String password);

    /**
     * Returns the list of albums in the server
     * @return Response that contains list of albums via JSON
     */
    @GET
    @Path("/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList(@PathParam("password") String password);

    /**
     * Returns the list of pictures for the given album.
     * @param albumName Album name
     * @return Response that contains list of pictures via JSON
     */
    @GET
    @Path("/{albumName}/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName")String albumName,@PathParam("password") String password);

    /**
     * Returns the contents of picture in album
     * @param albumName Album name
     * @param pictureName Picture name
     * @return Response that contains picture content via octet stream
     */
    @GET
    @Path("/{albumName}/{picture}/key/{password}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName,@PathParam("password") String password);

    /**
     * Create a new album
     * @param albumName Album name
     * @return Response that contains name of the album created via JSON
     */
    @POST
    @Path("/key/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName,@PathParam("password") String password);

    /**
     * Add new picture to an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @param pictureData Picture data via octed stream
     * @return Response is 200 if upload was successefull, 404 otherwise
     */
    @POST
    @Path("/{albumName}/{pictureName}/key/{password}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData,@PathParam("password")String password);

    /**
     * Delete an album
     * @param albumName Album name
     * @return Response is 200 if album is deleted, 404 otherwise
     */
    @DELETE
    @Path("/{albumName}/key/{password}")
    public Response deleteAlbum(@PathParam("albumName") String albumName, @PathParam("password") String password);

    /**
     * Delete a picture from an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @return Response is 200 if picture deleted,404 otherwise
     */
    @DELETE
    @Path("/{albumName}/{pictureName}/key/{password}")
    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName,@PathParam("password")String password);
}
