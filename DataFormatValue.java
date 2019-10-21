package com.sai.javafx.docking;

import javafx.scene.Node;

import java.util.function.Supplier;

public class DataFormatValue {
    private DockPane dockPane;
    private DockPosition dockPosition;
    private Supplier<Node> supplier;

    public DataFormatValue(DockPane dockPane, DockPosition dockPosition) {
        this.dockPane = dockPane;
        this.dockPosition = dockPosition;
    }

    public DataFormatValue(Supplier<Node> supplier) {
        this.supplier = supplier;
    }

    public DockPane getDockPane() {
        return dockPane;
    }

    public DockPosition getDockPosition() {
        return dockPosition;
    }

    public Supplier<Node> getSupplier() {
        return supplier;
    }
}
