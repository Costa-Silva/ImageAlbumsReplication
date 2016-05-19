package sd.tp1;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import sd.tp1.client.*;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;


/*
 * This class provides the album/picture content to the gui/f application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider{

	Gui gui;
	private static final int MAXCACHESIZE = 500000 ; //500kb 500000
	private int currentCacheSize;
	private DiscoveryClient discoveryClient;
	private Map<String,Map<String,byte[]>> cache;
	private Map<String,Integer> leastAccessedAlbum;
	private boolean gotnewInfo;

	private List<String> topics;

	KafkaConsumer<String, String> consumer;
	SharedGalleryContentProvider() {
		discoveryClient = new DiscoveryClient();
		discoveryClient.checkNewConnections();
		cacheInit();
		initKafkaConsumer();
	}

	private void initKafkaConsumer() {

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "consumer-tutorial" + System.nanoTime());
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		consumer= new KafkaConsumer<>(props);
		topics = new ArrayList<>();

		consumer.subscribe(topics);
		new Thread(()->{
			try {


				for (; ; ) {
					ConsumerRecords<String, String> records = consumer.poll(1000);
					System.out.println("TOPICS : " + topics.toString());
					System.out.println("POLLNG e records com " + records.count());
					records.forEach(r -> {
								if (r.value().contains("Create")){
									gui.updateAlbums();
								}
					});
				}
			}finally {
				consumer.close();
			}
		}).start();
	}


	public void cacheInit(){


		new Thread(()-> {
			while (true) {
				try{
					cache = new ConcurrentHashMap<>();
					leastAccessedAlbum = new ConcurrentHashMap<>();
					resetCurrentCacheSize();
					System.err.println("Cache cleared");
					Thread.sleep(120000); //2 minutos 120000
				} catch (InterruptedException e) {
					System.err.println("ERROR CACHE INIT");
					e.printStackTrace();
				}
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
		}
		newServerHost();
	}


	public void newServerHost(){

		new Thread(()-> {
			while (true){

				if (discoveryClient.newHostFound()){
					gui.updateAlbums();
					setGotnewInfo(true);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();


	}

	/**
	 * Returns the list of albums in the system.
	 * On error this method should return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {

		List<Album> list = new ArrayList<Album>();
		List<String> listString = new ArrayList<>();

		topics.clear();
		topics.add("Albums");

		if (isGotnewInfo()){
			if (cache != null && cache.size()>0 ){
				for (Map.Entry<String,Map<String,byte[]> > entry : cache.entrySet()){

					list.add(new SharedAlbum(entry.getKey()));
				}
				return list;

			}
		}



		List<String> listReceived;
		for (Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()) {
			if((listReceived = entry.getValue().getListOfAlbums())!=null) {
				listString.addAll(listReceived);
			}
		}

		for (String album: listString) {
			list.add(new SharedAlbum(album));

			if (cache.get(album)==null){
				cache.put(album,new HashMap<>());
				leastAccessedAlbum.put(album,1);
			}
		}

		topics.addAll(listString);

		setGotnewInfo(false);
		return list;

	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		List<Picture> list = new ArrayList<Picture>();
		Map<String,byte[]> picturesMap = new HashMap<>();

		if (cache!=null && cache.size()>0) {
			for (Map.Entry<String, Map<String, byte[]>> entryAlbums : cache.entrySet()) {
				if (entryAlbums.getKey().equals(album.getName())) {
					if (cache.get(entryAlbums.getKey()).size() > 0) {
						for (Map.Entry<String, byte[]> pictures : entryAlbums.getValue().entrySet()) {
							list.add(new SharedPicture(pictures.getKey()));
						}
					} else {
						for (Map.Entry<String, SharedGalleryClient> entrySV : discoveryClient.getServers().entrySet()) {
							List<String> listReceived = entrySV.getValue().getListOfPictures(album.getName());
							if (listReceived != null && listReceived.size() > 0) {
								for (String picture : listReceived) {

									Picture pictureObj = new SharedPicture(picture);

									byte[] pictureData = getPictureData(album, pictureObj);
									picturesMap.put(picture, pictureData);

									setCurrentCacheSize(pictureData.length);

									list.add(pictureObj);
								}
							}
						}

						checkandRemovefromCache();
						cache.put(entryAlbums.getKey(), picturesMap);
					}
				}
			}
		}

		if (leastAccessedAlbum.get(album.getName())!=null){

			int newCounter=leastAccessedAlbum.remove(album.getName()) +1;
			leastAccessedAlbum.put(album.getName(),newCounter);
		}else{
			leastAccessedAlbum.put(album.getName(),0);
			cache.put(album.getName(),new HashMap<>());

		}

		return list;
	}


	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {

		if (getCurrentCacheSize()>0){

			for (Map.Entry<String,Map<String,byte[]> > entry : cache.entrySet()){

				if (entry.getKey().equals(album.getName())){

					for (Map.Entry<String,byte[]> pictures :entry.getValue().entrySet()){
						if (pictures.getKey().equals(picture.getName())){
							return pictures.getValue();
						}
					}
				}
			}
		}

		byte[] aux;
		for (Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()) {
			if((aux = entry.getValue().getPictureData(album.getName(),picture.getName()))!=null){
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
		SharedGalleryClient client = null;
		long minServerSize=Integer.MAX_VALUE;



		//LESS POPULATED SERVER
		for (Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()) {

			long serverSize = entry.getValue().getServerSize();
			if (serverSize < minServerSize ){
				minServerSize = serverSize;
				client =entry.getValue();
			}
		}
		if (client!=null)
			if ((nome = client.createAlbum(name)) != null) {

				album = new SharedAlbum(nome);
			}
		leastAccessedAlbum.put(album.getName(),0);
		cache.put(album.getName(),new HashMap<>());
		return album;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		int albumSize=0;
		for(Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()) {
			entry.getValue().deleteAlbum(album.getName());

		}
		if (cache.get(album.getName())!=null)
			for (Map.Entry<String,byte[]> entryAlbum: cache.remove(album.getName()).entrySet()){
				albumSize+=entryAlbum.getValue().length;
			}
		setCurrentCacheSize(-albumSize);
		leastAccessedAlbum.remove(album.getName());
	}

	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		boolean success=false;
		SharedGalleryClient client = null;
		long minServerSize=Integer.MAX_VALUE;

		for (Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()){
			List<String> listOfAlbums = entry.getValue().getListOfAlbums();

			if(listOfAlbums!=null)
				if (listOfAlbums.contains(album.getName())){
					long serverSize = entry.getValue().getServerSize();
					if (serverSize < minServerSize ){
						minServerSize = serverSize;
						client = entry.getValue();
					}
				}
		}
		if (client!=null)
			success=client.uploadPicture(album.getName(),name,data);

		if (success) {

			if (cache!=null && cache.size()>0) {
				for (Map.Entry<String, Map<String, byte[]>> entryAlbums : cache.entrySet()) {


					if (entryAlbums.getKey().equals(album.getName())) {

						checkandRemovefromCache();
						Map<String, byte[]> picturesMap = entryAlbums.getValue();
						picturesMap.put(name, data);
						cache.put(entryAlbums.getKey(), picturesMap);
						setCurrentCacheSize(data.length);
						checkandRemovefromCache();
					}
				}
			}

			return new SharedPicture(name);

		}
		return null;
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {

		boolean success=false;
		for(Map.Entry<String,SharedGalleryClient> entry : discoveryClient.getServers().entrySet()) {

			List<String> listReceived = entry.getValue().getListOfAlbums();
			if(listReceived!=null) {
				for (String albumName : listReceived) {
					if (albumName.equals(album.getName())) {
						success = entry.getValue().deletePicture(albumName,picture.getName());
					}
				}
			}
		}
		if (success){
			if (cache!=null && cache.size()>0) {
				for (Map.Entry<String, Map<String, byte[]>> entryAlbums : cache.entrySet()) {


					if (entryAlbums.getKey().equals(album.getName())) {
						Map<String, byte[]> picturesMap = entryAlbums.getValue();

						setCurrentCacheSize(-(picturesMap.remove(picture.getName()).length));
						cache.put(entryAlbums.getKey(), picturesMap);
					}
				}
			}
		}
		return success;
	}



	public synchronized void setCurrentCacheSize(int currentValue){
		currentCacheSize+=currentValue;
	}
	public synchronized void resetCurrentCacheSize(){
		currentCacheSize=0;
	}
	public synchronized int getCurrentCacheSize(){
		return currentCacheSize;
	}

	private void checkandRemovefromCache() {
		while (getCurrentCacheSize()>MAXCACHESIZE){
			String lonelyAlbum =getMinimumAlbumName();

			int albumSize=0;
			for (Map.Entry<String,byte[]> entry: cache.remove(lonelyAlbum).entrySet()){
				albumSize+=entry.getValue().length;
			}

			leastAccessedAlbum.remove(lonelyAlbum);
			System.out.println("Cache limited exceeded. Removing "+lonelyAlbum);
			setCurrentCacheSize(-albumSize);
		}
	}

	public String getMinimumAlbumName(){

		String minimum="";

		int minvalue=Integer.MAX_VALUE;
		for (Map.Entry<String, Integer> entry: leastAccessedAlbum.entrySet() ) {

			if (entry.getValue()<minvalue){
				minvalue=entry.getValue();
				minimum=entry.getKey();
			}

		}

		return minimum;
	}

	public synchronized boolean isGotnewInfo() {
		return gotnewInfo;
	}

	public synchronized void setGotnewInfo(boolean gotnewInfo) {
		this.gotnewInfo = gotnewInfo;
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
