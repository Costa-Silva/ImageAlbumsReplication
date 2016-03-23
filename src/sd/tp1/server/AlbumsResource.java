package sd.tp1.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntónioSilva on 23/03/2016.
 */
@Path("/albums")
public class AlbumsResource {

    List<String> albums = new ArrayList<>();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList() {
        albums.add("album1");
        albums.add("album2"); //testes
        System.out.println("Sending Albums");
        if (albums == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        else
            return Response.ok(albums).build();
    }




}
