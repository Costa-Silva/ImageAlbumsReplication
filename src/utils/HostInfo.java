package utils;

import java.net.InetAddress;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
public class HostInfo {

    private InetAddress address;
    private int port;

    public HostInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
