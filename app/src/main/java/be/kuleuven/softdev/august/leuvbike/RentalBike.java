package be.kuleuven.softdev.august.leuvbike;

public class RentalBike {
    //private FirebaseMarker marker;
    private double longitude;
    private double latitude;
    private int bikeId;
    private String owner;  //of kunnen dit ook veranderen in een id ofzo
    private boolean available;
    //nog dingen toe te voegen

    public RentalBike(){}

    public RentalBike(double latitude, double longitude, int bikeId, String owner, boolean available){
        //this.marker = new FirebaseMarker(latitude,longitude,bikeId);
        this.longitude = longitude;
        this.latitude = latitude;
        this.bikeId = bikeId;
        this.owner = owner;
        this.available = available;
    }

    /*public void setMarker(FirebaseMarker newMarker){
        this.marker = newMarker;
    }*/
    public void setLongitude(double newLongitude){
        this.longitude = newLongitude;
    }
    public void setLatitude(double newLatitude){
        this.latitude = newLatitude;
    }
    public void setBikeId(int newId){
        this.bikeId = newId;
    }
    public void setOwner(String newOwner){
        this.owner = newOwner;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getOwner(){
        return owner;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public int getBikeId(){
        return bikeId;
    }
    /*public FirebaseMarker getMarker(){
        return marker;
    }*/
    public boolean getAvailable(){
        return available;
    }




}

