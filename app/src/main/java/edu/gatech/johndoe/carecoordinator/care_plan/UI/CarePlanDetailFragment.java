package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.gatech.johndoe.carecoordinator.ContentListFragment;
import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.patient.*;
import edu.gatech.johndoe.carecoordinator.patient.UI.InnerCarePlanAdapter;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientDetailFragment;
import edu.gatech.johndoe.carecoordinator.patient.email.FinalReferralEmail;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmail;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmailFactory;
import edu.gatech.johndoe.carecoordinator.util.OnLatLongUpdateListener;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class CarePlanDetailFragment extends Fragment {
    //

    private static final String ARG_REFERRAL = "carePlan";
    private CarePlan carePlan;
    private OnFragmentInteractionListener mListener;
    private String patientConditionText;
    private Patient patient;
    private Map<String, ArrayList<String>> suggestedCommunitiesMap = new HashMap<>();
    private ArrayList<String> suggestedCommunities = new ArrayList<>();
    private RadioButton firstR, secondR, thirdR, forthR, fifthR;
    private ArrayList<String> communityList = new ArrayList<String>();

    /**
     *
     * @param carePlan carePlan.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    public static CarePlanDetailFragment newInstance(CarePlan carePlan) {
        CarePlanDetailFragment fragment = new CarePlanDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERRAL, new Gson().toJson(carePlan));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (carePlan == null) {
                carePlan = new Gson().fromJson(savedInstanceState.getString("carePlan"), CarePlan.class);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            carePlan = new Gson().fromJson(getArguments().getString(ARG_REFERRAL), CarePlan.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_plan_detail, container, false);
        // Attach an listener to read the data at our posts reference
        Utility.getAllPatients();
        for (Patient pat: Utility.patient_list) {
            if (pat.getId().equals(carePlan.getPatientID())) {
                patient = pat;
            }
        }

        View.OnClickListener patientClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                Fragment detailFragment = PatientDetailFragment.newInstance(patient, Utility.getAllRelatedReferrals(patient.getReferralList()));
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (MainActivity.isInExpandedMode) {
                    //noinspection ResourceType
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.replace(R.id.detailFragmentContainer, detailFragment, "detail").addToBackStack(null);
                } else {
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(R.id.contentContainer, detailFragment, "detail").addToBackStack(null);
                }
                transaction.commit();
            }

        };

        Integer resourceCount = 0;
        Integer loopCount = 0;
        Set<Double> keys = patient.getDistanceSortedCommunities().keySet();
        String carePlanType = "NONE";
        String workingCarePlan = "NONE";

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;

        try {
            address = coder.getFromLocationName(patient.getFullAddress(),1);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            System.out.println(location.getLatitude() + "  " + location.getLongitude());
            patient.setLatitude(location.getLatitude());
            patient.setLongitude(location.getLongitude());
            Utility.sortCommunitiesByDistance(patient);
        } catch (Exception e) {
            System.out.println("wrong");
        }

//
        if (patient.getReferralList() != null) {
            for (String carePlanID : patient.getReferralList()) {
                if (!carePlanType.equals("NONE"))
                    break;
                for (CarePlan carePlan : Utility.carePlan_list) {
                    if (carePlan.getId().equals(carePlanID)) {
                        if (carePlan.getStatus().equals("OPENED")) {
                            carePlanType = carePlan.getType();
                            workingCarePlan = carePlan.getId();
                        }
                    }
                }
            }
        }

        if (!carePlanType.equals("NONE")) {
            for (Object key : keys) {
                Log.e("key", key.toString());
                if (loopCount >= patient.getDistanceSortedCommunities().size() || resourceCount >= 5)
                    break;

                Object o = patient.getDistanceSortedCommunities().get(key);
                for (Community community : Utility.community_list) {
                    if (community.getId().equals(o.toString())) {
                        if (carePlanType.equals("Diet Plan") && community.getCommunityType().equals("nutritionist")) {

                            suggestedCommunities.add(community.getId());
                            resourceCount++;
                            loopCount++;
                        } else if (carePlanType.equals("Exercise Plan") && community.getCommunityType().equals("physical")) {
                            suggestedCommunities.add(community.getId());
                            resourceCount++;
                            loopCount++;
                        } else {
                            loopCount++;
                        }
                        break;
                    }
                }
            }
            if (!patient.getSuggestedCommunities().containsKey(workingCarePlan)) {
                patient.addSuggestedCommunities(workingCarePlan, suggestedCommunities);
            }
        }

        suggestedCommunitiesMap = patient.getSuggestedCommunities();
        communityList = new ArrayList<String>();
        if (!suggestedCommunitiesMap.keySet().isEmpty()) {
            System.out.println("keyset " + suggestedCommunitiesMap.keySet());
            String b = "";
            for (String a : suggestedCommunitiesMap.keySet()) {
                b = a;
                System.out.println("yeah " + a);
            }
            System.out.println(suggestedCommunitiesMap.get(b));
            ArrayList<String> c = suggestedCommunitiesMap.get(b);
            for (String a : c) {
                for (Community com : Utility.community_list) {
                    if (a.equals(com.getId())) {
                        communityList.add(com.getName());
                        System.out.println("Suggested name " + com.getName());
                    }
                }
            }
        }

        TextView physician_name_short = (TextView) view.findViewById(R.id.care_plan_physician_name);
        TextView patient_name_short = (TextView) view.findViewById(R.id.care_plan_patient_name);
        TextView patient_name = (TextView) view.findViewById(R.id.patient_name2);
        TextView patient_condition = (TextView) view.findViewById(R.id.care_plan_condition);
        TextView physician_name = (TextView) view.findViewById(R.id.physician_name);
        TextView type = (TextView) view.findViewById(R.id.care_plan_type);
        TextView details = (TextView) view.findViewById(R.id.care_plan_detail);
        TextView care_plan_goal = (TextView) view.findViewById(R.id.care_plan_goal);
        TextView period = (TextView) view.findViewById(R.id.care_plan_period);
        // Set images
        int imageId = getResources().getIdentifier(patient.getImageName(), "drawable", getActivity().getPackageName());
        ImageView image = (ImageView) view.findViewById(R.id.image_patient);
        image.setImageResource(imageId);
        image.setOnClickListener(patientClickListener);
        imageId = getResources().getIdentifier(carePlan.getPhysicianImageName(), "drawable", getActivity().getPackageName());
        image = (ImageView) view.findViewById(R.id.image_physician);
        image.setImageResource(imageId);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.community_suggestion_list);
        //
        type.setText(carePlan.getType());
        details.setText(carePlan.getDetail());


        patient_name.setOnClickListener(patientClickListener);

        if (carePlan.getStatus().equals("UNOPENED")) {
            System.out.println(carePlan.getStatus() + " and " + carePlan.getId() + " is updated");
            for (CarePlan cp : Utility.carePlan_list) {
                if (cp.getId().equals(carePlan.getId())) {
                    Firebase ref = new Firebase("https://cdccoordinator2.firebaseio.com/care_plans");
                    Firebase alanRef = ref.child(carePlan.getId());
                    cp.setStatus("OPENED");
                    Map<String, Object> cp2 = new HashMap<String, Object>();
                    alanRef.updateChildren(cp2);
                    cp2.put("status", "OPENED");
                    alanRef.updateChildren(cp2);
                }
            }

        } else if (carePlan.getStatus().equals("OPENED")){
            System.out.println(carePlan.getStatus()+ " and " + carePlan.getId());
        } else if (carePlan.getStatus().equals("ACTIVE")) {
            System.out.println(carePlan.getStatus()+ " and " + carePlan.getId());
        } else {
            System.out.println(carePlan.getStatus()+ " and " + carePlan.getId());
        }

        if (patient == null) {
            patient_name.setText("Dummy");
            patient_condition.setText("N/A");
            physician_name.setText("N/A");
            care_plan_goal.setText("N/A");
            physician_name_short.setText("Dr. Who");
            physician_name_short.setText("Patient Who");
            period.setText("N/A");
        } else {
            Resources res = getResources();
            // Condition that the care plan addresses
            patient_condition.setText(formPatientConditionText());
            // Physician name
            String physicianName = carePlan.getPhysicianName();
            String physicianNameShort = physicianName.substring(physicianName.indexOf(',') + 2);
            physician_name.setText(physicianName);
            physician_name_short.setText(String.format(res.getString(R.string.care_plan_physician_name), physicianNameShort));
            // Patient name
            String patientName = carePlan.getPatientName();
            patient_name.setText(patientName);
            patient_name_short.setText(patientName.substring(patientName.indexOf(',') + 2));
            // Goal of the care plan
            String care_plan_goal_text = String.format(res.getString(R.string.care_plan_goal_text), carePlan.getGoalType(), carePlan.getGoalValue());
            care_plan_goal.setText(care_plan_goal_text);
            period.setText(carePlan.getPeriod());
        }


        Button reviewedButton = (Button) view.findViewById(R.id.buttonreviewed);
        Button erefButton = (Button) view.findViewById(R.id.buttonereferral);
        reviewedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (patient != null) {
                    System.out.println("CarePlan ID " + carePlan.getId());
                    for (CarePlan cp : Utility.carePlan_list) {
                        if (cp.getId().equals(carePlan.getId())) {
                            Firebase ref = new Firebase("https://cdccoordinator2.firebaseio.com/care_plans");
                            Firebase alanRef = ref.child(carePlan.getId());
                            cp.setStatus("ACTIVE");
                            Map<String, Object> cp2 = new HashMap<String, Object>();
                            alanRef.updateChildren(cp2);
                            cp2.put("status", "ACTIVE");
                            alanRef.updateChildren(cp2);
//                            System.out.println("Updated");
                        }
                    }
                    ContentListFragment contentListFragment = (ContentListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
//                    contentListFragment.updateCarePlanStatus();
                    contentListFragment.getAdapter().notifyDataSetChanged();
                } else {
                    System.out.println("null patient");
                }
            }
        });

        erefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });


        if (communityList.size() < 1) {
            communityList.add("No Suggested Community");
        }
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(new InnerCommunityAdapter(communityList, getActivity().getSupportFragmentManager()));
        list.setHasFixedSize(true);
        list.setVisibility(View.VISIBLE);

        ContentListFragment contentListFragment = (ContentListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
        contentListFragment.getAdapter().notifyDataSetChanged();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String formPatientConditionText() {
        StringBuilder text = new StringBuilder();
        text.append(carePlan.getConditionSystem())
                .append(" - ")
                .append(carePlan.getConditionCode());
        return text.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("carePlan", new Gson().toJson(carePlan));
    }
    private final void showPopUp() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Woo");
        dialog.setContentView(R.layout.pop_up);
        List<String> stringList=new ArrayList<>();  // here is list
        for(int i=0;i<communityList.size();i++) {
            stringList.add(communityList.get(i));
        }
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }


        dialog.show();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int childCount = group.getChildCount();
                int childCount = communityList.size();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Log.e("selected RadioButton->",btn.getText().toString());
                        try {
                            // activate the final referral email
                            System.out.println("hello?");
//                            Toast.makeText(getActivity().getApplicationContext(), "final referral" + btn.getText(), Toast.LENGTH_LONG);
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity().getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

}
