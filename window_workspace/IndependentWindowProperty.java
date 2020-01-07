package com.sai.javafx.independentwindow.workspace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class IndependentWindowProperty {
    private String type;
    private String name;
    private List<IndependentWindowPropertyAttribute> attributes;

    public IndependentWindowProperty() {

    }

    public IndependentWindowProperty(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public List<IndependentWindowPropertyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<IndependentWindowPropertyAttribute> attributes) {
        this.attributes = attributes;
    }
}
