package sd.tp1.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by paulo on 03/05/2016.
 */
public class InsecureClass {
    static public class InsecureHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    static public class InsecureTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            Arrays.asList( chain ).forEach(i -> {
                System.err.println( "type: " + i.getType() + "from: " + i.getNotBefore() + " to: " + i.getNotAfter() );
            });
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
