package com.sai.javafx.docking;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.stream.Stream;

public class DockPane extends SplitPane {
    private static final double INDICATORS_SIZE = 152.0;
    private GridPane dockPosIndicator;
    private Rectangle dockAreaIndicator;
    private ObservableList<Node> dockPosButtons;

    /**
     * The popup used to display the root dock indicator buttons and the docking area indicator.
     */
    private Popup dockIndicatorOverlay;

    /**
     * The popup used to display the local dock indicator buttons. This allows these indicator buttons
     * to be displayed outside the window of this dock pane.
     */
    private Popup dockIndicatorPopup;

    private BooleanProperty showing = new SimpleBooleanProperty();

    private Timeline hideTimeline;

    private DockPane parentPane;

    public DockPane(DockNodeContent dockNodeContent) {
        DockNode dockNode = new DockNode(dockNodeContent, this);
        getItems().add(dockNode);

        buildDockIndicatorOverlay();
        buildDockIndicatorPopup();
        buildHideTimeline();

        showing.addListener((obs, old, show) -> {
            if (hideTimeline.getStatus() == Animation.Status.RUNNING) {
                hideTimeline.stop();
            }
            if (show) {
                if (!dockIndicatorOverlay.isShowing()) {
                    Point2D topLeft = DockPane.this.localToScreen(0, 0);
                    double shiftW = (DockPane.this.getLayoutBounds().getWidth() - INDICATORS_SIZE) / 2;
                    double shiftH = (DockPane.this.getLayoutBounds().getHeight() - INDICATORS_SIZE) / 2;
                    dockIndicatorOverlay.show(DockPane.this, topLeft.getX(), topLeft.getY());
                    dockIndicatorPopup.show(DockPane.this, topLeft.getX() + shiftW, topLeft.getY() + shiftH);
                }
            } else {
                hideTimeline.playFromStart();
            }
        });
        addHandlers(dockNode);
    }

    public void onChildRemoved() {
        if (getItems().isEmpty()) {
            if (getParentPane() != null) {
                getParentPane().getItems().removeAll(DockPane.this);
                getParentPane().onChildRemoved();
            } else {
                getScene().getWindow().hide();
            }
        }
    }


    public void removeChild(Node node) {
        getItems().remove(node);
        if (node instanceof DockNode) {
            DockNode dockNode = (DockNode) node;
            DockNodeContent dockNodeContent = dockNode.getDockNodeContent();
            dockNodeContent.getCloseHandler().accept(dockNodeContent.getContent());
        }
        onChildRemoved();
    }

    public void removeChildWithoutHandler(Node node) {
        getItems().remove(node);
        onChildRemoved();
    }

    public void popOutChild(DockNode dockNode) {
        Bounds bounds = dockNode.getLayoutBounds();
        Bounds screenBounds = dockNode.localToScreen(dockNode.getLayoutBounds());
        DockNodeContent dockNodeContent = dockNode.getDockNodeContent();
        DockableWindow window = DockableWindow.openWindow(dockNodeContent, dockNodeContent.getTitle(), new Point2D(screenBounds.getMinX(), screenBounds.getMinY()),
                () -> dockNodeContent.getCloseHandler().accept(dockNodeContent.getContent()),
                () -> {
                    getItems().remove(dockNode);
                    onChildRemoved();
                });
        window.setWidth(bounds.getWidth());
        window.setHeight(bounds.getHeight());
    }

