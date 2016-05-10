package sd.tp1.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
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
    @Path("/serverBytes/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace(@PathParam("password") String password) {

        return Response.ok(mainDirectory.length()).build();
    }

    @GET
    @Path("/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList(@PathParam("password") String password) {
        List<String> list = ServersUtils.getAlbumList();
        if (list!=null){
            return Response.ok(list).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("/{albumName}/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName, @PathParam("password") String password) {

        List<String> list = ServersUtils.getPicturesList(albumName);

        if (list!=null) {
            return Response.ok(list).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/{albumName}/{picture}/key/{password}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName,@PathParam("password") String password){

        byte[] array = ServersUtils.getPictureData(albumName,pictureName);
        if (array!=null && array.length>0){
            return Response.ok(array).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @POST
    @Path("/key/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName,@PathParam("password") String password){

        String response = ServersUtils.createAlbum(albumName);

        if (response!=null){
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }


    @POST
    @Path("/{albumName}/{pictureName}/key/{password}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData,@PathParam("password")String password){

        if (ServersUtils.uploadPicture(albumName,pictureName,pictureData)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    @DELETE
    @Path("/{albumName}/key/{password}")
    public Response deleteAlbum(@PathParam("albumName") String albumName,@PathParam("password") String password){

        if (ServersUtils.deleteAlbum(albumName)){
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @DELETE
    @Path("/{albumName}/{pictureName}/key/{password}")

    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName,@PathParam("password")String password){

        if (ServersUtils.deletePicture(albumName,pictureName)){
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("/search/{pattern}/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchSomething(@PathParam("pattern") String pattern){


        List<String> list = new ArrayList<>();
        File[] albums = mainDirectory.listFiles();
        for (File album:albums) {
            if (album.isDirectory()) {
                File[] albumContent = album.listFiles();

                for (File picture : albumContent) {
                    if (picture.isFile()) {

                        if (picture.getName().contains(pattern) && picture.getName().endsWith("jpg")) {

                            System.out.println(picture.getName());

                            list.add("http://localhost:8080/albums/" + album.getName() + "/" + picture.getName());

                        }
                    }
                }
            }
        }
        Response rep;


        if (list.size()>0){

            rep = Response.ok(list.toArray(new String[list.size()])).build();
            rep.getHeaders().add("Access-Control-Allow-Origin", "*");
            return rep;
        }

        rep = Response.status(Response.Status.NOT_FOUND).build();
        rep.getHeaders().add("Access-Control-Allow-Origin", "*");
        return rep;
    }



}





