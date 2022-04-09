package com.team2.finance;

public class bankObj {
    int id;
    String Bank_Name;
    String City;
    double X_Coordinate;
    double Y_Coordinate;

    public bankObj(double X_Coordinate, double Y_Coordinate, String Bank_Name) {
        super();
        this.X_Coordinate = X_Coordinate;
        this.Y_Coordinate = Y_Coordinate;
        this.Bank_Name = Bank_Name;
    }

    public boolean equals(Object o) {
        bankObj c = (bankObj) o;
        return c.X_Coordinate == X_Coordinate && c.Y_Coordinate == Y_Coordinate;
    }

}

