package eu.dowsing.leap.brick;

import java.util.LinkedList;
import java.util.List;

import com.leapmotion.leap.Hand;

public class BrickGesture {

    public enum GestureType {
        /** when primary hand goes from complete left to complete right side. **/
        LEFT2RIGHT,
        /** when primary hand goes from almost complete left to complete right side. **/
        ALMOST_LEFT2RIGHT,
        /** when the primary hand goes from the complete right to the complete left side. **/
        RIGHT2LEFT,
        /** when the primary hand goes from the almost complete right to the complete left side. **/
        ALMOST_RIGHT2LEFT,
        /** when both hands seem to divide the air into two halves **/
        DIVIDE,
        /** When both hands come together to the middle from both sides **/
        UNITE,
        /** When no gesture could be discerned. **/
        NONE
    }

    /** movement the primary hand has made since recognized. **/
    private List<Brick> primaryMovement = new LinkedList<>();
    /** movement the primary hand has made since made active. **/
    private List<Brick> primaryActiveMovement = new LinkedList<>();

    /** Movement the secondary hand has made since recognized. **/
    private List<Brick> secondaryMovement = new LinkedList<>();
    /** Movement the secondary hand has made since made active. **/
    private List<Brick> secondaryActiveMovement = new LinkedList<>();

    /** Most recent primary hand or null. **/
    private Brick prim;
    /** Most recent secondary hand or null. **/
    private Brick sec;

    private GestureType gestureType;

    /**
     * Clear all gesture information.
     */
    public void clear() {
        primaryMovement.clear();
        primaryActiveMovement.clear();

        secondaryMovement.clear();
        secondaryActiveMovement.clear();
    }

    public GestureType getGestureType() {
        return this.gestureType;
    }

    public Brick getPrimary() {
        return prim;
    }

    public Brick getSecondary() {
        return sec;
    }

    /**
     * Get the state that started the active movement or null if there is none.
     * 
     * @return
     */
    public Brick getPrimaryActiveStart() {
        if (this.primaryActiveMovement.size() == 0) {
            return null;
        } else {
            return this.primaryActiveMovement.get(0);
        }
    }

    /**
     * Get the current state of the active movement or null if there is none.
     * 
     * @return
     */
    public Brick getPrimaryActiveCurrent() {
        if (this.primaryActiveMovement.size() == 0) {
            return null;
        } else {
            return this.primaryActiveMovement.get(primaryActiveMovement.size() - 1);
        }
    }

    /**
     * Get the state that started the active movement for the secondary hand, or null if there is none.
     * 
     * @return
     */
    public Brick getSecondaryActiveStart() {
        if (secondaryActiveMovement.size() == 0) {
            return null;
        } else {
            return this.secondaryActiveMovement.get(0);
        }
    }

    /**
     * Get the current state of the active movement for the secondary hand, or null if there is none.
     * 
     * @return
     */
    public Brick getSecondaryActiveCurrent() {
        if (secondaryActiveMovement.size() == 0) {
            return null;
        } else {
            return this.secondaryActiveMovement.get(secondaryActiveMovement.size() - 1);
        }
    }

