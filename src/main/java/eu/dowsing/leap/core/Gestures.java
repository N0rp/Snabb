package eu.dowsing.leap.core;

public class Gestures {
    private boolean hasSwiped = false;

    public Gestures(boolean swiped) {
        this.hasSwiped = swiped;
    }

    public boolean getSwiped() {
        return hasSwiped;
    }

}
