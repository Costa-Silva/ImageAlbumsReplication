package sd.tp1;
import java.util.*;

import sd.tp1.client.*;
import sd.tp1.client.ws.Server;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider{

	Gui gui;
	private ClientDiscovery clientDiscovery;

	SharedGalleryContentProvider() {
		// TODO: code to do when shared gallery starts
		clientDiscovery = new ClientDiscovery();
		clientDiscovery.checkNewConnections();
	}



	/**
	 *  Downcall from the GUI to register itself, so that it can be updated via upcalls.
	 */
	@Override
	public void register(Gui gui) {
		if( this.gui == null ) {
			this.gui = gui;
			new Thread(()->{
				for(;;) {
					List<Album> l = getListOfAlbums();
					if (l != null){
						if (!l.isEmpty()) {
							Iterator<Album> it = l.iterator();
							while (it.hasNext()) {
								gui.updateAlbum(it.next());
							}
							gui.updateAlbums();
						}
					}
						try {
							Thread.sleep(4000);
						}catch (Exception e) {
						}
					}

			}).start();
		}
	}

	/**
	 * Returns the list of albums in the system.
	 * On error this method should return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {
		// TODO: obtain remote information
		List<Album> lst = new ArrayList<Album>();

		for (Map.Entry<String,Server> entry : clientDiscovery.getServers().entrySet()) {

			List<String> listReceived = GetAlbumList.getAlbums(entry.getValue(),entry.getKey());
			if(listReceived!=null) {
				for (String album : listReceived) {
					lst.add(new SharedAlbum(album));
				}
			}else return null;
		}
		return lst;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		// TODO: obtain remote information 
		List<Picture> lst = new ArrayList<Picture>();

		for (Map.Entry<String,Server> entry : clientDiscovery.getServers().entrySet()) {
			List<String> listReceived = GetPicturesListClient.getPictures(entry.getValue(), entry.getKey(), album.getName());
			if(listReceived!=null){
				for (String picture:listReceived) {
					lst.add(new SharedPicture(picture));
				}
			}else return null;
		}

		return lst;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		// TODO: obtain remote information
		byte[] aux;
		for (Map.Entry<String,Server> entry : clientDiscovery.getServers().entrySet()) {
			if((aux = GetPictureData.getPictureData(entry.getValue(), entry.getKey(), album.getName(), picture.getName()))!=null){
				return aux;
			}
		}
		return null;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		// TODO: contact servers to create album
		String nome;

		for (Map.Entry<String,Server> entry : clientDiscovery.getServers().entrySet()) {
			if ((nome = CreateAlbum.createAlbum(entry.getValue(), entry.getKey(), name)) != null) {

				return new SharedAlbum(nome);
			}
		}return null;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		// TODO: contact servers to delete album
		for(Map.Entry<String,Server> entry : clientDiscovery.getServers().entrySet()) {
			DeleteAlbum.deleteAlbum(entry.getValue(),entry.getKey(), album.getName());
		}
	}

	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		// TODO: contact servers to add picture name with contents data 
		return new SharedPicture(name);
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		// TODO: contact servers to delete picture from album 
		return true;
	}

	/**
	 * Represents a shared album.
	 */
	static class SharedAlbum implements GalleryContentProvider.Album {
		final String name;

		SharedAlbum(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}


	/**
	 * Represents a shared picture.
	 */
	static class SharedPicture implements GalleryContentProvider.Picture {
		final String name;

		SharedPicture(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
