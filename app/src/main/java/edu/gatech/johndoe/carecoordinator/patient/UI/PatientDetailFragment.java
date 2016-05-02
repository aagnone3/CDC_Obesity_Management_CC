package edu.gatech.johndoe.carecoordinator.patient.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmail;
import edu.gatech.johndoe.carecoordinator.patient.email.PatientEmailFactory;


public class PatientDetailFragment extends Fragment {
    private static final String ARG_PATIENT = "patient";
    private static final String ARG_PATIENT_REFERRALS = "referralList_in_patient";
    private Patient patient;
    private Patient emailPatient;
    private List<CarePlan> carePlanList;
    private OnFragmentInteractionListener mListener;
    Type listType = new TypeToken<ArrayList<CarePlan>>() {}.getType();

    public static PatientDetailFragment newInstance(Patient patient, List<CarePlan> carePlanList) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT, new Gson().toJson(patient));
        args.putString(ARG_PATIENT_REFERRALS, new Gson().toJson(carePlanList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Set<Double> keys = patient.getDistanceSortedCommunities().keySet();
//        Log.e("in", "onCreate");
//        for (Object o : keys) {
//            Log.e("onCreateKey", o.toString());
//        }
        if (getArguments() != null) {
            patient = new Gson().fromJson(getArguments().getString(ARG_PATIENT), Patient.class);
            carePlanList = new Gson().fromJson(getArguments().getString(ARG_PATIENT_REFERRALS), listType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        final View view = inflater.inflate(R.layout.fragment_patient_detail, container, false);
        // Set patient-specific information
        TextView patient_name = (TextView) view.findViewById(R.id.patient_name);
        TextView patient_gender = (TextView) view.findViewById(R.id.patient_gender);
        TextView patient_birth_date = (TextView) view.findViewById(R.id.patient_dob);
        TextView patient_age = (TextView) view.findViewById(R.id.patient_age);
        final TextView patient_address_first = (TextView) view.findViewById(R.id.patient_address_first_line);
        final TextView patient_address_second = (TextView) view.findViewById(R.id.patient_address_second_line);
        TextView patient_email = (TextView) view.findViewById(R.id.patient_email);
        TextView patient_phone = (TextView) view.findViewById(R.id.patient_phone);
        ImageView patient_image = (ImageView) view.findViewById(R.id.patientImage);
        String imageName = patient.getImageName();
        if (imageName != null) {
            int imageId = getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());
            patient_image.setImageResource(imageId);
        } else {
            patient_image.setImageResource(R.drawable.randomchild0);
        }

        patient_name.setText(patient.getFull_name_last());
        patient_gender.setText(patient.getGender());
        patient_age.setText(String.valueOf(patient.getAge()));
        patient_birth_date.setText(patient.getFormatted_birth_date());
        patient_address_first.setText(patient.getAddress_first());
        patient_address_second.setText(patient.getAddress_second());
        patient_email.setText(patient.getEmail());
        patient_phone.setText(patient.getPhoneNumber());
        // Set listeners for actionable patient information
        View.OnClickListener addressClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .setTitle(patient.getAddress_first())
                        .setMessage("Do you want to view address of "
                                + patient.getFull_name_last() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri mapsUri = Uri.parse("geo:33.771344,-84.5675555?q="
                                        + Uri.encode(patient.getAddress_first() + ", "
                                        + patient.getAddress_second()));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        patient_address_first.setOnClickListener(addressClickListener);
        patient_address_second.setOnClickListener(addressClickListener);
        patient_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.getMenuInflater().inflate(R.menu.email_select, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        System.out.println(item.getTitle());
                        sendPatientEmail(item.getItemId());

                        return true;
                    }
                });
                popup.show();
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.getMenuInflater().inflate(R.menu.email_select, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        sendPatientEmail(item.getItemId());
                        return true;
                    }
                });
                popup.show();
            }
        });

        RecyclerView list = (RecyclerView) view.findViewById(R.id.patient_care_plan_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(new InnerCarePlanAdapter(carePlanList, getActivity().getSupportFragmentManager()));
        list.setHasFixedSize(true);
        list.setVisibility(View.VISIBLE);
        return view;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
//        Set<Double> keys = patient.getDistanceSortedCommunities().keySet();
//        Log.e("in", "setPatient");
//        for (Object o : keys){
//            Log.e("setPatientKey", o.toString());
//        }
    }

    public void setEmailPatient(Patient patient) {
        this.emailPatient = patient;
    }

    private void sendPatientEmail(int selectedMenuId) {
        // Email intent

        System.out.println("selectedMenuId and emailPatient " + selectedMenuId + " " + emailPatient);
        PatientEmail email = PatientEmailFactory.getEmailBody(
                selectedMenuId,
                emailPatient);
        try {
            startActivity(Intent.createChooser(email.getEmailIntent(), "Send mail..."));
            Log.i("Finished email...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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

}
