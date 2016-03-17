package sd.tp1.server;

/**
 * Created by AntónioSilva on 17/03/2016.
 */

import java.util.Date;

public class FileInfo implements java.io.Serializable {
    public String name;
    public long length;
    public Date modified;
    public boolean isFile;

    public FileInfo() {
    }

    public FileInfo(String name, long length, Date modified, boolean isFile) {
        this.name = name;
        this.length = length;
        this.modified = modified;
        this.isFile = isFile;
    }

    public String toString() {
        return String.format("Name: %s\nLength: %d\nDate modified: %s\nType: %s\n", name, length, modified,
                isFile ? "File" : "Directory");
    }
}