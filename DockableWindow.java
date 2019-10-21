package com.sai.javafx.docking;

import com.sai.javafx.docking.window.XfeAppModule;
import com.sai.javafx.docking.window.XfeWindow;
import javafx.geometry.Point2D;
import javafx.stage.WindowEvent;

public class DockableWindow extends XfeWindow {

    public DockableWindow(DockNodeContent dockNodeContent) {
        DockPane dockPane = new DockPane(dockNodeContent);
        setWindowContent(dockPane);
    }

    public static DockableWindow openWindow(DockNodeContent dockNodeContent, String title, Point2D position, Runnable closeRunnable, Runnable onShownRunnable) {
        DockableWindow window = new DockableWindow(dockNodeContent);
        window.setWindowTitle(title);
        window.getWindowAttributes().setDefaultPosition(position);
        window.getXfeWindow().getIcons().setAll(XfeAppModule.getIcons());
        window.setOnCloseRequest((WindowEvent t) -> {
            window.close();
            if(closeRunnable!=null) {
                closeRunnable.run();
            }
        });
        window.setOnShown((WindowEvent t) -> {
            if(onShownRunnable!=null) {
                onShownRunnable.run();
            }
        });
        window.show();
        return window;
    }
}
