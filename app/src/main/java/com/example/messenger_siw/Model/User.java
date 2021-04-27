package com.example.messenger_siw.Model;

public class User {
 private String id;
 private String username;
 private String imageURL;
 private String status;


 //Constructors
 public User() {

 }
 public User(String id,String username,String imageURL,String status) {
     this.id = id;
     this.username = username;
     this.imageURL = imageURL;
     this.status = status;
 }
//Getters and Setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
