package edu.gatech.johndoe.carecoordinator.community;

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
    private String hours;
    private String description;

    private Double latitude;
    private Double longitude;

    private int patientCount;
    private int zipcode;

    /* will we want a "patient list" for each community resource?
    if so, figure out how this will be stored and updated on firebase
    can most likely just use patient IDs */

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

    public String getHours() {
        return hours;
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

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }
}
