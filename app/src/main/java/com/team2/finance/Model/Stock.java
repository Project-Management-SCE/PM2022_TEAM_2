package com.team2.finance.Model;

public class Stock {
    private String symbol;
    private String name;
    private String date;
    private String closeRate;

    public Stock(String symbol, String name, String date, String closeRate) {
        this.symbol = symbol;
        this.name = name;
        this.date = date;
        this.closeRate = closeRate;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(String closeRate) {
        this.closeRate = closeRate;
    }
}
