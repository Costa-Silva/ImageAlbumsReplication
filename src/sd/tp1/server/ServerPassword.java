package sd.tp1.server;

/**
 * Created by paulo on 10/05/2016.
 */
public class ServerPassword {

    private static String password;

    public ServerPassword(String password){
        this.password=password;
    }

    public static boolean checkPassword(String pass){
        return password.equalsIgnoreCase(pass);
    }
}
