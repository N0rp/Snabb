package eu.dowsing.leap.brick;

import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.ScreenTapGesture;

import eu.dowsing.leap.brick.HandRect.Importance;

public class BrickMenuController extends Listener {

    /** NO ID FOUND is equal to -1. **/
    private final static int NO_ID = -1;

    // private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

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

    private ObjectProperty<Swipy> gestury = new SimpleObjectProperty<>();

    // public ObservableValue<Point2D> pointProperty() {
    // return point;
    // }
    //
    // public ObjectProperty<Swipy> gestureProperty() {
    // return gestury;
    // }

    /** Id of the primary controlling hand. -1 if there is no primary Hand. **/
    private int primaryId = BrickMenuController.NO_ID;
    /** Id of the secondary controlling hand. -1 if there is no secondary Hand. **/
    private int secondaryId = BrickMenuController.NO_ID;

    /** If true a gesture has been recognized and the hands need to be reset into non-active by the user. **/
    private boolean activeGestureResponse = false;

    private List<ActiveMovementListener> activeMovementListener = new LinkedList<>();

    private BrickMenuView view;

    private BrickMenuAdapterInterface adapter;

    private BrickGesture gesture = new BrickGesture();

    public BrickMenuController(BrickMenuView view, BrickMenuAdapterInterface adapter) {
        this.view = view;
        this.adapter = adapter;
    }

    public void addActiveMovementListener(ActiveMovementListener listener) {
        this.activeMovementListener.add(listener);
    }

    public void removeActiveMovementListener(ActiveMovementListener listener) {
        this.activeMovementListener.remove(listener);
    }

    /**
     * Notifies listeners and returns if at most one of them has handled the gesture.
     * 
     * @return <code>true</code> if the gesture was handled by at most one listener, else <code>false</code>
     */
    private boolean notifyActiveMovementListener() {
        boolean handled = false;
        for (ActiveMovementListener listener : this.activeMovementListener) {
            handled |= listener.onActiveMovement(gesture);
            if (handled) {
                // only allow the first listener to handle gesture
                break;
            }
        }
        return handled;
    }

    @Override
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                handleMovements(frame.hands());
                // Hand hand = frame.hands().get(0);

                // if (hand.isValid()) {
                // // hand.palmPosition()
                // Vector intersect = screen.intersect(hand.palmPosition(), hand.direction(), true);
                // // point.setValue(new Point2D(screen.widthPixels() * Math.min(1d, Math.max(0d, intersect.getX())),
                // // screen.heightPixels() * Math.min(1d, Math.max(0d, (1d - intersect.getY())))));
                // // System.out.println("Palm position: " + hand.palmPosition());
                // }

