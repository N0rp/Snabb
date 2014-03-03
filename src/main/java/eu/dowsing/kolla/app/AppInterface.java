package eu.dowsing.kolla.app;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import com.leapmotion.leap.Controller;

import eu.dowsing.kolla.widget.WidgetInterface;

/**
 * Interface for an app.
 * 
 * @author richardg
 * 
 */
public interface AppInterface {

    /**
     * Get the name of the app.
     */
    String getName();

    /**
     * Called when the app needs to be resized.
     * 
     * @param window
     *            the window that was resized
     * @param width
     * @param height
     */
    void onAppResize(int window, double width, double height);

    /**
     * Called when app is assigned an app switcher.
     * 
     * @param appSwitcher
     */
    void onAppSwitcherUpdate(AppSwitcher appSwitcher);

    /**
     * Returns the current app switcher, if there is one.
     * 
     * @return the current appswitcher or <code>null</code> if there is none.
     */
    AppSwitcher getAppSwitcher();

    /**
     * Add a widget to the app.
     */
    void addWidget(WidgetInterface widget);

    /**
     * Get the view for this app.
     * 
     * @return
     */
    Pane getView();

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
