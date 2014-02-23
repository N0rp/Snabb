package eu.dowsing.leap.brick.presentation;

import javafx.application.Platform;
import javafx.stage.Stage;
import eu.dowsing.leap.ScreenController;
import eu.dowsing.leap.brick.ActiveMovementListener;
import eu.dowsing.leap.brick.BrickGesture;
import eu.dowsing.leap.brick.BrickGesture.GestureType;
import eu.dowsing.leap.pres.Browser;

public class PresentationController implements ActiveMovementListener, ScreenController {

    private Browser browser;
    private Stage stage;
    private boolean isActive;

    public PresentationController(Stage stage, Browser browser) {
        this.stage = stage;
        this.browser = browser;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean onActiveMovement(BrickGesture gesture) {
        if (isActive) {
            if (gesture.getGestureType() == GestureType.LEFT2RIGHT
                    || gesture.getGestureType() == GestureType.ALMOST_LEFT2RIGHT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is left to right");
                        browser.gotoNextPage();
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.RIGHT2LEFT
                    || gesture.getGestureType() == GestureType.ALMOST_RIGHT2LEFT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is right to left");
                        browser.gotoPrevPage();
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.DIVIDE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is divide");
                        stage.setFullScreen(true);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.UNITE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is unite");
                        stage.setFullScreen(false);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.NONE) {
                Platform.runLater(new Runnable() {
                    public void run() {

                    }
                });
                return false;
            }
        }
        return false;
    }

}
