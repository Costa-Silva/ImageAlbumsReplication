package sd.tp1;

import javafx.application.Application;
import javafx.stage.Stage;
import sd.tp1.gui.impl.GalleryWindow;

import java.util.Scanner;

/*
 * Launches the local shared gallery application.
 */
public class SharedGallery extends Application {

	GalleryWindow window;
	
	public SharedGallery() {
		System.out.println("Kafka's broker hostname");
		Scanner s = new Scanner(System.in);
		String hostname = s.nextLine();

		window = new GalleryWindow(
				new SharedGalleryContentProvider(hostname)
		);
	}	
	
	
    public static void main(String[] args){
        launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		window.start(primaryStage);
	}
}
