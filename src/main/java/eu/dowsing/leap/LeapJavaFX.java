package eu.dowsing.leap;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.sun.glass.ui.Robot;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

import eu.dowsing.leap.Browser.UrlLocation;
import eu.dowsing.leap.SimpleLeapListener.Direction;
import eu.dowsing.leap.SimpleLeapListener.Swipy;
import eu.dowsing.leap.gesture.DoubleHandListener;
import eu.dowsing.leap.storage.MainProperties;
import eu.dowsing.leap.storage.MainProperties.Key;

public class LeapJavaFX extends Application {
    // private final AudioClip ALERT_AUDIOCLIP;

    private SimpleLeapListener listener = new SimpleLeapListener();
    private DoubleHandListener doubleListener = new DoubleHandListener();
    private Controller leapController = new Controller();

    private AnchorPane root = new AnchorPane();
    private Circle circle = new Circle(50, Color.DEEPSKYBLUE);

    private MainProperties mainPropManager = MainProperties.getInstance();

    public LeapJavaFX() {
        // File f = new File(mainPropManager.getProperty(Key.BEEP_SOUND));
        // System.out.println("Absolute file path is : " + f.getAbsolutePath());
        // ALERT_AUDIOCLIP = new AudioClip("file://" + f.getAbsolutePath());
    }

    private enum Visualize {
        Leap, Presentation
    }

    private Visualize visualize = Visualize.Presentation;

    private Browser browser;

    @Override
    public void start(Stage primaryStage) {
        // init leap
        leapController.addListener(listener);
        leapController.addListener(doubleListener);

        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);
        if (leapController.config().setFloat("Gesture.Swipe.MinLength", 100)
                && leapController.config().setFloat("Gesture.Swipe.MinVelocity", 250))
            leapController.config().save();

        // init view
        Scene scene = null;
        if (visualize == Visualize.Leap) {
            primaryStage.setTitle("Leap Test View");
            loadCircleDisplay(scene);
            scene = new Scene(root, 800, 600);
        } else if (visualize == Visualize.Presentation) {
            // create the scene
            primaryStage.setTitle("Presentation");

            this.browser = new Browser(Browser.LOCAL_PRES, UrlLocation.Local);
            scene = new Scene(browser, 750, 500, Color.web("#666970"));
        }

        loadMusic();
        initLeap(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPdf() {

        // PdfDecoder pdf = new PdfDecoder();
        //
        // try {
        // pdf.openPdfFile("res/doc/Teshttp://open.spotify.com/track/7FoMWu6ddV7qMP7itqWoLEt.pdf");
        // } catch (PdfException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // showPage(1);

    }

    private void loadCircleDisplay(final Scene scene) {

        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);
        // circle.setLayoutX(circle.getRadius());
        // circle.setLayoutY(circle.getRadius());
        // root.getChildren().add(circle);
        // final Scene scene = new Scene(root, 800, 600);

        listener.pointProperty().addListener(new ChangeListener<Point2D>() {
            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Point2D d = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(), t1.getY()
                                - scene.getY() - scene.getWindow().getY());
                        double dx = d.getX(), dy = d.getY();
                        if (dx >= 0d && dx <= root.getWidth() - 2d * circle.getRadius() && dy >= 0d
                                && dy <= root.getHeight() - 2d * circle.getRadius()) {
                            circle.setTranslateX(dx);
                            circle.setTranslateY(dy);
                        }
                    }
                });
            }
        });
    }

    private void loadMusic() {
        String musicLocation = mainPropManager.getProperty(Key.MUSIC_LOCATION);
        System.out.println("Music is located in: " + musicLocation);
    }

    private void initLeap(final Scene scene) {
        listener.gestureProperty().addListener(new ChangeListener<Swipy>() {

            @Override
            public void changed(ObservableValue<? extends Swipy> observable, Swipy oldValue, Swipy newValue) {
                if (newValue.isSwiped()) {
                    System.out.println("  Found swip gesture");
                    if (newValue.getDirection() == Direction.LEFT) {
                        System.out.println("  Swiped Left!!! at Observer");
                        FXRobot fxrobot = FXRobotFactory.createRobot(scene);
                        fxrobot.keyPress(javafx.scene.input.KeyCode.LEFT);
                        // ALERT_AUDIOCLIP.play();
                        if (browser != null) {
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    browser.prevPage();
                                }
                            });
                        }

                    } else if (newValue.getDirection() == Direction.RIGHT) {
                        System.out.println("  Swiped Right!!! at Observer");
                        FXRobot fxrobot = FXRobotFactory.createRobot(scene);
                        fxrobot.keyPress(javafx.scene.input.KeyCode.RIGHT);
                        // ALERT_AUDIOCLIP.play();
                        Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
                        robot.keyPress(KeyCode.RIGHT.impl_getCode());

                        if (browser != null) {
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    browser.nextPage();
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    @Override
    public void stop() {
        leapController.removeListener(listener);

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Load Presentation");
        // ODPackage p = new ODPackage(new File("res/doc/Test.odp"));
        // ODSingleXMLDocument doc = p.toSingle();

        // System.out.println("Load Spreadsheet");
        // final OpenDocument spread = new OpenDocument();
        // spread.loadFrom("res/doc/Test.ods");
        Application.launch(LeapJavaFX.class);
    }
}
