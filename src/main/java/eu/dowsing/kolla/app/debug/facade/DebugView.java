package eu.dowsing.kolla.app.debug.facade;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class DebugView extends Pane {

    private Circle circle = new Circle(50, Color.DEEPSKYBLUE);
    private Text debugText;

    public DebugView() {
        init(this);
    }

    private void init(final Pane root) {

        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);

        debugText = TextBuilder.create().text("Overlay").translateY(100).font(Font.font("Arial", FontWeight.BOLD, 18))
                .fill(Color.BLUE).build();

        // circle.setLayoutX(circle.getRadius());
        // circle.setLayoutY(circle.getRadius());
        // root.getChildren().add(circle);
        // final Scene scene = new Scene(root, 800, 600);

        // listener.pointProperty().addListener(new ChangeListener<Point2D>() {
        // @Override
        // public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
        // Platform.runLater(new Runnable() {
        // @Override
        // public void run() {
        // if (LeapJavaFX.this.scene != null) {
        // Scene scene = LeapJavaFX.this.scene;
        //
        // Point2D d = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
        // t1.getY() - scene.getY() - scene.getWindow().getY());
        // double dx = d.getX(), dy = d.getY();
        // if (dx >= 0d && dx <= root.getWidth() - 2d * circle.getRadius() && dy >= 0d
        // && dy <= root.getHeight() - 2d * circle.getRadius()) {
        // circle.setTranslateX(dx);
        // circle.setTranslateY(dy);
        // }
        // }
        // }
        // });
        // }
        // });
    }

}
