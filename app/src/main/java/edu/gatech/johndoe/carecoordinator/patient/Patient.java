package edu.gatech.johndoe.carecoordinator.patient;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.PractitionerRoleEnum;
import ca.uhn.fhir.model.primitive.InstantDt;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.community.Community;

public class Patient {
    private String id;
    private List<ResourceReferenceDt> careProviders;
    private String type;
    private String first_name;
    private String last_name;
    private String full_name_first;
    private String full_name_last;
    private String gender;
    private Date birth_date;
    private String formatted_birth_date;
    private int age;
    private String address_first;
    private String address_second;
    private String email;
    private boolean active;
    private Date lastUpdated;
    private String phoneNumber;
    private Date dateOfimport;
    private List<String> referralList;
    private List<String> communityList;


    public Patient() {}

    public Patient(ca.uhn.fhir.model.dstu2.resource.Patient patient) {
        try {
            id = patient.getId().getIdPart();
            careProviders = patient.getCareProvider();
            type = patient.getResourceName();
            first_name = patient.getNameFirstRep().getGivenAsSingleString();
            last_name = patient.getNameFirstRep().getFamilyAsSingleString();
            full_name_first = first_name.concat(", " + last_name);
            full_name_last = last_name.concat(", " + first_name);
            gender = patient.getGender();
            birth_date = patient.getBirthDate();
            formatted_birth_date = new SimpleDateFormat(" MMM d, yyyy", Locale.US).format(birth_date);
            age = getAge();
            address_first = patient.getAddressFirstRep().getLineFirstRep().toString().toUpperCase();
            address_second = patient.getAddressFirstRep().getCity().toUpperCase() + ", " +
                    patient.getAddressFirstRep().getState().toUpperCase() + " " + patient.getAddressFirstRep().getPostalCode();
            email = getEmailFromTelecom(patient.getTelecom());
            phoneNumber = getPhoneFromTelecom(patient.getTelecom());
            active = patient.getActive();
            lastUpdated = ((InstantDt) patient.getResourceMetadata().get(ResourceMetadataKeyEnum.UPDATED)).getValue();
            dateOfimport = new Date();
            referralList = new LinkedList<>();
            communityList = new LinkedList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Practitioner getPCP() {
        // Pull in all care providers. Look for the PCP, and return its handle if found
        List<ResourceReferenceDt> careProviders = this.careProviders;
        Practitioner pcp = null;
        try {
            for (ResourceReferenceDt res : careProviders) {
                Practitioner provider = (Practitioner) res.getResource();
                List<Practitioner.PractitionerRole> roles = provider.getPractitionerRole();
                for (Practitioner.PractitionerRole role : roles) {
                    if (role.getRole().getValueAsEnum().contains(PractitionerRoleEnum.DOCTOR)) {
                        // This provider is a PCP (via many simplifying assumptions)
                        pcp = provider;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Practitioner", "*** PCP Lookup failed");
            pcp = new Practitioner();
        }
        return pcp;
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
        return "(206) 123-4567";
//        return phoneNumber;
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

    public String getFull_name_first() {
        return full_name_first;
    }

    public void setFull_name_first(String full_name_first) {
        this.full_name_first = full_name_first;
    }

    public String getFull_name_last() {
        return full_name_last;
    }

    public void setFull_name_last(String full_name_last) {
        this.full_name_last = full_name_last;
    }

    public String getGender() {
        return gender.toUpperCase().charAt(0) + gender.substring(1);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public String getFormatted_birth_date() {
        return formatted_birth_date;
    }

    public void setFormatted_birth_date(String formatted_birth_date) {
        this.formatted_birth_date = formatted_birth_date;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress_first() {
        return address_first;
    }

    public void setAddress_first(String address_first) {
        this.address_first = address_first;
    }

    public String getAddress_second() {
        return address_second;
    }

    public void setAddress_second(String address_second) {
        this.address_second = address_second;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfimport() {
        return dateOfimport;
    }

    public void setDateOfimport(Date dateOfimport) {
        this.dateOfimport = dateOfimport;
    }

    public List<String> getReferralList() {
        return referralList;
    }

    public void setReferralList(List<String> referralList) {
        this.referralList = referralList;
    }

    public List<String> getCommunityList() {
        return communityList;
    }

    public void setCommunityList(List<String> communityList) {
        this.communityList = communityList;
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

    public void removeEHR(CarePlan e) {
        referralList.remove(e.getId());
    }

    public void addReferral(CarePlan e) {
        referralList.add(e.getId());
    }

    public void removeCommunity(Community c) {
        communityList.remove(c.getId());
    }

    public void addCommunity(Community c) {
        communityList.add(c.getId());
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", full_name_first='" + full_name_first + '\'' +
                ", full_name_last='" + full_name_last + '\'' +
                ", gender='" + gender + '\'' +
                ", birth_date=" + birth_date +
                ", formatted_birth_date='" + formatted_birth_date + '\'' +
                ", age=" + age +
                ", address_first='" + address_first + '\'' +
                ", address_second='" + address_second + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + active +
                ", lastUpdated=" + lastUpdated +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfimport=" + dateOfimport +
                ", referralList=" + referralList +
                ", communityList=" + communityList +
                '}';
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Patient) {
            if (hashCode() == o.hashCode()) {
                return true;
            }
        }
        return false;
    }

}