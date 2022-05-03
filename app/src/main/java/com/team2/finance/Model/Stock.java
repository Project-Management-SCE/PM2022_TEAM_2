package com.team2.finance.Model;

public class Stock {
    private String symbol;
    private String name;

    private String closeRate;

    public Stock(String symbol, String name,  String closeRate) {
        this.symbol = symbol;
        this.name = name;
        this.closeRate = closeRate;
    }


    public String getSymbol() {
        return symbol;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCloseRate() {
        return closeRate;
    }


}
