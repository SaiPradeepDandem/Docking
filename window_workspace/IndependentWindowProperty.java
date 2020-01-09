package com.sai.javafx.independentwindow.workspace;

import java.io.Serializable;
import java.util.List;

public class IndependentWindowProperty  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String name;
    private List<IndependentWindowPropertyAttribute> attributes;

    public IndependentWindowProperty() {

    }

    public IndependentWindowProperty(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndependentWindowPropertyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<IndependentWindowPropertyAttribute> attributes) {
        this.attributes = attributes;
    }
}
