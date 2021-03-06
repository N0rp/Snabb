package eu.dowsing.leap.experiments;

public interface ScreenController {

    /**
     * Sets the controller as active or inactive
     * 
     * @param active
     */
    void setActive(boolean active);

    /**
     * Returns if the controller is active.
     * 
     * @return
     */
    boolean isActive();

}
