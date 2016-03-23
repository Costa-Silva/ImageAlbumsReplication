package sd.tp1.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
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
    Map<String,String> albumsMap= new HashMap<>();
    Map<String,String> picturesMap = new HashMap<>();
    File mainDirectory = new File(MAINSOURCE);



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList() {


        if (mainDirectory.isDirectory()) {

            File[] files = mainDirectory.listFiles();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && albumsMap.get(file.getName())==null ){

                    albumsMap.put(file.getName(),file.getName());
                }

            }
            List<String> albums = new ArrayList<>(albumsMap.values());
            return Response.ok(albums).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/{albumName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName) {

        File album = new File(MAINSOURCE+albumName);

        if (albumsMap == null || !mainDirectory.isDirectory() || !album.exists() ) {

            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else{

            File albumDir = new File(album.getAbsolutePath());

            File[] files = albumDir.listFiles();

            for (File file: files) {

                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") && picturesMap.get(file.getName())==null ){

                    picturesMap.put(file.getName(),file.getName());

                }
            }

            List<String> list = new ArrayList<>(picturesMap.values());
            return Response.ok(list).build();

        }

    }

}



