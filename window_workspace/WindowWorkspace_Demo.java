package com.sai.javafx.independentwindow.workspace;

import com.sai.javafx.docking.window.*;
import com.sun.javafx.css.StyleManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.util.*;

/**
 * Test application for checking the functionality of saving IndependentWindow workspace settings .
 */
public class WindowWorkspace_Demo extends Application implements XfeWindowManager {
    public static Stage primaryStage;

    SecureRandom rnd = new SecureRandom();

    private final Map<WinType, XfeWindow> openedWindows = new HashMap<>();

    @Override
    public void register(String id, ManagedXfeWindow window) {

    }

    @Override
    public void unregister(String id, ManagedXfeWindow window) {

    }

    enum WinType {
        ORDER, TRADE, DEAL;
    }

    enum User {
        KALLAN, MALONE, DOQUINO, ROBINSON;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        WindowWorkspace_Demo.primaryStage = primaryStage;
        final VBox pane = new VBox();
        pane.setSpacing(10);


        final ChoiceBox<User> users = new ChoiceBox<>();
        users.getItems().addAll(Arrays.asList(User.values()));
        final HBox userBox = new HBox(new Label("Select User : "), users);
        userBox.setSpacing(10);

        final Button orders = new Button("Orders");
        orders.setOnAction(e -> openWindow(WinType.ORDER, users.getValue()));
        final Button trades = new Button("Trades");
        trades.setOnAction(e -> openWindow(WinType.TRADE, users.getValue()));
        final Button deals = new Button("Deals");
        deals.setOnAction(e -> openWindow(WinType.DEAL, users.getValue()));
        final VBox buttonBox = new VBox(orders, trades, deals);
        buttonBox.setDisable(true);
        buttonBox.setSpacing(10);

