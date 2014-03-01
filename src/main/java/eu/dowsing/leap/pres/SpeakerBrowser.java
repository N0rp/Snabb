package eu.dowsing.leap.pres;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import eu.dowsing.leap.pres.Browser.UrlLocation;

public class SpeakerBrowser extends Region {

    private final WebView webview = new WebView();
    private final WebEngine webEngine = webview.getEngine();

    private final String consoleTemplate = "<!DOCTYPE html>" + "<html id=\"impressconsole\"><head>"
            + "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"{{cssFile}}\">" + "</head><body>"
            + "<div id=\"console\">" + "<div id=\"views\">" + "<iframe id=\"slideView\" scrolling=\"no\"></iframe>"
            + "<iframe id=\"preView\" scrolling=\"no\"></iframe>" + "<div id=\"blocker\"></div>" + "</div>"
            + "<div id=\"notes\"></div>" + "</div>" + "<div id=\"controls\"> "
            + "<div id=\"prev\"><a  href=\"#\" onclick=\"impress().prev(); return false;\" />Prev</a></div>"
            + "<div id=\"next\"><a  href=\"#\" onclick=\"impress().next(); return false;\" />Next</a></div>"
            + "<div id=\"clock\">00:00:00 AM</div>" + "<div id=\"timer\" onclick=\"timerReset()\">00m 00s</div>"
            + "<div id=\"status\">Loading</div>" + "</div>" + "</body></html>";

    private final String cssFile = "css/impressConsole.css";

    public SpeakerBrowser(String url, UrlLocation location) {
        // apply the styles
        getStyleClass().add("browser");
        if (location == UrlLocation.Local) {
            loadFile(url);
        } else if (location == UrlLocation.Web) {
            // load the web page
            loadWeb(url);
        }

        // loadFile(LOCAL_BORED);
        // loadFile(LOCAL_PRES);
        // add the web view to the scene
        getChildren().add(webview);

        enablePresenter();
    }

    private void enablePresenter() {
        webEngine.executeScript("consoleWindow = window.open();");
        webEngine.executeScript("consoleWindow.document.open();");
        webEngine.executeScript("consoleWindow.document.write(consoleTemplate.replace(\"{{cssFile}}\", cssFile))");
        webEngine.executeScript("consoleWindow.impress = window.impress;");
        webEngine.executeScript("consoleWindow.isconsoleWindow = true;");
        webEngine.executeScript("");
        webEngine.executeScript("registerKeyEvent([33, 37, 38], impress().prev);");
        webEngine.executeScript("registerKeyEvent([34, 39, 40], impress().next);");
        webEngine.executeScript("registerKeyEvent([32], spaceHandler);");
        webEngine.executeScript("");
        webEngine.executeScript("onStepEnter();");
        webEngine.executeScript("consoleWindow.initialized = false;");
        webEngine.executeScript("consoleWindow.document.close();");
        webEngine.executeScript("consoleWindow.initialized = false;");
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

}
