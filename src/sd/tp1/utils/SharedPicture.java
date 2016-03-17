package sd.tp1.utils;

import sd.tp1.gui.GalleryContentProvider;

/**
 * Created by AntónioSilva on 16/03/2016.
 */
/**
 * Represents a shared picture.
 */

public class SharedPicture implements GalleryContentProvider.Picture {
    private String name;

    public SharedPicture(String name){
        this.name=name;


    }

    public String getName() {
        return name;
    }


}
