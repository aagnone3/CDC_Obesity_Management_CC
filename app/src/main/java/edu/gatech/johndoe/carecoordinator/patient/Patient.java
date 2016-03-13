package edu.gatech.johndoe.carecoordinator.patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.primitive.InstantDt;

public class Patient {
    private String id;
    private String type;
    private String first_name;
    private String last_name;
    private String gender;
    private Date birth_date;
    private AddressDt address;
    private String email;
    private boolean isActive;
    private Date lastUpdated;
    private boolean sortedByImport;
    private List<EHR> ehr;
    private String phoneNumber;
    private Date dateOfimport;

    // Note patient may have multiple names in the server, implementation currently selects the
    // first name in the list returned by getName()
    // TODO verify that this is ok
    public Patient(ca.uhn.fhir.model.dstu2.resource.Patient patient) {
        id = patient.getId().getIdPart();
        type = patient.getResourceName();
        first_name = patient.getNameFirstRep().getGivenAsSingleString();
        last_name = patient.getNameFirstRep().getFamilyAsSingleString();
        gender = patient.getGender();
        birth_date = patient.getBirthDate();
        address = patient.getAddressFirstRep();
        email = getEmailFromTelecom(patient.getTelecom());
        phoneNumber = getPhoneFromTelecom(patient.getTelecom());
        isActive = patient.getActive();
        lastUpdated = ((InstantDt) patient.getResourceMetadata().get(ResourceMetadataKeyEnum.UPDATED)).getValue();
        ehr = new ArrayList<>();
        dateOfimport = new Date();
    }

    private String getEmailFromTelecom(List<ContactPointDt> contactPoints) {
        String email = "Email not found";
        for (ContactPointDt contact : contactPoints) {
            if (contact.getSystemElement().getValueAsEnum() == ContactPointSystemEnum.EMAIL) {
                email = contact.getValue();
            }
        }
        return email;
    }

    private String getPhoneFromTelecom(List<ContactPointDt> contactPoints) {
        String phoneNumber = "Phone number not found";
        for (ContactPointDt contact : contactPoints) {
            if (contact.getSystemElement().getValueAsEnum() == ContactPointSystemEnum.PHONE
                    && contact.getUseElement().getValueAsEnum() == ContactPointUseEnum.MOBILE) {
                phoneNumber = contact.getValue();
            }
        }
//        return phoneNumber;
        return "(234) 567-8912";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type.toUpperCase().charAt(0) + type.substring(1);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName_first() {
        return first_name.concat(" " + last_name);
    }

    public String getName_last() {
        return last_name.concat(", " + first_name);
    }

    public String getGender() {
        return gender.toUpperCase().charAt(0) + gender.substring(1);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return new SimpleDateFormat(" MMM d, yyyy").format(birth_date);
    }

    public int getAge() {
        Calendar dob = Calendar.getInstance();
        dob.setTime(birth_date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        return age;
    }

    public String getAddressFirstLine() {
        return address.getLineFirstRep().toString().toUpperCase();
    }

    public String getAddressSecondLine() {
        return address.getCity().toUpperCase() + ", " +
                address.getState().toUpperCase() + " " + address.getPostalCode();
    }

    public AddressDt getAddress() {
        return address;
    }

    public void setAddress(AddressDt address) {
        this.address = address;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getDateOfimport() { return dateOfimport; }

    public EHR getEHR(int index) {
        return ehr.get(index);
    }

    public EHR removeEHR(int index) {
        return ehr.remove(index);
    }

    public boolean isPending() {
        for (EHR e : ehr) {
            if (e.isPending()) {
                return true;
            }
        }
        return false;
    }
    public void addEHR(EHR ehr) {
        if (this.ehr != null) {
            this.ehr.add(ehr);
            if (sortedByImport) {
                Collections.sort(this.ehr, new ComparatorImport());
            } else {
                Collections.sort(this.ehr, new ComparatorIssue());
            }
        }
    }

    public List<EHR> getEHR_by_import() {
        Collections.sort(ehr, new ComparatorImport());
        sortedByImport = true;
        return ehr;
    }

    public List<EHR> getEHR_by_issue() {
        Collections.sort(ehr, new ComparatorIssue());
        sortedByImport = false;
        return ehr;
    }

    public class ComparatorImport implements Comparator<EHR> {
        @Override
        public int compare(EHR e1, EHR e2) {
            return e1.getDateOfimport().compareTo(e2.getDateOfimport());
        }
    }

    public class ComparatorIssue implements Comparator<EHR> {
        @Override
        public int compare(EHR e1, EHR e2) {
            return e1.getIssueDate().compareTo(e2.getIssueDate());
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nTYPE: " + type + "\nName: " + first_name + " " + last_name +
                "\nGender: " + gender + "\nBirth Date: " + birth_date + "\nAddress: " + address +
                "\nisActive: " + isActive + "\nlastUpdated: " + lastUpdated + "\nehr: " + ehr +
                "\nsortedByImport: " + sortedByImport;
    }

}