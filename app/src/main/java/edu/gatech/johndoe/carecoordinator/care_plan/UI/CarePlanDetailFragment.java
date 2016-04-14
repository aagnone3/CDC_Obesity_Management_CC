package edu.gatech.johndoe.carecoordinator.care_plan.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.patient.*;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmail;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmailFactory;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 3/17/2016.
 */
public class CarePlanDetailFragment extends Fragment {

    private static final String ARG_REFERRAL = "carePlan";

    private CarePlan carePlan;

    private OnFragmentInteractionListener mListener;

    private TextView title, patientID, carePlanID, issueDate, details, patient_name, patient_gender, patient_birth_date, patient_age, patient_email, patient_phone;
    private Button reviewedButton, erefButton;
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

        patient_name = (TextView) view.findViewById(R.id.patient_name2);
        patient_gender = (TextView) view.findViewById(R.id.patient_gender2);
        patient_birth_date = (TextView) view.findViewById(R.id.patient_dob2);
        patient_age = (TextView) view.findViewById(R.id.patient_age2);
        patient_email = (TextView) view.findViewById(R.id.patient_email2);
        patient_phone = (TextView) view.findViewById(R.id.patient_phone2);
        title = (TextView) view.findViewById(R.id.care_plan_title);
        patientID = (TextView) view.findViewById(R.id.patient_ID);
        carePlanID = (TextView) view.findViewById(R.id.care_plan_ID);
//        issueDate, details
        details = (TextView) view.findViewById(R.id.care_plan_detail);
        title.setText(carePlan.getTitle());
        patientID.setText(carePlan.getPatientID());
        carePlanID.setText(carePlan.getId());
        details.setText(carePlan.getDetail());

//        title =

        patient_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_email)
                        .setTitle(patient.getEmail())
                        .setMessage("Do you want to send an email to " + patient.getFull_name_first() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendPatientEmail();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        patient_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_menu_call)
                            .setTitle(patient.getPhoneNumber())
                            .setMessage("Do you want to call this number? ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + patient.getPhoneNumber()));
                                    startActivity(in);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } catch (java.lang.NullPointerException ex) {
                    Toast.makeText(getActivity().getApplicationContext(), "The number is not valid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (patient == null) {
            patient_name.setText("Dummy");
            patient_gender.setText("Male");
            patient_age.setText(String.valueOf(10));
            patient_birth_date.setText("April 10, 1992");
            patient_email.setText("abc@mail.com");
            patient_phone.setText("xxx-yyy-zzzz");
        } else {
            patient_name.setText(patient.getFull_name_first());
            patient_gender.setText(patient.getGender());
            patient_age.setText(String.valueOf(patient.getAge()));
            patient_birth_date.setText(patient.getFormatted_birth_date());
            patient_email.setText(patient.getEmail());
            patient_phone.setText(patient.getPhoneNumber());
        }

        reviewedButton = (Button) view.findViewById(R.id.buttonreviewed);
        erefButton = (Button) view.findViewById(R.id.buttonereferral);
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
                                System.out.println(post.getTitle());
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


    private void sendPatientEmail() {
        // Email intent
        PatientEmail email = PatientEmailFactory.getEmailBody(
                PatientEmailFactory.EMAIL_TYPE.FINAL_REFERRAL,
                patient);

        try {
            startActivity(Intent.createChooser(email.getEmailIntent(), "Send mail..."));
            Log.i("Finished email...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("carePlan", new Gson().toJson(carePlan));
    }
}
