package eu.dowsing.kolla.app.music;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import com.leapmotion.leap.Controller;

import eu.dowsing.kolla.app.BasicApp;
import eu.dowsing.leap.storage.MainProperties;
import eu.dowsing.leap.storage.MainProperties.Key;

/**
 * Plays music.
 * 
 * @author richardg
 * 
 */
public class MusicApp extends BasicApp {

    private MainProperties mainPropManager = MainProperties.getInstance();

    /**
     * Create a new music player app.
     * 
     * @param playFirstTrack
     *            if <code>true</code> the first track will be played.
     */
    public MusicApp(boolean playFirstTrack) {
        super("MusicMan");
        loadMusic(playFirstTrack);
        setView(new MusicView());
    }

    private void loadMusic(boolean playFirstTrack) {
        File test;

        test = new File("/Users/richardg/Music");
        if (!test.exists()) {
            System.err.println("Does not exist: " + test.getName());
        }

        String musicLocation = mainPropManager.getProperty(Key.MUSIC_LOCATION);
        System.out.println("Music is located in: " + musicLocation);
        File music = new File(musicLocation);
        List<File> tracks = null;
        if (music.exists()) {
            tracks = getTracks(music);
            if (tracks.size() > 0) {
                System.out.println("Found " + tracks.size() + " tracks. First track is " + tracks.get(0).getName());
            } else {
                System.out.println("Found no music tracks");
            }
        } else {
            System.err.println("Music Location could not be found: " + musicLocation);
        }

        if (tracks != null && tracks.size() > 0) {
            String path = tracks.get(0).getAbsolutePath();
            // path = path.replaceAll("[^a-zA-Z0-9.-]", "_");
            try {
                path = URLEncoder.encode(path, "UTF-8");
                // path = "res/audio/music/Test.mp3";

                // path = "res/audio/beep-07-Soundjay.wav";

                path = "/Users/richardg/Documents/GIT/Snabb/res/audio/music/Test.mp3";

                if (!(new File(path)).exists()) {
                    System.err.println("File does not exist " + path);
                }

                // path = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
                path = "file://" + path;
                System.out.println("Loading file: " + path);
                Media media = new Media(path);
                System.out.println("Metadata is " + media.getMetadata());
                final MediaPlayer player = new MediaPlayer(media);

                if (playFirstTrack) {
                    (new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.play();
                        }
                    })).start();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all music tracks in the file.
     * 
     * @param parent
     * @return
     */
    private List<File> getTracks(File parent) {
        List<File> tracks = new LinkedList<>();
        if (parent.isDirectory()) {
            for (File child : parent.listFiles()) {
                // Do something with child
                if (child.isFile()) {
                    String extension = "";

                    int i = child.getName().lastIndexOf('.');
                    if (i > 0) {
                        extension = child.getName().substring(i + 1);
                        if (extension.equals("mp3")) {
                            tracks.add(child);
                        }
                    }
                } else if (child.isDirectory()) {
                    tracks.addAll(getTracks(child));
                }
            }
        }
        return tracks;
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
