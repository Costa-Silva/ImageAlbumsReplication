package sd.tp1;
import java.util.*;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import sd.tp1.client.*;
import sd.tp1.client.CreateAlbum;
import sd.tp1.client.DeleteAlbum;
import sd.tp1.client.DeletePicture;
import sd.tp1.client.GetAlbumList;
import sd.tp1.client.GetPictureData;
import sd.tp1.client.GetPicturesList;
import sd.tp1.client.UploadPicture;
import sd.tp1.client.ws.*;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

import javax.ws.rs.client.WebTarget;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider{

	Gui gui;
	private DiscoveryClient discoveryClient;
	private Map<String,Map<String,Byte>> cache;

	SharedGalleryContentProvider() {
		cache = new HashMap<>();
		discoveryClient = new DiscoveryClient();
		discoveryClient.checkNewConnections();
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
						Thread.sleep(7000);
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




		List<Album> list = new ArrayList<Album>();

		for (Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {

			List<String> listReceived = GetAlbumList.getAlbums(entry.getValue());
			if (listReceived != null) {
				for (String album : listReceived) {
					list.add(new SharedAlbum(album));
				}
			}
		}

		for (Map.Entry<String,WebTarget> entry : discoveryClient.getRESTServers().entrySet()) {

			List<String> listReceived = GetAlbumListREST.getAlbumList(entry.getValue());
			if(listReceived!=null) {
				for (String album : listReceived) {
					list.add(new SharedAlbum(album));
				}
			}
		}



		return list;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		List<Picture> list = new ArrayList<Picture>();

		for (Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {
			List<String> listReceived = GetPicturesList.getPictures(entry.getValue(),album.getName());
			if(listReceived!=null){
				for (String picture:listReceived) {
					list.add(new SharedPicture(picture));
				}
			}
		}

		for (Map.Entry<String,WebTarget> entry :discoveryClient.getRESTServers().entrySet()) {

			List<String> listReceived = GetAlbumListREST.getAlbumList(entry.getValue());
			if(listReceived!=null) {
				for (String albumName : GetPicturesListREST.getPicturesList(entry.getValue(), album.getName())) {
					list.add(new SharedPicture(albumName));
				}
			}
		}

		return list;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		byte[] aux;
		for (Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {
			if((aux = GetPictureData.getPictureData(entry.getValue(),album.getName(), picture.getName()))!=null){
				return aux;
			}
		}

		for (Map.Entry<String,WebTarget> entry : discoveryClient.getRESTServers().entrySet()) {
			if((aux = GetPictureDataREST.getPictureData(entry.getValue(), album.getName(), picture.getName()))!=null){
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
		String nome;
		Album album = null;
		WebTarget target = null;
		Server server=null;
		long minServerSize=Integer.MAX_VALUE;
		String type="";

		for (Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {

			long serverSize = ServerSize.getServerSize(entry.getValue());
			if (serverSize < minServerSize ){
				minServerSize = serverSize;
				server= entry.getValue();
				type="WS";
			}

		}

		for (Map.Entry<String,WebTarget> entry : discoveryClient.getRESTServers().entrySet()) {
			long serverSize = ServerSizeREST.getServerSize(entry.getValue());

			if (serverSize < minServerSize ){
				minServerSize = serverSize;
				type="REST";
				target=entry.getValue();
			}

		}

		if (type.equals("REST")){
			if ((nome = CreateAlbumREST.createAlbum(target, name)) != null) {

				album = new SharedAlbum(nome);
			}

		}else if(type.equals("WS")){
			if ((nome = CreateAlbum.createAlbum(server, name)) != null) {
				album = new SharedAlbum(nome);
			}
		}

		return album;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		for(Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {
			DeleteAlbum.deleteAlbum(entry.getValue(), album.getName());
		}
		for(Map.Entry<String,WebTarget> entry : discoveryClient.getRESTServers().entrySet()) {

			DeleteAlbumREST.deleteAlbum(entry.getValue(), album.getName());
		}
	}

	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		boolean success=false;

		WebTarget target = null;
		Server server=null;
		long minServerSize=Integer.MAX_VALUE;
		String type="";

		for (Map.Entry<String,Server> entryWS : discoveryClient.getWebServicesServers().entrySet()){
			List<String> listOfAlbums = GetAlbumList.getAlbums(entryWS.getValue());

			if(listOfAlbums!=null)
			if (listOfAlbums.contains(album.getName())){
				long serverSize = ServerSize.getServerSize(entryWS.getValue());
				if (serverSize < minServerSize ){
					minServerSize = serverSize;
					server= entryWS.getValue();
					type="WS";
				}

			}

		}


		for (Map.Entry<String,WebTarget> entryREST : discoveryClient.getRESTServers().entrySet()){
			List<String> listOfAlbums = GetAlbumListREST.getAlbumList(entryREST.getValue());

			if (listOfAlbums!=null)
			if (listOfAlbums.contains(album.getName())){
				long serverSize = ServerSizeREST.getServerSize(entryREST.getValue());
				if (serverSize < minServerSize ){
					minServerSize = serverSize;
					target= entryREST.getValue();
					type="REST";
				}
			}

		}

		if (type.equals("REST")){
				success=UploadPictureREST.uploadPicture(target,album.getName(),name,data);
		}else if(type.equals("WS")){
			success=UploadPicture.uploadPicture(server,data,album.getName(),name);
		}
		if (success)
			return new SharedPicture(name);
		return null;
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {

		boolean success=false;
		for(Map.Entry<String,Server> entry : discoveryClient.getWebServicesServers().entrySet()) {

			List<String> listReceived = GetAlbumList.getAlbums(entry.getValue());
			if(listReceived!=null) {
				for (String albumName : listReceived) {
					if (albumName.equals(album.getName())) {
						success = DeletePicture.deletePicture(entry.getValue(),album.getName(),picture.getName());
					}
				}


			}

		}

		for(Map.Entry<String,WebTarget> entry : discoveryClient.getRESTServers().entrySet()) {

			List<String> listReceived = GetAlbumListREST.getAlbumList(entry.getValue());
			if(listReceived!=null) {
				for (String albumName : listReceived) {
					if (albumName.equals(album.getName())) {
						success = DeletePictureREST.deletePicture(entry.getValue(),album.getName(),picture.getName());
					}
				}
			}
		}

		return success;
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
