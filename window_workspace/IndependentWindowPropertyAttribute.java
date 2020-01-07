package com.sai.javafx.independentwindow.workspace;

import javax.xml.bind.annotation.XmlAttribute;

public class IndependentWindowPropertyAttribute {
    private String name;
    private String value;

    public IndependentWindowPropertyAttribute() {

    }

    public IndependentWindowPropertyAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
