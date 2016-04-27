package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import edu.gatech.johndoe.carecoordinator.MainActivity;
import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.patient.*;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientDetailFragment;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class CarePlanDetailFragment extends Fragment {


    private static final String ARG_REFERRAL = "carePlan";

    private CarePlan carePlan;
    private OnFragmentInteractionListener mListener;
    private String patientConditionText;
    private Patient patient;

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
        int imageId = getResources().getIdentifier(carePlan.getPatientImageName(), "drawable", getActivity().getPackageName());
        ImageView image = (ImageView) view.findViewById(R.id.image_patient);
        image.setImageResource(imageId);
        imageId = getResources().getIdentifier(carePlan.getPhysicianImageName(), "drawable", getActivity().getPackageName());
        image = (ImageView) view.findViewById(R.id.image_physician);
        image.setImageResource(imageId);
        //
        type.setText(carePlan.getType());
        details.setText(carePlan.getDetail());

        patient_name.setOnClickListener(new View.OnClickListener() {

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

        });

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
//                    Utility.updateReferralStatus(carePlan.getId(), true);
                    // need something to add to update.

                    Firebase ref = new Firebase("https://cdccoordinator2.firebaseio.com/care_plans");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                CarePlan post = postSnapshot.getValue(CarePlan.class);
                                System.out.println(post.getType());
                            }
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                } else {
                    System.out.println("null patient");
                }
            }
        });

        erefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carePlan.isPending()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Need to pend the message.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Generated E-CarePlan.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

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
}
