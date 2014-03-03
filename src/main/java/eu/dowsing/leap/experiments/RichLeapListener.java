package eu.dowsing.leap.experiments;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;

public class RichLeapListener extends Listener {

    private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    @Override
    public void onFrame(Controller controller) {
        Frame tmpFrame = new Frame();
        Hand tmpHand = new Hand();

        HandList tmpHandList = new HandList();

        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                Hand hand = frame.hands().get(0);
                if (hand.isValid()) {
                    // hand.palmPosition()
                    Vector intersect = screen.intersect(hand.palmPosition(),
                            hand.direction(), true);
                    point.setValue(new Point2D(screen.widthPixels()
                            * Math.min(1d, Math.max(0d, intersect.getX())),
                            screen.heightPixels()
                                    * Math.min(1d, Math.max(0d,
                                            (1d - intersect.getY())))));
                }
            }
        }
    }
}
