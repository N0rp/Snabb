package eu.dowsing.leap;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

/**
 * Contains all visualizations for the leap motion control
 * 
 * @author richardg
 * 
 */
public class LeapMenu extends Pane {

    private Text txt;

    private HandRect[] horizRects;

    public LeapMenu() {
        setOverlay(this);
    }

    public void setText(String text) {
        this.txt.setText(text);
    }

    private void setOverlay(Pane p) {
        Rectangle r = RectangleBuilder.create().height(100).width(100).arcHeight(200).arcWidth(200).stroke(Color.RED)
                .fill(Color.web("red", 0.1)).translateY(100).build();

        this.txt = TextBuilder.create().text("Overlay").translateY(100).font(Font.font("Arial", FontWeight.BOLD, 18))
                .fill(Color.BLUE).build();

        Rectangle left = RectangleBuilder.create().height(100).width(100).arcHeight(0).arcWidth(0).stroke(Color.BLUE)
                .fill(Color.web("blue", 0.1)).translateX(100).build();

        Rectangle right = RectangleBuilder.create().height(100).width(100).arcHeight(400).arcWidth(0)
                .stroke(Color.GREEN).fill(Color.web("green", 0.1)).translateX(200).translateY(200).build();
        p.getChildren().addAll(r, txt, left, right);
        // create horizontal rectangles to display leap location
        createHorizontalRectangles(p, 5);
    }

    private void createHorizontalRectangles(Pane p, int count) {
        final int rectCount = 5;
        final int fingerCount = 5;
        System.out.println("Creating horizontal rectangles");
        horizRects = new HandRect[rectCount];

        final int winWidth = (int) 750;
        final int winHeight = (int) 500;

        final int rectMargin = 10;
        final int rectHeight = 20;
        final int rectWidth = (int) (winWidth / rectCount);
        final int rectRealWidth = rectWidth - (2 * rectMargin);

        int rectX = rectMargin;
        int rectY = winHeight - rectHeight; // rectangle on bottom edge

        int miniMargin = 10;
        int miniRectWidth = (rectRealWidth / fingerCount);
        int miniRectRealWidth = miniRectWidth - miniMargin;
        int miniRectHeight = miniRectRealWidth;
        int miniRectY = rectY - miniRectHeight - rectMargin;

        // draw bottom hand rectangles
        for (int i = 0; i < horizRects.length; i++) {
            horizRects[i] = new HandRect(p, rectHeight, rectWidth, rectX, miniRectY, miniRectHeight, miniRectWidth);

            rectX += rectWidth;
        }
    }
}
