package eu.dowsing.leap.experiments;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.leapmotion.leap.Controller;


public class LeapJavaFX extends Application {
    private SimpleLeapListener listener = new SimpleLeapListener();
    private Controller leapController = new Controller();
     
    private AnchorPane root = new AnchorPane();
    private Circle circle=new Circle(50,Color.DEEPSKYBLUE);
     
    @Override
    public void start(Stage primaryStage) {
         
        leapController.addListener(listener);       
        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);
        final Scene scene = new Scene(root, 800, 600);       
         
        
        listener.pointProperty().addListener(new ChangeListener<Point2D>(){
            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Point2D d=root.sceneToLocal(t1.getX()-scene.getX()-scene.getWindow().getX(),
                                                    t1.getY()-scene.getY()-scene.getWindow().getY());
                        double dx=d.getX(), dy=d.getY();
                        if(dx>=0d && dx<=root.getWidth()-2d*circle.getRadius() &&
                           dy>=0d && dy<=root.getHeight()-2d*circle.getRadius()){
                            circle.setTranslateX(dx);
                            circle.setTranslateY(dy);                               
                        }
                    }
                });
            }
        });
         
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop(){
        leapController.removeListener(listener);
         
    }
    
    public static void main(String[] args) {
        Application.launch(LeapJavaFX.class);
    }
}
