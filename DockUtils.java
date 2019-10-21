package com.sai.javafx.docking;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Robot;
import javafx.geometry.Point2D;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;

public class DockUtils {
    public static final DataFormat DOCK_FORMAT_SOURCE = new DataFormat("dock/source");
    public static final DataFormat DOCK_FORMAT_TARGET = new DataFormat("dock/target");
    private static final Robot robot = Application.GetApplication().createRobot();

    public static Point2D getMousePosition() {
        return new Point2D(robot.getMouseX(), robot.getMouseY());
    }

    public static Point2D getMouseOnScreenPosition(MouseEvent event) {
        return new Point2D(event.getScreenX(), event.getScreenY());
    }

    public static Point2D getMouseInScenePosition(MouseEvent event) {
        return new Point2D(event.getSceneX(), event.getSceneY());
    }
}
