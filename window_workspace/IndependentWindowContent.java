package com.sai.javafx.independentwindow.workspace;

import com.sai.javafx.docking.window.XfeWindow;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Content node of the IndependentWindow. This node has some custom functionality to include workspace setting saving.
 */
public class IndependentWindowContent extends StackPane {
    /**
     * User name of current logged in user.
     */
    private String userName;

    /**
     * Workspace object of the independent window.
     */
    protected IndependentWindowWorkspace workspace;

    /**
     * Builds the properties of workspace for the window content. The subClass will define the content properties if it has any to save to workspace.
     *
     * @return List of properties. By default returns {@code null}.
     */
    protected List<IndependentWindowProperty> buildContentProperties() {
        return null;
    }

    /**
     * Applies the properties of workspace object to the window content node. The subClass will apply the content properties if it has any to apply.
     */
    protected void applyContentProperties() {
        // Empty
    }

    /**
     * Applies the standard window properties to the IndependentWindow from the workspace object.
     */
    private void applyWindowProperties() {
        if (getScene() != null) {
            final XfeWindow window = (XfeWindow) getScene().getWindow();
            window.setWidth(workspace.getWidth());
            window.setHeight(workspace.getHeight());
            window.setX(workspace.getX());
            window.setY(workspace.getY());
            if (workspace.isShowing()) {
                window.showWindow();
            }
            if (workspace.getState() != null) {
                window.setWindowState(XfeWindow.WindowState.valueOf(workspace.getState()));
            }
        }
    }

    /**
     * Updates the standard properties of IndependentWindow to the workspace object.
     */
    private void updateWindowProperties() {
        if (getScene() != null) {
            final XfeWindow window = (XfeWindow) getScene().getWindow();
            workspace.setWidth(window.getWidth());
            workspace.setHeight(window.getHeight());
            workspace.setX(window.getX());
            workspace.setY(window.getY());
            workspace.setShowing(window.isShowing());
            workspace.setState(window.getCurrentWindowState().toString());
        }
    }

    /**
     * Saves the workspace object to the file system.
     */
    public final void saveToWorkspace() {
        if (workspace != null) {
            System.out.println("Saving to workspace......");
            updateWindowProperties();
            workspace.setProperties(buildContentProperties());
            WorkspaceUtil.save(userName, getId(), workspace);
        }
    }

    /**
     * Sets the workspace object. Also applies all the window and content properties from the object to the window and node correspondingly.
     *
     * @param workspace Workspace object retrieved from file system
     */
    public void setWorkspace(IndependentWindowWorkspace workspace) {
        this.workspace = workspace;
        applyWindowProperties();
        applyContentProperties();
    }

    /**
     * Sets the current logged in user name.
     *
     * @param userName User name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
