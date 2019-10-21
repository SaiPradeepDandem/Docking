package com.sai.javafx.docking;

import java.util.function.Supplier;

/**
 * Singleton DockBoard (similar to DragClipBoard) for docking purpose.
 */
public class DockBoard {

    private static DockBoard dockBoard = new DockBoard();

    /**
     * Don't let anyone else instantiate this class
     */
    private DockBoard() {
    }

    public static DockBoard getDockBoard() {
        return dockBoard;
    }

    private Supplier<DockNodeContent> dockContentSupplier;

    public Supplier<DockNodeContent> getDockContentSupplier() {
        return dockContentSupplier;
    }

    public void setDockContentSupplier(Supplier<DockNodeContent> dockContentSupplier) {
        this.dockContentSupplier = dockContentSupplier;
    }
}
