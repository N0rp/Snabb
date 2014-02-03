/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.idrsolutions.com
 * Help section for developers at http://www.idrsolutions.com/java-pdf-library-support/
 *
 * List of all example and a link to zip at http://www.idrsolutions.com/java-code-examples-for-pdf-files/
 *
 * (C) Copyright 1997-2014, IDRsolutions and Contributors.
 *
 *   This file is part of JPedal
 *
    This source code is copyright IDRSolutions 2012
    
    Original code from http://files.idrsolutions.com/samplecode/org/jpedal/examples/images/ConvertPagesToImages.java.html


 *
 *
 * ---------------
 * BaseViewerFX.java
 * ---------------
 */

package eu.dowsing.leap.core;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.jpedal.PdfDecoderFX;
import org.jpedal.exception.PdfException;
import org.jpedal.objects.PdfPageData;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

import eu.dowsing.leap.core.GesturesListener.Swipe;
import eu.dowsing.leap.gesture.DoubleHandListener;

public class RichViewerFX extends Application {

    private org.jpedal.PdfDecoderFX pdf; // = new org.jpedal.PdfDecoderFX();

    /**
     * Enum to control how we fit the content to the page.
     * 
     * AUTO will automatically fit the content to the stage depending on its
     * orientation WIDTH will fit the content to the stage width depending on
     * its orientation HEIGHT will fit the content to the stage height depending
     * on its orientation
     */
    public enum FitToPage {
        AUTO, WIDTH, HEIGHT;
    }

    String                     PDFfile;

    // Variable to hold the current file/directory
    File                       file;

    // These two variables are todo with PDF encryption & passwords
    private String             password            = null;                    // Holds
                                                                               // the
                                                                               // password
                                                                               // from
                                                                               // the
                                                                               // JVM
                                                                               // or
                                                                               // from
                                                                               // User
                                                                               // input
    private boolean            closePasswordPrompt = false;                   // boolean
                                                                               // controls
                                                                               // whether
                                                                               // or
                                                                               // not
                                                                               // we
                                                                               // should
                                                                               // close
                                                                               // the
                                                                               // prompt
                                                                               // box

    // Layout panes
    private VBox               top;
    private HBox               bottom;
    private VBox               center;
    private ScrollPane         sp;
    // Group is a container which holds the decoded PDF content
    private Group              group;

    // displays "page x of y"
    private Text               pageLoc;
    // for the location of the pdf file
    private Text               fileLoc;
    private Text               spacer;

    private float              scale               = 1.0f;

    private float[]            scalings            = { 0.01f, 0.1f, 0.25f,
            0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 4.0f, 7.5f, 10.0f };

    private int                currentScaling      = 5;

    private float              insetX              = 25;

    private float              insetY              = 25;

    private int                currentPage         = 1;

    Stage                      stage;

    Scene                      scene;

    // Controls size of he stage, in theory setting this to a higher value will
    // increase image quality as there's more pixels due to higher image
    // resolutions
    int                        FXscaling           = 1;

    private RichLeapListener   pointlistener       = new RichLeapListener();
    private GesturesListener   gesturelistener     = new GesturesListener();
    private HandSignListener   handSignlistener    = new HandSignListener();
    private DoubleHandListener doubleListener      = new DoubleHandListener();
    private Controller         leapController      = new Controller();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Base Viewer FX - " + PdfDecoderFX.version);

        this.stage = stage;
        pdf = new org.jpedal.PdfDecoderFX();

        scene = setupViewer(800, 600);

        // Get command line arguments
        List<String> args = this.getParameters().getUnnamed();

        /**
         * setup initial display Setting this before loadPDF() gives access to
         * the toolbar buttons when called in loadPDF() via id.
         */
        stage.setScene(scene);
        stage.show();

