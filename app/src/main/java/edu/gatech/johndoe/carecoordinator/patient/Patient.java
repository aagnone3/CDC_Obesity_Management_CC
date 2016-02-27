package edu.gatech.johndoe.carecoordinator.patient;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;

public class Patient implements Parcelable{
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
    private List<EHR> ehr;
    private boolean sortedByImport;

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
        // TODO get patient email from our database
        email = "Need Email from Database";
        isActive = patient.getActive();
        lastUpdated = (Date) patient.getResourceMetadata().get("lastUpdated");
        ehr = new ArrayList<>();
        sortedByImport = true;
    }

    /*
    public Patient(JSONObject json) {
        try {
            JSONObject resource = new JSONObject(json.getJSONArray("entry").get(0).toString()).getJSONObject("resource");
            id = resource.getString("id");
            type = resource.getString("resourceType");
            JSONObject name = new JSONObject(resource.getJSONArray("name").get(0).toString());
            first_name = name.getJSONArray("given").join(" ").replaceAll("\"", "");
            last_name = name.getJSONArray("family").join(" ").replaceAll("\"", "");
            gender = resource.getString("gender");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            birth_date = format.parse(resource.getString("birthDate"));
            address = new Address(new JSONObject(resource.getJSONArray("address").get(0).toString()));
            // TODO get email from database
            email = "anthonyagnone@gmail.com";
            isActive = resource.getBoolean("active");
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);
            lastUpdated = format.parse(json.getJSONObject("meta").getString("lastUpdated"));
            ehr = new ArrayList<>();
            sortedByImport = true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    private Patient(Parcel in) {
        id = in.readString();
        type = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        gender = in.readString();
        birth_date = new Date(in.readLong());
        address = new AddressDt();
        address.setUse(AddressUseEnum.valueOf(in.readString()));
        address.setText(in.readString());
        address.setCity(in.readString());
        address.setState(in.readString());
        address.setPostalCode(in.readString());
//        address = (Address) in.readTypedObject(Address.CREATOR);
        email = in.readString();
        isActive = in.readByte() != 0;
        lastUpdated = new Date(in.readLong());
        in.readTypedList(ehr, EHR.CREATOR);
        sortedByImport = in.readByte() != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
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
        return gender;
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

    public AddressDt getAddress() {
        return address;
    }

    public void setAddress(AddressDt address) {
        this.address = address;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(gender);
        dest.writeLong(birth_date.getTime());
        dest.writeString(address.getUse());
        dest.writeString(address.getText());
        dest.writeString(address.getCity());
        dest.writeString(address.getState());
        dest.writeString(address.getPostalCode());
        dest.writeString(email);
//        dest.writeTypedObject(address, 0);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeLong(lastUpdated.getTime());
        dest.writeTypedList(ehr);
        dest.writeByte((byte) (sortedByImport ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

}