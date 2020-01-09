package com.sai.javafx.independentwindow.workspace;

import java.io.Serializable;
import java.util.List;

public class IndependentWindowWorkspace  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userName;
    private boolean showing;
    private String state;
    private double x;
    private double y;
    private double width;
    private double height;
    private List<IndependentWindowProperty> properties;
    private List<IndependentWindowPropertyAttribute> attributes;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

     public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

     public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<IndependentWindowProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<IndependentWindowProperty> properties) {
        this.properties = properties;
    }

    public List<IndependentWindowPropertyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<IndependentWindowPropertyAttribute> attributes) {
        this.attributes = attributes;
    }
}
