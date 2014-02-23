package eu.dowsing.leap.brick;

public interface ActiveMovementListener {

    /**
     * Notified once hand is active and has moved to a new (sub)category.
     * 
     * @param gesture
     *            the brick gesture
     * @return <code>true</code> if this gesture was handled, else <code>false</code>.
     */
    boolean onActiveMovement(BrickGesture gesture);

}
