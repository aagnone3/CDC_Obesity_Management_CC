package edu.gatech.johndoe.carecoordinator.care_plan;

import java.util.Date;

public class CarePlan {
    private String id;
    private String fhirId;
    private String patientName;
    private String patientID;
    private String patientUniqueID;
    private String physicianName;
    private String conditionSystem;
    private String conditionCode;
    private String goalType;
    private double goalValue;
    private String type;
    private String detail;
    private boolean pending;
    private Date issueDate;
    private Date dateOfimport;
    private int status; /* number coding
                            1: unopened
                            2: opened
                            3: sent recommendation
                            4: sent E-referral to community

                        */

    public CarePlan() {}

    public CarePlan(String id, String fhirId, String patientID, String patientUniqueID,
                    String physicianName, String conditionSystem, String conditionCode,
                    String goalType, double goalValue,
                    String patientName, String type, String detail, boolean pending, Date issueDate) {
        this.id = id;
        this.fhirId = fhirId;
        this.patientID = patientID;
        this.patientUniqueID = patientUniqueID;
        this.physicianName = physicianName;
        this.conditionSystem = conditionSystem;
        this.conditionCode = conditionCode;
        this.patientName = patientName;
        this.goalValue = goalValue;
        this.patientName = patientName;
        this.type = type;
        this.detail = detail;
        this.pending = pending;
        this.issueDate = issueDate;
        this.dateOfimport = new Date();
        this.status = 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFhirId() {
        return fhirId;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setFhirId(String fhirId) {
        this.fhirId = fhirId;
    }

    public String getPatientUniqueID() {
        return patientUniqueID;
    }

    public void setPatientUniqueID(String patientUniqueID) {
        this.patientUniqueID = patientUniqueID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDateOfimport() {
        return dateOfimport;
    }

    public void setDateOfimport(Date dateOfimport) {
        this.dateOfimport = dateOfimport;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }

    public String getConditionSystem() {
        return conditionSystem;
    }

    public void setConditionSystem(String conditionSystem) {
        this.conditionSystem = conditionSystem;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
    }

    @Override
    public String toString() {
        return "CarePlan{" +
                "id='" + id + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientID='" + patientID + '\'' +
                ", patientUniqueID='" + patientUniqueID + '\'' +
                ", physicianName='" + physicianName + '\'' +
                ", conditionSystem='" + conditionSystem + '\'' +
                ", conditionCode='" + conditionCode + '\'' +
                ", goalType='" + goalType + '\'' +
                ", goalValue='" + goalValue + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", pending=" + pending +
                ", issueDate=" + issueDate +
                ", dateOfimport=" + dateOfimport +
                ", Status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CarePlan) {
            if (hashCode() == o.hashCode()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id);
    }
}