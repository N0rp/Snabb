package eu.dowsing.leap.experiments;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;

public class HandSignListener extends Listener {

    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                Hand hand = frame.hands().get(0);
                if (hand.isValid()) {

                }
            }
        }
    }

}
