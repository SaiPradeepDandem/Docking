import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.concurrent.atomic.AtomicInteger;

public class MenuClone_Demo extends Application {
    ObservableList<MenuItem> globalItems = FXCollections.observableArrayList();
    BooleanProperty refresh = new SimpleBooleanProperty();
    InvalidationListener listener = (p) -> refresh.set(!refresh.get());

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        VBox settings = new VBox();
        settings.setSpacing(5);

        Pair<MenuItem, GridPane> item1 = buildMenuItemSetting("Item 1");
        settings.getChildren().add(item1.getValue());
        globalItems.add(item1.getKey());

        Pair<MenuItem, GridPane> item2 = buildMenuItemSetting("Item 2");
        settings.getChildren().add(item2.getValue());
        globalItems.add(item2.getKey());

        Pair<MenuItem, GridPane> item3 = buildMenuItemSetting("Item 3");
        settings.getChildren().add(item3.getValue());
        globalItems.add(item3.getKey());

        Pair<Menu, GridPane> menu1 = buildMenuSetting("Menu 1", true);
        settings.getChildren().add(menu1.getValue());
        globalItems.add(menu1.getKey());

        AtomicInteger itemCount = new AtomicInteger(4);
        AtomicInteger menuCount = new AtomicInteger(2);

        Button addMenuItem = new Button("Add Menu Item");
        addMenuItem.setOnAction(e -> {
            Pair<MenuItem, GridPane> menuItem = buildMenuItemSetting("Item " + itemCount.getAndIncrement());
            settings.getChildren().add(menuItem.getValue());
            globalItems.add(menuItem.getKey());
        });
        Button addMenu = new Button("Add Menu");
        addMenu.setOnAction(e -> {
            Pair<Menu, GridPane> menu = buildMenuSetting("Menu " + menuCount.getAndIncrement(), false);
            settings.getChildren().add(menu.getValue());
            globalItems.add(menu.getKey());
        });
        HBox buttons = new HBox(addMenuItem, addMenu);
        buttons.setSpacing(10);

        VBox sourcePane = new VBox(settings, buttons);
        sourcePane.setSpacing(10);


        listenGlobalItems(globalItems);
        MenuBar menuBar = new MenuBar();
        createMenu(menuBar,"Main 1");
        createMenu(menuBar,"Main 2");
        createMenu(menuBar,"Main 3");

        root.getChildren().addAll(sourcePane, menuBar);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMenu(MenuBar menuBar, String title){
        Menu menuBarItem = new Menu(title);
        cloneAndCopy(menuBarItem.getItems(), globalItems);
        refresh.addListener(p->{
            menuBarItem.getItems().clear();
            cloneAndCopy(menuBarItem.getItems(), globalItems);
        });
        menuBar.getMenus().addAll(menuBarItem);
    }

    private void listenGlobalItems(ObservableList<MenuItem> sourceItems) {
        sourceItems.addListener((ListChangeListener<? super MenuItem>) p->{
            listener.invalidated(null);
            if(p.next()){
                p.getAddedSubList().forEach(this::addListener);
            }
        });
        sourceItems.forEach(this::addListener);
    }

    private void addListener(MenuItem sourceItem){
        if (sourceItem instanceof Menu) {
            Menu sourceMenu = (Menu) sourceItem;
            sourceMenu.getItems().addListener(listener);
            listenGlobalItems(sourceMenu.getItems());
        }
        sourceItem.disableProperty().addListener(listener);
        sourceItem.visibleProperty().addListener(listener);
        sourceItem.onActionProperty().addListener(listener);
    }

    private void cloneAndCopy(ObservableList<MenuItem> items, ObservableList<MenuItem> globalItems) {
        items.addAll(cloneItems(globalItems));
    }

    private ObservableList<MenuItem> cloneItems(ObservableList<MenuItem> sourceItems) {
        ObservableList<MenuItem> targetItems = FXCollections.observableArrayList();
        sourceItems.forEach(sourceItem -> {
            if (sourceItem instanceof Menu) {
                Menu sourceMenu = (Menu) sourceItem;
                Menu targetMenu = new Menu(sourceMenu.getText());
                targetMenu.getItems().addAll(cloneItems(sourceMenu.getItems()));
                targetMenu.setDisable(sourceMenu.isDisable());
                targetMenu.setVisible(sourceMenu.isVisible());
                targetItems.add(targetMenu);
            } else {
                MenuItem targetMenuItem = new MenuItem(sourceItem.getText());
                targetMenuItem.setDisable(sourceItem.isDisable());
                targetMenuItem.setVisible(sourceItem.isVisible());
                targetMenuItem.setOnAction(sourceItem.getOnAction());
                targetItems.add(targetMenuItem);
            }
        });
        return targetItems;
    }

    private Pair<Menu, GridPane> buildMenuSetting(String item, boolean prefill) {
        GridPane settings = new GridPane();
        settings.setHgap(5);
        CheckBox disable = new CheckBox("Disable");
        CheckBox visible = new CheckBox("Visible");
        visible.setSelected(true);
        settings.addRow(0, new Label(item + " :  "), disable, visible);
        Menu menu = new Menu(item);
        menu.disableProperty().bind(disable.selectedProperty());
        menu.visibleProperty().bind(visible.selectedProperty());

        VBox subSettings = new VBox();
        subSettings.setSpacing(5);

        AtomicInteger count = new AtomicInteger(1);
        if (prefill) {
            Pair<MenuItem, GridPane> item1 = buildMenuItemSetting(item + " - Item " + count.getAndIncrement());
            subSettings.getChildren().add(item1.getValue());
            menu.getItems().add(item1.getKey());

            Pair<MenuItem, GridPane> item2 = buildMenuItemSetting(item + " - Item " + count.getAndIncrement());
            subSettings.getChildren().add(item2.getValue());
            menu.getItems().add(item2.getKey());
        }
        settings.add(subSettings, 1, 1, 2, 1);

        Button addItem = new Button("Add Item");
        addItem.setOnAction(e -> {
            Pair<MenuItem, GridPane> itm = buildMenuItemSetting(item + " - Item " + count.getAndIncrement());
            subSettings.getChildren().add(itm.getValue());
            menu.getItems().add(itm.getKey());
        });
        settings.add(addItem, 1, 2, 2, 1);

        return new Pair<>(menu, settings);
    }

    private Pair<MenuItem, GridPane> buildMenuItemSetting(String item) {
        GridPane settings = new GridPane();
        settings.setHgap(5);
        CheckBox disable = new CheckBox("Disable");
        CheckBox visible = new CheckBox("Visible");
        visible.setSelected(true);
        settings.addRow(0, new Label(item + " :  "), disable, visible);
        MenuItem menuitem = new MenuItem(item);
        menuitem.setOnAction(e -> System.out.println("Clicked " + item));
        menuitem.disableProperty().bind(disable.selectedProperty());
        menuitem.visibleProperty().bind(visible.selectedProperty());
        return new Pair<>(menuitem, settings);
    }
}
