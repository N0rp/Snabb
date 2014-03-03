package eu.dowsing.kolla.app.presentation;

import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import com.leapmotion.leap.Controller;

import eu.dowsing.kolla.app.AppSwitcher;
import eu.dowsing.kolla.app.BasicApp;
import eu.dowsing.kolla.widget.brick.ActiveMovementListener;
import eu.dowsing.kolla.widget.brick.BrickMenuAdapterInterface;
import eu.dowsing.kolla.widget.brick.NumberTypedListener;
import eu.dowsing.kolla.widget.brick.facade.BrickMenuView;
import eu.dowsing.kolla.widget.brick.model.BrickGesture;
import eu.dowsing.kolla.widget.brick.model.BrickGesture.GestureType;
import eu.dowsing.kolla.widget.presentation.PageLoadCompleteListener;
import eu.dowsing.kolla.widget.presentation.PresentationAdapterInterface;
import eu.dowsing.kolla.widget.presentation.model.PresentationAdapter;
import eu.dowsing.kolla.widget.presentation.model.PresentationAdapter.UrlLocation;
import eu.dowsing.kolla.widget.presentation.view.PresentationView;
import eu.dowsing.leap.brick.presentation.BasicMenuAdapter;
import eu.dowsing.leap.experiments.ScreenController;

/**
 * Displays a presentation.
 * 
 * @author richardg
 * 
 */
public class PresentationApp extends BasicApp implements ActiveMovementListener, ScreenController {

    private PresentationView presView;
    private PresentationAdapterInterface presAdapter;
    private BrickMenuView brickMenu;
    private BrickMenuAdapterInterface adapter;

    private AppSwitcher appSwitcher;
    private boolean isActive;

    /**
     * Create a new presentation app.
     */
    public PresentationApp() {
        super("Presentation");
        Pane pane = new StackPane();
        setView(pane);

        initView();
        initControl();
    }

    private void initView() {
        int sceneWidth = 800;
        int sceneHeight = 400;

        // init brick menu
        adapter = new BasicMenuAdapter();
        brickMenu = new BrickMenuView(sceneWidth, sceneHeight);
        brickMenu.setMouseTransparent(true);
        brickMenu.setAdapter(adapter);

        // init browser
        this.presView = new PresentationView(PresentationAdapter.LOCAL_INNOTALK, UrlLocation.Local);
        // overlay.setBrowser(browser);

        // pC = new PresentationController(stage, browser);
        // this.browser = new PresentationView(PresentationView.LOCAL_BORED, UrlLocation.Local.Local);

        getView().getChildren().addAll(brickMenu, presView);
    }

    private void initControl() {

        brickMenu.addActiveMovementListener(this);

        brickMenu.addNumberTypedListener(new NumberTypedListener() {

            @Override
            public void onNumberUpdate(int number, boolean isSubmitted) {
                if (isSubmitted) {

                    presAdapter.gotoPage(number - 1);
                }
            }
        });

        // Label test = new Label("Test");
        // box.getChildren().add(test);
        // box.getChildren().add(browser);

        presView.addPageLoadCompleteListener(new PageLoadCompleteListener() {

            @Override
            public void onPageLoadComplete() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        brickMenu.setCurrentKeyInputText("Laden Fertig");
                    }
                });
            }
        });
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean onActiveMovement(BrickGesture gesture) {
        if (isActive) {
            if (gesture.getGestureType() == GestureType.LEFT2RIGHT
                    || gesture.getGestureType() == GestureType.ALMOST_LEFT2RIGHT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is left to right");
                        presAdapter.gotoPreviousPage();
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.RIGHT2LEFT
                    || gesture.getGestureType() == GestureType.ALMOST_RIGHT2LEFT) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is right to left");
                        presAdapter.gotoNextPage();
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.DIVIDE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is divide");
                        appSwitcher.setFullScreen(PresentationApp.this, true);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.UNITE) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        System.out.println("Gesture is unite");
                        appSwitcher.setFullScreen(PresentationApp.this, false);
                    }
                });
                return true;
            } else if (gesture.getGestureType() == GestureType.NONE) {
                Platform.runLater(new Runnable() {
                    public void run() {

                    }
                });
                return false;
            }
        }
        return false;
    }

    @Override
    public void onSchedule(AppSwitcher appSwitcher, long scheduleSeconds) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLeapFrame(Controller controller) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyboardEvent(KeyEvent event) {
        // TODO Auto-generated method stub

    }

}
