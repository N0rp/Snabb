package eu.dowsing.leap;

import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import eu.dowsing.leap.brick.ActiveMovementListener;
import eu.dowsing.leap.brick.BrickGesture;
import eu.dowsing.leap.brick.BrickGesture.GestureType;

public class DebugController implements ScreenController, ActiveMovementListener {

    private Stage stage;
    private Text text;
    private boolean active;

    public DebugController(Stage stage, Text text) {
        this.stage = stage;
        this.text = text;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean onActiveMovement(BrickGesture gesture) {
        if (isActive()) {
            if (gesture.getGestureType() == GestureType.LEFT2RIGHT
                    || gesture.getGestureType() == GestureType.ALMOST_LEFT2RIGHT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is left to right");
                        text.setText("Left to Right");
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.RIGHT2LEFT
                    || gesture.getGestureType() == GestureType.ALMOST_RIGHT2LEFT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is right to left");
                        text.setText("Right to Left");
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.DIVIDE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is divide");
                        text.setText("Divide");
                        stage.setFullScreen(true);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.UNITE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is unite");
                        text.setText("Unite");
                        stage.setFullScreen(false);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.NONE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        text.setText("---");
                    }
                });
                return false;
            }
        }
        return false;
    }

}
