package eu.dowsing.kolla.widget;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

import com.leapmotion.leap.Controller;

import eu.dowsing.kolla.app.AppSwitcher;

public interface WidgetInterface {

    /**
     * Get the node for this widget.
     * 
     * @return the node for this widget
     */
    Node getNode();

    /**
     * Called on a schedule. Apps can use this to clear their interface.
     * 
     * @param scheduleSeconds
     *            the time in miliseconds when this method is called
     */
    void onSchedule(AppSwitcher appSwitcher, long scheduleSeconds);

    /**
     * Called for every new leap motion frame.
     * 
     * @param controller
     *            leap motion controller
     */
    void onLeapFrame(Controller controller);

    /**
     * Called for every keyboard event.
     * 
     * @param event
     */
    void onKeyboardEvent(KeyEvent event);
}
