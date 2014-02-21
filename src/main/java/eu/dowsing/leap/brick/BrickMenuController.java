package eu.dowsing.leap.brick;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
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
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

import eu.dowsing.leap.brick.HandRect.Importance;
import eu.dowsing.leap.brick.HandRect.Position;

public class BrickMenuController extends Listener {

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

    private Hand primary;
    private Hand secondary;

    private BrickMenuView view;

    private BrickMenuAdapterInterface adapter;

    public BrickMenuController(BrickMenuView view, BrickMenuAdapterInterface adapter) {
        this.view = view;
        this.adapter = adapter;
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
    private Hand getBestHand(HandList hands, final Hand previous, final Hand exclude) {
        Hand current = null;
        if (previous != null) {
            // check if primary hand still available
            for (Hand hand : hands) {
                if (hand.isValid() && hand.id() == previous.id()) {
                    if (!hand.isValid()) {
                        current = null;
                    } else {
                        current = hand;
                    }
                }
            }
        } else {
            // find a new good hand
            if (exclude == null || hands.frontmost().id() != exclude.id()) {
                current = hands.frontmost();
            } else {
                // pick the first hand found
                for (Hand hand : hands) {
                    if (hand.isValid() && hand.id() != exclude.id()) {
                        current = hand;
                        break;
                    }
                }
            }
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
        this.primary = getBestHand(hands, primary, secondary);
        return this.primary;
    }

    /**
     * Get the hand responsible for supplementing the primary hand.
     * 
     * @param hands
     * @return
     */
    private Hand getSecondaryHand(HandList hands) {
        this.secondary = getBestHand(hands, secondary, primary);
        return this.secondary;
    }

    private Position getHandRoll(Hand hand) {
        // roll is 90 or -90 when the hand is vertical
        Vector normal = hand.palmNormal();
        double roll = Math.toDegrees(normal.roll());
        roll = Math.abs(roll);
        if (roll > 75 && roll < 105) {
            return Position.VERTICAL;
        } else {
            return Position.VERTICAL;
        }
    }

    private void handleMovements(HandList hands) {
        Hand primary = getPrimaryHand(hands);
        Hand secondary = getSecondaryHand(hands);

        view.clearHands();
        if (primary != null) {
            // get primary (sub)category
            int pcategory = getCategory(primary);
            if (pcategory >= 0) {
                int psubCategory = getSubCategory(pcategory, primary);
                if (psubCategory >= 0) {
                    view.showHand(pcategory, psubCategory, Importance.PRIMARY, getHandRoll(primary), primary.fingers()
                            .count());
                }
            }

            if (secondary != null) {
                // get secondary (sub)category
                int scategory = getCategory(secondary);
                if (scategory >= 0) {
                    int ssubCategory = getSubCategory(scategory, secondary);
                    if (ssubCategory >= 0) {
                        view.showHand(scategory, ssubCategory, Importance.SECONDARY, getHandRoll(secondary), secondary
                                .fingers().count());
                    }
                }
            }
        }

        if (!hands.isEmpty()) {

            Hand hand = primary;
            if (primary == null) {
                return;
            }
            FingerList fingers = hand.fingers();
            if (!fingers.isEmpty()) {
                // Calculate the hand's average finger tip position
                Vector avgPos = Vector.zero();
                List<Integer> fingerIds = new LinkedList<>();
                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                    if (finger.isValid() && finger.isFinger()) {
                        fingerIds.add(finger.id());
                    }
                }
                avgPos = avgPos.divide(fingers.count());
                // System.out.println("Hand has " + fingers.count() + " fingers, average finger tip position: " +
                // avgPos);
                // System.out.println("Hand has fingers with ids: " + fingerIds);
            }

            // Get the hand's sphere radius and palm position
            // System.out.println("Hand sphere radius: " + hand.sphereRadius() + " mm, palm position: "
            // + hand.palmPosition());

            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();

            // Calculate the hand's pitch, roll, and yaw angles
            // System.out.println("Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, " + "roll: "
            // + Math.toDegrees(normal.roll()) + " degrees, " + "yaw: " + Math.toDegrees(direction.yaw())
            // + " degrees");
            // System.out.println("Primary Hand position: " + primary.palmPosition() + "normal: " + normal);
        }
    }

    private int getCategories() {
        if (this.adapter != null) {
            return this.adapter.getCategories();
        } else {
            return 0;
        }
    }

    private int getSubCategories(int category) {
        if (this.adapter != null) {
            return this.adapter.getSubCategories(category);
        } else {
            return 0;
        }
    }

    /**
     * Get the category index for the hand.
     * 
     * @param hand
     * @return the category for the hand, or -1 if there is none.
     */
    private int getCategory(Hand hand) {
        final int minmin = 0;
        final int min = minmin + 100;
        final int max = 400;
        final int maxmax = max + 100;
        // x between -200 (left) and 200 (right)
        // y between 0 (on leap) and 500 (high above leap)?
        // z between -200 (behind leap) and 200 (between leap and user)
        Vector pos = hand.palmPosition();
        if (pos.getY() > minmin && pos.getY() < maxmax) {
            int count = getCategories();
            if (pos.getY() <= min) {
                return 0;
            } else if (pos.getY() >= max) {
                return count - 1;
            } else {
                return (int) (pos.getY() - min) / (max / count);
            }
        } else {
            return -1;
        }
    }

    /**
     * Get the subcategory.
     * 
     * @param category
     * @param hand
     * @return get the subcategory or -1 if there is none.
     */
    private int getSubCategory(int category, Hand hand) {
        final int min = -200;
        final int max = -min;
        final int minmin = min - 50;
        final int maxmax = max + 50;
        // x between -200 (left) and 200 (right)
        // y between 0 (on leap) and 500 (high above leap)?
        // z between -200 (behind leap) and 200 (between leap and user)
        Vector pos = hand.palmPosition();
        if (pos.getX() > minmin && pos.getX() < maxmax) {
            int count = getSubCategories(category);
            if (pos.getX() <= min) {
                return 0;
            } else if (pos.getX() >= max) {
                return count - 1;
            } else {
                float x = pos.getX() + max;
                return (int) (x) / (max * 2 / count);
            }
        }

        return -1;
    }

    private void handleGestures(Controller controller, GestureList gestures) {
        boolean foundSwipe = false;
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);
            switch (gesture.type()) {
                case TYPE_SWIPE:
                    if (gesture.state() == Gesture.State.STATE_STOP) {
                        SwipeGesture swipe = new SwipeGesture(gesture);
                        System.out.println("Swipe id: " + swipe.id() + ", " + swipe.state() + ", fingers "
                                + gesture.pointables().count() + " position: " + swipe.position() + ", direction: "
                                + swipe.direction() + ", speed: " + swipe.speed() + ", duration: " + swipe.duration());
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
        }
    }
}
