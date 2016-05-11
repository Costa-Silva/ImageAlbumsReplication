package sd.tp1.server;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.codec.binary.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * Created by Antonio on 10/05/16.
 */
@Path("/albums")
public class AlbumsProxyResource {

    private OAuth20Service service;
    private OAuth2AccessToken accessToken;
    private String srvpass;
    private Map<String,String> albumsIdName;
    private Map<String,Map<String,String >> picturesIdName;
    public AlbumsProxyResource(OAuth20Service service, OAuth2AccessToken accessToken,String srvpass){
        this.service = service;
        this.accessToken = accessToken;
        this.srvpass= srvpass;
        albumsIdName = new HashMap<>();
        picturesIdName = new HashMap<>();
    }

    private boolean checkPassword(String srvpass){
        return this.srvpass.equals(srvpass);
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

        if (checkPassword(password)) {
            String imgurUrl = "https://api.imgur.com/3/account/me/albums";
            try {
                List<String> albumsTitleList = new LinkedList<>();
                OAuthRequest albumsReq = new OAuthRequest(Verb.GET, imgurUrl, service);
                service.signRequest(accessToken, albumsReq);
                final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
                if (albumsRes.getCode() == 200) {

                    JSONParser parser = new JSONParser();

                    JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
                    JSONArray images = (JSONArray) res.get("data");

                    Iterator albumsIt = images.iterator();
                    albumsIdName.clear();
                    while(albumsIt.hasNext()){

                        JSONObject objects = (JSONObject) albumsIt.next();
                        String title = objects.get("title").toString();
                        String albumId= objects.get("id").toString();
                        albumsTitleList.add(title);
                        albumsIdName.put(albumId,title);
                    }
                }else {
                    System.err.println("No 200 code received");
                }
                if (albumsTitleList != null) {
                    return Response.ok(albumsTitleList).build();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @GET
    @Path("/{albumName}/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName, @PathParam("password") String password) {
        List<String> picturesList = new LinkedList<>();

        if (checkPassword(password)) {
            if (albumsIdName.get(albumName)!=null){
                String albumUrl = "https://api.imgur.com/3/album/"+albumsIdName.get(albumName)+"/images";
                try{
                    OAuthRequest albumReq = new OAuthRequest(Verb.GET,albumUrl,service);
                    service.signRequest(accessToken,albumReq);
                    final com.github.scribejava.core.model.Response albumPRes = albumReq.send();
                    if (albumPRes.getCode() == 200){
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());
                        System.out.println(res.toJSONString());
                        JSONArray images = (JSONArray) res.get("data");

                        Iterator albumsIt = images.iterator();
                        while (albumsIt.hasNext()){

                            JSONObject objects = (JSONObject) albumsIt.next();
                            String title = (String)objects.get("title");
                            String pictureId= (String)objects.get("id");
                            picturesList.add(title);

                            String albumId="";
                            for(String key : albumsIdName.keySet()){
                                if (albumsIdName.get(key).equals(albumName)){
                                    albumId=key;
                                    break;
                                }
                            }

                            Map<String,String> picOfAlbum = new HashMap<>();
                            picOfAlbum.put(title,albumId);
                            picturesIdName.put(pictureId,picOfAlbum);
                            System.out.println("recebi: "+title);
                        }

                    }else {
                        System.err.println("No 200 code received");
                    }
                    if (picturesList != null) {
                        return Response.ok(picturesList).build();
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }



    @GET
    @Path("/{albumName}/{picture}/key/{password}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName,@PathParam("password") String password){

        if (checkPassword(password)) {
            if (albumsIdName.get(albumName) != null) {
                if (picturesIdName.get(pictureName)!=null) {
                    try {
                        String imageUrl = "https://api.imgur.com/3/album/" + albumsIdName.get(albumName) + "/image/" + picturesIdName.get(pictureName);

                        OAuthRequest albumReq = new OAuthRequest(Verb.GET,imageUrl,service);
                        service.signRequest(accessToken,albumReq);
                        final com.github.scribejava.core.model.Response albumPRes = albumReq.send();
                        if (albumPRes.getCode() == 200) {
                            JSONParser parser = new JSONParser();
                            JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());

                            String downloadlink = (String)((JSONObject)res.get("data")).get("link");

                            URL url = new URL(downloadlink);

                            InputStream inputStream = new BufferedInputStream(url.openStream());

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            int n = 0;
                            while (-1 != (n = inputStream.read(buf))) {
                                out.write(buf, 0, n);
                            }
                            out.close();
                            inputStream.close();
                            return Response.ok(out.toByteArray()).build();
                        }else {
                            System.err.println("No 200 code received");
                        }

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Path("/key/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName,@PathParam("password") String password){
        if(checkPassword(password)){
                try{
                    String creationUrl = "https://api.imgur.com/3/album";
                    OAuthRequest albumReq = new OAuthRequest(Verb.POST,creationUrl,service);
                    albumReq.addParameter("title",albumName);
                    service.signRequest(accessToken,albumReq);

                    final com.github.scribejava.core.model.Response albumRes = albumReq.send();

                    if(albumRes.getCode()==200){
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(albumRes.getBody());

                        String albumID = (String)((JSONObject)res.get("data")).get("id");

                        albumsIdName.put(albumID,albumName);

                        return Response.ok().build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).build();

                }catch (Exception e){
                    e.printStackTrace();
                }

        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }


    @POST
    @Path("/{albumName}/{pictureName}/key/{password}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData,@PathParam("password")String password){
        if (checkPassword(password)){
            try{
                boolean hasAlbum = false;
                String albumID="";
                for (String key : albumsIdName.keySet()){
                    if(albumsIdName.get(key).equals(albumName)){
                        hasAlbum = true;
                        albumID=key;
                    }
                }
                if(hasAlbum) {
                    String upImageUrl = "https://api.imgur.com/3/image";
                    OAuthRequest upImageReq = new OAuthRequest(Verb.POST, upImageUrl, service);
                    upImageReq.addParameter("image", org.apache.commons.codec.binary.Base64.encodeBase64String(pictureData));
                    upImageReq.addParameter("title", pictureName);
                    upImageReq.addParameter("album", albumID);
                    service.signRequest(accessToken, upImageReq);

                    final com.github.scribejava.core.model.Response upImageRes = upImageReq.send();

                    if(upImageRes.getCode()==200){
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(upImageRes.getBody());

                        String imageID = (String)((JSONObject)res.get("data")).get("id");
                        Map<String,String> picOfAlbum = new HashMap<>();
                        picOfAlbum.put(pictureName,albumID);
                        picturesIdName.put(imageID,picOfAlbum);

                        return Response.ok().build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).build();


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
