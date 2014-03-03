package eu.dowsing.leap.experiments;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;

public class GesturesListener extends Listener {

    public enum Swipe {
        LEFT, RIGHT
    }

    /** time in miliseconds before a new gesture is recognized */
    private final static long     GESTURE_THRESHOLD  = 200;

    private final static double   GESTURE_DISTANCE_X = 0.5;

    /** time in miliseconds when the last gesture was made */
    private Map<Type, Long>       lastGestureAt      = new HashMap<>();

    private ObjectProperty<Swipe> gestury            = new SimpleObjectProperty<>();

    public ObjectProperty<Swipe> gestureProperty() {
        return gestury;
    }

    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        // look for gestures
        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            long currentGestureAt = System.currentTimeMillis();
            Gesture gesture = gestures.get(i);

            if (gesture.isValid()) {
                if (!lastGestureAt.containsKey(gesture.type())
                        || currentGestureAt - lastGestureAt.get(gesture.type()) > GESTURE_THRESHOLD) {
                    // if there has passed enough time between the gestures
                    switch (gesture.type()) {
                    case TYPE_SWIPE:
                        SwipeGesture swipe = new SwipeGesture(gesture);
                        System.out.println("Swipe gesture with direction: x:"
                                + swipe.direction().getX() + " y:"
                                + swipe.direction().getY());
                        System.out.println("Swipe gesture with duration: "
                                + swipe.duration());
                        if (Math.abs(swipe.direction().getX()) > GESTURE_DISTANCE_X) {
                            lastGestureAt.put(gesture.type(), currentGestureAt);
                            if (swipe.direction().getX() < -GESTURE_DISTANCE_X) {
                                gestury.setValue(Swipe.LEFT);
                            } else if (swipe.direction().getX() > GESTURE_DISTANCE_X) {
                                gestury.setValue(Swipe.RIGHT);
                            }
                        }
                        break;
                    default:

                        break;
                    }
                } else {
                    // System.out.println("Gesture TOO soon, difference only "
                    // + (currentGestureAt - lastGestureAt.get(gesture
                    // .type())));
                }
            }
        }
    }
}
