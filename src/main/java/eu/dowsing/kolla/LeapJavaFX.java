package eu.dowsing.kolla;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import eu.dowsing.kolla.app.AppSwitcher;
import eu.dowsing.kolla.app.debug.DebugApp;
import eu.dowsing.kolla.app.presentation.PresentationApp;
import eu.dowsing.kolla.widget.presentation.view.PresentationView;
import eu.dowsing.leap.experiments.ScreenController;
import eu.dowsing.leap.storage.MainProperties;

public class LeapJavaFX extends Application {
    // private final AudioClip ALERT_AUDIOCLIP;

    private enum Visualize {
        Test, Presentation
    }

    // private SimpleLeapListener listener = new SimpleLeapListener();
    private MainProperties mainPropManager = MainProperties.getInstance();

    private Visualize visualize = Visualize.Presentation;

    private PresentationView browser;

    private Scene scene;

    private final int sceneWidth = 800;
    private final int sceneHeight = 600;

    private final boolean playFirstTrack = false;

    public final static boolean showLeapMenuOnStart = false;

    private AppSwitcher appSwitcher;

    public LeapJavaFX() {
        // File f = new File(mainPropManager.getProperty(Key.BEEP_SOUND));
        // System.out.println("Absolute file path is : " + f.getAbsolutePath());
        // ALERT_AUDIOCLIP = new AudioClip("file://" + f.getAbsolutePath());
    }

    @Override
    public void start(final Stage primaryStage) {
        appSwitcher = new AppSwitcher(primaryStage);
        int debugId = appSwitcher.addApp(new DebugApp());
        int presentationId = appSwitcher.addApp(new PresentationApp());
        appSwitcher.showApp(presentationId);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // stop();
                System.out.println("OnCloseRequest");
                // stop();
                appSwitcher.shutdown();
            }
        });

        // init view
        this.scene = null;

        // create all possible screens
        // initScreens(primaryStage, menuController);

        // pick one of the possible screens
        // if (visualize == Visualize.Test) {
        // primaryStage.setTitle("Leap Test View");
        // currentRoot = screens.get(Visualize.Test);
        // dC.setActive(true);
        // } else if (visualize == Visualize.Presentation) {
        // primaryStage.setTitle("Presentation");
        // currentRoot = screens.get(Visualize.Presentation);
        // pC.setActive(true);
        // }

        scene = new Scene(appSwitcher.getAppView(), this.sceneWidth, this.sceneHeight);

        initLeap(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Showing");
        // Stage secondStage = new SpeakerWindow(700, 700, Browser.LOCAL_HOVERCRAFT, UrlLocation.Local);
        // secondStage.show();
        // secondStage.setX(0);
    }

    private ScreenController pC;
    private ScreenController dC;

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
        appSwitcher.shutdown();
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
