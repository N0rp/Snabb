package eu.dowsing.kolla.app;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.layout.Pane;
import eu.dowsing.kolla.widget.WidgetInterface;

/**
 * Minimal app.
 * 
 * @author richardg
 * 
 */
public abstract class BasicApp implements AppInterface {

    private final String name;
    private Pane view;

    private List<WidgetInterface> widgets = new LinkedList<>();

    private AppSwitcher appSwitcher;

    public BasicApp(String name) {
        this.name = name;
    }

    protected void setView(Pane view) {
        this.view = view;
    }

    @Override
    public Pane getView() {
        return view;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onAppResize(int window, double width, double height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAppSwitcherUpdate(AppSwitcher appSwitcher) {
        this.appSwitcher = appSwitcher;
    }

    @Override
    public AppSwitcher getAppSwitcher() {
        return this.appSwitcher;
    }

    @Override
    public void addWidget(WidgetInterface widget) {
        this.widgets.add(widget);
        this.view.getChildren().add(widget.getNode());
    }

    @Override
    public void onSchedule(AppSwitcher appSwitcher, long scheduleTime) {
        for (WidgetInterface widget : widgets) {
            widget.onSchedule(appSwitcher, scheduleTime);
        }
    }
}
