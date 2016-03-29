package edu.gatech.johndoe.carecoordinator.community;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract community class for all community resource types
 * contains basic information for each community resource type
 */
public class CommunityResource {

    /* private variable declarations */
    private String name;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String openDays;
    private String openHour;
    private String closeHour;
    private String description;

    private Double latitude;
    private Double longitude;

    private int patientCount;
    private int zipcode;

    public List<String> patientList;



    /* class methods */

    public CommunityResource() {}

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getOpenDays() {
        return openDays;
    }

    public String getOpenHour() {
        return openHour;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public int getZipcode() {
        return zipcode;
    }

    public List<String> getPatientList() {
        return patientList;
    }

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }
}
