package be.kuleuven.softdev.august.leuvbike;

import java.util.ArrayList;

public class UserBikeRides {
    private ArrayList<BikeRide> bikerides;

    public UserBikeRides(){}

    public ArrayList<BikeRide> getBikerides() {
        return bikerides;
    }

    public void setBikerides(ArrayList<BikeRide> bikerides) {
        this.bikerides = bikerides;
    }
}
