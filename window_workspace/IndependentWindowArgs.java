import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class IndependentWindowArgs {
   private String id;
   private Window owner;
   private Node node;
   private String title;
   private XfeWindowManager xfeWindowManager;
   private Rectangle2D bounds;
   private EventHandler<WindowEvent> hidingHandler;

   public IndependentWindowArgs(String id, Node node, String title, XfeWindowManager xfeWindowManager,EventHandler<WindowEvent> hidingHandler, Rectangle2D bounds) {
      this.id = id;
      this.node = node;
      this.title = title;
      this.xfeWindowManager = xfeWindowManager;
      this.hidingHandler = hidingHandler;
      this.bounds = bounds;
   }

   public String getId() {
      return id;
   }

   public void setOwner(Window owner) {
      this.owner = owner;
   }

   public Window getOwner() {
      return owner;
   }

   public Node getNode() {
      return node;
   }

   public String getTitle() {
      return title;
   }

   public XfeWindowManager getXfeWindowManager() {
      return xfeWindowManager;
   }

   public Rectangle2D getBounds() {
      return bounds;
   }

   public EventHandler<WindowEvent> getHidingHandler() {
      return hidingHandler;
   }
}
