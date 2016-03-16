package utils;

import sd.tp1.gui.GalleryContentProvider;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
/**
 * Represents a shared album.
 */

public class SharedAlbum implements GalleryContentProvider.Album {
    private String name;

    public SharedAlbum(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
