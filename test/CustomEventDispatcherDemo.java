import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomEventDispatcherDemo extends Application {
    public static final EventType<MouseEvent> MOUSE_DBL_CLICKED = new EventType<>(MouseEvent.ANY, "MOUSE_DBL_CLICKED");

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle box1 = new Rectangle(150, 150);
        box1.setEventDispatcher(new CustomEventDispatcher(box1.getEventDispatcher()));
        box1.setStyle("-fx-fill:red;-fx-stroke-width:2px;-fx-stroke:black;");
        addEventHandlers(box1, "Box-1");

        Rectangle box2 = new Rectangle(150, 150);
        box2.setEventDispatcher(new CustomEventDispatcher(box2.getEventDispatcher()));
        box2.setStyle("-fx-fill:yellow;-fx-stroke-width:2px;-fx-stroke:black;");
        addEventHandlers(box2, "Box-2");

        HBox pane = new HBox(box1, box2);
        pane.setSpacing(10);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new StackPane(pane), 450, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void addEventHandlers(Node node, String n) {
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> System.out.println("\n" + n + " mouse pressed filter"));
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> System.out.println("" + n + " mouse pressed handler"));
        node.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> System.out.println("\n" + n + " mouse released filter"));
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> System.out.println("" + n + " mouse released handler"));
        node.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> System.out.println("\n" + n + " mouse clicked filter"));
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println("" + n + " mouse clicked handler"));
        node.addEventFilter(MOUSE_DBL_CLICKED, e -> System.out.println("\n" + n + " mouse double clicked filter"));
        node.addEventHandler(MOUSE_DBL_CLICKED, e -> System.out.println(n + " mouse double clicked handler"));
    }

    class CustomEventDispatcher implements EventDispatcher {
        private static final long PRESS_DELAY = 300;

        private static final long DOUBLE_CLICK_DELAY = 250;

        private final EventDispatcher defaultEventDispatcher;

        private Timeline pressedTimeline;

        private Timeline clickedTimeline;

        private boolean isClickedEvent;

        /**
         * Constructor.
         *
         * @param initial Default event dispatcher of a node
         */
        public CustomEventDispatcher(final EventDispatcher initial) {
            defaultEventDispatcher = initial;
        }

        @Override
        public Event dispatchEvent(final Event event, final EventDispatchChain tail) {
            final EventType<? extends Event> type = event.getEventType();
            if (type == MouseEvent.MOUSE_PRESSED
                    || type == MouseEvent.MOUSE_RELEASED
                    || type == MouseEvent.MOUSE_CLICKED) {
                final MouseEvent mouseEvent = (MouseEvent) event;
                final EventTarget eventTarget = event.getTarget();
                Event returnEvent = null;
                if (type == MouseEvent.MOUSE_PRESSED) {
                    returnEvent = processMouseEntered(mouseEvent, eventTarget);
                } else if (type == MouseEvent.MOUSE_RELEASED) {
                    returnEvent = processMouseReleased(mouseEvent);
                } else if (type == MouseEvent.MOUSE_CLICKED) {
                    returnEvent = processMouseClicked(mouseEvent, eventTarget);
                }

                if (returnEvent != null) {
                    return returnEvent;
                }
            }
            return defaultEventDispatcher.dispatchEvent(event, tail);
        }

        /**
         * Creates a copy of the provided mouse event type with the mouse event.
         *
         * @param e         MouseEvent
         * @param eventType Event type that need to be created
         * @return New mouse event instance
         */
        private MouseEvent copy(final MouseEvent e, final EventType<? extends MouseEvent> eventType) {
            return new MouseEvent(eventType, e.getSceneX(), e.getSceneY(), e.getScreenX(), e.getScreenY(),
                    e.getButton(), e.getClickCount(), e.isShiftDown(), e.isControlDown(), e.isAltDown(),
                    e.isMetaDown(), e.isPrimaryButtonDown(), e.isMiddleButtonDown(),
                    e.isSecondaryButtonDown(), e.isSynthesized(), e.isPopupTrigger(),
                    e.isStillSincePress(), e.getPickResult());
        }

        /**
         * @param mouseEvent
         * @param eventTarget
         * @return
         */
        private Event processMouseClicked(final MouseEvent mouseEvent, final EventTarget eventTarget) {
            if (!isClickedEvent) {
                return mouseEvent;
            }
            if (mouseEvent.getClickCount() > 1) {
                if (clickedTimeline != null) {
                    clickedTimeline.stop();
                    clickedTimeline = null;
                    final MouseEvent dblClickedEvent = copy(mouseEvent, MOUSE_DBL_CLICKED);
                    Event.fireEvent(eventTarget, dblClickedEvent);
                }
                return mouseEvent;
            }
            if (clickedTimeline == null) {
                final MouseEvent clickedEvent = copy(mouseEvent, mouseEvent.getEventType());
                clickedTimeline = new Timeline(new KeyFrame(Duration.millis(DOUBLE_CLICK_DELAY), e -> {
                    Event.fireEvent(eventTarget, clickedEvent);
                    clickedTimeline = null;
                }));
                clickedTimeline.play();
                return mouseEvent;
            }
            return null;
        }

        /**
         * @param mouseEvent
         * @param eventTarget
         * @return
         */
        private Event processMouseEntered(final MouseEvent mouseEvent, final EventTarget eventTarget) {
            if (clickedTimeline != null && clickedTimeline.getStatus() == Animation.Status.RUNNING) {
                return mouseEvent;
            }
            isClickedEvent = false;
            if (pressedTimeline == null) {
                final MouseEvent pressedEvent = copy(mouseEvent, mouseEvent.getEventType());
                pressedTimeline = new Timeline(new KeyFrame(Duration.millis(PRESS_DELAY), e -> {
                    Event.fireEvent(eventTarget, pressedEvent);
                    pressedTimeline = null;
                }));
                pressedTimeline.play();
                return mouseEvent;
            }
            return null;
        }

        /**
         * @param mouseEvent
         * @return
         */
        private Event processMouseReleased(final MouseEvent mouseEvent) {
            if (clickedTimeline != null && clickedTimeline.getStatus() == Animation.Status.RUNNING) {
                return mouseEvent;
            }
            if (pressedTimeline != null) {
                isClickedEvent = true;
                pressedTimeline.stop();
                pressedTimeline = null;
                return mouseEvent;
            }
            return null;
        }
    }
}
