package xfe.independentwindow.workspace;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import xfe.XfeAppModule;
import xfe.icap.themes.StyleSheet;
import xfe.ui.window.XfeWindow;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Test application for checking the functionality of saving IndependentWindow workspace settings .
 */
public class WindowWorkspace_Demo extends Application {
   public static Stage primaryStage;

   SecureRandom rnd = new SecureRandom();

   private final Map<WinType, XfeWindow> openedWindows = new HashMap<>();

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
      openedWindows.forEach((key,window)->window.close());
      openedWindows.clear();
   }

   private void openWindow(WinType type, User user) {
      if(openedWindows.get(type)!=null){
         openedWindows.get(type).toFront();
      }else {
         Node table;
         switch (type) {
            case ORDER:
               TableView<Order> t1 = buildOrderTable();
               t1.setItems(getOrders());
               table = t1;
               break;
            case TRADE:
               TableView<Trade> t2 = buildTradeTable();
               t2.setItems(getTrades());
               table = t2;
               break;
            default:
               TableView<Deal> t3 = buildDealTable();
               t3.setItems(getDeals());
               table = t3;
               break;
         }
         XfeWindow xfeWindow = new XfeWindow();
         xfeWindow.setWindowContent(new StackPane(table), user + " " + type.toString());
         xfeWindow.getXfeWindow().getIcons().setAll(XfeAppModule.getIcons());
         xfeWindow.showWindow();
         openedWindows.put(type,xfeWindow);
      }
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
