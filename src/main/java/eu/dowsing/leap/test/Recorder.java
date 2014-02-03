package eu.dowsing.leap.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;

public class Recorder extends Listener {

    private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    /** Recorded hand movements. **/
    private List<HandList> hands = null;

    /** If <code>true</code> will record new frames. **/
    private boolean isRecording = false;

    /**
     * Start recording hand movements and forget about the previously recorded movements.
     */
    public void startRecording() {
        isRecording = true;
        hands = new LinkedList<>();
    }

    /**
     * Save a recording to disk. Call after {@link #stopRecording()}.
     * 
     * @param location
     *            file location
     * @throws IOException
     */
    public void saveRecording(String location) throws IOException {
        File f = new File(location);
        FileWriter writer = new FileWriter(f);

    }

    /**
     * Stop recording hand movements. Call {@link #saveRecording(String)} afterwards to save the movements.
     */
    public void stopRecording() {
        isRecording = false;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        if (isRecording && !frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                this.hands.add(frame.hands());
            }
        }
    }
}