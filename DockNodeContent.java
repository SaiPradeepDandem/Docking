package com.sai.javafx.docking;

import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.util.function.Consumer;

public class DockNodeContent {
    private String title;
    private Node content;

    private Tab sourceTab;
    private int sourceTabIndex;
    private Consumer<Node> closeHandler;

    public DockNodeContent() {
    }

    public DockNodeContent(String title, Node content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }

   public Tab getSourceTab() {
      return sourceTab;
   }

   public void setSourceTab(Tab sourceTab) {
      this.sourceTab = sourceTab;
   }

   public int getSourceTabIndex() {
      return sourceTabIndex;
   }

   public void setSourceTabIndex(int sourceTabIndex) {
      this.sourceTabIndex = sourceTabIndex;
   }

   public Consumer<Node> getCloseHandler() {
      return closeHandler;
   }

   public void setCloseHandler(Consumer<Node> closeHandler) {
      this.closeHandler = closeHandler;
   }
}
