package eu.dowsing.leap.gesture;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;

public class FullHand {
    private Finger thumb;
    private Finger ring;
    private Finger middle;
    private Finger point;
    private Finger pinky;

    private enum HandType {
        LEFT, RIGHT, UNKNOWN
    }

    /**
     * Analyze fingers on hand.
     * 
     * @param hand
     *            the hand to analyze
     * @return returns <code>true</code> if hand could be analzed, else <code>false</code>
     */
    public boolean analyze(Hand hand) {
        // only a hand with all 5 fingers can be analyzed
        if (hand.fingers().count() != 5) {
            return false;
        }
        Finger leftmost = hand.fingers().leftmost();
        Finger rightmost = hand.fingers().rightmost();

        return true;
    }

}