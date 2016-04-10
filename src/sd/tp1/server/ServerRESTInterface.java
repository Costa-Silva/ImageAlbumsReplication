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
    @Path("/serverBytes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace();

    /**
     * Returns the list of albums in the server
     * @return Response that contains list of albums via JSON
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList();

    /**
     * Returns the list of pictures for the given album.
     * @param albumName Album name
     * @return Response that contains list of pictures via JSON
     */
    @GET
    @Path("/{albumName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName")String albumName);

    /**
     * Returns the contents of picture in album
     * @param albumName Album name
     * @param pictureName Picture name
     * @return Response that contains picture content via octet stream
     */
    @GET
    @Path("/{albumName}/{picture}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName);

    /**
     * Create a new album
     * @param albumName Album name
     * @return Response that contains name of the album created via JSON
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName);

    /**
     * Add new picture to an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @param pictureData Picture data via octed stream
     * @return Response is 200 if upload was successefull, 404 otherwise
     */
    @POST
    @Path("/{albumName}/{pictureName}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData);

    /**
     * Delete an album
     * @param albumName Album name
     * @return Response is 200 if album is deleted, 404 otherwise
     */
    @DELETE
    @Path("/{albumName}")
    public Response deleteAlbum(@PathParam("albumName") String albumName);

    /**
     * Delete a picture from an album
     * @param albumName Album name
     * @param pictureName Picture name
     * @return Response is 200 if picture deleted,404 otherwise
     */
    @DELETE
    @Path("/{albumName}/{pictureName}")
    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName);
}
