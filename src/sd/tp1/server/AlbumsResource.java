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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
@Path("/albums")
public class AlbumsResource {

    public static final String MAINSOURCE= "./src/";
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

        if (mainDirectory.isDirectory()) {

            File[] files = mainDirectory.listFiles();

            List<String> albums = new ArrayList<>();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".")  ){

                    albums.add(file.getName());
                }

            }
            return Response.ok(albums).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/{albumName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName) {

        File album = new File(MAINSOURCE+albumName);

        if (!mainDirectory.isDirectory() || !album.exists() ) {

            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{

            List<String> list = new ArrayList<>();

            File albumDir = new File(album.getAbsolutePath());

            File[] files = albumDir.listFiles();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") ){

                    list.add(file.getName());

                }
            }


            return Response.ok(list).build();

        }

    }

    @GET
    @Path("/{albumName}/{picture}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pic){
        byte[] array;

        File album = new File(MAINSOURCE+albumName);

        if (!mainDirectory.isDirectory() || !album.exists() ) {

            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{

            List<String> list = new ArrayList<>();

            File albumDir = new File(album.getAbsolutePath());

            File[] files = albumDir.listFiles();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && file.getName().equals(pic) )
                    try {
                        RandomAccessFile f = new RandomAccessFile(file, "r");
                        array = new byte[(int) f.length()];

                        f.readFully(array);

                        return Response.ok(array).build();

                    } catch (Exception e) {

                    }
            }


            return Response.ok(null).build();

        }
    }

    @POST
    @Path("/{albumName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(@PathParam("albumName") String albumName){
        SharedAlbum sharedAlbum = new SharedAlbum(albumName);
        File album = new File(MAINSOURCE+sharedAlbum.getName());
        if(!album.exists()){
            album.mkdir();
            return Response.ok(album.getName()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }


    @POST
    @Path("/{albumName}/{pictureName}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData){

        if (mainDirectory.isDirectory()){

            File album = new File(mainDirectory.getAbsolutePath()+ "/"+ albumName);
            if (album.exists()) {
                File newPicture = new File(album.getAbsoluteFile()+ "/" + pictureName);

                try {
                    Files.write(newPicture.toPath(),pictureData, StandardOpenOption.CREATE_NEW);
                    return Response.ok().build();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }
    @DELETE
    @Path("/{albumName}")
    public Response deleteAlbum(@PathParam("albumName") String albumName){
        File album = new File(MAINSOURCE+albumName);
        if(album.isDirectory()){
            File delAlbum = new File(album.getName().concat(".deleted"));
            album.renameTo(delAlbum);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{albumName}/{pictureName}")
    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName){
        if (mainDirectory.isDirectory()) {

            File album = new File(mainDirectory.getAbsolutePath() + "/" + albumName);
            if (album.exists()) {
                File picture = new File(album.getAbsolutePath()+"/"+pictureName);

                File delpicture = new File(picture.getAbsolutePath().concat(".deleted"));



                boolean success = picture.renameTo(delpicture);

                return Response.ok().build();

            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();


    }
    /**
     * Represents a shared album.
     */
    static class SharedAlbum implements GalleryContentProvider.Album {
        final String name;

        SharedAlbum(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }


}





