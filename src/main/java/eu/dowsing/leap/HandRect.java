package eu.dowsing.leap;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

import com.leapmotion.leap.Hand;

/**
 * Represents a complete hand including its fingers.
 * 
 * @author richardg
 * 
 */
public class HandRect {

    private Rectangle horizontal;
    private Rectangle[] fingerRects;

    public HandRect(Pane p, int rectHeight, int rectWidth, int rectX, int rectY, int miniRectHeight, int miniRectWidth) {
        drawIndicator(p, rectHeight, rectWidth, rectX, rectY, miniRectHeight, miniRectWidth);
    }

    private void drawIndicator(Pane p, int rectHeight, int rectWidth, int rectX, int rectY, int miniRectHeight,
            int miniRectWidth) {
        final int fingerCount = 5;
        fingerRects = new Rectangle[fingerCount];

        final int rectMargin = 10;
        final int rectRealWidth = rectWidth - (2 * rectMargin);

        int miniMargin = rectMargin / 2;
        int miniRectRealWidth = miniRectWidth - miniMargin;
        int miniRectX = rectX + (miniMargin / 2);
        int miniRectY = rectY;

        // first create the rectangle indicating position of the hand
        Rectangle rect = RectangleBuilder.create().height(rectHeight).width(rectRealWidth).arcHeight(0).arcWidth(0)
                .stroke(Color.RED).fill(Color.web("blue", 0.1)).translateX(rectX).translateY(rectY).build();
        p.getChildren().add(rect);
        horizontal = rect;

        // now create the rectangles indicating fingers found
        for (int i = 0; i < fingerRects.length; i++) {
            Rectangle mini = RectangleBuilder.create().height(miniRectHeight).width(miniRectRealWidth).arcHeight(0)
                    .arcWidth(0).stroke(Color.GREEN).fill(Color.web("blue", 0.1))
                    .translateX(miniRectX + (i * miniRectWidth)).translateY(miniRectY).build();
            fingerRects[i] = mini;
            p.getChildren().add(mini);
        }
    }

    public Color getPitchColor(Hand h) {

        double direction = Math.toDegrees(h.direction().pitch());

        if (direction < 10 && direction > -10) {
            return Color.web("blue", 0.1);
        } else if (direction < 100 && direction > 80) {
            return Color.web("green", 0.1);
        } else if (direction < -80 && direction > -100) {
            return Color.web("yellow", 0.1);
        } else {
            return Color.web("red", 0.1);
        }
    }

    public void showHand(Hand h) {

        if (h.isValid()) {
            // first all rectangles visible
            setRectangleVisibility(true);

            // then we hide invisible fingers
            int fingersVisible = h.fingers().count();
            for (int i = fingersVisible; i < fingerRects.length; i++) {
                fingerRects[i].setVisible(false);
            }

        }
    }

    public void hide() {
        setRectangleVisibility(false);
    }

    private void setRectangleVisibility(boolean visible) {
        horizontal.setVisible(visible);
        for (Rectangle rect : this.fingerRects) {
            rect.setVisible(visible);
        }
    }

}