    /**
     * Add the hands.
     * 
     * @param primary
     *            the primary hand, or null
     * @param secondary
     *            the secondary hand or null
     * @param adapter
     * @return <code>true</code> if movement was found, else <code>false</code>
     */
    public boolean addHands(Hand primary, Hand secondary, BrickMenuAdapterInterface adapter) {
        boolean activeMovement = false;

        if (primary == null) {
            prim = null;
            primaryMovement.clear();
            primaryActiveMovement.clear();
        } else {
            prim = new Brick(primary, adapter);
            primaryMovement.add(prim);
            if (prim.isActive()) {
                if (primaryActiveMovement.size() == 0) {
                    primaryActiveMovement.add(prim);
                } else {
                    Brick last = primaryActiveMovement.get(primaryActiveMovement.size() - 1);
                    if (last.getCategory() != prim.getCategory() || last.getSubCategory() != prim.getSubCategory()) {
                        primaryActiveMovement.add(prim);
                        activeMovement = true;
                    }
                }
            }
        }

        if (secondary == null) {
            sec = null;
            secondaryMovement.clear();
            secondaryActiveMovement.clear();
        } else {
            sec = new Brick(secondary, adapter);
            secondaryMovement.add(sec);
            if (sec.isActive()) {
                if (secondaryActiveMovement.size() == 0) {
                    secondaryActiveMovement.add(sec);
                } else {
                    // only add if something has changed
                    Brick last = secondaryActiveMovement.get(secondaryActiveMovement.size() - 1);
                    if (last.getCategory() != sec.getCategory() || last.getSubCategory() != sec.getSubCategory()) {
                        secondaryActiveMovement.add(sec);
                        activeMovement = true;
                    }
                }
            }
        }

        this.gestureType = calculateGestureType();

        return activeMovement;
    }

    private GestureType calculateGestureType() {
        Brick primStart = getPrimaryActiveStart();
        Brick primEnd = getPrimaryActiveCurrent();

        Brick secStart = getSecondaryActiveStart();
        Brick secEnd = getSecondaryActiveCurrent();

        if (primStart != null && primStart != primEnd) {
            // if (primEnd.getSubCategory() == 0) {
            // int maxCat = primStart.getMaxCategory();
            // int maxSubCat = primStart.getMaxSubCategory();
            // boolean startLeft = primStart.isLeft();
            // boolean startRight = primStart.isRight();
            // boolean endLeft = primEnd.isLeft();
            // boolean endRight = primEnd.isRight();
            // // System.out.println("At the beginning");
            // }
            if (primStart.getCategory() == 0) {
                if (primStart.isLeft() && primEnd.isRight()) {
                    return GestureType.LEFT2RIGHT;
                } else if (primStart.isAlmostLeft() && primEnd.isRight()) {
                    return GestureType.ALMOST_LEFT2RIGHT;
                } else if (primStart.isRight() && primEnd.isLeft()) {
                    return GestureType.RIGHT2LEFT;
                } else if (primStart.isAlmostRight() && primEnd.isLeft()) {
                    return GestureType.ALMOST_RIGHT2LEFT;
                } else if (secStart != null && secStart != secEnd) {
                    // use Math.floor and Math.ceil on the max subcategory instead
                    final int Middle4Left = 2;
                    final int Middle4Right = 3;
                    if (// right hand moved from right middle to right end
                    (primStart.getSubCategory() == Middle4Right
                            && primEnd.getSubCategory() == primEnd.getMaxSubCategory()
                            // left hand moved from left middle to left end
                            && secStart.getSubCategory() == Middle4Left && secEnd.getSubCategory() == 0)
                            ||
                            // right hand moved from the right middle to the right end
                            (secStart.getSubCategory() == Middle4Right
                                    && secEnd.getSubCategory() == secEnd.getMaxSubCategory()
                                    // left hand moved from the left middle to the left end
                                    && primStart.getSubCategory() == Middle4Left && primEnd.getSubCategory() == 0)) {
                        return GestureType.DIVIDE;
                    } else if ((primStart.isRight() && primEnd.getSubCategory() == Middle4Right && secStart.isLeft() && secEnd
                            .getSubCategory() == Middle4Left)
                            || (secStart.isRight() && secEnd.getSubCategory() == Middle4Right && primStart.isLeft() && secEnd
                                    .getSubCategory() == Middle4Left)) {
                        return GestureType.UNITE;
                    }
                }
            }
        }

        return GestureType.NONE;
    }

    @Override
    public String toString() {
        String str = "Gesture:";
        str += " Primary From: " + getPrimaryActiveStart() + " To: " + getPrimaryActiveCurrent();
        str += " Secondary From: " + getSecondaryActiveStart() + " To: " + getSecondaryActiveCurrent();
        return str;
    }

}
