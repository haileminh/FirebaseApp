package net.hailm.firebaseapp.model.dbmodels;

/**
 * Created by hai.lm on 17/04/2018.
 */

public class AddressModel {
    private String address;
    private double latitude;
    private double longitude;
    private double distance;

    public AddressModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

