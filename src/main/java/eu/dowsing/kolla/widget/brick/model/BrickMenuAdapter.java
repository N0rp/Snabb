package eu.dowsing.leap.brick.presentation;

import eu.dowsing.kolla.widget.brick.BrickMenuAdapterInterface;

public class BasicMenuAdapter implements BrickMenuAdapterInterface {

    /** The current category set by the users primary hand. -1 if there is no category. **/
    private int currentCategory = -1;

    /**
     * Get the current category, set by the users primary hand.
     * 
     * @return the current category or -1 if there is none
     */
    public int getCurrentCategory() {
        return this.currentCategory;
    }

    @Override
    public int getCategories() {
        // TODO
        return 5;
    }

    @Override
    public int getSubCategories(int categoryIndex) {
        // TODO for now we have 6 subcategories per level
        return 6;
    }

}
