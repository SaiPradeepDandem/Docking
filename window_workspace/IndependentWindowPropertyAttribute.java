package com.sai.javafx.independentwindow.workspace;

import java.io.Serializable;

public class IndependentWindowPropertyAttribute implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Object value;

    public IndependentWindowPropertyAttribute() {

    }

    public IndependentWindowPropertyAttribute(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
