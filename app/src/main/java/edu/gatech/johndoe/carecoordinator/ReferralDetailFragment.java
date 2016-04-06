package edu.gatech.johndoe.carecoordinator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import org.slf4j.helpers.Util;
import org.w3c.dom.Text;

import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmail;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmailFactory;
import edu.gatech.johndoe.carecoordinator.util.Utility;

/**
 * Created by rakyu012 on 3/17/2016.
 */
public class ReferralDetailFragment extends Fragment {

    private static final String ARG_REFERRAL = "referral";

    private EHR referral;

    private OnFragmentInteractionListener mListener;

    private TextView listName, listDetails, name, date, details;
    private Button reviewedButton, erefButton;
    private Patient patient;

    /**
     *
     * @param referral Community.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    public static ReferralDetailFragment newInstance(EHR referral) {
        ReferralDetailFragment fragment = new ReferralDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERRAL, new Gson().toJson(referral));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (referral == null) {
                referral = new Gson().fromJson(savedInstanceState.getString("referral"), EHR.class);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            referral = new Gson().fromJson(getArguments().getString(ARG_REFERRAL), EHR.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral_detail, container, false);
        // Attach an listener to read the data at our posts reference
        Utility.getAllPatients();
        for (Patient pat: Utility.patient_list) {
//            System.out.println("Patient id " + pat.getId() + " and " + referral.getPatientID());
//            System.out.println(pat.getId().equals(referral.getPatientID()));
            if (pat.getId().equals(referral.getPatientID())) {
//                System.out.println("Found");
                patient = pat;
            }
        }
//        System.out.println("Referrance ID " + referral.getId());

        TextView paitent_refID = (TextView) view.findViewById(R.id.referral_id);
        TextView patient_name = (TextView) view.findViewById(R.id.patient_name2);
        TextView patient_id = (TextView) view.findViewById(R.id.patient_id2);
        TextView patient_gender = (TextView) view.findViewById(R.id.patient_gender2);
        TextView patient_birth_date = (TextView) view.findViewById(R.id.patient_dob2);
        TextView patient_age = (TextView) view.findViewById(R.id.patient_age2);
        TextView patient_email = (TextView) view.findViewById(R.id.patient_email2);
        TextView patient_phone = (TextView) view.findViewById(R.id.patient_phone2);


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
            }
        });
//        TextView referral_details = (TextView) view.findViewById(R.id.referral_details);
        if (patient == null) {
            paitent_refID.setText(referral.getId());
            patient_name.setText("Dummy");
            patient_id.setText(referral.getPatientID());
            patient_gender.setText("Male");
            patient_age.setText(String.valueOf(10));
            patient_birth_date.setText(referral.getIssueDate().toString());
            patient_email.setText("abc@mail.com");
            patient_phone.setText("xxx-yyy-zzzz");
        } else {
            paitent_refID.setText(referral.getId());
            patient_name.setText(patient.getFull_name_first());
            patient_id.setText(referral.getPatientID());
            patient_gender.setText(patient.getGender());
            patient_age.setText(String.valueOf(patient.getAge()));
            patient_birth_date.setText(referral.getIssueDate().toString());
            patient_email.setText(patient.getEmail());
            patient_phone.setText(patient.getPhoneNumber());
        }

        reviewedButton = (Button) view.findViewById(R.id.buttonreviewed);
        erefButton = (Button) view.findViewById(R.id.buttonereferral);
        reviewedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patient != null) {
                    System.out.println("Referral ID " + referral.getId());
                    Utility.updateReferralStatus(referral.getId(), true);
                } else {
                    System.out.println("null patient");
                }
            }
        });;

        erefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (referral.isPending()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Need to pend the message.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Generated E-referral.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });;

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

        outState.putString("referral", new Gson().toJson(referral));
    }
}
