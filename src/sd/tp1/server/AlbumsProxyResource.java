package sd.tp1.server;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Antonio on 10/05/16.
 */
@Path("/albums")
public class AlbumsProxyResource {

    public static final String MAINSOURCE = "."+ File.separator+"src"+File.separator;
    private  File mainDirectory = new File(MAINSOURCE);

    private OAuth20Service service;

    private OAuth2AccessToken accessToken;

    public AlbumsProxyResource(OAuth20Service service, OAuth2AccessToken accessToken){
        this.service = service;
        this.accessToken = accessToken;
    }

    @GET
    @Path("/serverBytes/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace(@PathParam("password") String password) {

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList(@PathParam("password") String password) {

        String imgurUrl = "https://api.imgur.com/3/account/me/albums";
        try {
            OAuthRequest albumsReq = new OAuthRequest(Verb.GET, imgurUrl, service);
            service.signRequest(accessToken, albumsReq);
            final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
            if (albumsRes.getCode() == 200) {

                JSONParser parser = new JSONParser();

                JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
                JSONArray images = (JSONArray) res.get("data");

                Iterator albumsIt = images.iterator();

                while(albumsIt.hasNext()){
                    System.out.println(albumsIt.next());
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @GET
    @Path("/{albumName}/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName, @PathParam("password") String password) {


            String albumID = albumName;

        String albumUrl = "https://api.imgur.com/3/album/"+albumID+"/images";
        try{
            OAuthRequest albumReq = new OAuthRequest(Verb.GET,albumUrl,service);
            service.signRequest(accessToken,albumReq);
            final com.github.scribejava.core.model.Response albumPRes = albumReq.send();

            if (albumPRes.getCode()==200){

                JSONParser parser = new JSONParser();

                JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());

                System.out.print(res.toJSONString());
            }
        }catch (ParseException e){
            e.printStackTrace();
        }





        return Response.status(Response.Status.UNAUTHORIZED).build();
    }



    @GET
    @Path("/{albumName}/{picture}/key/{password}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName,@PathParam("password") String password){

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Path("/key/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName,@PathParam("password") String password){

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Path("/{albumName}/{pictureName}/key/{password}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData,@PathParam("password")String password){

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    @DELETE
    @Path("/{albumName}/key/{password}")
    public Response deleteAlbum(@PathParam("albumName") String albumName,@PathParam("password") String password){


        return Response.status(Response.Status.UNAUTHORIZED).build();
    }



    @DELETE
    @Path("/{albumName}/{pictureName}/key/{password}")

    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName,@PathParam("password")String password){


        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


}
