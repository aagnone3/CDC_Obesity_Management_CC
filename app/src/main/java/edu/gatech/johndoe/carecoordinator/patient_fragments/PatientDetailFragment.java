package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import edu.gatech.johndoe.carecoordinator.OnFragmentInteractionListener;
import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.PatientEmail;


public class PatientDetailFragment extends Fragment {
    private static final String ARG_PATIENT = "patient";
    private Patient patient;
    private OnFragmentInteractionListener mListener;

    public static PatientDetailFragment newInstance(Patient patient) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT, new Gson().toJson(patient));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patient = new Gson().fromJson(getArguments().getString(ARG_PATIENT), Patient.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_detail, container, false);
        System.out.println("PatientDetailFragment onCreateView: " + container);
        TextView patient_name = (TextView) view.findViewById(R.id.patient_name);
        TextView patient_id = (TextView) view.findViewById(R.id.patient_id);
        TextView patient_type = (TextView) view.findViewById(R.id.patient_type);
        TextView patient_gender = (TextView) view.findViewById(R.id.patient_gender);
        TextView patient_birth_date = (TextView) view.findViewById(R.id.patient_dob);
        TextView patient_age = (TextView) view.findViewById(R.id.patient_age);
        TextView patient_address = (TextView) view.findViewById(R.id.patient_address);
        TextView patient_email = (TextView) view.findViewById(R.id.patient_email);
        if (patient_name != null)
            patient_name.setText(patient.getName_first());
        if (patient_id != null)
            patient_id.setText(patient.getId());
        if (patient_type != null)
            patient_type.setText(patient.getType().toUpperCase().charAt(0) + patient.getType().substring(1));
        if (patient_gender != null)
            patient_gender.setText(patient.getGender().toUpperCase().charAt(0) + patient.getGender().substring(1));
        if (patient_age != null)
            patient_age.setText(String.valueOf(patient.getAge()));
        if (patient_birth_date != null)
            patient_birth_date.setText(patient.getBirthDate());
        if (patient_address != null)
            patient_address.setText(" " + patient.getAddress().toString());
        if (patient_email != null)
            patient_email.setText(" " + patient.getEmail());

        // Option to email the patient
        final Button button = (Button) view.findViewById(R.id.patient_email_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendPatientEmail();
            }
        });

        ListView list = (ListView) view.findViewById(R.id.listView_ehr);
        EHRAdapter adapter = new EHRAdapter(getActivity(), R.id.ehr_list_view_row, patient, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }

    private void sendPatientEmail() {
        // Email intent
        PatientEmail email = new PatientEmail(patient);

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
