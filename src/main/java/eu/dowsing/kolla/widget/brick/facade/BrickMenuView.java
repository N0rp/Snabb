package eu.dowsing.kolla.widget.brick.facade;

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
import eu.dowsing.kolla.widget.brick.BrickMenuAdapterInterface;
import eu.dowsing.kolla.widget.brick.facade.BrickView.Importance;
import eu.dowsing.kolla.widget.brick.model.BrickModel;
import eu.dowsing.leap.pres.Browser;

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

    private Text currentStep;
    private Text currentKeyInput;

    private List<List<BrickView>> categories = new LinkedList<>();

    private int sceneWidth;
    private int sceneHeight;

    private Rectangle noadapter;

    private BrickMenuAdapterInterface adapter;

    private Browser browser;

    public BrickMenuView(int sceneWidth, int sceneHeight) {
        this.sceneWidth = sceneHeight;
        this.sceneHeight = sceneHeight;

        // show a big red rectangle indicating no adapter has been set yet
        this.noadapter = RectangleBuilder.create().height(this.sceneHeight).width(this.sceneHeight).stroke(Color.RED)
                .fill(Color.web("red", 0.1)).build();
        getChildren().add(noadapter);
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public void setCurrentStepText(String text) {
        if (this.currentStep != null) {
            if (browser != null) {
                text += " - " + browser.getCompletion() + "%";
            }
            this.currentStep.setText(text);

        }
    }

    public void setCurrentKeyInputText(String text) {
        if (this.currentKeyInput != null) {
            this.currentKeyInput.setText(text);
        }
    }

    /**
     * Will clear the view of displaying any hands
     */
    public void clearHands() {
        for (List<BrickView> rectangles : this.categories) {
            for (BrickView rect : rectangles) {
                rect.setVisible(false);
            }
        }
    }

    /**
     * Show only the indicators for the given category
     * 
     * @param category
     */
    public void showCategoryHint(BrickModel hand) {
        if (hand.getCategory() >= 0) {
            // hide everything
            setCategoryHintVisibile(false);
            // show category menu if hand in right position
            if (hand.getSubCategory() == 0 || hand.getSubCategory() + 1 == adapter.getSubCategories(hand.getCategory())) {
                // if the hand is at either end
                for (List<BrickView> subCategories : this.categories) {
                    for (int i = 0; i < subCategories.size(); i++) {
                        BrickView rect = subCategories.get(i);
                        rect.setHintVisible(i == hand.getSubCategory());
                    }
                }
            }
            // show sub categories
            List<BrickView> subCategories = categories.get(hand.getCategory());
            for (BrickView rect : subCategories) {
                rect.setHintVisible(true);
            }
        } else {
            setCategoryHintVisibile(true);
        }
    }

    private void setCategoryHintVisibile(boolean visible) {
        for (List<BrickView> subCategory : this.categories) {
            for (BrickView rect : subCategory) {
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
    public void showHand(Importance importance, BrickModel hand, boolean handledGesture, BrickModel gestureStart) {
        BrickView rect = categories.get(hand.getCategory()).get(hand.getSubCategory());
        rect.showHand(importance, hand.getHandRoll(), hand.getFingerCount(), handledGesture);

        if (gestureStart != null && gestureStart.getCategory() >= 0 && gestureStart.getSubCategory() >= 0) {
            BrickView startHint = categories.get(gestureStart.getCategory()).get(gestureStart.getSubCategory());
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

        int fontWeight = 18;

        // int txtX = this.sceneHeight - rectHeight; // rectangle on bottom edge
        // int txtY = this.sceneHeight - rectHeight; // rectangle on bottom edge
        this.currentStep = TextBuilder.create().text("Overlay").translateY(fontWeight).translateX(sceneWidth / 2)
                .font(Font.font("Arial", FontWeight.BOLD, fontWeight)).fill(Color.BLUE).build();
        this.currentKeyInput = TextBuilder.create().text("Overlay").translateY(fontWeight * 2)
                .translateX(sceneWidth / 2).font(Font.font("Arial", FontWeight.BOLD, fontWeight)).fill(Color.RED)
                .build();
        getChildren().addAll(currentStep);
        getChildren().addAll(currentKeyInput);
        // int txtWidth = (int) txt.getBoundsInLocal().getWidth();
        // int txtHeight = (int) txt.getBoundsInLocal().getHeight();
        // System.out.println("->Overlay txt witdht is " + txtWidth);

        // Rectangle left = RectangleBuilder.create().height(100).width(100).arcHeight(0).arcWidth(0).stroke(Color.BLUE)
        // .fill(Color.web("blue", 0.1)).translateX(100).build();
        //
        // Rectangle right = RectangleBuilder.create().height(100).width(100).arcHeight(400).arcWidth(0)
        // .stroke(Color.GREEN).fill(Color.web("green", 0.1)).translateX(200).translateY(200).build();
        // p.getChildren().addAll(r, txt, left, right);

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
            List<BrickView> horizRects = new LinkedList<>();
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
                horizRects.add(new BrickView(p, rectHeight, rectWidth, rectX, miniRectY, miniRectHeight, miniRectWidth));
                rectX += rectWidth;
            }
            rectY -= (this.sceneWidth / rectCount);
            System.out.println("RectY is now: " + rectY);
        }
    }
}
