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
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.core.MediaType;

/**
 * Created by Antonio on 09/05/16.
 */
public class ServerProxy {



    /**
     * Exemplo de acesso ao servico Imgur.
     * <p>
     * O URL base para programadores esta disponivel em: <br>
     * https://api.imgur.com/
     * <p>
     * A API REST do sistema esta disponivel em: <br>
     * https://api.imgur.com/endpoints
     * <p>
     * Para poder aceder ao servico Imgur, deve criar uma app em:
     * https://api.imgur.com/oauth2/addclient onde obtera a apiKey e a apiSecret a
     * usar na criacao do objecto OAuthService.
     * Deve use a opcao: OAuth 2 authorization without a callback URL
     * <p>
     * Este exemplo usa a biblioteca OAuth Scribe, disponivel em:
     * https://github.com/scribejava/scribejava
     * Para descarregar a biblioteca deve descarregar o jar do core:
     * http://mvnrepository.com/artifact/com.github.scribejava/scribejava-core
     * e da API
     * http://mvnrepository.com/artifact/com.github.scribejava/scribejava-apis
     * <p>
     * e a biblioteca json-simple, disponivel em:
     * http://code.google.com/p/json-simple/
     * <p>
     * e a biblioteca apache commons codec, disponivel em:
     * http://commons.apache.org/proper/commons-codec/
     */

    private static final String SERVICE_NAME = "Imgur";

    public static void main(String... args) {
        try {
            // Substituir pela API key atribuida
            final String apiKey = "2ad0de2edda68b0";
            // Substituir pelo API secret atribuido
            final String apiSecret = "18737726e52ee67e856c21cd7eac98e194cf0d26";

            final OAuth20Service service = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
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
            final OAuth2AccessToken accessToken = service.getAccessToken(code);

            System.out.println("Please introduce URL..");


            String urll;
            //"        https://api.imgur.com/3/account/me/images/      "

            while(!(urll= in.nextLine()).equals("")) {

                OAuthRequest albumsReq = new OAuthRequest(Verb.GET, urll, service);
                service.signRequest(accessToken, albumsReq);
                final Response albumsRes = albumsReq.send();
                System.out.println(albumsRes.getCode());

                JSONParser parser = new JSONParser();
                JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());

                JSONArray images = (JSONArray) res.get("data");

                for (int i = 0; i < images.size() ; i++) {

                    String eachImage = images.get(i).toString();

                    String downloadLink = eachImage.split("\"link\":\"")[1].split("\",")[0].replace("\\/","/");

                    String imageId= downloadLink.split(".com/")[1];

                    System.out.println(downloadLink);


                    URL url = new URL(downloadLink);
                    InputStream inputStream = new BufferedInputStream(url.openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1!=(n=inputStream.read(buf)))
                    {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    inputStream.close();
                    byte[] response = out.toByteArray();
                    FileOutputStream fos = new FileOutputStream("./"+imageId);
                    fos.write(response);
                    fos.close();

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
