package sd.tp1.utils;

/**
 * Created by AntónioSilva on 03/04/2016.
 */
public class AlbumInfo {

    private String albumName;
    private int timesAccessed;


    public AlbumInfo(String albumName) {

        this.timesAccessed = 0;
        this.albumName = albumName;
    }



    public int getTimesAccessed() {
        return timesAccessed;
    }

    public void setTimesAccessed(int timesAccessed) {
        this.timesAccessed = timesAccessed;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
