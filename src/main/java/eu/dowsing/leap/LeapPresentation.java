package eu.dowsing.leap;

import java.io.File;
import java.net.MalformedURLException;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LeapPresentation extends Application {
    private Scene scene;

    @Override
    public void start(Stage stage) {
        // create the scene
        stage.setTitle("Web View");
        scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
        stage.setScene(scene);
        // scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Browser extends Region {

    private final static String WEB_BORED = "http://bartaz.github.io/impress.js/#/bored";
    private final static String WEB_STRUT = "http://strut.io/editor/#";
    private final static String LOCAL_BORED = "res/web/bored/index.html";
    private final static String LOCAL_PRES = "res/web/temp/Deck Title.html";

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser() {
        // apply the styles
        getStyleClass().add("browser");
        // load the web page
        // loadWeb(WEB_BORED);
        // loadFile(LOCAL_BORED);
        loadFile(LOCAL_PRES);
        // add the web view to the scene
        getChildren().add(browser);
    }

    private void loadWeb(String url) {
        webEngine.load(url);
    }

    private void loadFile(String path) {
        File f = new File(path);

        try {
            webEngine.load(f.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 750;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 500;
    }
}
