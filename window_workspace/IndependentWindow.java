package com.sai.javafx.independentwindow.workspace;

import com.sai.javafx.docking.window.ManagedXfeWindow;
import com.sai.javafx.docking.window.XfeWindowManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Duration;

public class IndependentWindow extends ManagedXfeWindow {

    private MenuBar menu;
    private IndependentWindowContent independentWindowContent;

    public IndependentWindow(String id, Window owner, IndependentWindowContent content, String title, XfeWindowManager xfeWindowManager) {
        this(id, owner, content, title, xfeWindowManager, null);
    }

    public IndependentWindow(IndependentWindowArgs args) {
        this(args.getId(), args.getOwner(), args.getContent(), args.getTitle(), args.getXfeWindowManager(), args.getBounds());
    }

    public IndependentWindow(String id, Window owner, IndependentWindowContent content, String title, XfeWindowManager xfeWindowManager, Rectangle2D bounds) {
        super(id, owner, null, title, xfeWindowManager, bounds);
        independentWindowContent = content;
        independentWindowContent.setId(id);
        getLabelTitle().setStyle("-fx-font-weight:bold;");
        getWindowPane().getStyleClass().add("xfe-independent-window");
        // TODO: Need to update
        if (content != null) {
            final StackPane stageRoot = new StackPane();
            stageRoot.getChildren().add(content);
            setWindowContent(stageRoot, "ICAP");
            setContent(content);
        }

        // Set custom event dispatcher on icon.
        //EventDispatcher defaultDispatcher = getIconContainer().getEventDispatcher();
        //getIconContainer().setEventDispatcher(new XfeEventDispatcher(defaultDispatcher));
        setWindowTitle(title);
        // setLabelSubTitle(title);

        bindWorkspace();
    }

    public IndependentWindowContent getIndependentWindowContent() {
        return independentWindowContent;
    }

    private void bindWorkspace() {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> independentWindowContent.saveToWorkspace()));
        InvalidationListener listener = e -> {
            if (tl.getStatus() == Animation.Status.RUNNING) {
                tl.playFromStart();
            } else {
                tl.play();
            }
        };
        xProperty().addListener(listener);
        yProperty().addListener(listener);
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
        showingProperty().addListener(listener);
        currentWindowStateProperty().addListener(listener);
    }

    public void setUserMenu(MenuBar menu) {
        this.menu = menu;
        getHeaderPane().getChildren().add(menu);
    }

    public Menu getUserMenu() {
        if (menu != null) {
            return menu.getMenus().get(0);
        }
        return null;
    }
}
