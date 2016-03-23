package sd.tp1.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
@Path("/albums")
public class AlbumsResource {

    String mainSource= "./src";


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList() {
        List<String> albums = new ArrayList<>();
        System.out.println("Sending Albums");
        File mainDirectory = new File(mainSource);

        if (mainDirectory.isDirectory()) {


            File[] files = mainDirectory.listFiles();

            for (File file: files) {
                if (!file.getName().endsWith(".deleted") && !file.getName().startsWith(".") ){

                    albums.add(file.getName());

                }

            }

            return Response.ok(albums).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



}





