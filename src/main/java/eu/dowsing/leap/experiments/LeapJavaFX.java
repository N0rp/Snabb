package eu.dowsing.leap.experiments;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.ODSingleXMLDocument;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

import eu.dowsing.leap.experiments.SimpleLeapListener.Gestures;
import eu.dowsing.leap.gesture.DoubleHandListener;
import eu.dowsing.leap.storage.MainProperties;
import eu.dowsing.leap.storage.MainProperties.Key;

public class LeapJavaFX extends Application {
    private final AudioClip ALERT_AUDIOCLIP;

    private SimpleLeapListener listener = new SimpleLeapListener();
    private DoubleHandListener doubleListener = new DoubleHandListener();
    private Controller leapController = new Controller();

    private AnchorPane root = new AnchorPane();
    private Circle circle = new Circle(50, Color.DEEPSKYBLUE);

    private MainProperties mainPropManager = MainProperties.getInstance();

    public LeapJavaFX() {
        File f = new File(mainPropManager.getProperty(Key.BEEP_SOUND));
        ALERT_AUDIOCLIP = new AudioClip("file://" + f.getAbsolutePath());
    }

    @Override
    public void start(Stage primaryStage) {

        PdfDecoder pdf = new PdfDecoder();
        try {
            pdf.openPdfFile("res/doc/Test.pdf");
        } catch (PdfException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // showPage(1);

        String musicLocation = mainPropManager.getProperty(Key.MUSIC_LOCATION);
        System.out.println("Music is located in: " + musicLocation);
        // leapController.addListener(listener);
        leapController.addListener(listener);
        leapController.addListener(doubleListener);

        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);

        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);
        final Scene scene = new Scene(root, 800, 600);

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

        listener.gestureProperty().addListener(new ChangeListener<Gestures>() {

            @Override
            public void changed(ObservableValue<? extends Gestures> observable, Gestures oldValue, Gestures newValue) {
                if (newValue.getSwiped()) {
                    System.out.println("Swiped!!! at Observer");
                    FXRobot robot = FXRobotFactory.createRobot(scene);
                    robot.keyPress(javafx.scene.input.KeyCode.LEFT);
                    ALERT_AUDIOCLIP.play();
                }
            }

        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Load Presentation");
        ODPackage p = new ODPackage(new File("res/doc/Test.odp"));
        ODSingleXMLDocument doc = p.toSingle();

        // System.out.println("Load Spreadsheet");
        // final OpenDocument spread = new OpenDocument();
        // spread.loadFrom("res/doc/Test.ods");
        Application.launch(LeapJavaFX.class);
    }
}
