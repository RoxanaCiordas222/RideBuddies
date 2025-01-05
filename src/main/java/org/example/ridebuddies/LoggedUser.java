package org.example.ridebuddies;

public class LoggedUser {
    private static User user;
    public static User getUser(){
        return user;
    }
    public static void setUser(User user){
        LoggedUser.user = user;
    }
}
