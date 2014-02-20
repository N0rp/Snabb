package eu.dowsing.leap.pres;

public interface SlideChangedListener {

    /**
     * Called when the slide show moves to the next slide.
     * 
     * @param slideIndex
     *            the current slide index
     */
    void onSlideChanged(int slideIndex);

}
