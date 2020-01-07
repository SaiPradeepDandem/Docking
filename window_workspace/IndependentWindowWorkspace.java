package com.sai.javafx.independentwindow.workspace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "IndependentWindow")
public class IndependentWindowWorkspace {
    private String id;
    private String userName;
    private boolean showing;
    private String state;
    private double x;
    private double y;
    private double width;
    private double height;
    private List<IndependentWindowProperty> properties;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlAttribute
    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    @XmlAttribute
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @XmlAttribute
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @XmlAttribute
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @XmlAttribute
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @XmlAttribute
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<IndependentWindowProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<IndependentWindowProperty> properties) {
        this.properties = properties;
    }
}
