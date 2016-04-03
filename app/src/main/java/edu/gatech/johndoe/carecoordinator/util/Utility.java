package edu.gatech.johndoe.carecoordinator.util;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
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
import edu.gatech.johndoe.carecoordinator.CommunityAdapter;
import edu.gatech.johndoe.carecoordinator.CommunityDetailFragment;
import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.ReferralDetailFragment;
import edu.gatech.johndoe.carecoordinator.ReferralListAdapter;
import edu.gatech.johndoe.carecoordinator.UnselectedFragment;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.community.Nutritionist;
import edu.gatech.johndoe.carecoordinator.community.Physical;
import edu.gatech.johndoe.carecoordinator.community.Restaurant;
import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient_fragments.PatientAdapter;
import edu.gatech.johndoe.carecoordinator.patient_fragments.PatientDetailFragment;

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
            new Firebase("https://cdccoordinator2.firebaseio.com/community_resources");
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
    public static final String UPDATE_MESSAGE = "Data Updated.";

    public static void dummyDataGenerator () {
        Random random = new Random();
        // Generating Fake Communities
//        List<Community> communities = new ArrayList<>();
//        for (int i = 1; i <= 30; i++) {
//            communities.add(new Community(String.valueOf(i), "Community" + i, "450 Madison Court, Deactur, GA 30030", "(678) 148 - 4606", "johndoe@gmail.com", "No Information"));
//        }
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
//                Community community = communities.get((int) (Math.random()* 10));
//                community.addPatient(patient);
//                patient.addCommunity(community);
                savePatient(patient);
            }
        }
        //Saving Fake Communities
//        for (Community c : communities)
//            saveCommunity(c);
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

    public static void updateReferral(final Context context,
                                      final ContentListFragment contentListFragment,
                                      final FragmentTransaction transaction,
                                      final boolean isInExpandedMode,
                                      final boolean refresh,
                                      final boolean toast) {

        Query queryRef = REFERRALS_REF.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EHR> updated = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    updated.add(ds.getValue(EHR.class));
                referral_list = updated;

                if (refresh && MainActivity.currentNavigationItemId == R.id.nav_referrals) {
                    contentListFragment.setAdapter(
                            new ReferralListAdapter(Utility.referral_list),
                            ContentListFragment.ContentType.Referral);

                    if (isInExpandedMode) {
                        //FIXME: replace with updated referral detail if needed
                        for (EHR e : referral_list) {
                            if (ReferralListAdapter.currentReferral != null) {
                                if (e.getId().equals(ReferralListAdapter.currentReferral.getId())) {
                                    transaction.replace(
                                            R.id.detailFragmentContainer,
                                            ReferralDetailFragment.newInstance(e),
                                            "detail").commit();
                                    break;
                                }
                            }
                        }
                        transaction.replace(R.id.detailFragmentContainer, new UnselectedFragment(), "detail").commit();

                    }
                }

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

    public static void updatePatient(final Context context,
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
                            new PatientAdapter(Utility.patient_list,
                            PatientAdapter.currentPosition),
                            ContentListFragment.ContentType.Patient);

                    if (isInExpandedMode) {
                        for (edu.gatech.johndoe.carecoordinator.patient.Patient p : patient_list) {
                            if (PatientAdapter.currentPatient != null) {
                                if (p.getId().equals(PatientAdapter.currentPatient.getId())) {
                                    transaction.replace(
                                            R.id.detailFragmentContainer,
                                            PatientDetailFragment.newInstance(p, Utility.getAllRelatedReferrals(p.getEhrList())),
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

    public static void updateCommunity(final Context context,
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
                            new CommunityAdapter(Utility.community_list,
                                    CommunityAdapter.currentPosition),
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

    public static void update(final Context context,
                              final ContentListFragment contentListFragment,
                              final FragmentTransaction transaction,
                              final boolean isInExpandedMode,
                              final int id) {
        String targets = "012";
        targets.replace(String.valueOf(id), "");
        switch (id) {
            case 0:
                updateReferral(context, contentListFragment,
                        transaction, isInExpandedMode, true, true);
                break;
            case 1:
                updatePatient(context, contentListFragment,
                        transaction, isInExpandedMode, true, true);
                break;
            case 2:
                updateCommunity(context, contentListFragment,
                        transaction, isInExpandedMode, true, true);
                break;
        }
        for (int i = 0; i < targets.length(); i++) {
            switch (Integer.valueOf(targets.charAt(i))) {
                case 0:
                    updateReferral(context, contentListFragment,
                            transaction, isInExpandedMode, false, false);
                    break;
                case 1:
                    updatePatient(context, contentListFragment,
                            transaction, isInExpandedMode, false, false);
                    break;
                case 2:
                    updateCommunity(context, contentListFragment,
                            transaction, isInExpandedMode, false, false);
                    break;
            }
        }
    }

    public static void fhirUpdate() {
        //TODO

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
