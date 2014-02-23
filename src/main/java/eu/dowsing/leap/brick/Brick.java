package eu.dowsing.leap.brick;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

public class Brick {

    /** NO CATEGORY FOUND is equal to -1. **/
    private final static int NO_CAT = -1;

    public enum Position {
        HORIZONTAL, VERTICAL
    }

    private int category;
    private int subCategory;
    private int fingerCount;
    private Position handRoll;

    public Brick(Hand hand, BrickMenuAdapterInterface adapter) {
        this.category = getCategory(hand, adapter);
        this.subCategory = getSubCategory(category, hand, adapter);
        this.fingerCount = hand.fingers().count();
        this.handRoll = getHandRoll(hand);
    }

    public int getCategory() {
        return this.category;
    }

    public int getSubCategory() {
        return this.subCategory;
    }

    public int getFingerCount() {
        return this.fingerCount;
    }

    public Position getHandRoll() {
        return this.handRoll;
    }

    public boolean isActive() {
        return this.handRoll == Position.VERTICAL;
    }

    private Position getHandRoll(Hand hand) {
        // roll is 90 or -90 when the hand is vertical
        Vector normal = hand.palmNormal();
        double roll = Math.toDegrees(normal.roll());
        roll = Math.abs(roll);
        if (roll > 45 && roll < 135) {
            return Position.VERTICAL;
        } else {
            return Position.HORIZONTAL;
        }
    }

    private int getCategories(BrickMenuAdapterInterface adapter) {
        if (adapter != null) {
            return adapter.getCategories();
        } else {
            return 0;
        }
    }

    private int getSubCategories(int category, BrickMenuAdapterInterface adapter) {
        if (adapter != null) {
            return adapter.getSubCategories(category);
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
    private int getCategory(Hand hand, BrickMenuAdapterInterface adapter) {
        final int minmin = 0;
        final int min = minmin + 100;
        final int max = 400;
        final int maxmax = max + 100;
        // x between -200 (left) and 200 (right)
        // y between 0 (on leap) and 500 (high above leap)?
        // z between -200 (behind leap) and 200 (between leap and user)
        Vector pos = hand.palmPosition();
        if (pos.getY() > minmin && pos.getY() < maxmax) {
            int count = getCategories(adapter);
            if (pos.getY() <= min) {
                return 0;
            } else if (pos.getY() >= max) {
                return count - 1;
            } else {
                int result = (int) (pos.getY() - min) / (max / count);
                if (result >= count) {
                    return count - 1;
                } else {
                    return result;
                }
            }
        }

        return Brick.NO_CAT;
    }

    /**
     * Get the subcategory.
     * 
     * @param category
     * @param hand
     * @return get the subcategory or -1 if there is none.
     */
    private int getSubCategory(int category, Hand hand, BrickMenuAdapterInterface adapter) {
        if (category < 0) {
            return Brick.NO_CAT;
        }
        final int min = -200;
        final int max = -min;
        final int minmin = min - 50;
        final int maxmax = max + 50;
        // x between -200 (left) and 200 (right)
        // y between 0 (on leap) and 500 (high above leap)?
        // z between -200 (behind leap) and 200 (between leap and user)
        Vector pos = hand.palmPosition();
        if (pos.getX() > minmin && pos.getX() < maxmax) {
            int count = getSubCategories(category, adapter);
            if (pos.getX() <= min) {
                return 0;
            } else if (pos.getX() >= max) {
                return count - 1;
            } else {
                float x = pos.getX() + max;
                int result = (int) (x) / (max * 2 / count);
                if (result >= count) {
                    return count - 1;
                } else {
                    return result;
                }
            }
        }

        return Brick.NO_CAT;
    }

}
