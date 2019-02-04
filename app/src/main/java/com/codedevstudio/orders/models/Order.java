package com.codedevstudio.orders.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//Модель для базы данных Firebase для упрощения взаимодействия с БД
@IgnoreExtraProperties

public class Order implements Serializable, Comparable<Order>{
    private String title, subtitle, address, phone;

    @SerializedName("executor_id")
    private String executorId;

    @SerializedName("creator_id")
    private String creatorId;
    private int cost;
    private String id;
    private long time;
    private double lat, lng;
    private User creator;
    private Status status;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public enum Status{
        WAITFORTHEACCEPT, INDONE, OPEN, SUCCESFULLYDONE, EXPIRED, PENDING
    }
    public Order(){}

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public String getPhone() {
        return phone;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getCost() {
        return cost;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getCreatorId() {
        return creator.getId();
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", executorId='" + executorId + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", cost=" + cost +
                ", id='" + id + '\'' +
                ", time=" + time +
                ", lat=" + lat +
                ", lng=" + lng +
                ", status=" + status +
                '}';
    }

    public Order(String title, String subtitle, String address, String phone, int cost, long time, double lat, double lng) {
        this.title = title;
        this.subtitle = subtitle;
        this.address = address;
        this.phone = phone;
        this.cost = cost;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }

    //Переопределенный метод для сравнения по статусу
    @Override
    public int compareTo(@NonNull Order s1) {
        return this.getStatus().compareTo(s1.getStatus());
    }
}
