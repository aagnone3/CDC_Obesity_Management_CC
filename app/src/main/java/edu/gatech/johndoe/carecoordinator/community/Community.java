package edu.gatech.johndoe.carecoordinator.community;

import java.util.List;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

/**
 * abstract community class for all community resource types
 * contains basic information for each community resource type
 */
public class Community {

    private String id;
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

    private String communityType;


    public Community() {}

    public Community(String name, int patientCount) {
        this.name = name;
        this.patientCount = patientCount;
    }

    public String getId() {
        return id;
    }

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

    public String getCommunityType() {
        return communityType;
    }

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }

    public void addPatient(Patient p) {
        patientList.add(p.getId());
    }

    public void removePatient(Patient p) {
        patientList.remove(p.getId());
    }

    public enum CommunityType {
        NUTRITIONIST("nutritionist"), PHYSICAL("physical"), RESTAURANT("restaurant");

        public static final CommunityType values[] = values();

        private final String type;

        CommunityType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Community && ((Community) o).getId().equals(id);
    }
}
