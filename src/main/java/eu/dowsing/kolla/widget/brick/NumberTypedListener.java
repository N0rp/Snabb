package eu.dowsing.kolla.widget.brick;

public interface NumberTypedListener {

    /**
     * Called when the user has typed a new number.
     * 
     * @param number
     *            the current number
     * @param <code>true</code> if the number was submitted by the user, else <code>false</code>
     */
    void onNumberUpdate(int number, boolean isSubmitted);
}
