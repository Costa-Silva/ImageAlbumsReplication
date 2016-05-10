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
import java.util.Scanner;

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


        while(true) {

            if(in.nextLine().equals("img")){
                downloadImagesFromIMAGER();
            }
        }

    }


    public static void downloadImagesFromIMAGER(){
        try {
            String imgurUrl = "https://api.imgur.com/3/account/me/images/";
            OAuthRequest albumsReq = new OAuthRequest(Verb.GET, imgurUrl, service);
            service.signRequest(accessToken, albumsReq);
            final Response albumsRes = albumsReq.send();
            System.out.println(albumsRes.getCode());

            JSONParser parser = new JSONParser();

            JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());

            JSONArray images = (JSONArray) res.get("data");

            for (int i = 0; i < images.size(); i++) {

                String eachImage = images.get(i).toString();

                String downloadLink = eachImage.split("\"link\":\"")[1].split("\",")[0].replace("\\/", "/");

                String imageId = downloadLink.split(".com/")[1];

                System.out.println(downloadLink);


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

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
