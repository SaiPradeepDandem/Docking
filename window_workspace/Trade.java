package com.sai.javafx.independentwindow.workspace;

import javafx.beans.property.*;

public class Trade {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty trader = new SimpleStringProperty();
    private BooleanProperty insider = new SimpleBooleanProperty();
    private DoubleProperty amount = new SimpleDoubleProperty();

    public Trade(int a, String b, boolean c, double d) {
        setId(a);
        setTrader(b);
        setInsider(c);
        setAmount(d);
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

    public String getTrader() {
        return trader.get();
    }

    public StringProperty traderProperty() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader.set(trader);
    }

    public boolean isInsider() {
        return insider.get();
    }

    public BooleanProperty insiderProperty() {
        return insider;
    }

    public void setInsider(boolean insider) {
        this.insider.set(insider);
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
}
