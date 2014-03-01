package eu.dowsing.kolla;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

import eu.dowsing.kolla.app.presentation.PresentationController;
import eu.dowsing.kolla.widget.brick.BrickMenuAdapterInterface;
import eu.dowsing.kolla.widget.brick.NumberTypedListener;
import eu.dowsing.kolla.widget.brick.control.BrickMenuController;
import eu.dowsing.kolla.widget.brick.facade.BrickMenuView;
import eu.dowsing.leap.brick.presentation.BasicMenuAdapter;
import eu.dowsing.leap.pres.Browser;
import eu.dowsing.leap.pres.Browser.UrlLocation;
import eu.dowsing.leap.pres.PageLoadCompleteListener;
import eu.dowsing.leap.pres.SlideChangedListener;
import eu.dowsing.leap.storage.MainProperties;
import eu.dowsing.leap.storage.MainProperties.Key;

public class LeapJavaFX extends Application {
    // private final AudioClip ALERT_AUDIOCLIP;

    private enum Visualize {
        Test, Presentation
    }

    // private SimpleLeapListener listener = new SimpleLeapListener();
    private BrickMenuController menuController;
    private Controller leapController = new Controller();

    private Pane root;
    private Circle circle = new Circle(50, Color.DEEPSKYBLUE);
    private Text debugText;

    private MainProperties mainPropManager = MainProperties.getInstance();

    private Visualize visualize = Visualize.Presentation;

    private Browser browser;

    private Scene scene;

    private Map<Visualize, Pane> screens = new HashMap<Visualize, Pane>();

    private BrickMenuView overlay;

    private final int sceneWidth = 800;
    private final int sceneHeight = 600;

    private final boolean playFirstTrack = false;

    public final static boolean showLeapMenuOnStart = false;

    private ScheduledExecutorService executorService;

    public LeapJavaFX() {
        // File f = new File(mainPropManager.getProperty(Key.BEEP_SOUND));
        // System.out.println("Absolute file path is : " + f.getAbsolutePath());
        // ALERT_AUDIOCLIP = new AudioClip("file://" + f.getAbsolutePath());
    }

    @Override
    public void start(final Stage primaryStage) {
        BrickMenuAdapterInterface menuAdapter = new BasicMenuAdapter();
        overlay = new BrickMenuView(this.sceneWidth, this.sceneHeight);
        overlay.setMouseTransparent(true);
        overlay.setAdapter(menuAdapter);

        // create new executor service
        executorService = Executors.newScheduledThreadPool(1);

        menuController = new BrickMenuController(leapController, primaryStage, overlay, menuAdapter, executorService);

        // schedule first run, subsequent runs will be schedule by runnable itself
        executorService.schedule(menuController, 1, TimeUnit.SECONDS);

        // enable leap gestures and set their configuration
        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);
        if (leapController.config().setFloat("Gesture.Swipe.MinLength", 100)
                && leapController.config().setFloat("Gesture.Swipe.MinVelocity", 250)) {
            leapController.config().save();
        }

