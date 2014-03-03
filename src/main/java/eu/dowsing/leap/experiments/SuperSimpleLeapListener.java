package eu.dowsing.leap.experiments;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class SuperSimpleLeapListener extends Listener {

    public enum Direction {
        LEFT, RIGHT
    }

    public class Swipy {
        private Direction dir;

        private boolean swiped = false;

        public Swipy() {
            this.swiped = false;
        }

        public Swipy(Direction dir) {
            this.swiped = true;
            this.dir = dir;
        }

        public Direction getDirection() {
            return dir;
        }

        public boolean isSwiped() {
            return this.swiped;
        }

    }

    private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    private ObjectProperty<Swipy> gestury = new SimpleObjectProperty<>();

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    public ObjectProperty<Swipy> gestureProperty() {
        return gestury;
    }

    @Override
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                Hand hand = frame.hands().get(0);
                if (hand.isValid()) {
                    // hand.palmPosition()
                    Vector intersect = screen.intersect(hand.palmPosition(), hand.direction(), true);
                    point.setValue(new Point2D(screen.widthPixels() * Math.min(1d, Math.max(0d, intersect.getX())),
                            screen.heightPixels() * Math.min(1d, Math.max(0d, (1d - intersect.getY())))));
                    // System.out.println("Palm position: " + hand.palmPosition());
                }

                // look for gestures
                GestureList gestures = frame.gestures();
                boolean foundSwipe = false;
                for (int i = 0; i < gestures.count(); i++) {
                    Gesture gesture = gestures.get(i);
                    switch (gesture.type()) {
                        case TYPE_SWIPE:
                            if (gesture.state() == Gesture.State.STATE_STOP) {
                                SwipeGesture swipe = new SwipeGesture(gesture);
                                System.out.println("Swipe id: " + swipe.id() + ", " + swipe.state() + ", fingers "
                                        + gesture.pointables().count() + " position: " + swipe.position()
                                        + ", direction: " + swipe.direction() + ", speed: " + swipe.speed()
                                        + ", duration: " + swipe.duration());
                                if (swipe.hands().count() > 0 && swipe.hands().get(0).fingers().count() == 2) {
                                    System.out.println(" Two-Finger Swipe");
                                    if (Math.abs(swipe.startPosition().getX()) > 100) {
                                        System.out.println(" Swipe started reasonably");
                                        foundSwipe = true;
                                        System.out.println(" Swipe done");
                                        if (swipe.direction().getX() > 0) {
                                            // swipe right
                                            System.out.println(" Swipe Right");
                                            gestury.setValue(new Swipy(Direction.RIGHT));
                                        } else if (swipe.direction().getX() < 0) {
                                            // swipe left
                                            System.out.println(" Swipe Left");
                                            gestury.setValue(new Swipy(Direction.LEFT));
                                        }
                                    }
                                }
                            }
                            break;
                        default:

                            break;
                    }
                }
                if (!foundSwipe) {
                    gestury.set(new Swipy());
                }
            }
        }
    }
}
