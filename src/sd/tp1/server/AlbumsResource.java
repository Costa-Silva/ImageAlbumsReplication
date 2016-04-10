package sd.tp1.server;

import sd.tp1.gui.GalleryContentProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
@Path("/albums")
public class AlbumsResource implements ServerRESTInterface{

    public static final String MAINSOURCE = "."+File.separator+"src"+File.separator;
    File mainDirectory = new File(MAINSOURCE);

    @GET
    @Path("/serverBytes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace() {
        return Response.ok(mainDirectory.length()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList() {
        List<String> list = ServersUtils.getAlbumList();
        if (list!=null){
            return Response.ok(list).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("/{albumName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName) {

        List<String> list = ServersUtils.getPicturesList(albumName);

        if (list!=null) {
            return Response.ok(list).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/{albumName}/{picture}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName){

        byte[] array = ServersUtils.getPictureData(albumName,pictureName);
        if (array!=null && array.length>0){
            return Response.ok(array).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName){

        String response = ServersUtils.createAlbum(albumName);

        if (response!=null){
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }


    @POST
    @Path("/{albumName}/{pictureName}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData){

        if (ServersUtils.uploadPicture(albumName,pictureName,pictureData)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    @DELETE
    @Path("/{albumName}")
    public Response deleteAlbum(@PathParam("albumName") String albumName){

        if (ServersUtils.deleteAlbum(albumName)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @DELETE
    @Path("/{albumName}/{pictureName}")
    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName){

        if (ServersUtils.deletePicture(albumName,pictureName)){
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}





