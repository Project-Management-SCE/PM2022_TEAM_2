package com.team2.finance;

public class AtmObject {
    String id;
    String Bank_Name;
    String Branch_Address;
    String City;
    float X_Coordinate;
    float Y_Coordinate;

    public AtmObject(String id, String bank_Name, String ATM_Address, String city, float x_Coordinate, float y_Coordinate) {
        this.id = id;
        Bank_Name = bank_Name;
        Branch_Address = ATM_Address;
        City = city;
        X_Coordinate = x_Coordinate;
        Y_Coordinate = y_Coordinate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_Name() {
        return Bank_Name;
    }

    public void setBank_Name(String bank_Name) {
        Bank_Name = bank_Name;
    }

    public String getBranch_Address() {
        return Branch_Address;
    }

    public void setBranch_Address(String branch_Address) {
        Branch_Address = branch_Address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public double getX_Coordinate() {
        return X_Coordinate;
    }

    public void setX_Coordinate(float x_Coordinate) {
        X_Coordinate = x_Coordinate;
    }

    public double getY_Coordinate() {
        return Y_Coordinate;
    }

    public void setY_Coordinate(float y_Coordinate) {
        Y_Coordinate = y_Coordinate;
    }
}
