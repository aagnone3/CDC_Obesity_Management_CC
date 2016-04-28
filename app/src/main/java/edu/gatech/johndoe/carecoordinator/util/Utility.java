package edu.gatech.johndoe.carecoordinator.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.UnselectedFragment;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanDetailFragment;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanListAdapter;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.community.Nutritionist;
import edu.gatech.johndoe.carecoordinator.community.Physical;
import edu.gatech.johndoe.carecoordinator.community.Restaurant;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityAdapter;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityDetailFragment;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientAdapter;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientDetailFragment;

public class Utility {
    private static final String SERVER_BASE = "http://52.72.172.54:8080/fhir/baseDstu2";
    private static final String MAPS_API_KEY = "AIzaSyADCKXv1I_2Z0zAQ8CPMs-32YmhKGtkYBY";
    private static final FhirContext ctx = FhirContext.forDstu2();
    private static final IGenericClient client = ctx.newRestfulGenericClient(SERVER_BASE);
    public static final Firebase CARE_PLANS_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/care_plans");
    public static final Firebase PATIENTS_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/patients");
    public static final Firebase COMMUNITES_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/community_resources");
    public static final Firebase INCOMING_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/incoming");
    public static List<CarePlan> carePlan_list = new ArrayList<>();
    public static List<edu.gatech.johndoe.carecoordinator.patient.Patient> patient_list = new ArrayList<>();
    public static List<Community> community_list = new ArrayList<>();
    public static Map<String, List<CarePlan>> careplans = new HashMap<>();
    public static final String UPDATE_MESSAGE = "Data Updated.";
    private static final Semaphore available = new Semaphore(1);
    private static boolean fromSorting;

    public static void populateDatabase() {
        for (int i = 1; i <= 25; i++) {
            ca.uhn.fhir.model.dstu2.resource.Patient p = getFHIRPatientByID(i);
            if (p != null) {
                edu.gatech.johndoe.carecoordinator.patient.Patient patient = new edu.gatech.johndoe.carecoordinator.patient.Patient(p);
                List<String> ids = new ArrayList<>();
                List<CarePlan> targets = careplans.get(patient.getId());
                if (targets != null) {
                    for (CarePlan c : targets)
                        ids.add(c.getId());
                    patient.setReferralList(ids);
                }
                savePatient(patient);
            }
        }
    }

    public static void saveCarePlan(CarePlan carePlan) {
        Firebase ref = CARE_PLANS_REF.child(carePlan.getId());
        ref.setValue(carePlan);
    }

    public static void savePatient(edu.gatech.johndoe.carecoordinator.patient.Patient p) {
        Firebase ref = PATIENTS_REF.child(p.getId());
        ref.child("active").setValue(p.isActive());
        ref.child("address_first").setValue(p.getAddress_first());
        ref.child("address_second").setValue(p.getAddress_second());
        ref.child("age").setValue(p.getAge());
        ref.child("birth_date").setValue(p.getBirth_date());
        ref.child("communityList").setValue(p.getCommunityList());
        ref.child("referralList").setValue(p.getReferralList());
        ref.child("dateOfimport").setValue(p.getDateOfimport());
        ref.child("email").setValue(p.getEmail());
        ref.child("first_name").setValue(p.getFirst_name());
        ref.child("formatted_birth_date").setValue(p.getFormatted_birth_date());
        ref.child("full_name_first").setValue(p.getFull_name_first());
        ref.child("full_name_last").setValue(p.getFull_name_last());
        ref.child("gender").setValue(p.getGender());
        ref.child("id").setValue(p.getId());
        ref.child("lastUpdated").setValue(p.getLastUpdated());
        ref.child("last_name").setValue(p.getLast_name());
        ref.child("phoneNumber").setValue(p.getPhoneNumber());
        ref.child("type").setValue(p.getType());
    }

    public static void saveCommunityResource(Community community) {
        Firebase ref = COMMUNITES_REF.child(community.getId());
        ref.setValue(community);
    }