        users.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                buttonBox.setDisable(false);
                toggleWorkspaceToUser(val);
            }
        });

        pane.getChildren().addAll(userBox, buttonBox);
        final StackPane root = new StackPane(pane);
        root.setPadding(new Insets(10));

        final XfeWindow xfeWindow = new XfeWindow();
        xfeWindow.applyTo(primaryStage);
        xfeWindow.setWindowContent(root, "Independent Window Workspace Demo");
        xfeWindow.getXfeWindow().getIcons().setAll(XfeAppModule.getIcons());

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(StyleSheet.BASE.getPath());
        StyleManager.getInstance().addUserAgentStylesheet(StyleSheet.GILTS_BASE.getPath());
        primaryStage.show();
    }

    private void toggleWorkspaceToUser(User user) {
        openedWindows.forEach((key, window) -> window.close());
        openedWindows.clear();
        Map<String, IndependentWindowWorkspace> windowMap = WorkspaceUtil.read(user.toString());
        windowMap.forEach((id, obj) -> {
            System.out.println(id + "  --  " + obj);
        });
    }

    private void openWindow(WinType type, User user) {
        if (openedWindows.get(type) != null) {
            openedWindows.get(type).toFront();
        } else {
            // Build window
            IndependentWindowContent content = getWindowContent(type);
            content.setUserName(user.toString());
            IndependentWindow xfeWindow = new IndependentWindow(type.toString(), null, content, user + " " + type.toString(), this);
            xfeWindow.getXfeWindow().getIcons().setAll(XfeAppModule.getIcons());
            openedWindows.put(type, xfeWindow);

            // Read/Create workspace file
            final String userName = user.toString();
            final String id = type.toString();
            IndependentWindowWorkspace workspace = WorkspaceUtil.read(userName, id);
            if (workspace == null) {
                workspace = new IndependentWindowWorkspace();
                workspace.setId(type.toString());
                workspace.setUserName(user.toString());
            } else {
                // Apply workspace settings
                workspace = WorkspaceUtil.read(userName, id);
            }
            content.setWorkspace(workspace);

            // Show window
            xfeWindow.showWindow();
        }
    }

    private IndependentWindowContent getWindowContent(WinType type) {
        IndependentWindowContent content;
        switch (type) {
            case ORDER:
                TableView<Order> t1 = buildOrderTable();
                t1.setItems(getOrders());
                content = buildWindowContent(t1);
                break;
            case TRADE:
                TableView<Trade> t2 = buildTradeTable();
                t2.setItems(getTrades());
                content = buildWindowContent(t2);
                break;
            default:
                TableView<Deal> t3 = buildDealTable();
                t3.setItems(getDeals());
                content = buildWindowContent(t3);
                break;
        }
        return content;
    }

    private <K> IndependentWindowContent buildWindowContent(TableView<K> table) {
        IndependentWindowContent content = new IndependentWindowContent() {
            @Override
            protected List<IndependentWindowProperty> buildContentProperties() {
                final List<IndependentWindowProperty> properties = new ArrayList<>();
                table.getColumns().forEach(column -> {
                    final IndependentWindowProperty colProperty = new IndependentWindowProperty("column", column.getText());
                    final List<IndependentWindowPropertyAttribute> attributes = new ArrayList<>();
                    attributes.add(new IndependentWindowPropertyAttribute("width", column.getWidth()));
                    attributes.add(new IndependentWindowPropertyAttribute("visible", column.isVisible()));
                    attributes.add(new IndependentWindowPropertyAttribute("watchList", new SomeSerializableObj("Hellow", 230.0, 45, true)));
                    colProperty.setAttributes(attributes);
                    properties.add(colProperty);
                });
                return properties;
            }

            @Override
            protected void applyContentProperties() {
                if (workspace != null && workspace.getProperties() != null) {
                    workspace.getProperties().forEach(prop -> {
                        if (prop.getType().equals("column")) {
                            Optional<TableColumn<K, ?>> column = table.getColumns().stream().filter(col -> col.getText().equals(prop.getName())).findFirst();
                            column.ifPresent(col ->
                                    prop.getAttributes().forEach(attr -> {
                                        switch (attr.getName()) {
                                            case "width":
                                                col.setPrefWidth((double) attr.getValue());
                                                break;
                                            case "visible":
                                                col.setVisible((boolean) attr.getValue());
                                                break;
                                        }
                                    })
                            );
                        }
                    });
                }
            }
        };
        content.getChildren().add(table);
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> content.saveToWorkspace()));
        InvalidationListener listener = e -> {
            if (tl.getStatus() == Animation.Status.RUNNING) {
                tl.playFromStart();
            } else {
                tl.play();
            }
        };
        table.getColumns().forEach(column -> {
            column.widthProperty().addListener(listener);
            column.visibleProperty().addListener(listener);
        });
        return content;
    }

    private ObservableList<Order> getOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        orders.add(new Order(1, "Order 1", 34.00));
        orders.add(new Order(2, "Order 2", 44.00));
        orders.add(new Order(3, "Order 3", 367.00));
        return orders;
    }

    private ObservableList<Trade> getTrades() {
        ObservableList<Trade> trades = FXCollections.observableArrayList();
        trades.add(new Trade(6, "Trade 1", true, 11.00));
        trades.add(new Trade(7, "Trade 2", false, 84.00));
        trades.add(new Trade(8, "Trade 3", true, 37.00));
        trades.add(new Trade(9, "Trade 4", true, 22.00));
        return trades;
    }

    private ObservableList<Deal> getDeals() {
        ObservableList<Deal> deals = FXCollections.observableArrayList();
        deals.add(new Deal(23, "Deal 1", true, 144.00, false));
        deals.add(new Deal(56, "Deal 2", false, 4.00, true));
        deals.add(new Deal(78, "Deal 3", true, 35.00, false));
        return deals;
    }

    private TableView<Order> buildOrderTable() {
        TableView<Order> tableView = new TableView<>();
        TableColumn<Order, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        TableColumn<Order, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        TableColumn<Order, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(param -> param.getValue().priceProperty().asObject());
        tableView.getColumns().addAll(idColumn, nameColumn, priceColumn);
        return tableView;
    }

    private TableView<Trade> buildTradeTable() {
        TableView<Trade> tableView = new TableView<>();
        TableColumn<Trade, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        TableColumn<Trade, String> traderColumn = new TableColumn<>("Trader");
        traderColumn.setCellValueFactory(param -> param.getValue().traderProperty());
        TableColumn<Trade, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty().asObject());
        TableColumn<Trade, Boolean> insiderColumn = new TableColumn<>("Insider");
        insiderColumn.setCellValueFactory(param -> param.getValue().insiderProperty());
        tableView.getColumns().addAll(idColumn, traderColumn, amountColumn, insiderColumn);
        return tableView;
    }

    private TableView<Deal> buildDealTable() {
        TableView<Deal> tableView = new TableView<>();
        TableColumn<Deal, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        TableColumn<Deal, String> dealerColumn = new TableColumn<>("Dealer");
        dealerColumn.setCellValueFactory(param -> param.getValue().dealerProperty());
        TableColumn<Deal, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(param -> param.getValue().amountProperty().asObject());
        TableColumn<Deal, Boolean> bigOrSmallColumn = new TableColumn<>("Is Big?");
        bigOrSmallColumn.setCellValueFactory(param -> param.getValue().bigOrSmallProperty());
        TableColumn<Deal, Boolean> closedColumn = new TableColumn<>("Is Closed?");
        closedColumn.setCellValueFactory(param -> param.getValue().closedProperty());
        tableView.getColumns().addAll(idColumn, dealerColumn, amountColumn, bigOrSmallColumn, closedColumn);
        return tableView;
    }


}
