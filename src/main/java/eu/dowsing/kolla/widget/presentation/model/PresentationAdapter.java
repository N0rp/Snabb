package eu.dowsing.kolla.widget.presentation.model;

import javafx.application.Platform;
import eu.dowsing.kolla.widget.presentation.PresentationAdapterInterface;
import eu.dowsing.kolla.widget.presentation.SlideChangedListener;
import eu.dowsing.kolla.widget.presentation.view.PresentationView;

public class PresentationAdapter implements PresentationAdapterInterface {

    /** the url location **/
    public enum UrlLocation {
        /** local file system **/
        Local,
        /** on the web **/
        Web
    }

    public final static String WEB_BORED = "http://bartaz.github.io/impress.js/#/bored";
    public final static String WEB_STRUT = "http://strut.io/editor/#";
    public final static String LOCAL_BORED = "res/web/bored/index.html";
    public final static String LOCAL_PRES = "res/web/temp/Deck Title.html";

    public final static String LOCAL_INNOTALK = "res/web/innotalk/innotalk/index.html";

    private int currentStep = -1;

    private PresentationView view;

    public PresentationAdapter(PresentationView view) {
        this.view = view;

        view.addSlideChangedListener(new SlideChangedListener() {

            @Override
            public void onSlideChanged(final int slideIndex) {
                currentStep = slideIndex;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // System.out.println("Slide changed");

                    }
                });
            }
        });
    }

    // public void setBrowser(PresentationView browser) {
    // this.browser = browser;
    // }
    //

    @Override
    public void gotoPage(int index) {
        this.currentStep = index;
        view.gotoPage(currentStep);
    }

    @Override
    public void gotoNextPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotoPreviousPage() {
        // TODO Auto-generated method stub

    }
}
