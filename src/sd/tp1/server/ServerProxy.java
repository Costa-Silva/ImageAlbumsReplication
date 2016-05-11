package sd.tp1.server;

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Created by Antonio on 09/05/16.
 */
public class ServerProxy {


    private static final String SERVICE_NAME = "Imgur";

    private static final String apiKey = "2ad0de2edda68b0";

    private static final String apiSecret = "18737726e52ee67e856c21cd7eac98e194cf0d26";

    private static OAuth20Service service;

    private static OAuth2AccessToken accessToken;
    public static void main(String... args) {

        service = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
                .build(ImgurApi.instance());
        final Scanner in = new Scanner(System.in);

        // Obtain the Authorization URL
        System.out.println("A obter o Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Necessario dar permissao neste URL:");
        System.out.println(authorizationUrl);
        System.out.println("e copiar o codigo obtido para aqui:");
        System.out.print(">>");

        final String code = in.nextLine();

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("A obter o Access Token!");
        accessToken = service.getAccessToken(code);

        System.out.println("option");

        while(true) {
            String typo= in.nextLine();
            if(typo.equals("imgs")){
                downloadImagesFromIMAGER();
            }

            if (typo.equals("img")){
                System.out.println("id for image");
                String id= in.nextLine() ;
                downloadImageFromIMAGEr(id);
            }

            if (typo.equals("albums")){
                listOfAlbums();
            }

            if (typo.equals("nana")){
                newMethod();
            }

            if (typo.equals("c album")){
                System.out.println("album title");
                String album= in.nextLine() ;
                createAlbum(album);
            }

            if(typo.equals("UP")){
                System.out.println("album title");
                String album= in.nextLine() ;
                System.out.println("picture title");
                String pic= in.nextLine() ;
                File f = new File("C:/Users/paulo/Imagem.jpg");
                try {
                    String data = Base64.encodeBase64String(Files.readAllBytes(f.toPath()));
                    uploadPicture(album,pic,data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                
            }


        }
    }

    private static void uploadPicture(String album, String pic, String data) {
        String upImageUrl = "https://api.imgur.com/3/image";
        OAuthRequest upImageReq = new OAuthRequest(Verb.POST,upImageUrl,service);
        upImageReq.addParameter("image",data);
        upImageReq.addParameter("title",pic);
        upImageReq.addParameter("album","8YkHV");
        service.signRequest(accessToken,upImageReq);

        final Response upImageRes = upImageReq.send();
        JSONParser parser = new JSONParser();

        try {
            JSONObject res = (JSONObject) parser.parse(upImageRes.getBody());
            System.out.println(res.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(upImageRes.getCode()==200){
            System.out.println("Criou");
        }else {
            System.out.println("Nao criou");
        }

    }

    private static void createAlbum(String albumName) {

        String creationUrl = "https://api.imgur.com/3/album";
        OAuthRequest albumReq = new OAuthRequest(Verb.POST,creationUrl,service);
        albumReq.addParameter("title",albumName);
        service.signRequest(accessToken,albumReq);

        final Response albumRes = albumReq.send();
        JSONParser parser = new JSONParser();

        try {
            JSONObject res = (JSONObject) parser.parse(albumRes.getBody());
            System.out.println(res.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(albumRes.getCode()==200){
            System.out.println("Criou");
        }else {
            System.out.println("Nao criou");
        }


    }

    private static void downloadImageFromIMAGEr(String id) {
        String imgurUrl = "https://api.imgur.com/3/account/me/image/" + id;
        try {
            OAuthRequest imageReq = new OAuthRequest(Verb.GET, imgurUrl, service);
            service.signRequest(accessToken, imageReq);
            final Response imagesRes = imageReq.send();
            if (imagesRes.getCode() == 200) {
                JSONParser parser = new JSONParser();

                JSONObject res = (JSONObject) parser.parse(imagesRes.getBody());

                String image = res.get("data").toString();

                String downloadLink = image.split("\"link\":\"")[1].split("\",")[0].replace("\\/", "/");

                String imageId = downloadLink.split(".com/")[1];

                System.out.println(downloadLink);

                downloadContentAndSave(downloadLink, imageId);
            }else {
                System.err.println("No 200 code received");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static void downloadImagesFromIMAGER() {
        try {
            String imgurUrl = "https://api.imgur.com/3/account/me/images/";
            OAuthRequest imagesReq = new OAuthRequest(Verb.GET, imgurUrl, service);
            service.signRequest(accessToken, imagesReq);
            final Response imagesRes = imagesReq.send();
            if (imagesRes.getCode() == 200){

                JSONParser parser = new JSONParser();
                JSONObject res = (JSONObject) parser.parse(imagesRes.getBody());

                JSONArray images = (JSONArray) res.get("data");

                for (int i = 0; i < images.size(); i++) {
                    String eachImage = images.get(i).toString();
                    String downloadLink = eachImage.split("\"link\":\"")[1].split("\",")[0].replace("\\/", "/");
                    String imageId = downloadLink.split(".com/")[1];
                    downloadContentAndSave(downloadLink,imageId);
                }
            }else {
                System.err.println("No 200 code received");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void listOfAlbums(){

        String imgurUrl = "https://api.imgur.com/3/account/me/albums";
        try {
            OAuthRequest albumsReq = new OAuthRequest(Verb.GET, imgurUrl, service);
            service.signRequest(accessToken, albumsReq);
            final Response albumsRes = albumsReq.send();
            if (albumsRes.getCode() == 200) {

                JSONParser parser = new JSONParser();

                JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
                JSONArray images = (JSONArray) res.get("data");

                Iterator albumsIt = images.iterator();

                while(albumsIt.hasNext()){

                    JSONObject objects = (JSONObject) albumsIt.next();

                    System.out.println( objects.get("title").toString()+" "+objects.get("id").toString() );


                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private static void downloadContentAndSave(String downloadLink,String imageId){

        try {

            URL url = new URL(downloadLink);

            InputStream inputStream = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = inputStream.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            inputStream.close();
            byte[] response = out.toByteArray();
            FileOutputStream fos = new FileOutputStream("./" + imageId);
            fos.write(response);
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void newMethod(){
        String albumUrl = "https://api.imgur.com/3/album/"+"8YkHV"+"/images";
        try{
            OAuthRequest albumReq = new OAuthRequest(Verb.GET,albumUrl,service);
            service.signRequest(accessToken,albumReq);
            final com.github.scribejava.core.model.Response albumPRes = albumReq.send();
            if (albumPRes.getCode() == 200){
                JSONParser parser = new JSONParser();
                JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());
                JSONArray images = (JSONArray) res.get("data");

                Iterator albumsIt = images.iterator();
                while (albumsIt.hasNext()){

                    JSONObject objects = (JSONObject) albumsIt.next();
                    System.out.println(objects.toJSONString());

                    String title = (String)objects.get("title");

                    System.out.println("recebi: "+title);
                }

            }else {
                System.err.println("No 200 code received");
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
}
