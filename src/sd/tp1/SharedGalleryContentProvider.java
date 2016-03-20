package sd.tp1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import sd.tp1.client.*;
import sd.tp1.client.ws.GetPicturesList;
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
	private String serverHost;
	private Server serverConnection;
	private ClientDiscovery clientDiscovery;
	private Map<String,Server> serverHashMap;
	private Map<String,String> serverHosts;


	SharedGalleryContentProvider() {
		// TODO: code to do when shared gallery starts
		serverHashMap = new HashMap<>();
		clientDiscovery = new ClientDiscovery();

		clientDiscovery.sendMulticast();




		search4Servers();


	}



	public void search4Servers(){



		new Thread(()->{
			for(;;) {

				System.out.println("Entrei no loop do search4fserver");
				while(clientDiscovery.getServers().size()==0){
				}
				System.out.println("sai no loop do search4fserver");



				serverHosts = clientDiscovery.getServers();


				for (Map.Entry<String,String> entry : serverHosts.entrySet()) {
					String x = entry.getKey();

					Server y = ClientDiscovery.getServer(entry.getKey());



					serverHashMap.put(x,y);
				}

				try {
					Thread.sleep(2000);
				} catch (Exception e) {}
			}
		}).start();



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
					if( ! l.isEmpty() ) {
						System.out.println("tenho albums: " + l.size());
						gui.updateAlbum(l.iterator().next());
					}
						try {
						Thread.sleep(5000);
					} catch (Exception e) {}
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

		System.out.println(serverHashMap.size());

		for (Map.Entry<String,Server> entry : serverHashMap.entrySet()) {

			List<String> listReceived = GetAlbumList.getAlbums(entry.getValue(),entry.getKey());

			for (String album:listReceived) {
				lst.add( new SharedAlbum(album));
			}

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


		List<String> listReceived = GetPicturesListClient.getPictures(serverConnection,serverHost,album.getName());


		for (String picture:listReceived) {
			lst.add( new SharedPicture(picture));
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
		return GetPictureData.getPictureData(serverConnection,serverHost,album.getName(),picture.getName());
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		// TODO: contact servers to create album
		String nome;
		if ((nome = CreateAlbum.createAlbum(serverConnection,serverHost,name))!=null){
			return new SharedAlbum(nome);
		}else return null;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		// TODO: contact servers to delete album
		DeleteAlbum.deleteAlbum(serverConnection,serverHost,album.getName());
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
