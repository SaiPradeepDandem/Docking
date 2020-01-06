package com.sai.javafx.independentwindow.workspace;

import javafx.beans.property.*;

public class Deal {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty dealer = new SimpleStringProperty();
    private BooleanProperty bigOrSmall = new SimpleBooleanProperty();
    private DoubleProperty amount = new SimpleDoubleProperty();
    private BooleanProperty closed = new SimpleBooleanProperty();

    public Deal(int a, String b, boolean c, double d, boolean e) {
        setId(a);
        setDealer(b);
        setBigOrSmall(c);
        setAmount(d);
        setClosed(e);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getDealer() {
        return dealer.get();
    }

    public StringProperty dealerProperty() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer.set(dealer);
    }

    public boolean isBigOrSmall() {
        return bigOrSmall.get();
    }

    public BooleanProperty bigOrSmallProperty() {
        return bigOrSmall;
    }

    public void setBigOrSmall(boolean bigOrSmall) {
        this.bigOrSmall.set(bigOrSmall);
    }

    public double getAmount() {
        return amount.get();
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public boolean isClosed() {
        return closed.get();
    }

    public BooleanProperty closedProperty() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed.set(closed);
    }
}
