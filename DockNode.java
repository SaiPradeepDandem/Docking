package com.sai.javafx.docking;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Transform;

public class DockNode extends VBox {

    private DockNodeContent dockNodeContent;
    private Label labelTitle;
    private BorderPane windowTitleBar;
    private StackPane container;
    private DockPane dockPane;

    public DockNode(DockNodeContent dockNodeContent, DockPane dockPane) {
        this.dockPane = dockPane;
        this.dockNodeContent = dockNodeContent;
        labelTitle = new Label(dockNodeContent.getTitle());
        buildTitleBar();
        container = new StackPane(dockNodeContent.getContent());
        container.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(container, Priority.ALWAYS);

        getChildren().addAll(windowTitleBar, container);
        getStyleClass().add("xfe-docknode");
    }

    public DockNodeContent getDockNodeContent() {
        return dockNodeContent;
    }

    private void buildTitleBar() {
        StackPane buttonPopOut = new StackPane();
        Tooltip.install(buttonPopOut, new Tooltip("Move to new window"));
        buttonPopOut.getStyleClass().add("xfe-docknode-button-detach");
        buttonPopOut.setOnMouseClicked(evt -> getDockPane().popOutChild(DockNode.this));

        StackPane buttonClose = new StackPane();
        Tooltip.install(buttonClose, new Tooltip("Close"));
        buttonClose.getStyleClass().add("xfe-docknode-button-close");
        buttonClose.setOnMouseClicked(evt -> getDockPane().removeChild(DockNode.this));

        HBox dynamicTitleBox = new HBox(labelTitle);
        dynamicTitleBox.setSpacing(10);
        dynamicTitleBox.setAlignment(Pos.CENTER_LEFT);
        HBox buttonPane = new HBox(buttonPopOut, buttonClose);
        buttonPane.setPadding(new Insets(3));
        buttonPane.setSpacing(6);

        StackPane headerPane = new StackPane();
        windowTitleBar = new BorderPane();
        windowTitleBar.setLeft(dynamicTitleBox);
        BorderPane.setAlignment(windowTitleBar, Pos.CENTER_LEFT);
        windowTitleBar.setCenter(headerPane);
        BorderPane.setAlignment(windowTitleBar, Pos.CENTER_RIGHT);
        windowTitleBar.setRight(buttonPane);
        BorderPane.setAlignment(windowTitleBar, Pos.CENTER_RIGHT);
        windowTitleBar.setPadding(new Insets(0, 0, 0, 10));
        windowTitleBar.getStyleClass().add("xfe-docknode-title-bar");
        BorderPane.setAlignment(windowTitleBar, Pos.TOP_CENTER);

        windowTitleBar.setOnDragDetected(
                (MouseEvent event) -> {
                    windowTitleBar.startFullDrag();
                    SnapshotParameters snapshotParams = new SnapshotParameters();
                    snapshotParams.setTransform(Transform.scale(0.4, 0.4));
                    DockBoard.getDockBoard().setDockContentSupplier(() -> {
                        getDockPane().removeChildWithoutHandler(DockNode.this);
                        return getDockNodeContent();
                    });

                    final ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.put(DockUtils.DOCK_FORMAT_SOURCE, "");
                    final WritableImage snapshot = DockNode.this.snapshot(snapshotParams, null);
                    final Dragboard db = windowTitleBar.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(snapshot, 40, 40);
                    db.setContent(clipboardContent);
                    event.consume();
                }
        );

        windowTitleBar.setOnDragDone(
                (DragEvent event) -> {
                    DockBoard.getDockBoard().setDockContentSupplier(null);
                    event.consume();
                }
        );
    }

    public DockNodeContent switchContent(DockNodeContent other) {
        DockNodeContent ret = this.dockNodeContent;
        labelTitle.setText(other.getTitle());
        container.getChildren().clear();
        container.getChildren().add(other.getContent());
        this.dockNodeContent = other;
        return ret;
    }

    public DockPane getDockPane() {
        return dockPane;
    }
}
