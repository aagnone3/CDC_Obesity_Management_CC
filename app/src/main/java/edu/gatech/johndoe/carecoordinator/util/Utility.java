package edu.gatech.johndoe.carecoordinator.util;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.johndoe.carecoordinator.Community;
import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.ReferralListAdapter;
import edu.gatech.johndoe.carecoordinator.community.Nutritionist;
import edu.gatech.johndoe.carecoordinator.community.Physical;
import edu.gatech.johndoe.carecoordinator.community.Restaurant;
import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient_fragments.PatientAdapter;

/*import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;*/


public class Utility {
    private static final String SERVER_BASE = "http://52.72.172.54:8080/fhir/baseDstu2";
    private static final String MAPS_API_KEY = "AIzaSyADCKXv1I_2Z0zAQ8CPMs-32YmhKGtkYBY";
    private static final FhirContext ctx = FhirContext.forDstu2();
    public static final Firebase REFERRALS_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/referrals");
    public static final Firebase PATIENTS_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/patients");
    public static final Firebase COMMUNITES_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/communities");
    public static final Firebase PHYSICAL_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/community_resources/physical");
    public static final Firebase NUTRITIONIST_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/community_resources/nutritionist");
    public static final Firebase RESTAURANT_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/community_resources/restaurant");
    public static final Firebase INCOMING_REF =
            new Firebase("https://cdccoordinator2.firebaseio.com/incoming");
    public static List<EHR> referral_list = new ArrayList<>();
    public static List<edu.gatech.johndoe.carecoordinator.patient.Patient> patient_list = new ArrayList<>();
    public static List<Community> community_list = new ArrayList<>();
    public static List<Physical> physical_list = new ArrayList<>();
    public static List<Nutritionist> nutritionist_list = new ArrayList<>();
    public static List<Restaurant> restaurant_list = new ArrayList<>();

    public static void dummyDataGenerator () {
        Random random = new Random();
        // Generating Fake Communities
        List<Community> communities = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            communities.add(new Community(String.valueOf(i), "Community" + i, "450 Madison Court, Deactur, GA 30030", "(678) 148 - 4606", "johndoe@gmail.com", "No Information"));
        }
        // Generating Fake Patients and their Referrals
        int j, ehrID = 1;
        for (int i = 1; i < 50; i++) {
            ca.uhn.fhir.model.dstu2.resource.Patient p = get_patient_info_by_id(i);
            if (p != null) {
                edu.gatech.johndoe.carecoordinator.patient.Patient patient = new edu.gatech.johndoe.carecoordinator.patient.Patient(p);
                for (j = ehrID; j <= ehrID + random.nextInt(15); j++) {
                    EHR ehr = new EHR(String.valueOf(j), patient.getId(), "Referral " + j, "None", j % 2 == 0, new Date());
                    patient.addEHR(ehr);
                    saveReferral(ehr);
                }
                ehrID = j;
                Community community = communities.get((int) (Math.random()* 10));
                community.addPatient(patient);
                patient.addCommunity(community);
                savePatient(patient);
            }
        }
        //Saving Fake Communities
        for (Community c : communities)
            saveCommunity(c);
    }

    public static void saveReferral(EHR ehr) {
        Firebase ref = REFERRALS_REF.child(ehr.getId());
        ref.setValue(ehr);
    }

    public static void savePatient(edu.gatech.johndoe.carecoordinator.patient.Patient p) {
        Firebase ref = PATIENTS_REF.child(p.getId());
        ref.setValue(p);
    }
    
    public static void saveCommunity(Community community) {
        Firebase ref = COMMUNITES_REF.child(community.getId());
        ref.setValue(community);
    }

    public static void addReferral(final String id) {
        Query queryRef = REFERRALS_REF.orderByChild("id").equalTo(id);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                referral_list.add(dataSnapshot.child(id).getValue(EHR.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("ReferralByID", firebaseError.getMessage());
            }
        });
    }

    public static void getAllReferrals() {
        Query queryRef = REFERRALS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EHR ehr = ds.getValue(EHR.class);
                    if (!referral_list.contains(ehr)) {
                        referral_list.add(ehr);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllReferrals", firebaseError.getMessage());
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
                    community_list.add(ds.getValue(Community.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCommunities", firebaseError.getMessage());
            }
        });

        Query physRef = PHYSICAL_REF.orderByKey();
        physRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    physical_list.add(ds.getValue(Physical.class));
                    Log.e("physical_list", physical_list.get(0).getName());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCommunities_Physical", firebaseError.getMessage());
            }
        });

        Query nutRef = NUTRITIONIST_REF.orderByKey();
        nutRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    nutritionist_list.add(ds.getValue(Nutritionist.class));
                    Log.e("nutritionist_list", nutritionist_list.get(0).getFirstName());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCommunities_Nut", firebaseError.getMessage());
            }
        });

        Query restRef = RESTAURANT_REF.orderByKey();
        restRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    restaurant_list.add(ds.getValue(Restaurant.class));
                    Log.e("restaurant_list", restaurant_list.get(0).getFoodType());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("AllCommunities_Rest", firebaseError.getMessage());
            }
        });
    }

    public static void updateReferralStatus(String id, boolean status) {
        Map<String, Object> container = new HashMap<>();
        container.put(id + "/pending", status);
        REFERRALS_REF.updateChildren(container);
        for (EHR referral : referral_list) {
            if (referral.getId().equals(id)) {
                referral.setPending(status);
                break;
            }
        }
    }

    public static List<EHR> getAllRelatedReferrals(List<String> referralIDs) {
        List<EHR> result = new ArrayList<>();
        for (EHR referral : referral_list) {
            for (String id : referralIDs) {
                if (id.equals(referral.getId())) {
                    result.add(referral);
                    break;
                }
            }
        }
        Collections.sort(result, new Comparator<EHR>() {
            @Override
            public int compare(EHR e1, EHR e2) {
                return Integer.valueOf(e1.getId()) - Integer.valueOf(e2.getId());
            }
        });
        return result;
    }

    public static void update(final ContentListFragment contentListFragment, final int id) {
        Query queryRef = REFERRALS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EHR> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    updated.add(ds.getValue(EHR.class));
                referral_list = updated;
                if (id == R.id.nav_referrals) {
                    contentListFragment.setAdapter(new ReferralListAdapter(Utility.referral_list), ContentListFragment.ContentType.Referral);
                }
                Log.i("Update Referrals", "Done with updating referrals.");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Update Referrals", firebaseError.getMessage());
            }
        });

        queryRef = PATIENTS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<edu.gatech.johndoe.carecoordinator.patient.Patient> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    updated.add(ds.getValue(edu.gatech.johndoe.carecoordinator.patient.Patient.class));
                patient_list = updated;

                if (id == R.id.nav_patients) {
                    contentListFragment.setAdapter(new PatientAdapter(Utility.patient_list), ContentListFragment.ContentType.Patient);
                } 
                Log.i("Update Patients", "Done with updating patients.");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Update Patients", firebaseError.getMessage());
            }
        });
    }

    public static ca.uhn.fhir.model.dstu2.resource.Patient get_patient_info_by_id(int id) {
        // Create the client for interacting with the FHIR server
        IGenericClient client = ctx.newRestfulGenericClient(SERVER_BASE);

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

    public static ArrayList<Community> getCommunities() {
        ArrayList<Community> communities = new ArrayList<>(Arrays.asList(
                new Community("YMCA11", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54),
                new Community("YMCA", 120), new Community("Farmer's Market", 54)
        ));  // FIXME: replace with real data

        return communities;
    }


}
