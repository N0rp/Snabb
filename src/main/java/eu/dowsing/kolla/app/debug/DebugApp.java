package eu.dowsing.kolla.app.debug;

import javafx.scene.input.KeyEvent;

import com.leapmotion.leap.Controller;

import eu.dowsing.kolla.app.BasicApp;
import eu.dowsing.kolla.app.debug.facade.DebugView;

public class DebugApp extends BasicApp {

    public DebugApp() {
        super("Debug");
        setView(new DebugView());

        // dC = new DebugController(stage, debugText);
        // menuController.addActiveMovementListener((DebugController) dC);
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
