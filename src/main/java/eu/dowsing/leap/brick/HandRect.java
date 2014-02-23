package eu.dowsing.leap.brick;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

import com.leapmotion.leap.Hand;

import eu.dowsing.leap.brick.Brick.Position;

/**
 * Represents a complete hand including its fingers.
 * 
 * @author richardg
 * 
 */
public class HandRect {

    // port(left hand:red) and starboard(right hand:green)

    public enum Importance {
        PRIMARY, SECONDARY
    }

    private Rectangle horizontal;
    private Rectangle vertical;
    private Rectangle[] fingerRects;

    private Circle indicator;

    public HandRect(Pane p, int rectHeight, int rectWidth, int rectX, int rectY, int miniRectHeight, int miniRectWidth) {
        drawIndicator(p, rectHeight, rectWidth, rectX, rectY, miniRectHeight, miniRectWidth);
    }

    private void drawIndicator(Pane p, int hHeight, int hWidth, int rectX, int rectY, int mHeight, int mWidth) {
        final int fingerCount = 5;
        fingerRects = new Rectangle[fingerCount];

        final int rectMargin = 10;
        final int hRealWidth = hWidth - (2 * rectMargin);

        // create the measure for the mini finger rectangles
        int miniRectMargin = rectMargin / 2;
        int mRealWidth = mWidth - miniRectMargin;
        int mRectX = rectX + (miniRectMargin / 2);
        int mRectY = rectY;

        // create measures for the vertical rectangle
        final int vWidth = hHeight;
        final int vHeight = hWidth / 2;

        // create the circle indicating where the hand can be
        this.indicator = CircleBuilder.create().radius(hHeight / 2).centerX(rectX + (hWidth / 2) - (hHeight / 2))
                .centerY(rectY + (hHeight / 2)).fill(Color.web("grey", 0.1)).build();
        p.getChildren().add(indicator);

        // create the rectangle indicating position of the hand
        horizontal = RectangleBuilder.create().height(hHeight).width(hRealWidth).arcHeight(0).arcWidth(0)
                .stroke(Color.RED).fill(Color.web("blue", 0.1)).translateX(rectX).translateY(rectY).build();
        p.getChildren().add(horizontal);

        // create rectangle indicating if the hand is vertical
        vertical = RectangleBuilder.create().height(vHeight).width(vWidth).arcHeight(0).arcWidth(0).stroke(Color.RED)
                .fill(Color.web("blue", 0.1)).translateX(rectX + (vWidth / 2)).translateY(rectY - (vHeight / 2))
                .build();
        p.getChildren().add(vertical);

        // now create the rectangles indicating fingers found
        for (int i = 0; i < fingerRects.length; i++) {
            Rectangle mini = RectangleBuilder.create().height(mHeight).width(mRealWidth).arcHeight(0).arcWidth(0)
                    .stroke(Color.GREEN).fill(Color.web("blue", 0.1)).translateX(mRectX + (i * mWidth))
                    .translateY(mRectY).build();
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

    public Color getHandColor(Importance importance) {
        // port(left hand/secondary:red) and starboard(right hand/primary:green)
        if (importance == Importance.PRIMARY) {
            return Color.web("green", 1);
        } else if (importance == Importance.SECONDARY) {
            return Color.web("red", 1);
        } else {
            return Color.web("yellow", 1);
        }
    }

    public void showHand(Importance importance, Position pos, int fingerCount) {
        // first all rectangles visible
        setVisible(true);

        // hide vertical or horizontal position
        if (pos == Position.HORIZONTAL) {
            horizontal.setFill(getHandColor(importance));
            vertical.setVisible(false);
        } else if (pos == Position.VERTICAL) {
            horizontal.setVisible(false);
            vertical.setFill(getHandColor(importance));
        }

        // then we hide invisible fingers
        for (int i = fingerCount; i < fingerRects.length; i++) {
            fingerRects[i].setVisible(false);
        }
    }

    /**
     * Show or hide the complete hand with all indicators
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        horizontal.setVisible(visible);
        vertical.setVisible(visible);
        for (Rectangle rect : this.fingerRects) {
            rect.setVisible(visible);
        }
    }

    /**
     * Show or hide only the hand hint.
     * 
     * @param visible
     */
    public void setHintVisible(boolean visible) {
        this.indicator.setVisible(visible);
    }

}
