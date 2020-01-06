package xfe.ui.window;

import javafx.event.EventDispatcher;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import xfe.ui.events.XfeEventDispatcher;

public class IndependentWindow extends ManagedXfeWindow {

   private MenuBar menu;

   public IndependentWindow(String id, Window owner, Node node, String title, XfeWindowManager xfeWindowManager) {
      this(id, owner, node, title, xfeWindowManager, null);
   }

   public IndependentWindow(IndependentWindowArgs args) {
      this(args.getId(), args.getOwner(), args.getNode(), args.getTitle(), args.getXfeWindowManager(), args.getBounds());
   }

   public IndependentWindow(String id, Window owner, Node node, String title, XfeWindowManager xfeWindowManager, Rectangle2D bounds) {
      super(id, owner, null, title, xfeWindowManager, bounds);
      getLabelTitle().setStyle("-fx-font-weight:bold;");
      getWindowPane().getStyleClass().add("xfe-independent-window");
      // TODO: Need to update
      if (node != null) {
         final StackPane stageRoot = new StackPane();
         stageRoot.getChildren().add(node);
         setWindowContent(stageRoot, "ICAP");
         setContent(node);
      }

      // Set custom event dispatcher on icon.
      //EventDispatcher defaultDispatcher = getIconContainer().getEventDispatcher();
      //getIconContainer().setEventDispatcher(new XfeEventDispatcher(defaultDispatcher));
      setWindowTitle(title);
      // setLabelSubTitle(title);
   }

   public void setUserMenu(MenuBar menu) {
      this.menu = menu;
      getHeaderPane().getChildren().add(menu);
   }

   public Menu getUserMenu() {
      if (menu != null) {
         return menu.getMenus().get(0);
      }
      return null;
   }
}
