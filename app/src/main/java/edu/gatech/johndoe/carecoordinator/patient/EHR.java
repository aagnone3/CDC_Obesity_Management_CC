package edu.gatech.johndoe.carecoordinator.patient;

import android.os.Parcel;

import java.util.Date;

public class EHR {
    private boolean isPending;
    private Date issueDate;
    private Date dateOfimport;

    public EHR() {
        this(new Date(), true);
    }

    public EHR(Date issueDate) {
        this(issueDate, true);
    }

    public EHR(boolean isPending) {
        this(new Date(), isPending);
    }

    public EHR(Date issueDate, boolean isPending) {
        this.issueDate = issueDate;
        this.isPending = isPending;
        this.dateOfimport = new Date();
    }

    private EHR(Parcel in) {
        isPending = in.readByte() != 0;
        issueDate = new Date(in.readLong());
        dateOfimport = new Date(in.readLong());
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isClosed() {
        return !isPending;
    }

    public void setPending() {
        isPending = true;
    }

    public void setClosed() {
        isPending = false;
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

    @Override
    public String toString() {
        return isPending ? "Pending" : "Closed";
    }

}
