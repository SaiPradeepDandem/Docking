/*
 * Copyright 2014 Jens Deters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sai.javafx.docking;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.util.*;

/**
 * A simple Utility to make all {@link Tab}s of a {@link TabPane} detachable.
 */
public class TabPaneDetacher {

    private TabPane tabPane;
    private Tab currentTab;
    private final List<Tab> originalTabs;
    private final Map<Integer, Tab> tabTransferMap;
    private String[] stylesheets;
    private final BooleanProperty alwaysOnTop;

    private TabPaneDetacher() {
        originalTabs = new ArrayList<>();
        stylesheets = new String[]{};
        tabTransferMap = new HashMap<>();
        alwaysOnTop = new SimpleBooleanProperty();
    }

    /**
     * Creates a new instance of the TabPaneDetacher
     *
     * @return The new instance of the TabPaneDetacher.
     */
    public static TabPaneDetacher create() {
        return new TabPaneDetacher();
    }

    public BooleanProperty alwaysOnTopProperty() {
        return alwaysOnTop;
    }

    public Boolean isAlwaysOnTop() {
        return alwaysOnTop.get();
    }

    /**
     * Sets whether detached Tabs should be always on top.
     *
     * @param alwaysOnTop The state to be set.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher alwaysOnTop(boolean alwaysOnTop) {
        alwaysOnTopProperty().set(alwaysOnTop);
        return this;
    }

    /**
     * Sets the stylesheets that should be assigend to the new created {@link Stage}.
     *
     * @param stylesheets The stylesheets to be set.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher stylesheets(String... stylesheets) {
        this.stylesheets = stylesheets;
        return this;
    }

    /**
     * Make all added {@link Tab}s of the given {@link TabPane} detachable.
     *
     * @param tabPane The {@link TabPane} to take over.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher makeTabsDetachable(TabPane tabPane) {
        this.tabPane = tabPane;
        originalTabs.addAll(tabPane.getTabs());
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            tabTransferMap.put(i, tabPane.getTabs().get(i));
        }
        tabPane.getTabs().stream().forEach(t -> {
            t.setClosable(false);
        });
        tabPane.setOnDragDetected(
                (MouseEvent event) -> {
                    if (event.getSource() instanceof TabPane) {
                        tabPane.startFullDrag();
                        Pane rootPane = (Pane) tabPane.getScene().getRoot();
                        rootPane.setOnDragOver((DragEvent event1) -> {
                            event1.acceptTransferModes(TransferMode.ANY);
                            event1.consume();
                        });
                        currentTab = tabPane.getSelectionModel().getSelectedItem();

                        SnapshotParameters snapshotParams = new SnapshotParameters();
                        snapshotParams.setTransform(Transform.scale(0.4, 0.4));
                        WritableImage snapshot = currentTab.getContent().snapshot(snapshotParams, null);

                        int originalTabIndex = originalTabs.indexOf(currentTab);

                        DockBoard.getDockBoard().setDockContentSupplier(() -> {
                            Pane content = (Pane) currentTab.getContent();
                            if (content == null) {
                                throw new IllegalArgumentException("Can not detach Tab '" + currentTab.getText() + "': content is empty (null).");
                            }
                            currentTab.setContent(null);
                            final DockNodeContent dockNodeContent = new DockNodeContent(currentTab.getText(), content);
                            dockNodeContent.setSourceTab(currentTab);
                            dockNodeContent.setSourceTabIndex(originalTabIndex);
                            Tab tab = currentTab;
                            dockNodeContent.setCloseHandler(node -> onTabClose(tab, content, originalTabIndex));
                            currentTab.getTabPane().getTabs().remove(currentTab);

                            return dockNodeContent;
                        });

                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.put(DockUtils.DOCK_FORMAT_SOURCE, "");

                        Dragboard db = tabPane.startDragAndDrop(TransferMode.MOVE);
                        db.setDragView(snapshot, 40, 40);
                        db.setContent(clipboardContent);
                    }
                    event.consume();
                }
        );

        tabPane.setOnDragDone(
                (DragEvent event) -> {
                    if (!event.isAccepted()) {
                        openTabInStage(currentTab, event);
                        tabPane.setCursor(Cursor.DEFAULT);
                    }
                    DockBoard.getDockBoard().setDockContentSupplier(null);
                    event.consume();
                }
        );
        return this;
    }

    /**
     * Opens the content of the given {@link Tab} in a separate Stage. While the content is removed from the {@link Tab} it is
     * added to the root of a new {@link Stage}. The Window title is set to the name of the {@link Tab};
     *
     * @param tab The {@link Tab} to get the content from.
     */
    public void openTabInStage(final Tab tab, DragEvent event) {
        if (tab == null) {
            return;
        }
        int originalTab = originalTabs.indexOf(tab);
        tabTransferMap.remove(originalTab);
        Pane content = (Pane) tab.getContent();
        if (content == null) {
            throw new IllegalArgumentException("Can not detach Tab '" + tab.getText() + "': content is empty (null).");
        }
        tab.setContent(null);
        int originalTabIndex = originalTabs.indexOf(tab);

        DockNodeContent dockNodeContent = new DockNodeContent(tab.getText(), content);
        dockNodeContent.setSourceTab(tab);
        dockNodeContent.setSourceTabIndex(originalTab);
        dockNodeContent.setCloseHandler(node -> onTabClose(tab, content, originalTabIndex));

        DockableWindow.openWindow(dockNodeContent, tab.getText(), DockUtils.getMousePosition(),
                () -> onTabClose(tab, content, originalTabIndex),
                () -> tab.getTabPane().getTabs().remove(tab));
    }


    private void onTabClose(Tab tab, Pane content, int originalTabIndex) {
        tab.setContent(content);
        tabTransferMap.put(originalTabIndex, tab);
        int index = 0;
        SortedSet<Integer> keys = new TreeSet<>(tabTransferMap.keySet());
        for (Integer key : keys) {
            Tab eachTab = tabTransferMap.get(key);
            if (!tabPane.getTabs().contains(eachTab) && eachTab.getContent() != null) {
                tabPane.getTabs().add(index, eachTab);
            }
            index++;
        }
        tabPane.getSelectionModel().select(tab);
    }
}