        leapController.addListener(menuController);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // stop();
                System.out.println("OnCloseRequest");
                // stop();
                executorService.shutdown();
            }
        });

        // init view
        this.scene = null;
        this.root = null;

        // create all possible screens
        initScreens(primaryStage, menuController);

        // pick one of the possible screens
        Pane currentRoot = null;
        if (visualize == Visualize.Test) {
            primaryStage.setTitle("Leap Test View");
            currentRoot = screens.get(Visualize.Test);
            dC.setActive(true);
        } else if (visualize == Visualize.Presentation) {
            primaryStage.setTitle("Presentation");
            currentRoot = screens.get(Visualize.Presentation);
            pC.setActive(true);
        }

        this.root = new StackPane();
        root.getChildren().addAll(currentRoot, overlay);

        scene = new Scene(root, this.sceneWidth, this.sceneHeight);

        loadMusic();
        initLeap(scene);

        // KeyEvent.KEY_RELEASED
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, menuController);
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                // logitech presenter keyboard events are:
                // F5 / ESCAPE is presentation Start/Pause
                // PERIOD is blank screen on/off
                // next/previous slide is not caught here,
                // probably directed to webview instead, but does what it should anyway
                if (event.getCode() == KeyCode.F5) {
                    // go fullscreen
                    primaryStage.setFullScreen(true);
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    // undo fullscreen
                    primaryStage.setFullScreen(false);
                    event.consume();
                } else if (event.getCode() == KeyCode.PERIOD) {
                    // show blank screen
                    browser.setVisible(!browser.isVisible());
                }
            }

        });

        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Showing");
        // Stage secondStage = new SpeakerWindow(700, 700, Browser.LOCAL_HOVERCRAFT, UrlLocation.Local);
        // secondStage.show();
        // secondStage.setX(0);
    }

    private ScreenController pC;
    private ScreenController dC;

    private void initScreens(final Stage stage, BrickMenuController menuController) {
        this.screens.put(Visualize.Test, createScreen(stage, Visualize.Test, menuController));
        this.screens.put(Visualize.Presentation, createScreen(stage, Visualize.Presentation, menuController));
    }

    private Pane createScreen(final Stage stage, Visualize visualize, BrickMenuController menuController) {
        if (visualize == Visualize.Test) {
            Pane current = new AnchorPane();
            loadCircleDisplay(current);

            dC = new DebugController(stage, debugText);
            menuController.addActiveMovementListener((DebugController) dC);
            return current;
        } else if (visualize == Visualize.Presentation) {
            // VBox box = new VBox();
            this.browser = new Browser(Browser.LOCAL_INNOTALK, UrlLocation.Local);
            overlay.setBrowser(browser);

            pC = new PresentationController(stage, browser);
            menuController.addActiveMovementListener((PresentationController) pC);
            Pane pane = new StackPane();
            pane.getChildren().add(browser);

            menuController.addNumberTypedListener(new NumberTypedListener() {

                @Override
                public void onNumberUpdate(int number, boolean isSubmitted) {
                    if (isSubmitted) {
                        browser.gotoPage(number - 1);
                    }
                }
            });

            // Label test = new Label("Test");
            // box.getChildren().add(test);
            // box.getChildren().add(browser);

            this.browser.addSlideChangedListener(new SlideChangedListener() {

                @Override
                public void onSlideChanged(final int slideIndex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // System.out.println("Slide changed");
                            overlay.setCurrentStepText(slideIndex + "");
                        }
                    });
                }
            });

            this.browser.addPageLoadCompleteListener(new PageLoadCompleteListener() {

                @Override
                public void onPageLoadComplete() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            overlay.setCurrentKeyInputText("Laden Fertig");
                        }
                    });
                }
            });

            return pane;
        } else {
            return null;
        }
    }

    // private void loadPdf() {

    // PdfDecoder pdf = new PdfDecoder();
    //
    // try {
    // pdf.openPdfFile("res/doc/Teshttp://open.spotify.com/track/7FoMWu6ddV7qMP7itqWoLEt.pdf");
    // } catch (PdfException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // showPage(1);

    // }

    private void loadCircleDisplay(final Pane root) {

        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);

        debugText = TextBuilder.create().text("Overlay").translateY(100).font(Font.font("Arial", FontWeight.BOLD, 18))
                .fill(Color.BLUE).build();

        // circle.setLayoutX(circle.getRadius());
        // circle.setLayoutY(circle.getRadius());
        // root.getChildren().add(circle);
        // final Scene scene = new Scene(root, 800, 600);

        // listener.pointProperty().addListener(new ChangeListener<Point2D>() {
        // @Override
        // public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
        // Platform.runLater(new Runnable() {
        // @Override
        // public void run() {
        // if (LeapJavaFX.this.scene != null) {
        // Scene scene = LeapJavaFX.this.scene;
        //
        // Point2D d = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
        // t1.getY() - scene.getY() - scene.getWindow().getY());
        // double dx = d.getX(), dy = d.getY();
        // if (dx >= 0d && dx <= root.getWidth() - 2d * circle.getRadius() && dy >= 0d
        // && dy <= root.getHeight() - 2d * circle.getRadius()) {
        // circle.setTranslateX(dx);
        // circle.setTranslateY(dy);
        // }
        // }
        // }
        // });
        // }
        // });
    }

    /**
     * Get all music tracks in the file.
     * 
     * @param parent
     * @return
     */
    private List<File> getTracks(File parent) {
        List<File> tracks = new LinkedList<>();
        if (parent.isDirectory()) {
            for (File child : parent.listFiles()) {
                // Do something with child
                if (child.isFile()) {
                    String extension = "";

                    int i = child.getName().lastIndexOf('.');
                    if (i > 0) {
                        extension = child.getName().substring(i + 1);
                        if (extension.equals("mp3")) {
                            tracks.add(child);
                        }
                    }
                } else if (child.isDirectory()) {
                    tracks.addAll(getTracks(child));
                }
            }
        }
        return tracks;
    }

    private void loadMusic() {
        File test;

        test = new File("/Users/richardg/Music");
        if (!test.exists()) {
            System.err.println("Does not exist: " + test.getName());
        }

        String musicLocation = mainPropManager.getProperty(Key.MUSIC_LOCATION);
        System.out.println("Music is located in: " + musicLocation);
        File music = new File(musicLocation);
        List<File> tracks = null;
        if (music.exists()) {
            tracks = getTracks(music);
            if (tracks.size() > 0) {
                System.out.println("Found " + tracks.size() + " tracks. First track is " + tracks.get(0).getName());
            } else {
                System.out.println("Found no music tracks");
            }
        } else {
            System.err.println("Music Location could not be found: " + musicLocation);
        }

        if (tracks != null && tracks.size() > 0) {
            String path = tracks.get(0).getAbsolutePath();
            // path = path.replaceAll("[^a-zA-Z0-9.-]", "_");
            try {
                path = URLEncoder.encode(path, "UTF-8");
                // path = "res/audio/music/Test.mp3";

                // path = "res/audio/beep-07-Soundjay.wav";

                path = "/Users/richardg/Documents/GIT/Snabb/res/audio/music/Test.mp3";

                if (!(new File(path)).exists()) {
                    System.err.println("File does not exist " + path);
                }

                // path = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
                path = "file://" + path;
                System.out.println("Loading file: " + path);
                Media media = new Media(path);
                System.out.println("Metadata is " + media.getMetadata());
                final MediaPlayer player = new MediaPlayer(media);

                if (playFirstTrack) {
                    (new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.play();
                        }
                    })).start();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // private void loadTrack(String )

    private void initLeap(final Scene scene) {
        // listener.gestureProperty().addListener(new ChangeListener<Swipy>() {
        //
        // @Override
        // public void changed(ObservableValue<? extends Swipy> observable, Swipy oldValue, Swipy newValue) {
        // if (newValue.isSwiped()) {
        // System.out.println("  Found swip gesture");
        // if (newValue.getDirection() == Direction.LEFT) {
        // System.out.println("  Swiped Left!!! at Observer");
        // FXRobot fxrobot = FXRobotFactory.createRobot(scene);
        // fxrobot.keyPress(javafx.scene.input.KeyCode.LEFT);
        // // ALERT_AUDIOCLIP.play();
        // if (browser != null) {
        // Platform.runLater(new Runnable() {
        //
        // @Override
        // public void run() {
        // browser.gotoPrevPage();
        // }
        // });
        // }
        //
        // } else if (newValue.getDirection() == Direction.RIGHT) {
        // System.out.println("  Swiped Right!!! at Observer");
        // FXRobot fxrobot = FXRobotFactory.createRobot(scene);
        // fxrobot.keyPress(javafx.scene.input.KeyCode.RIGHT);
        // // ALERT_AUDIOCLIP.play();
        // Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
        // robot.keyPress(KeyCode.RIGHT.impl_getCode());
        //
        // if (browser != null) {
        // Platform.runLater(new Runnable() {
        //
        // @Override
        // public void run() {
        // browser.gotoNextPage();
        // }
        // });
        // }
        // }
        // }
        // }
        // });

    }

    @Override
    public void stop() {
        System.out.println("JavaFx stop");
        executorService.shutdown();
        leapController.removeListener(menuController);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Load Presentation");
        // ODPackage p = new ODPackage(new File("res/doc/Test.odp"));
        // ODSingleXMLDocument doc = p.toSingle();

        // System.out.println("Load Spreadsheet");
        // final OpenDocument spread = new OpenDocument();
        // spread.loadFrom("res/doc/Test.ods");

        // set special loggin properties
        // System.setProperty("java.util.logging.config.file", "config/logging.properties");
        // java.util.logging.Logger.getLogger(com.sun.webpane.sg.prism.WCGraphicsPrismContext.class.getName()).setLevel(
        // java.util.logging.Level.OFF);
        // java.util.logging.Logger.getLogger(javafx.scene.web.WebView.class.getName()).setLevel(
        // java.util.logging.Level.OFF);
        // java.util.logging.Logger.getLogger(javafx.scene.web.WebView.class.getName()).setLevel(
        // java.util.logging.Level.OFF);

        // java.util.logging.Logger.getLogger(WCGraphicsPrismContext.class.getName()).setLevel(Level.OFF);
        // java.util.logging.Logger.getLogger(javafx.scene.web.WebEngine.class.getName()).setLevel(Level.OFF);
        // System.setProperty("detailedDebugMode", "false");

        Application.launch(LeapJavaFX.class);
    }
}