    public static void addCarePlan(final String id) {
        Query queryRef = CARE_PLANS_REF.orderByChild("id").equalTo(id);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carePlan_list.add(dataSnapshot.child(id).getValue(CarePlan.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("ReferralByID", firebaseError.getMessage());
            }
        });
    }

    public static void getAllCarePlans() {
        Query queryRef = CARE_PLANS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CarePlan carePlan = ds.getValue(CarePlan.class);
                    if (!carePlan_list.contains(carePlan)) {
                        carePlan_list.add(carePlan);
                    }

//                    List<CarePlan> carePlan_pending = new ArrayList<>();
//                    List<CarePlan> carePlan_Npending = new ArrayList<>();
//                    for (CarePlan carePlan2 : carePlan_list) {
//                        if (carePlan2.isPending()) {
//                            carePlan_pending.add(carePlan2);
//                        } else {
//                            carePlan_Npending.add(carePlan2);
//                        }
//                    }
//
//                    carePlan_list.clear();

                    Collections.sort(carePlan_list, new Comparator<CarePlan>() {
                        @Override
                        public int compare(CarePlan lhs, CarePlan rhs) {

                            return rhs.getDateOfimport().compareTo(lhs.getDateOfimport());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCarePlans", firebaseError.getMessage());
            }
        });
    }

    public static void addPatient(final String id) {
        Query queryRef = PATIENTS_REF.orderByChild("id").equalTo(id);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient_list.add(dataSnapshot.child(id).getValue(edu.gatech.johndoe.carecoordinator.patient.Patient.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("PatientByID", firebaseError.getMessage());
            }
        });
    }

    public static void getAllPatients() {
        Query queryRef = PATIENTS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    edu.gatech.johndoe.carecoordinator.patient.Patient p = ds.getValue(edu.gatech.johndoe.carecoordinator.patient.Patient.class);
                    if (!patient_list.contains(p)) {
                        //TODO: update the patient branch in Firebase to have lat/long set to 0 for new patients so can remove the following two lines
                        p.setLatitude(0.0);
                        p.setLongitude(0.0);
                        p.setDistanceSortedCommunities(new TreeMap<Double, String>());
                        patient_list.add(p);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllPatients", firebaseError.getMessage());
            }
        });
    }

    public static void updatePatientLatLong(final edu.gatech.johndoe.carecoordinator.patient.Patient patient, final OnLatLongUpdateListener listener) {
        if (patient.getLatitude() != 0 && patient.getLongitude() != 0) {
            return;
        }

        new LatLongUpdate(new OnLatLongUpdateListener() {
            @Override
            public void onUpdate(double[] coordinates) {
                if (coordinates != null) {
                    patient.setLatitude(coordinates[0]);
                    patient.setLongitude(coordinates[1]);
                    //TODO: update patient in firebase
                }

                if (listener != null) {
                    listener.onUpdate(coordinates);
                }
            }
        }).execute(patient.getFullAddress());
    }

    public static void sortCommunitiesByDistance(final edu.gatech.johndoe.carecoordinator.patient.Patient patient){
        if (!patient.getDistanceSortedCommunities().isEmpty())
            return;

        //sort communities that already have lat/long stored in database
        for (Community community : community_list){
            if (community.getLatitude() != 0 && community.getLongitude() != 0) {
                Double distance = distance(patient.getLatitude(), patient.getLongitude(), community.getLatitude(), community.getLongitude());
                patient.addCommunityDistance(distance, community.getId());
                //Community newCommunity = (Community) patient.getDistanceSortedCommunities().get(distance);
                //Log.e("test", newCommunity.getName());
            }
        }

        //sort remaining communities (those without lat/long in database)
        for (final Community community : community_list){
            //note: running this code in the listener to ensure synchronization
            updateCommunityLatLong(community, new OnLatLongUpdateListener() {
                @Override
                public void onUpdate(double[] coordinates) {
                    try {
                        available.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Double distance = distance(patient.getLatitude(), patient.getLongitude(), community.getLatitude(), community.getLongitude());
                    patient.addCommunityDistance(distance, community.getId());
                    available.release();
                }
            });
        }
    }

    public static Double distance(Double lat1, Double long1, Double lat2, Double long2){
        double theta = long1 - long2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }

    public static Double deg2rad(Double deg){
        return (deg * Math.PI / 180.0);
    }

    public static Double rad2deg(Double rad){
        return (rad * 180 / Math.PI);
    }

    public static void addCommunity(final String id) {
        Query queryRef = COMMUNITES_REF.orderByChild("id").equalTo(id);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                community_list.add(dataSnapshot.child(id).getValue(Community.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("CommunityID", firebaseError.getMessage());
            }
        });
    }

    public static void getAllCommunities() {
        Query queryRef = COMMUNITES_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> ti = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> cr = ds.getValue(ti);
                    switch (cr.get("communityType").toString()) {
                        case "nutritionist":
                            community_list.add(ds.getValue(Nutritionist.class));
                            break;
                        case "physical":
                            community_list.add(ds.getValue(Physical.class));
                            break;
                        case "restaurant":
                            community_list.add(ds.getValue(Restaurant.class));
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCommunities", firebaseError.getMessage());
            }
        });
        Log.e("sizeComm", Integer.toString(community_list.size()));
    }

    public static void updateCommunityLatLong(final Community community, final OnLatLongUpdateListener listener) {
        if (community.getLatitude() != 0 && community.getLongitude() != 0) {
            return;
        }

        new LatLongUpdate(new OnLatLongUpdateListener() {
            @Override
            public void onUpdate(double[] coordinates) {
                if (coordinates != null) {
                    community.setLatitude(coordinates[0]);
                    community.setLongitude(coordinates[1]);
                    saveCommunityResource(community);
                }

                if (listener != null) {
                    listener.onUpdate(coordinates);
                }
            }
        }).execute(community.fullAddress());
    }

    public static void updateReferralStatus(String id, boolean status) {
        Map<String, Object> container = new HashMap<>();
//        container.put(id + "/pending", status);
        CARE_PLANS_REF.updateChildren(container);
        for (CarePlan carePlan : carePlan_list) {
            if (carePlan.getId().equals(id)) {
                carePlan.setPending(status);
                break;
            }
        }
    }

    public static List<CarePlan> getAllRelatedReferrals(List<String> referralIDs) {
        List<CarePlan> result = new ArrayList<>();
        if (referralIDs != null) {
            for (CarePlan carePlan : carePlan_list) {
                for (String id : referralIDs) {
                    if (id.equals(carePlan.getId())) {
                        result.add(carePlan);
                        break;
                    }
                }
            }
            Collections.sort(result, new Comparator<CarePlan>() {
                @Override
                public int compare(CarePlan e1, CarePlan e2) {
                    return Integer.valueOf(e1.getId()) - Integer.valueOf(e2.getId());
                }
            });
        }
        return result;
    }

    public static void updateCarePlans(final Context context,
                                       final ContentListFragment contentListFragment,
                                       final FragmentTransaction transaction,
                                       final boolean isInExpandedMode,
                                       final boolean refresh,
                                       final boolean toast) {

        Query queryRef = CARE_PLANS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CarePlan> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    updated.add(ds.getValue(CarePlan.class));
                carePlan_list = updated;

                if (refresh && MainActivity.currentNavigationItemId == R.id.nav_care_plans) {
                    contentListFragment.setAdapter(
                            new CarePlanListAdapter(Utility.carePlan_list),
                            ContentListFragment.ContentType.Referral);

                    if (isInExpandedMode) {
                        //FIXME: replace with updated referral detail if needed
                        for (CarePlan e : carePlan_list) {
                            if (CarePlanListAdapter.currentCarePlan != null) {
                                if (e.getId().equals(CarePlanListAdapter.currentCarePlan.getId())) {
                                    transaction.replace(
                                            R.id.detailFragmentContainer,
                                            CarePlanDetailFragment.newInstance(e),
                                            "detail").commit();
                                    break;
                                }
                            }
                        }
                        transaction.replace(R.id.detailFragmentContainer, new UnselectedFragment(), "detail").commit();

                    }
                }

                // TODO link care plans to patients

                if (toast)
                    Toast.makeText(context, UPDATE_MESSAGE, Toast.LENGTH_LONG).show();

                Log.i("Update Referrals", "Done with updating referrals.");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Update Referrals", firebaseError.getMessage());
            }
        });
    }

    public static void updatePatients(final Context context,
                                      final ContentListFragment contentListFragment,
                                      final FragmentTransaction transaction,
                                      final boolean isInExpandedMode,
                                      final boolean refresh,
                                      final boolean toast) {

        Query queryRef = PATIENTS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<edu.gatech.johndoe.carecoordinator.patient.Patient> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    updated.add(ds.getValue(edu.gatech.johndoe.carecoordinator.patient.Patient.class));
                patient_list = updated;

                if (refresh && MainActivity.currentNavigationItemId == R.id.nav_patients) {
                    contentListFragment.setAdapter(
                            new PatientAdapter(Utility.patient_list),
                            ContentListFragment.ContentType.Patient);

                    if (isInExpandedMode) {
                        for (edu.gatech.johndoe.carecoordinator.patient.Patient p : patient_list) {
                            if (PatientAdapter.currentPatient != null) {
                                if (p.getId().equals(PatientAdapter.currentPatient.getId())) {
                                    transaction.replace(
                                            R.id.detailFragmentContainer,
                                            PatientDetailFragment.newInstance(p, Utility.getAllRelatedReferrals(p.getReferralList())),
                                            "detail").commit();
                                    break;
                                }
                            }
                        }
                    }
                }

                if (toast)
                    Toast.makeText(context, UPDATE_MESSAGE, Toast.LENGTH_LONG).show();

                Log.i("Update Patients", "Done with updating patients.");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Update Patients", firebaseError.getMessage());
            }
        });
    }

    public static void updateCommunityResources(final Context context,
                                                final ContentListFragment contentListFragment,
                                                final FragmentTransaction transaction,
                                                final boolean isInExpandedMode,
                                                final boolean refresh,
                                                final boolean toast) {

        Query queryRef = COMMUNITES_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Community> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, Object>> ti = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> cr = ds.getValue(ti);
                    switch (cr.get("communityType").toString()) {
                        case "nutritionist":
                            updated.add(ds.getValue(Nutritionist.class));
                            break;
                        case "physical":
                            updated.add(ds.getValue(Physical.class));
                            break;
                        case "restaurant":
                            updated.add(ds.getValue(Restaurant.class));
                            break;
                    }
                }
                community_list = updated;

                if (refresh && MainActivity.currentNavigationItemId == R.id.nav_communities) {
                    contentListFragment.setAdapter(
                            new CommunityAdapter(Utility.community_list),
                            ContentListFragment.ContentType.Community);

                    if (isInExpandedMode) {
                        for (Community c : community_list) {
                            if (CommunityAdapter.currentCommunity != null) {
                                if (c.getId().equals(CommunityAdapter.currentCommunity.getId())) {
                                    transaction.replace(
                                            R.id.detailFragmentContainer,
                                            CommunityDetailFragment.newInstance(c),
                                            "detail").commit();
                                    break;
                                }
                            }
                        }
                    }
                }

                if (toast)
                    Toast.makeText(context, UPDATE_MESSAGE, Toast.LENGTH_LONG).show();

                Log.i("Update Patients", "Done with updating patients.");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Update Patients", firebaseError.getMessage());
            }
        });

        if (toast)
            Toast.makeText(context, UPDATE_MESSAGE, Toast.LENGTH_LONG).show();

        Log.i("Update Communities", "Done with updating communities.");
    }

    public static void getSubCarePlans () {
        for (CarePlan cp : carePlan_list) {
            ca.uhn.fhir.model.dstu2.resource.CarePlan careplan = getFHIRCarePlanByID(Integer.valueOf(cp.getFhirId()));
            try {
                String uniqueID = "";
                JSONObject result = getFHIRCarePlan(Integer.valueOf(cp.getFhirId()));
                JSONArray sub = result.getJSONArray("contained");
                for (int i = 0; i < sub.length(); i++) {
                    JSONObject type = (JSONObject) sub.get(i);
                    if (type.get("resourceType").equals("Patient")) {
                        sub = type.getJSONArray("identifier");
                        uniqueID = String.valueOf(((JSONObject) sub.get(0)).get("value"));

                        edu.gatech.johndoe.carecoordinator.patient.Patient p = new edu.gatech.johndoe.carecoordinator.patient.Patient(getFHIRPatientByID(Integer.valueOf(uniqueID.substring(5))));
                        if (!careplans.containsKey(p.getId()))
                            careplans.put(p.getId(), new ArrayList<CarePlan>());
                        careplans.get(p.getId()).add(cp);
                        break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void update(final Context context,
                              final ContentListFragment contentListFragment,
                              final FragmentTransaction transaction,
                              final boolean isInExpandedMode,
                              final int id) {

        final Query queryRef = INCOMING_REF;

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        Log.i("INCOMING", "ID: " + ds.getKey());
                        ca.uhn.fhir.model.dstu2.resource.Patient p = getFHIRPatient(Integer.valueOf(ds.getKey()));
                        if (p != null) {
                            edu.gatech.johndoe.carecoordinator.patient.Patient patient = new edu.gatech.johndoe.carecoordinator.patient.Patient(p);
                            List<String> ids = new ArrayList<>();
                            List<CarePlan> targets = careplans.get(patient.getId());
                            if (targets != null) {
                                for (CarePlan c : targets)
                                    ids.add(c.getId());
                                patient.setReferralList(ids);
                            }
                            savePatient(patient);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                INCOMING_REF.removeValue();

                String targets = "012";
                targets.replace(String.valueOf(id), "");
                switch (id) {
                    case 0:
                        updateCarePlans(context, contentListFragment,
                                transaction, isInExpandedMode, true, true);
                        break;
                    case 1:
                        updatePatients(context, contentListFragment,
                                transaction, isInExpandedMode, true, true);
                        break;
                    case 2:
                        updateCommunityResources(context, contentListFragment,
                                transaction, isInExpandedMode, true, true);
                        break;
                }
                for (int i = 0; i < targets.length(); i++) {
                    switch (Integer.valueOf(targets.charAt(i))) {
                        case 0:
                            updateCarePlans(context, contentListFragment,
                                    transaction, isInExpandedMode, false, false);
                            break;
                        case 1:
                            updatePatients(context, contentListFragment,
                                    transaction, isInExpandedMode, false, false);
                            break;
                        case 2:
                            updateCommunityResources(context, contentListFragment,
                                    transaction, isInExpandedMode, false, false);
                            break;
                    }
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("INCOMING", firebaseError.getMessage());
            }
        });


    }

    public static Patient getFHIRPatient(int id) {
        Bundle results = client.search()
                .forResource(Patient.class)
                .where(Patient.RES_ID.matchesExactly().value(String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        Patient patient = null;
        if (results.getEntry().size() == 0) {
            Log.e("getFHIRPatient", "No results matching the search criteria!");
        } else {
            patient = (Patient) results.getEntry().get(0).getResource();
        }
        return patient;
    }

    public static JSONObject getFHIRCarePlan(int id) throws Exception{
        Bundle results = client.search()
                .forResource(ca.uhn.fhir.model.dstu2.resource.CarePlan.class)
                .where(ca.uhn.fhir.model.dstu2.resource.CarePlan.RES_ID.matchesExactly().value(String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        String result = "";
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        } else {
            try {
                String line = "";
                URL url = new URL(results.getEntry().get(0).getFullUrl());
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = in.readLine()) != null)
                    result += line;
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new JSONObject(result);
    }

    public static ca.uhn.fhir.model.dstu2.resource.Patient getFHIRPatientByID(int id) {

        // Obtain the results from the patient query to the FHIR server
        Bundle results = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly()
                        .systemAndIdentifier("uniqueId", "99999" + String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        ca.uhn.fhir.model.dstu2.resource.Patient patient = null;
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        } else {
            patient = (Patient) results.getEntry().get(0).getResource();
        }
        return patient;
    }

    public static ca.uhn.fhir.model.dstu2.resource.CarePlan getFHIRCarePlanByID(int id) {
        // Obtain the results from the query to the FHIR server
        Bundle results = client.search()
                .forResource(ca.uhn.fhir.model.dstu2.resource.CarePlan.class)
                .where(ca.uhn.fhir.model.dstu2.resource.CarePlan.RES_ID.matchesExactly().value(String.valueOf(id)))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        ca.uhn.fhir.model.dstu2.resource.CarePlan carePlan = null;
        if (results.getEntry().size() == 0) {
            System.out.println("No results matching the search criteria!");
        } else {
            carePlan = (ca.uhn.fhir.model.dstu2.resource.CarePlan) results.getEntry().get(0).getResource();
        }
        return carePlan;
    }

    public static class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public ImageDownloadTask(ImageView bmImage) {
            this.imageView = bmImage;
        }

        protected Bitmap doInBackground(String... url) {
            Bitmap picture = null;

            try {
                InputStream in = new java.net.URL(url[0]).openStream();
                picture = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Utility", "Error while downloading image: " + e.getMessage());
            }

            return picture;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
