package be.kuleuven.softdev.august.leuvbike;

public class BikeRide {
    private double startLng;
    private double startLat;
    private double endLng;
    private double endLat;
    private RentalBike bike;
    private double distanceRidden;
    private int rideDuration;
    private int rideId;
    private String userId;
    private double cost;


    public BikeRide(){}

    public BikeRide(double startLat, double startLng, double endLat, double endLng, RentalBike bike, double distanceRidden, int rideDuration, int rideId, String userId, double cost){
        this.startLat = startLat;
        this.startLng = startLng;
        this.bike = bike;
        this.endLat = endLat;
        this.endLng = endLng;
        this.distanceRidden = distanceRidden;
        this.rideDuration = rideDuration;
        this.rideId = rideId;
        this.userId = userId;
        this.cost = cost;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }
    public void setBike(RentalBike bike) {
        this.bike = bike;
    }
    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }
    public void setDistanceRidden(double distanceRidden) {
        this.distanceRidden = distanceRidden;
    }
    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }
    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }
    public void setRideDuration(int rideDuration) {
        this.rideDuration = rideDuration;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
    public void setCost(double cost) { this.cost = cost;}
    public void setUserId(String userId) {this.userId = userId;}

    public double getDistanceRidden() {
        return distanceRidden;
    }
    public double getEndLat() {
        return endLat;
    }
    public double getEndLng() {
        return endLng;
    }
    public double getStartLat() {
        return startLat;
    }
    public double getStartLng() {
        return startLng;
    }
    public int getRideDuration() {
        return rideDuration;
    }
    public RentalBike getBike() {
        return bike;
    }
    public int getRideId() {
        return rideId;
    }
    public String getUserId() {return userId;}
    public double getCost() { return cost;  }


}
