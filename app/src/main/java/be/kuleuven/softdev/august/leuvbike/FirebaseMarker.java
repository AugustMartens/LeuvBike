package be.kuleuven.softdev.august.leuvbike;

public class FirebaseMarker {
    private double latitude;
    private double longitude;
    private int id;


    public FirebaseMarker(){}

    public FirebaseMarker(double latitude, double longitude, int id){
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
    public int getId(){
        return id;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setId(int id){
        this.id = id;
    }


}