    private void buildHideTimeline() {
        hideTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            dockIndicatorOverlay.hide();
            dockIndicatorPopup.hide();
        }));
        hideTimeline.setCycleCount(1);
    }

    private void buildDockIndicatorOverlay() {
        dockAreaIndicator = new Rectangle();
        dockAreaIndicator.getStyleClass().add("dock-area-indicator");
        Timeline dockAreaStrokeTimeline = new Timeline();
        dockAreaStrokeTimeline.setCycleCount(Timeline.INDEFINITE);
        KeyValue kv = new KeyValue(dockAreaIndicator.strokeDashOffsetProperty(), 12);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        dockAreaStrokeTimeline.getKeyFrames().add(kf);
        dockAreaStrokeTimeline.play();

        final DockPositionButton dockTop = new DockPositionButton(true, DockPosition.TOP);
        dockTop.getStyleClass().addAll("dock-button", "dock-top");
        StackPane.setMargin(dockTop, new Insets(5, 0, 0, 0));
        StackPane.setAlignment(dockTop, Pos.TOP_CENTER);

        final DockPositionButton dockRight = new DockPositionButton(true, DockPosition.RIGHT);
        dockRight.getStyleClass().addAll("dock-button", "dock-right");
        StackPane.setMargin(dockRight, new Insets(0, 5, 0, 0));
        StackPane.setAlignment(dockRight, Pos.CENTER_RIGHT);

        final DockPositionButton dockBottom = new DockPositionButton(true, DockPosition.BOTTOM);
        dockBottom.getStyleClass().addAll("dock-button", "dock-bottom");
        StackPane.setMargin(dockBottom, new Insets(0, 0, 5, 0));
        StackPane.setAlignment(dockBottom, Pos.BOTTOM_CENTER);

        final DockPositionButton dockLeft = new DockPositionButton(true, DockPosition.LEFT);
        dockLeft.getStyleClass().addAll("dock-button", "dock-left");
        StackPane.setMargin(dockLeft, new Insets(0, 0, 0, 5));
        StackPane.setAlignment(dockLeft, Pos.CENTER_LEFT);

        final StackPane dockRootPane = new StackPane();
        dockRootPane.setAlignment(Pos.TOP_LEFT);
        dockRootPane.getStyleClass().add("dock-root-pane");
        dockRootPane.prefWidthProperty().bind(this.widthProperty());
        dockRootPane.prefHeightProperty().bind(this.heightProperty());
        dockRootPane.getChildren().addAll(dockAreaIndicator, dockTop, dockRight, dockBottom, dockLeft);
        Stream.of(dockRootPane, dockTop, dockRight, dockBottom, dockLeft).forEach(this::addHandlers);
        Stream.of(dockTop, dockRight, dockBottom, dockLeft).forEach(node -> node.setVisible(false)); // DO IT LATER

        dockIndicatorOverlay = new Popup();
        dockIndicatorOverlay.setAutoFix(false);
        dockIndicatorOverlay.getContent().add(dockRootPane);
    }

    private void buildDockIndicatorPopup() {
        final DockPositionButton dockTop = new DockPositionButton(false, DockPosition.TOP);
        dockTop.getStyleClass().addAll("dock-button", "dock-top");
        final DockPositionButton dockRight = new DockPositionButton(false, DockPosition.RIGHT);
        dockRight.getStyleClass().addAll("dock-button", "dock-right");
        final DockPositionButton dockBottom = new DockPositionButton(false, DockPosition.BOTTOM);
        dockBottom.getStyleClass().addAll("dock-button", "dock-bottom");
        final DockPositionButton dockLeft = new DockPositionButton(false, DockPosition.LEFT);
        dockLeft.getStyleClass().addAll("dock-button", "dock-left");
        dockPosButtons = FXCollections.observableArrayList(dockTop, dockRight, dockBottom, dockLeft);
        dockPosButtons.forEach(this::addHandlers);

        dockPosIndicator = new GridPane();
        dockPosIndicator.setMaxSize(INDICATORS_SIZE, INDICATORS_SIZE);
        dockPosIndicator.getStyleClass().add("dock-pos-indicator");
        dockPosIndicator.add(dockTop, 1, 0);
        dockPosIndicator.add(dockRight, 2, 1);
        dockPosIndicator.add(dockBottom, 1, 2);
        dockPosIndicator.add(dockLeft, 0, 1);
        addHandlers(dockPosIndicator);

        dockIndicatorPopup = new Popup();
        dockIndicatorPopup.setAutoFix(false);
        dockIndicatorPopup.getContent().addAll(dockPosIndicator);
    }

    private boolean isValidDrag(DragEvent e) {
        return e.getDragboard().getContent(DockUtils.DOCK_FORMAT_SOURCE) != null;
    }

    private void removeHandlers(Node n) {
        n.setOnDragOver(null);
        n.setOnDragEntered(null);
        n.setOnDragExited(null);
    }

    private void addHandlers(Node node) {
        node.setOnDragOver(dragOverHandler);
        if (node instanceof DockPositionButton) {
            final DockPositionButton positionButton = (DockPositionButton) node;
            final DockPosition position = positionButton.getDockPosition();
            positionButton.setOnDragEntered(e -> {
                dockAreaIndicator.setVisible(true);

                double width = DockPane.this.getWidth();
                double height = DockPane.this.getHeight();
                if (positionButton.isDockRoot() && getParentPane() != null) {
                    width = getParentPane().getWidth();
                    height = getParentPane().getHeight();
                }
                if (position == DockPosition.TOP || position == DockPosition.BOTTOM) {
                    dockAreaIndicator.setWidth(width);
                    dockAreaIndicator.setHeight(height / 2);
                    dockAreaIndicator.setTranslateX(0);
                    dockAreaIndicator.setTranslateY(position == DockPosition.TOP ? 0 : height / 2);

                } else if (position == DockPosition.RIGHT || position == DockPosition.LEFT) {
                    dockAreaIndicator.setWidth(width / 2);
                    dockAreaIndicator.setHeight(height);
                    dockAreaIndicator.setTranslateX(position == DockPosition.LEFT ? 0 : width / 2);
                    dockAreaIndicator.setTranslateY(0);
                }
                e.consume();
            });
            positionButton.setOnDragExited(e -> {
                dockAreaIndicator.setVisible(false);
                e.consume();
            });
            positionButton.setOnDragDropped(e -> {
                if (isValidDrag(e)) {
                    final DockNodeContent dockNodeContent = DockBoard.getDockBoard().getDockContentSupplier().get();
                    dockNode(dockNodeContent, position);
                    dockAreaIndicator.setVisible(false);
                    e.setDropCompleted(true);
                }
                e.consume();
            });
        } else {
            node.setOnDragEntered(dragEnteredHandler);
            node.setOnDragExited(dragExitedHandler);
        }
    }

    private void dockNode(DockNodeContent dockNodeContent, DockPosition dockPosition) {
        DockNode prevDockNode = (DockNode) getItems().get(0); // Something terribly wrong if you get an error here !!
        DockNodeContent prevNodeContent = prevDockNode.getDockNodeContent();
        removeHandlers(prevDockNode);
        getItems().clear();
        // Create multiple DockPanes
        DockPane prevDockPane = new DockPane(prevNodeContent);
        prevDockPane.setParentPane(this);
        DockPane dockPane = new DockPane(dockNodeContent);
        dockPane.setParentPane(this);

        setOrientation((dockPosition == DockPosition.RIGHT || dockPosition == DockPosition.LEFT) ? Orientation.HORIZONTAL : Orientation.VERTICAL);
        if (dockPosition == DockPosition.LEFT || dockPosition == DockPosition.TOP) {
            getItems().addAll(dockPane, prevDockPane);
        } else {
            getItems().addAll(prevDockPane, dockPane);
        }
    }

    public DockPane getParentPane() {
        return parentPane;
    }

    public void setParentPane(DockPane parentPane) {
        this.parentPane = parentPane;
    }

    private final EventHandler<DragEvent> dragOverHandler = e -> {
        if (isValidDrag(e)) {
            e.acceptTransferModes(TransferMode.ANY);
        }
        e.consume();
    };
    private final EventHandler<DragEvent> dragEnteredHandler = e -> {
        showing.set(true);
        e.consume();
    };

    private final EventHandler<DragEvent> dragExitedHandler = e -> {
        showing.set(false);
        e.consume();
    };


}
