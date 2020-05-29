package be.kuleuven.softdev.august.leuvbike;

public class User {
    private String username;

    private double amountToPay;
    private String phoneNumber;


    public User(){}

    public User(String username, double amountToPay, String phoneNumber){
        this.amountToPay = amountToPay;

        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public double getAmountToPay() {
        return amountToPay;
    }
    public String getUsername() {
        return username;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setAmountToPay(int amountToPay) {
        this.amountToPay = amountToPay;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
