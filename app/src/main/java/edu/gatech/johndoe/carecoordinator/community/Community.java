package edu.gatech.johndoe.carecoordinator.community;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private String description;
    private String emailAddress;

    private Double latitude;
    private Double longitude;

    private int patientCount;
    private int zipcode;

    public List<String> patientList;

    public List<Map<String, String>> hours;

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

    public String getDescription() {
        return description;
    }

    public String getEmailAddress() {
        return emailAddress;
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

    public List<Map<String, String>> getHours() {
        return hours;
    }

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

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

    public String getFullAddress() {
        return String.format("%s, %s, %s %d", streetAddress, city, state, zipcode);
    }

    @SuppressLint("SimpleDateFormat")
    public String getHoursAsString() {
        StringBuilder hoursString = new StringBuilder();

        final String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < hours.size(); i++) {
            Map<String, String> day = hours.get(i);
            hoursString.append(days[i]).append(": ");

            if (day.get("open").isEmpty() && day.get("close").isEmpty()) {
                hoursString.append("Closed");
            } else {
                try {
                    DateFormat hourFormat = new SimpleDateFormat("HHmm");
                    Date openHour = hourFormat.parse(day.get("open"));
                    Date closeHour = hourFormat.parse(day.get("close"));

                    if (openHour.equals(closeHour)) {
                        hoursString.append("24 hours");
                    } else {
                        DateFormat hoursFormat = new SimpleDateFormat("h:mm aa");
                        hoursString.append(hoursFormat.format(openHour)).append(" - ").append(hoursFormat.format(closeHour));
                    }
                } catch (ParseException e) {
                    hoursString.append("N/A");
                }
            }

            if (i < hours.size() - 1) {
                hoursString.append('\n');
            }
        }

        return hoursString.toString();
    }
}