        // this.getParameters().getUnamed() will never be null according to FX
        // Javadoc, so no null check needed
        // http://docs.oracle.com/javafx/2/api/javafx/application/Application.Parameters.html#getUnnamed%28%29
        if (args.size() == 1) {
            String input = args.get(0);
            file = new File(input);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadPDF(file);
                }
            });

        }

        addListeners();

        setupLeap();
    }

    private void setupLeap() {
        leapController.addListener(pointlistener);
        leapController.addListener(gesturelistener);
        leapController.addListener(handSignlistener);
        leapController.addListener(doubleListener);

        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);

        // circle.setLayoutX(circle.getRadius());
        // circle.setLayoutY(circle.getRadius());
        // root.getChildren().add(circle);
        // final Scene scene = new Scene(root, 800, 600);

        pointlistener.pointProperty().addListener(
                new ChangeListener<Point2D>() {
                    @Override
                    public void changed(ObservableValue ov, Point2D t,
                            final Point2D t1) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
        gesturelistener.gestureProperty().addListener(
                new ChangeListener<Swipe>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends Swipe> observable,
                            Swipe oldValue, final Swipe newValue) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (newValue == Swipe.LEFT) {
                                    FXRobot robot = FXRobotFactory
                                            .createRobot(scene);
                                    robot.keyPress(javafx.scene.input.KeyCode.LEFT);
                                    openNextPage();
                                } else if (newValue == Swipe.RIGHT) {
                                    openPreviousPage();
                                }
                            }
                        });

                    }

                });

    }

    public Scene setupViewer(int w, int h) {

        /*
         * Setting up layout panes and assigning them to the appropiate
         * locations
         */
        BorderPane root = new BorderPane();

        top = new VBox();

        root.setTop(top);

        top.getChildren().add(setupToolBar());

        bottom = new HBox();
        bottom.setPadding(new Insets(0, 10, 0, 10));
        root.setBottom(bottom);

        sp = new ScrollPane();
        sp.setPannable(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // needs to be added via group so resizes (see
        // http://pixelduke.wordpress.com/2012/09/16/zooming-inside-a-scrollpane/)
        group = new Group();
        group.getChildren().add(pdf);
        sp.setContent(group);

        center = new VBox();

        center.getChildren().add(sp);

        center.setAlignment(Pos.CENTER);

        root.setCenter(center);

        /** Sets the text to be displayed at the bottom of the FX Viewer **/
        pageLoc = new Text("Page x of y");
        pageLoc.setId("navigation");
        bottom.getChildren().add(pageLoc);

        spacer = new Text(" of Document : ");
        spacer.setId("spacer");
        bottom.getChildren().add(spacer);

        fileLoc = new Text("No PDF Selected");
        fileLoc.setId("file_location");
        bottom.getChildren().add(fileLoc);

        scene = new Scene(root, w * FXscaling, h * FXscaling);

        return scene;
    }

    public void addListeners() {

        /**
         * auto adjust so dynamically resized as viewer width alters
         */
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldSceneWidth, Number newSceneWidth) {

                fitToX(FitToPage.WIDTH);

            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldSceneHeight, Number newSceneHeight) {

                fitToX(FitToPage.HEIGHT);

            }
        });

        /**
         * Controls for dragging a PDF into the scene Using the dragboard, which
         * extends the clipboard class, detect a file being dragged onto the
         * scene and if the user drops the file we load it.
         */
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });

        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    // Only get the first file from the list
                    file = db.getFiles().get(0);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            loadPDF(file);
                        }
                    });
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    /**
     * Sets up a MenuBar to be used at the top of the window.
     * 
     * It contains one Menu - navMenu - which allows the user to open and
     * navigate pdf files
     * 
     * @return
     */
    private ToolBar setupToolBar() {

        ToolBar toolBar = new ToolBar();

        Button open = new Button("Open");
        final Button back = new Button("Back");
        final Button forward = new Button("Forward");
        Button zoomIn = new Button("Zoom in");
        Button zoomOut = new Button("Zoom out");
        Button fitWidth = new Button("Fit to Width");
        Button fitHeight = new Button("Fit to Height");
        Button fitPage = new Button("Fit to Page");
        Button fullScreen = new Button("Full Screen");

        open.setId("open");
        back.setId("back");
        forward.setId("forward");
        zoomIn.setId("zoomIn");
        zoomOut.setId("zoomOut");
        fitWidth.setId("fitWidth");
        fitHeight.setId("fitHeight");
        fitPage.setId("fitPage");
        fullScreen.setId("fullScreen");

        /**
         * Toggle Full-screen Mode
         */
        if (stage != null) {
            fullScreen.setOnAction(new EventHandler<ActionEvent>() {
                // Toggle between fullscreen and windowed
                @Override
                public void handle(ActionEvent t) {
                    if (stage.isFullScreen()) {
                        stage.setFullScreen(false);
                    } else {
                        stage.setFullScreen(true);
                    }
                }
            });
        }

        /**
         * Open the PDF File
         */
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open PDF file");

                // Open directory from existing directory
                if (file != null) {
                    File existDirectory = file.getParentFile();
                    chooser.setInitialDirectory(existDirectory);
                }

                // Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                        "PDF files (*.pdf)", "*.pdf");
                chooser.getExtensionFilters().add(extFilter);

                file = chooser.showOpenDialog(null);

                if (file != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            loadPDF(file);
                        }
                    });
                }
            }
        });

        back.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                // Navigate backward
                openPreviousPage();

            }
        });

        forward.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                // Navigate forward
                openNextPage();
            }
        });

        zoomIn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                // Zoom in

                if (currentScaling < scalings.length - 1) {

                    currentScaling = findClosestIndex(scale, scalings);

                    if (scale >= scalings[findClosestIndex(scale, scalings)]) {

                        currentScaling++;

                    }

                    scale = scalings[currentScaling];

                }

                pdf.setPageParameters(scale, currentPage);

                resize();
            }
        });

        zoomOut.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                // Zoom out

                if (currentScaling > 0) {

                    currentScaling = findClosestIndex(scale, scalings);

                    if (scale <= scalings[findClosestIndex(scale, scalings)]) {

                        currentScaling--;

                    }

                    scale = scalings[currentScaling];

                }

                pdf.setPageParameters(scale, currentPage);

                resize();
            }
        });

        fitWidth.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                fitToX(FitToPage.WIDTH); // Fit to width

            }
        });

        fitHeight.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                fitToX(FitToPage.HEIGHT); // Fit to height

            }
        });

        fitPage.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                fitToX(FitToPage.AUTO); // Fit to Page

            }
        });

        toolBar.getItems().add(open);

        toolBar.getItems().add(back);
        toolBar.getItems().add(forward);
        toolBar.getItems().add(zoomIn);
        toolBar.getItems().add(zoomOut);
        toolBar.getItems().add(fitWidth);
        toolBar.getItems().add(fitHeight);
        toolBar.getItems().add(fitPage);

        if (stage != null) {
            toolBar.getItems().add(fullScreen);
        }

        return toolBar;
    }

    private void openNextPage() {
        if (currentPage < pdf.getPageCount()) {
            currentPage++;
            decodePage();
            fitToX(FitToPage.AUTO);
            updateNavButtons();
            System.out.println("OPEN next page");
        }
    }

    private void openPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            decodePage();
            fitToX(FitToPage.AUTO);
            updateNavButtons();
            System.out.println("OPEN previous page");
        }
    }

    public void loadPDF(File input) {

        if (input == null)
            return;

        scale = 1; // reset to default for new page

        currentPage = 1;

        PDFfile = input.getAbsolutePath();
        fileLoc.setText(PDFfile);

        try {

            // Open the pdf file so we can check for encryption
            pdf.openPdfFile(input.getAbsolutePath());

            /**
             * This code block deals with user input and JVM passwords in
             * Encrypted PDF documents.
             */
            if (pdf.isEncrypted()) {

                int passwordCount = 0; // Monitors how many attempts there have
                                       // been to the password
                closePasswordPrompt = false; // Do not close the prompt box

                // While the PDF content is not viewable, repeat until the
                // correct password is found
                while (!pdf.isFileViewable() && !closePasswordPrompt) {

                    /**
                     * See if there's a JVM flag for the password & Use it if
                     * there is Otherwise prompt the user to enter a password
                     */
                    if (System.getProperty("org.jpedal.password") != null) {
                        password = System.getProperty("org.jpedal.password");
                    } else if (!closePasswordPrompt) {
                        showPasswordPrompt(passwordCount);
                    }

                    // If we have a password, try and open the PdfFile again
                    // with the password
                    if (password != null) {
                        // pdf.setEncryptionPassword(password);
                        pdf.openPdfFile(input.getAbsolutePath(), password);
                    }
                    passwordCount = passwordCount + 1; // Increment he password
                                                       // attempt

                }

            }

        } catch (PdfException ex) {
            ex.printStackTrace();
            // If the pdf failed to open, don't decode it.
            return;
        }

        // Disable nav buttons on single page documents
        if (pdf.getPageCount() <= 1) {
            top.lookup("#forward").setDisable(true);
            top.lookup("#back").setDisable(true);
        } else {
            top.lookup("#forward").setDisable(false);
            top.lookup("#back").setDisable(false);
            updateNavButtons();
        }

        // PdfDecoder stuff...
        decodePage();

        fitToX(FitToPage.AUTO);

    }

    /**
     * This method will show a popup box and request for a password.
     * 
     * If the user does not enter the correct password it will ask them to try
     * again. If the user presses the Cross button, the password prompt will
     * close.
     * 
     * @param passwordCount
     *            is an int which represents the current input attempt
     */
    private void showPasswordPrompt(final int passwordCount) {

        // Setup password prompt content
        final Stage enterPasswordStage = new Stage();
        Button okButton = new Button("Ok");
        Text titleText = new Text("Password Request");
        final TextField inputPasswordField = new TextField(
                "Please Enter Password");

        // If the user has attempted to enter the password more than once,
        // change the text
        if (passwordCount >= 1) {
            titleText.setText("Incorrect Password");
            inputPasswordField.setText("Please Try Again");
        }

        // Setup the password prompt & add children
        enterPasswordStage.initModality(Modality.WINDOW_MODAL);
        enterPasswordStage.setScene(new Scene(VBoxBuilder.create()
                .children(titleText, inputPasswordField, okButton)
                .alignment(Pos.CENTER).padding(new Insets(10)).build()));

        // If the Ok button is pressed, store the user input as the password
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                password = inputPasswordField.getText();
                enterPasswordStage.hide();
            }
        });

        // Check whether stage is closed with the cross button, if it is stop
        // requesting password.
        enterPasswordStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                closePasswordPrompt = true;
            }
        });

        // Halt everything until the Ok button / Cross button is pressed
        enterPasswordStage.showAndWait();

    }

    private void resize() {

        if (scene != null) {

            sp.setHvalue(scale);
            double sceneWidth = scene.widthProperty().doubleValue();
            double sceneHeight = scene.heightProperty().doubleValue() - 60;

            double Xdiff = (sceneWidth - pdf.getPDFWidth()) / 2;
            if (Xdiff < insetX)
                Xdiff = insetX;

            pdf.setMaxSize(pdf.getPDFWidth(), pdf.getPDFHeight());
            // sp.setTranslateY(insetY);

            // Moves the group(which contains var pdf)
            // thus moving all decoded content by XDiff in X-plane.
            group.setTranslateX(Xdiff);

            sp.setMinSize(sceneWidth, sceneHeight);
            sp.setMaxSize(pdf.getPDFWidth(), pdf.getPDFHeight() + insetY);

            // pdf.setTranslateY(insetY);

        }
    }

    private void fitToX(FitToPage fitToPage) {

        float pageW = pdf.getPdfPageData().getCropBoxWidth2D(currentPage);
        float pageH = pdf.getPdfPageData().getCropBoxHeight2D(currentPage);
        int rotation = pdf.getPdfPageData().getRotation(currentPage);

        // Handle how we auto fit the content to the page
        if (fitToPage == FitToPage.AUTO) {
            if (pageW < pageH) {
                if (pdf.getPDFWidth() < pdf.getPDFHeight()) {
                    fitToX(FitToPage.HEIGHT);
                } else {
                    fitToX(FitToPage.WIDTH);
                }
            }
        }

        // Handle how we fit the content to the page width or height
        if (fitToPage == FitToPage.WIDTH) {
            float width = (float) (scene.getWidth());
            if (rotation == 90 || rotation == 270) {
                scale = (width - insetX - insetX) / pageH;
            } else {
                scale = (width - insetX - insetX) / pageW;
            }
        } else if (fitToPage == FitToPage.HEIGHT) {
            float height = (float) (scene.getHeight()
                    - top.getBoundsInLocal().getHeight() - bottom.getHeight());

            if (rotation == 90 || rotation == 270) {
                scale = (height - insetY - insetY) / pageW;
            } else {
                scale = (height - insetY - insetY) / pageH;
            }
        }

        pdf.setPageParameters(scale, currentPage);
        resize();
    }

    /**
     * Locate scaling value closest to current scaling setting
     * 
     * @param scale
     * @param scalings
     * @return
     */
    private static int findClosestIndex(float scale, float[] scalings) {
        float currentMinDiff = Float.MAX_VALUE;
        int closest = 0;

        for (int i = 0; i < scalings.length - 1; i++) {

            final float diff = Math.abs(scalings[i] - scale);

            if (diff < currentMinDiff) {
                currentMinDiff = diff;
                closest = i;
            }

        }
        return closest;
    }

    private void decodePage() {

        try {
            pageLoc.setText("Page " + currentPage + " of " + pdf.getPageCount()); // display
                                                                                  // what
                                                                                  // page
                                                                                  // we're
                                                                                  // currently
                                                                                  // on

            PdfPageData pageData = pdf.getPdfPageData();
            int rotation = pageData.getRotation(currentPage); // rotation angle
                                                              // of current page

            // Only call this when the page is displayed vertically, otherwise
            // it will mess up the document cropping on side-ways documents.
            if (rotation == 0 || rotation == 180) {
                pdf.setPageParameters(scale, currentPage);
            }

            pdf.decodePage(currentPage);
            // wait to ensure decoded
            pdf.waitForDecodingToFinish();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateNavButtons() {
        if (currentPage > 1) {
            top.lookup("#back").setDisable(false);
        } else {
            top.lookup("#back").setDisable(true);
        }

        if (currentPage < pdf.getPageCount()) {
            top.lookup("#forward").setDisable(false);
        } else {
            top.lookup("#forward").setDisable(true);
        }
    }

    public String getPDFfilename() {
        return PDFfile;
    }
}
