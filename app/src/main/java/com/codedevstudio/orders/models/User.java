package com.codedevstudio.orders.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

//Модель для упрощения взаимодействия с БД
@IgnoreExtraProperties
public class User implements Serializable {

    private Avatar avatar;

    private String id, email, firstName, lastName, password;

    private String avatar64;

    private String phone;

    public String getEmail() {
        return email;
    }

    public String getAuthenticationToken() {
        return AuthenticationToken;
    }

    private String bio;
    @SerializedName("authentication_token")
    private String AuthenticationToken;

    @SerializedName("busy?")
    private boolean Busy;

    public String getFullname(){
        return String.format("%s %s", getFirstName(), getLastName());
    }

    private int money;

    private List<Card> cards;

    public User(){}

    public UserCredentials getCredentials(){
        return new UserCredentials(getId(), getEmail(),getAuthenticationToken());
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User(String email, String firstName, String lastName, String bio, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.avatar = new Avatar();
        this.phone = phone;
    }

    public User(String email, String password, String firstName, String lastName, String bio, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.bio = bio;
        this.avatar = new Avatar();
        this.phone = phone;
    }

    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = new Avatar();
    }

    public int getMoney() {
        return money;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isBusy() {
        return Busy;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getAvatar64() {
        return avatar64;
    }

    public void setAvatar64(String avatar64) {
        this.avatar64 = avatar64;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
