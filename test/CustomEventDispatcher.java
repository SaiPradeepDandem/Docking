import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * Custom EventDispatcher to differentiate mouse clicked event from mouse pressed-released. Also to differentiate
 * from single click with double click.
 */
public class CustomEventDispatcher implements EventDispatcher {

    /** Mouse double click event type. */
    public static final EventType<MouseEvent> MOUSE_DBL_CLICKED = new EventType<>(MouseEvent.ANY, "MOUSE_DBL_CLICKED");
    
    /** Default delay to fire a press event in milliseconds. */
    private static final long DEFAULT_PRESS_DELAY = 300;

    /** Default delay to fire a double click event in milliseconds. */
    private static final long DEFAULT_DOUBLE_CLICK_DELAY = 250;

    /** Default event dispatcher of a node. */
    private final EventDispatcher defaultEventDispatcher;

    /** Timeline for dispatching mouse pressed event. */
    private Timeline pressedTimeline;

    /** Timeline for dispatching mouse clicked event. */
    private Timeline clickedTimeline;

    /** Specified whether to process the clicked event or not. */
    private boolean processClickedEvent;

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
            final Event returnEvent;
            if (type == MouseEvent.MOUSE_PRESSED) {
                returnEvent = dispatchMousePressedEvent(mouseEvent, eventTarget);
            } else if (type == MouseEvent.MOUSE_RELEASED) {
                returnEvent = dispatchMouseReleasedEvent(mouseEvent);
            } else {
                returnEvent = dispatchMouseClickedEvent(mouseEvent, eventTarget);
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
     * @param e MouseEvent
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
     * Dispatches the mouse clicked event to differentiate from double click with single click.
     *
     * @param mouseEvent MouseEvent
     * @param eventTarget Target node of the mouse event
     * @return Event
     */
    private Event dispatchMouseClickedEvent(final MouseEvent mouseEvent, final EventTarget eventTarget) {
        if (!processClickedEvent) {
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
            clickedTimeline = new Timeline(new KeyFrame(Duration.millis(DEFAULT_DOUBLE_CLICK_DELAY), e -> {
                Event.fireEvent(eventTarget, clickedEvent);
                clickedTimeline = null;
            }));
            clickedTimeline.play();
            return mouseEvent;
        }
        return null;
    }

    /**
     * Dispatches the mouse pressed event to differentiate from mouse clicked.
     *
     * @param mouseEvent MouseEvent
     * @param eventTarget Target node of the mouse event
     * @return Event
     */
    private Event dispatchMousePressedEvent(final MouseEvent mouseEvent, final EventTarget eventTarget) {
        if (clickedTimeline != null && clickedTimeline.getStatus() == Animation.Status.RUNNING) {
            return mouseEvent;
        }
        processClickedEvent = false;
        if (pressedTimeline == null) {
            final MouseEvent pressedEvent = copy(mouseEvent, mouseEvent.getEventType());
            pressedTimeline = new Timeline(new KeyFrame(Duration.millis(DEFAULT_PRESS_DELAY), e -> {
                Event.fireEvent(eventTarget, pressedEvent);
                pressedTimeline = null;
            }));
            pressedTimeline.play();
            return mouseEvent;
        }
        return null;
    }

    /**
     * Dispatches the mouse released event to differentiate from mouse clicked.
     *
     * @param mouseEvent MouseEvent
     * @return Event
     */
    private Event dispatchMouseReleasedEvent(final MouseEvent mouseEvent) {
        if (clickedTimeline != null && clickedTimeline.getStatus() == Animation.Status.RUNNING) {
            return mouseEvent;
        }
        if (pressedTimeline != null) {
            processClickedEvent = true;
            pressedTimeline.stop();
            pressedTimeline = null;
            return mouseEvent;
        }
        return null;
    }
}
