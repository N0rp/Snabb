package eu.dowsing.leap;

import java.io.File;
import java.net.MalformedURLException;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {

    public final static String WEB_BORED = "http://bartaz.github.io/impress.js/#/bored";
    public final static String WEB_STRUT = "http://strut.io/editor/#";
    public final static String LOCAL_BORED = "res/web/bored/index.html";
    public final static String LOCAL_PRES = "res/web/temp/Deck Title.html";

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    /** the url location **/
    public enum UrlLocation {
        /** local file system **/
        Local,
        /** on the web **/
        Web
    }

    public Browser(String url, UrlLocation location) {
        // apply the styles
        getStyleClass().add("browser");
        if (location == UrlLocation.Local) {
            // load the web page
            loadWeb(url);
        } else if (location == UrlLocation.Web) {
            loadFile(url);
        }
        // loadFile(LOCAL_BORED);
        // loadFile(LOCAL_PRES);
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