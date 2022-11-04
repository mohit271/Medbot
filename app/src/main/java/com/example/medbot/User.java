package com.example.medbot;

public class User {

    String userName,email,password,lastMessage,profilePic,userId,status;

    public User(String userName, String email, String password, String lastMessage, String profilePic, String userId,String status) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.userId = userId;

    }

    public User(){

    }
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
