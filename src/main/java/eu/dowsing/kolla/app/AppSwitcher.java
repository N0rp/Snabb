package eu.dowsing.kolla.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Listener;

/**
 * Knows all the apps and can switch between them. Apps can have multiple windows.
 * 
 * @author richardg
 * 
 */
public class AppSwitcher extends Listener implements EventHandler<KeyEvent> {

    private Map<Integer, AppInterface> apps = new HashMap<>();

    private static int maxId = 0;

    private final Stage primaryStage;

    private Pane view;

    private final ScheduledExecutorService executorService;

    private Controller leapController = new Controller();

    /** Time in mili seconds for the schedule. **/
    private final long scheduleTime = 1000;

    private AppInterface currentApp;

    /**
     * Create a new app switcher, using the primary stage.
     * 
     * @param primaryStage
     *            the primary stage
     */
    public AppSwitcher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = new StackPane();

        initLeap();

        // create new executor service
        executorService = Executors.newScheduledThreadPool(1);

        // schedule first run, subsequent runs will be schedule by runnable itself
        executorService.schedule(new Runnable() {

            @Override
            public void run() {
                // notify all apps
                for (Entry<Integer, AppInterface> entry : apps.entrySet()) {
                    entry.getValue().onSchedule(AppSwitcher.this, scheduleTime);
                }

                // schedule itself to be run again
                executorService.schedule(this, scheduleTime, TimeUnit.MILLISECONDS);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public Region getAppView() {
        return this.view;
    }

    private void initLeap() {

        // enable leap gestures and set their configuration
        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);
        if (leapController.config().setFloat("Gesture.Swipe.MinLength", 100)
                && leapController.config().setFloat("Gesture.Swipe.MinVelocity", 250)) {
            leapController.config().save();
        }

        leapController.addListener(this);
    }

    /**
     * Get the leap controller.
     * 
     * @return the leap controller
     */
    public Controller getLeapController() {
        return this.leapController;
    }

    private void initKeyboard() {
        // KeyEvent.KEY_RELEASED
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, this);
    }

    @Override
    public void handle(KeyEvent event) {
        // logitech presenter keyboard events are:
        // F5 / ESCAPE is presentation Start/Pause
        // PERIOD is blank screen on/off
        // next/previous slide is not caught here,
        // probably directed to webview instead, but does what it should anyway
        if (event.getCode() == KeyCode.F5) {
            // go fullscreen
            primaryStage.setFullScreen(true);
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            // undo fullscreen
            primaryStage.setFullScreen(false);
            event.consume();
        } else {
            if (currentApp != null) {
                currentApp.onKeyboardEvent(event);
            }
        }
        // } else if (event.getCode() == KeyCode.PERIOD) {
        // // show blank screen
        // // browser.setVisible(!browser.isVisible());
        // }
    }

    @Override
    public void onFrame(Controller controller) {
        if (currentApp != null) {
            currentApp.onLeapFrame(controller);
        }
    }

    /**
     * Add an app to the switcher and return the unique id of the app.
     * 
     * @param app
     *            an app
     * @return the unique id of the app
     */
    public int addApp(AppInterface app) {
        int id = generateId();
        apps.put(id, app);
        view.getChildren().add(app.getView());
        app.getView().setVisible(false);
        return id;
    }

    /**
     * Display the app with the given id.
     * 
     * @param id
     *            the id of the app.
     */
    public void showApp(int id) {
        if (currentApp != null) {
            currentApp.getView().setVisible(false);
        }
        currentApp = apps.get(id);
        currentApp.getView().setVisible(true);
        primaryStage.setTitle(currentApp.getName());
    }

    /**
     * Set the app to fullscreen
     * 
     * @param app
     * @param fullscreen
     *            if <code>true</code> app will be set to fullscreen, else fullscreen disabled.
     */
    public void setFullScreen(AppInterface app, boolean fullscreen) {
        this.primaryStage.setFullScreen(fullscreen);
    }

    /**
     * Generate a new app id and return it.
     * 
     * @return
     */
    private static int generateId() {
        return maxId++;
    }

    /**
     * Shutdown this switcher and children.
     * <p/>
     * 
     * <b>Note:</b> Very important to call before java exit.
     */
    public void shutdown() {
        leapController.removeListener(this);
        executorService.shutdown();
    }

}
