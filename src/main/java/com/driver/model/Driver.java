package com.driver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driverId;

    private String mobile;

    private String password;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("driver")
    private List<TripBooking> tripBookingList = new ArrayList<>();

    @OneToOne(mappedBy = "driver",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("driver")
    private Cab cab;

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TripBooking> getTripBookingList() {
        return tripBookingList;
    }

    public void setTripBookingList(List<TripBooking> tripBookingList) {
        this.tripBookingList = tripBookingList;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
    }

    public Driver(int driverId, String mobile, String password, List<TripBooking> tripBookingList, Cab cab) {
        this.driverId = driverId;
        this.mobile = mobile;
        this.password = password;
        this.tripBookingList = tripBookingList;
        this.cab = cab;
    }

    public Driver(int driverId, String mobile, String password) {
        this.driverId = driverId;
        this.mobile = mobile;
        this.password = password;
    }

    public Driver(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
    }

    public Driver() {
    }
}