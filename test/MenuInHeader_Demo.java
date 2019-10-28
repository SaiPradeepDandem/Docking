import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuInHeader_Demo extends Application {
    XfeWindow myStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Spring Board");
        button.setOnMouseClicked(e->{
            if(myStage==null){
                myStage = new XfeWindow();
                StackPane sp = new StackPane();
                sp.setPrefSize(150,200);
                sp.setMinSize(150,100);
                myStage.getWindowPane().setCenter(sp);
                myStage.getWindowAttributes().setMaximisable(false);
                myStage.getWindowAttributes().setMinimisable(false);
                myStage.getWindowAttributes().setVerticallyResizable(true);
                myStage.showWindow();
            }
            if(e.getClickCount()>1){
                Bounds screenBounds = button.localToScreen(button.getBoundsInLocal());
                myStage.setX(screenBounds.getMinX());
                myStage.setY(screenBounds.getMaxY());
            }
            myStage.toFront();
        });
        StackPane root = new StackPane(button);
        root.setPrefSize(600,300);

        XfeWindow xfeWindow = new XfeWindow();
        xfeWindow.applyTo(primaryStage);
        xfeWindow.setWindowContent(root,"Window Demo");
        xfeWindow.getXfeWindow().getIcons().setAll(XfeAppModule.getIcons());
        Menu menu = new Menu("Test User");
        menu.getItems().addAll(new MenuItem("Item 1"), new MenuItem("Item 2"));
        MenuBar menuBar = new MenuBar(menu);
        menuBar.getStyleClass().add("xfe-user-menu-bar");
        xfeWindow.getHeaderPane().getChildren().add(menuBar);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet("/window/base.css");
        StyleManager.getInstance().addUserAgentStylesheet("/window/gilts_base.css");
        primaryStage.show();
    }

/* User Menu Bar*/
/*.xfe-user-menu-bar,{
    -fx-padding: 0px;
    -fx-background-color: transparent;
    -fx-background-insets: 0px;
}

.xfe-user-menu-bar > .container > .menu-button:hover,
.xfe-user-menu-bar > .container > .menu-button:focused,
.xfe-user-menu-bar > .container > .menu-button:showing {
    -fx-background: #1E415D;
    -fx-background-color: #1E415D;
}

.xfe-user-menu-bar .menu-button > .label > .text{
    -fx-font-weight: bold;
    -fx-fill: #FFFFFF;
}
*/
}
