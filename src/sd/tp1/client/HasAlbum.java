package sd.tp1.client;

import sd.tp1.client.ws.Server;

/**
 * Created by paulo on 24/05/2016.
 */
public class HasAlbum {

    public static boolean hasAlbum(Server server, String albumName) {

    return server.hasAlbum(albumName);

    }
}
