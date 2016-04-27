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
    private String period;
    private String goalType;
    private String goalValue;
    private String type;
    private String detail;
    private String patientImageName;
    private String physicianImageName;
    private String status;
    private boolean pending;
    private Date issueDate;
    private Date dateOfimport;
    private int statusCode; /* number coding
                            1: unopened
                            2: opened
                            3: sent recommendation
                            4: sent E-referral to community

                        */

    public CarePlan() {}

    public CarePlan(String id, String fhirId, String patientID, String patientUniqueID,
                    String physicianName, String conditionSystem, String conditionCode,
                    String physicianImageName, String status,
                    String goalType, String goalValue, String period, String patientImageName,
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
        this.period = period;
        this.patientName = patientName;
        this.type = type;
        this.detail = detail;
        this.pending = pending;
        this.issueDate = issueDate;
        this.patientImageName = patientImageName;
        this.physicianImageName = physicianImageName;
        this.status = status;
        this.dateOfimport = new Date();
        this.statusCode = 1;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhysicianImageName() {
        return physicianImageName;
    }

    public void setPhysicianImageName(String physicianImageName) {
        this.physicianImageName = physicianImageName;
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

    public String getPatientImageName() {
        return patientImageName;
    }

    public void setPatientImageName(String patientImageName) {
        this.patientImageName = patientImageName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
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

    public String getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(String goalValue) {
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
                ", period='" + period + '\'' +
                ", goalType='" + goalType + '\'' +
                ", goalValue='" + goalValue + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", pending=" + pending +
                ", issueDate=" + issueDate +
                ", patientImageName=" + patientImageName +
                ", physicianImageName=" + physicianImageName +
                ", status=" + status +
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