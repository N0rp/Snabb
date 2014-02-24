package eu.dowsing.leap.pres;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDivElement;

public class Browser extends Region {

    public final static String WEB_BORED = "http://bartaz.github.io/impress.js/#/bored";
    public final static String WEB_STRUT = "http://strut.io/editor/#";
    public final static String LOCAL_BORED = "res/web/bored/index.html";
    public final static String LOCAL_PRES = "res/web/temp/Deck Title.html";

    private final WebView webview = new WebView();
    private final WebEngine webEngine = webview.getEngine();

    private List<SlideChangedListener> slideChangedListener = new LinkedList<>();
    private List<PageLoadCompleteListener> pageLoadCompleteListener = new LinkedList<>();

    private int maxStepFound = 1;

    /** the url location **/
    public enum UrlLocation {
        /** local file system **/
        Local,
        /** on the web **/
        Web
    }

    private int currentSlide = 1;

    public Browser(String url, UrlLocation location) {
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

        setupListeners();
    }

    /**
     * Get the current known completion in per cent
     * 
     * @return completion of the slide in per cent (0 - 100 %)
     */
    public double getCompletion() {
        if (maxStepFound > 0) {
            double result = (double) currentSlide / (double) maxStepFound * 100.0;
            return round(result, 1);
        } else {
            return 100;
        }
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Setup the listeners for special events.
     */
    private void setupListeners() {
        webview.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.UP) {
                    setSlideIndex(currentSlide - 1);
                } else if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.DOWN
                        || keyEvent.getCode() == KeyCode.SPACE) {
                    setSlideIndex(currentSlide + 1);
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    // consume escape key, used by logitech presenter to escape fullscreen presentation
                    // but in impress this triggers the overview, which we do not want to see
                    keyEvent.consume();
                }
            }
        });

        webEngine.getLoadWorker().stateProperty().addListener(new javafx.beans.value.ChangeListener<State>() {
            public void changed(ObservableValue ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    // only called once loading is actually done
                    System.out.println("Page Load Done");
                    notifyPageLoadCompleteListeners();

                    Document doc = webEngine.getDocument();
                    Element el = doc.getElementById("impress");
                    ((EventTarget) el).addEventListener("impress:stepenter", new EventListener() {

                        @Override
                        public void handleEvent(Event evt) {
                            if (evt.getTarget() instanceof HTMLDivElement) {
                                HTMLDivElement element = (HTMLDivElement) evt.getTarget();
                                // element id is either step-1, step-2, etc. or overview

                                // System.out.println("Event target id is " + element.getId());
                                String target = element.getId();
                                int step = -1;
                                if (target.contains("step-")) {
                                    target = target.replace("step-", "");
                                    step = Integer.parseInt(target);
                                } else if (target.contains("overview")) {
                                    step = 0;
                                }
                                if (step >= 0) {
                                    setSlideIndex(step);
                                    // remember the maximum step
                                    if (step > maxStepFound) {
                                        maxStepFound = step;
                                    }
                                }
                                // System.out.println("!!!Just did a step to " + step);
                            }
                        }
                    }, false);
                }
            }
        });
    }

    public WebView getWebView() {
        return this.webview;
    }

    public void gotoPage(int index) {
        webEngine.executeScript("impress().goto(" + index + ");");
    }

    public void gotoNextPage() {
        setSlideIndex(currentSlide + 1);
        webEngine.executeScript("impress().next();");
        getStepsLength();
    }

    public void gotoPrevPage() {
        setSlideIndex(currentSlide - 1);
        webEngine.executeScript("impress().prev();");
        getStepsLength();
    }

    public void getStepsLength() {
        Document doc = webEngine.getDocument();
        if (doc != null) {
            Element step1 = doc.getElementById("step-1");
            Element step9 = doc.getElementById("step-9");
            Element step10 = doc.getElementById("step-10");
            HTMLDivElement div1 = (HTMLDivElement) step1;

            NodeList elements = doc.getElementsByTagName("step active present");

            Element el = doc.getElementById("impress.activeStep");

            JSObject impress = (JSObject) webEngine.executeScript("impress");
            Object res = impress.call("getStepLength");
            Object memberSteps = impress.getMember("steps.length");
            Object activeStep = impress.getMember("activeStep");

            String test = "impress.steps";
            Object test1 = (Object) webEngine.executeScript(test);

            // System.out.println("Steps result is " + res);
            // System.out.println("Steps element is " + el);
            // System.out.println("Steps array is " + memberSteps);
            // System.out.println("ActiveStep is " + activeStep);
            // System.out.println("Test1 is " + test1);
        }

        // JSObject result2 = (JSObject) webEngine.executeScript("impress.steps;");
        // System.out.println("Stepsresult 2 is " + result2);
    }

    public void addSlideChangedListener(SlideChangedListener listener) {
        this.slideChangedListener.add(listener);
    }

    public boolean removeSlideChangedListener(SlideChangedListener listener) {
        if (this.slideChangedListener.contains(listener)) {
            return this.slideChangedListener.remove(listener);
        }
        return false;
    }

    private void notifySlideChangedListeners(int slideIndex) {
        getStepsLength();
        for (SlideChangedListener listener : this.slideChangedListener) {
            listener.onSlideChanged(slideIndex);
        }
    }

    public void addPageLoadCompleteListener(PageLoadCompleteListener listener) {
        this.pageLoadCompleteListener.add(listener);
    }

    public boolean removePageLoadCompleteListener(PageLoadCompleteListener listener) {
        if (this.pageLoadCompleteListener.contains(listener)) {
            return this.pageLoadCompleteListener.remove(listener);
        }
        return false;
    }

    private void notifyPageLoadCompleteListeners() {
        for (PageLoadCompleteListener listener : this.pageLoadCompleteListener) {
            listener.onPageLoadComplete();
        }
    }

    /**
     * Set the current slide index, please only use this method as it notifies the listeners.
     * 
     * @param index
     */
    private void setSlideIndex(int index) {
        // System.out.println("Slide now: " + index);
        this.currentSlide = index;
        notifySlideChangedListeners(index);
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
        layoutInArea(webview, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
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