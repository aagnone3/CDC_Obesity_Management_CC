package edu.gatech.johndoe.carecoordinator.patient;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Address implements Parcelable {
    private String use;
    private String address;
    private String city;
    private String state;
    private String zipcode;

    public Address(JSONObject json) throws JSONException {
        this(json.getString("use"), json.getJSONArray("line").join(" ").replaceAll("\"", ""),
                json.getString("city"), json.getString("state"), json.getString("postalCode"));
    }

    public Address(String use, String address, String city, String state, String zipcode) {
        this.use = use;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    private Address(Parcel in) {
        use = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        zipcode = in.readString();
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return address + ", " + city + ", " + state + " " + zipcode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(use);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zipcode);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

}
