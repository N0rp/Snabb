package eu.dowsing.leap.gesture;

import java.util.HashMap;
import java.util.Map;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Device;
import com.leapmotion.leap.DeviceList;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;

public class DoubleHandListener extends Listener {

    private enum HandForm {
        /** Hand is not valid **/
        INVALID,
        /** Hands form a fist, like rock **/
        ROCK,
        /** Hands are open, like rock **/
        PAPER,
        /** Hands form a V-Shape, like scissors **/
        SCISSORS,
        /** Hands form the sign of the devil and rock **/
        DEVIL,
        /** Hand form could not be determined **/
        UNDEFINED
    }

    /** The hands that started the hand listener. **/
    private Map<Integer, HandForm> activateHands;

    private Map<Integer, HandForm> currentHands;

    @Override
    public void onFrame(Controller controller) {
        Device device = new Device();
        DeviceList list = new DeviceList();

        Frame frame = controller.frame();
        currentHands = new HashMap<>();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                for (Hand hand : frame.hands()) {
                    if (hand.isValid()) {
                        setHandForm(hand, currentHands);
                    } else {
                        currentHands.put(hand.id(), HandForm.INVALID);
                    }
                }
            }
        }
    }

    /**
     * Determine form of hand and set it to the hash map.
     * 
     * @param hand
     *            the hand to analze
     */
    private void setHandForm(Hand hand, Map<Integer, HandForm> handMap) {

        // Get the hand's normal vector and direction
        Vector normal = hand.palmNormal();
        Vector direction = hand.direction();

        int invalidFingers = 0;
        for (Finger finger : hand.fingers()) {
            if (!finger.isValid()) {
                invalidFingers++;
            }
        }

        // Calculate the hand's pitch, roll, and yaw angles
        System.out
                .println("Hand finger count: " + hand.fingers().count() + " and " + invalidFingers
                        + " are invalid. Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, " + "roll: "
                        + Math.toDegrees(normal.roll()) + " degrees, " + "yaw: " + Math.toDegrees(direction.yaw())
                        + " degrees");

        if (hand.fingers().isEmpty()) {
            // if no fingers are visible
            handMap.put(hand.id(), HandForm.ROCK);
            System.out.println("Hand is rock");
        } else if (hand.fingers().count() == 5) {
            handMap.put(hand.id(), HandForm.PAPER);
            System.out.println("Hand is paper");
        } else {
            System.out.println("Hand is undefined, fingers found : " + hand.fingers().count() + ". ");
            handMap.put(hand.id(), HandForm.UNDEFINED);
        }
    }

}