                // look for gestures
                handleGestures(controller, frame.gestures());

            }
        } else {
            gesture.clear();
        }
    }

    private void handleGestures(Controller controller, GestureList gestures) {
        boolean foundSwipe = false;
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);
            switch (gesture.type()) {
                case TYPE_SWIPE:
                    if (gesture.state() == Gesture.State.STATE_STOP) {
                        // SwipeGesture swipe = new SwipeGesture(gesture);
                        // System.out.println("Swipe id: " + swipe.id() + ", " + swipe.state() + ", fingers "
                        // + gesture.pointables().count() + " position: " + swipe.position() + ", direction: "
                        // + swipe.direction() + ", speed: " + swipe.speed() + ", duration: " + swipe.duration());
                        // if (swipe.hands().count() > 0 && swipe.hands().get(0).fingers().count() == 2) {
                        // System.out.println(" Two-Finger Swipe");
                        // if (Math.abs(swipe.startPosition().getX()) > 100) {
                        // System.out.println(" Swipe started reasonably");
                        // foundSwipe = true;
                        // System.out.println(" Swipe done");
                        // if (swipe.direction().getX() > 0) {
                        // // swipe right
                        // System.out.println(" Swipe Right");
                        // gestury.setValue(new Swipy(Direction.RIGHT));
                        // } else if (swipe.direction().getX() < 0) {
                        // // swipe left
                        // System.out.println(" Swipe Left");
                        // gestury.setValue(new Swipy(Direction.LEFT));
                        // }
                        // }
                        // }
                    }
                    break;
                case TYPE_CIRCLE:
                    CircleGesture circle = new CircleGesture(gesture);

                    // Calculate clock direction using the angle between circle normal and pointable
                    String clockwiseness;
                    if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
                        // Clockwise if angle is less than 90 degrees
                        clockwiseness = "clockwise";
                    } else {
                        clockwiseness = "counterclockwise";
                    }

                    // Calculate angle swept since last frame
                    double sweptAngle = 0;
                    if (circle.state() != State.STATE_START) {
                        CircleGesture previousUpdate = new CircleGesture(controller.frame(1).gesture(circle.id()));
                        sweptAngle = (circle.progress() - previousUpdate.progress()) * 2 * Math.PI;
                    }

                    System.out.println("Circle id: " + circle.id() + ", " + circle.state() + ", progress: "
                            + circle.progress() + ", radius: " + circle.radius() + ", angle: "
                            + Math.toDegrees(sweptAngle) + ", " + clockwiseness);
                    break;
                case TYPE_SCREEN_TAP:
                    ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
                    System.out.println("Screen Tap id: " + screenTap.id() + ", " + screenTap.state() + ", position: "
                            + screenTap.position() + ", direction: " + screenTap.direction());
                    break;
                case TYPE_KEY_TAP:
                    KeyTapGesture keyTap = new KeyTapGesture(gesture);
                    System.out.println("Key Tap id: " + keyTap.id() + ", " + keyTap.state() + ", position: "
                            + keyTap.position() + ", direction: " + keyTap.direction());
                    break;
                default:

                    break;
            }
        }
        if (!foundSwipe) {
            gestury.set(new Swipy());
        }
    }

    private void handleMovements(HandList hands) {
        final Hand primary = getPrimaryHand(hands);
        final Hand secondary = getSecondaryHand(hands);

        boolean activeMovement = gesture.addHands(primary, secondary, adapter);
        final Brick prim = gesture.getPrimary();
        final Brick sec = gesture.getSecondary();

        if ((prim == null || !prim.isActive()) && (sec == null || !sec.isActive())) {
            // reset gesture notification
            this.activeGestureResponse = false;
            this.gesture.clear();
        }

        // if a movement was found but no gesture listener has responded a gesture
        if (activeMovement && !this.activeGestureResponse) {
            // remember if one of the listeners has responded to the gesture
            System.out.println("Notifying gesture listener with " + gesture);
            this.activeGestureResponse = notifyActiveMovementListener();
            if (this.activeGestureResponse) {
                System.out.println("GEsture was handled");
            }
        }

        view.clearHands();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (primary != null) {
                    if (prim.getSubCategory() >= 0) {
                        view.showHand(Importance.PRIMARY, prim, activeGestureResponse, gesture.getPrimaryActiveStart());
                    }

                    view.showCategoryHint(prim);
                }

                if (secondary != null) {
                    // System.out.println("Found Secondary");
                    Brick sec = new Brick(secondary, adapter);
                    if (sec.getSubCategory() >= 0) {
                        view.showHand(Importance.SECONDARY, sec, activeGestureResponse,
                                gesture.getSecondaryActiveStart());
                    }
                }
            }
        });

    }

    /**
     * Pick the best hand between a list and the previous good hand
     * 
     * @param hands
     *            list of all possible hands
     * @param previous
     *            the previous best hand
     * @param exclude
     *            the hand that is to be excluded
     * @return
     */
    private Hand getBestHand(HandList hands, final int previousId, final int excludeId) {
        Hand current = null;
        if (previousId >= 0) {
            // check if primary hand still available
            for (Hand hand : hands) {
                if (hand.id() == previousId) {
                    if (!hand.isValid()) {
                        current = null;
                    } else {
                        current = hand;
                    }
                }
            }
        } else {
            // find a new good hand
            if (excludeId < 0 || hands.frontmost().id() != excludeId) {
                current = hands.frontmost();
            } else {
                // pick the first hand found
                for (Hand hand : hands) {
                    if (hand.isValid() && hand.id() != excludeId) {
                        current = hand;
                        break;
                    }
                }
            }
        }

        // make sure at least one hand is picked
        if (current == null && hands.count() > 0 && hands.get(0).id() != excludeId) {
            return hands.get(0);
        }

        return current;
    }

    /**
     * Get the hand responsible for primary interaction
     * 
     * @param hands
     * @return
     */
    private Hand getPrimaryHand(HandList hands) {
        Hand primary = getBestHand(hands, primaryId, secondaryId);
        if (primary != null) {
            this.primaryId = primary.id();
        } else {
            this.primaryId = BrickMenuController.NO_ID;
        }
        return primary;
    }

    /**
     * Get the hand responsible for supplementing the primary hand.
     * 
     * @param hands
     * @return
     */
    private Hand getSecondaryHand(HandList hands) {
        Hand secondary = getBestHand(hands, secondaryId, primaryId);
        if (secondary != null) {
            this.secondaryId = secondary.id();
        } else {
            this.secondaryId = BrickMenuController.NO_ID;
        }
        return secondary;
    }
}
