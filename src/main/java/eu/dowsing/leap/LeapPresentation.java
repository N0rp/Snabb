package eu.dowsing.leap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import eu.dowsing.leap.Browser.UrlLocation;

public class LeapPresentation extends Application {
    private Scene scene;

    @Override
    public void start(Stage stage) {
        // create the scene
        stage.setTitle("Web View");
        scene = new Scene(new Browser(Browser.LOCAL_PRES, UrlLocation.Local), 750, 500, Color.web("#666970"));
        stage.setScene(scene);
        // scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
