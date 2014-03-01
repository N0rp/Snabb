package eu.dowsing.leap.pres;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import eu.dowsing.leap.pres.Browser.UrlLocation;

public class SpeakerWindow extends Stage {

    private SpeakerBrowser speakerBrowser;

    private int width;
    private int height;

    public SpeakerWindow(int width, int height, String url, UrlLocation location) {
        this.width = width;
        this.height = height;
        this.setScene(getSpeakerScene(url, location));
    }

    private Scene getSpeakerScene(String url, UrlLocation location) {
        this.speakerBrowser = new SpeakerBrowser(url, location);
        Text label = TextBuilder.create().text("Foobar").translateY(50).translateX(50)
                .font(Font.font("Arial", FontWeight.BOLD, 18)).fill(Color.BLUE).build();
        Pane pane = new Pane();
        pane.getChildren().add(label);
        pane.getChildren().add(speakerBrowser);

        Scene tmp = new Scene(pane, width, height);
        return tmp;

        // //Open speaker console when they press 'p'
        // registerKeyEvent([80], open, window);
        // var open = function() {
    }

}
