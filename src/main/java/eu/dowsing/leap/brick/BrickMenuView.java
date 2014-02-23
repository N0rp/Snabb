package eu.dowsing.leap.brick;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import eu.dowsing.leap.brick.HandRect.Importance;

/**
 * Contains all visualizations for the leap motion control
 * 
 * @author richardg
 * 
 */
public class BrickMenuView extends Pane {

    public enum Hand {
        PRIMARY, SECONDARY
    }

    private Text txt;

    private List<List<HandRect>> categories = new LinkedList<>();

    private int sceneWidth;
    private int sceneHeight;

    private Rectangle noadapter;

    private BrickMenuAdapterInterface adapter;

    public BrickMenuView(int sceneWidth, int sceneHeight) {
        this.sceneWidth = sceneHeight;
        this.sceneHeight = sceneHeight;

        // show a big red rectangle indicating no adapter has been set yet
        this.noadapter = RectangleBuilder.create().height(this.sceneHeight).width(this.sceneHeight).stroke(Color.RED)
                .fill(Color.web("red", 0.1)).build();
        getChildren().add(noadapter);
    }

    public void setText(String text) {
        if (this.txt != null) {
            this.txt.setText(text);
        }
    }

    /**
     * Will clear the view of displaying any hands
     */
    public void clearHands() {
        for (List<HandRect> rectangles : this.categories) {
            for (HandRect rect : rectangles) {
                rect.setVisible(false);
            }
        }
    }

    /**
     * Show only the indicators for the given category
     * 
     * @param category
     */
    public void showCategoryHint(Brick hand) {
        if (hand.getCategory() >= 0) {
            // hide everything
            setCategoryHintVisibile(false);
            // show category menu if hand in right position
            if (hand.getSubCategory() == 0 || hand.getSubCategory() + 1 == adapter.getSubCategories(hand.getCategory())) {
                // if the hand is at either end
                for (List<HandRect> subCategories : this.categories) {
                    for (int i = 0; i < subCategories.size(); i++) {
                        HandRect rect = subCategories.get(i);
                        rect.setHintVisible(i == hand.getSubCategory());
                    }
                }
            }
            // show sub categories
            List<HandRect> subCategories = categories.get(hand.getCategory());
            for (HandRect rect : subCategories) {
                rect.setHintVisible(true);
            }
        } else {
            setCategoryHintVisibile(true);
        }
    }

    private void setCategoryHintVisibile(boolean visible) {
        for (List<HandRect> subCategory : this.categories) {
            for (HandRect rect : subCategory) {
                rect.setHintVisible(visible);
            }
        }
    }

    /**
     * Display the given hand using the brick menu.
     * 
     * @param importance
     *            if the hand is of primary or secondary importance
     * @param hand
     *            the hand to display
     * @param handledGesture
     *            if <code>true</code> a gesture was handled which should be displayed.
     */
    public void showHand(Importance importance, Brick hand, boolean handledGesture, Brick gestureStart) {
        HandRect rect = categories.get(hand.getCategory()).get(hand.getSubCategory());
        rect.showHand(importance, hand.getHandRoll(), hand.getFingerCount(), handledGesture);

        if (gestureStart != null && gestureStart.getCategory() >= 0 && gestureStart.getSubCategory() >= 0) {
            HandRect startHint = categories.get(gestureStart.getCategory()).get(gestureStart.getSubCategory());
            startHint.setShowGestureStart(importance);
        }
    }

    /**
     * Set the adapter of the view.
     * 
     * @param adapter
     */
    public void setAdapter(BrickMenuAdapterInterface adapter) {
        this.adapter = adapter;
        drawOverlay();
    }

    private void drawOverlay() {
        this.noadapter.setVisible(false);
        // Rectangle r = RectangleBuilder.create().height(100).width(100).arcHeight(200).arcWidth(200).stroke(Color.RED)
        // .fill(Color.web("red", 0.1)).translateY(100).build();

        this.txt = TextBuilder.create().text("Overlay").translateY(100).font(Font.font("Arial", FontWeight.BOLD, 18))
                .fill(Color.BLUE).build();

        // Rectangle left = RectangleBuilder.create().height(100).width(100).arcHeight(0).arcWidth(0).stroke(Color.BLUE)
        // .fill(Color.web("blue", 0.1)).translateX(100).build();
        //
        // Rectangle right = RectangleBuilder.create().height(100).width(100).arcHeight(400).arcWidth(0)
        // .stroke(Color.GREEN).fill(Color.web("green", 0.1)).translateX(200).translateY(200).build();
        // p.getChildren().addAll(r, txt, left, right);
        getChildren().addAll(txt);
        // create horizontal rectangles to display leap location
        createHorizontalRectangles(this, 5);
    }

    private void createHorizontalRectangles(Pane p, int count) {
        final int fingerCount = 5;
        System.out.println("Creating horizontal rectangles");

        // we use the margin to leave space between rectangles

        // determine the form of the main rectangles
        final int rectMargin = 10;
        final int rectHeight = 20;

        int miniMargin = 10;

        int rectY = this.sceneHeight - rectHeight; // rectangle on bottom edge

        // draw rectangles
        for (int i = 0; i < adapter.getCategories(); i++) {
            // create space for horizontal rectangles
            List<HandRect> horizRects = new LinkedList<>();
            categories.add(horizRects);

            // create properties for rectangles
            final int rectCount = adapter.getSubCategories(i);
            final int rectWidth = (int) (this.sceneWidth / rectCount);
            final int rectRealWidth = rectWidth - (2 * rectMargin);

            // create mini rectangle properties
            int miniRectWidth = (rectRealWidth / fingerCount);
            int miniRectRealWidth = miniRectWidth - miniMargin;
            int miniRectHeight = miniRectRealWidth;

            int rectX = rectMargin;
            int miniRectY = rectY - miniRectHeight - rectMargin;

            for (int j = 0; j < rectCount; j++) {
                horizRects.add(new HandRect(p, rectHeight, rectWidth, rectX, miniRectY, miniRectHeight, miniRectWidth));
                rectX += rectWidth;
            }
            rectY -= (this.sceneWidth / rectCount);
            System.out.println("RectY is now: " + rectY);
        }
    }
}
