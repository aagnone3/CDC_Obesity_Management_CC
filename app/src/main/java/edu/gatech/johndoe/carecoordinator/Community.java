package edu.gatech.johndoe.carecoordinator;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.patient.Patient;

public class Community {
    public String id;
    public String name;
    public int patientCount;
    public String address;
    public String phoneNumber;
    public String email;
    public String detail;
    public List<String> patientList;

    public Community() {}

    public Community(String name, int patientCount) {
        this.name = name;
        this.patientCount = patientCount;
    }

    public Community(String id, String name, String address, String phoneNumber, String email, String detail) {
        this.id = id;
        this.name = name;
        this.patientCount = 0;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.detail = detail;
        this.patientList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPatientCount() {
        return patientList.size();
    }

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<String> patientList) {
        this.patientList = patientList;
    }

    public void addPatient(Patient p) {
        patientList.add(p.getId());
    }

    public void removePatient(Patient p) {
        patientList.remove(p.getId());
    }

    @Override
    public String toString() {
        return "Community{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", patientCount=" + patientCount +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", detail='" + detail + '\'' +
                ", patientList=" + patientList +
                '}';
    }
}
