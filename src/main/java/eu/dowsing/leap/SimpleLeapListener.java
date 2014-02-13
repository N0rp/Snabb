package eu.dowsing.leap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class SimpleLeapListener extends Listener {

    public enum Direction {
        LEFT, RIGHT
    }

    public class Gestures {
        private Direction dir;

        public Gestures(Direction dir) {
            this.dir = dir;
        }

        public Direction getDirection() {
            return dir;
        }

    }

    private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    private ObjectProperty<Gestures> gestury = new SimpleObjectProperty<>();

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    public ObjectProperty<Gestures> gestureProperty() {
        return gestury;
    }

    @Override
    public void onFrame(Controller controller) {
        Frame tmpFrame = new Frame();
        Hand tmpHand = new Hand();

        HandList tmpHandList = new HandList();

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
                }

                // look for gestures
                GestureList gestures = frame.gestures();
                for (int i = 0; i < gestures.count(); i++) {
                    Gesture gesture = gestures.get(i);

                    switch (gesture.type()) {
                        case TYPE_SWIPE:
                            SwipeGesture swipe = new SwipeGesture(gesture);
                            System.out.println("Swipe id: " + swipe.id() + ", " + swipe.state() + ", position: "
                                    + swipe.position() + ", direction: " + swipe.direction() + ", speed: "
                                    + swipe.speed() + ", duration: " + swipe.duration());
                            if (swipe.state().equals("STATE_STOP")) {
                                System.out.println("Swipe done");
                                if (swipe.direction().getX() > 0) {
                                    // swipe right
                                    System.out.println("Right");
                                    gestury.setValue(new Gestures(Direction.RIGHT));
                                } else if (swipe.direction().getX() < 0) {
                                    // swipe left
                                    System.out.println("Left");
                                    gestury.setValue(new Gestures(Direction.LEFT));
                                }
                            }
                            break;
                        default:

                            break;
                    }
                }
            }
        }
    }
}
