package com.sai.javafx.docking;

import javafx.scene.control.Button;

/**
   * Base class for a dock indicator button that allows it to be displayed during a dock event and
   * continue to receive input.
   *
   */
  public class DockPositionButton extends Button {
    /**
     * Whether this dock indicator button is used for docking a node relative to the root of the
     * dock pane.
     */
    private boolean dockRoot = true;
    /**
     * The docking position indicated by this button.
     */
    private DockPosition dockPosition;

    /**
     * Creates a new dock indicator button.
     */
    public DockPositionButton(boolean dockRoot, DockPosition dockPosition) {
      super();
      getStyleClass().clear();
      this.dockRoot = dockRoot;
      this.dockPosition = dockPosition;
    }

    /**
     * Whether this dock indicator button is used for docking a node relative to the root of the
     * dock pane.
     *
     * @param dockRoot Whether this indicator button is used for docking a node relative to the root
     *        of the dock pane.
     */
    public final void setDockRoot(boolean dockRoot) {
      this.dockRoot = dockRoot;
    }

    /**
     * The docking position indicated by this button.
     *
     * @param dockPosition The docking position indicated by this button.
     */
    public final void setDockPosition(DockPosition dockPosition) {
      this.dockPosition = dockPosition;
    }

    /**
     * The docking position indicated by this button.
     *
     * @return The docking position indicated by this button.
     */
    public final DockPosition getDockPosition() {
      return dockPosition;
    }

    /**
     * Whether this dock indicator button is used for docking a node relative to the root of the
     * dock pane.
     *
     * @return Whether this indicator button is used for docking a node relative to the root of the
     *         dock pane.
     */
    public final boolean isDockRoot() {
      return dockRoot;
    }
  }
