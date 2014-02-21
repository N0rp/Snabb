package eu.dowsing.leap.brick;

public interface BrickMenuAdapterInterface {

    /**
     * Get the number of categories to display.
     * 
     * @return
     */
    int getCategories();

    /**
     * Get the number of subcategories to display
     * 
     * @param categoryIndex
     *            the index of the category that the subcategories need to be known
     * @return
     */
    int getSubCategories(int categoryIndex);

}
