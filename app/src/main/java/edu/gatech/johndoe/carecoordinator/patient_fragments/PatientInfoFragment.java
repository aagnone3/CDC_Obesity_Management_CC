package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.content.Intent;
import android.net.Uri;
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

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.PatientEmail;


public class PatientInfoFragment extends Fragment {

    Patient p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_info, container, false);
        Bundle extras = getArguments();
        p = extras.getParcelable("PIF");
        TextView patient_name = (TextView) view.findViewById(R.id.patient_name);
        TextView patient_id = (TextView) view.findViewById(R.id.patient_id);
        TextView patient_type = (TextView) view.findViewById(R.id.patient_type);
        TextView patient_gender = (TextView) view.findViewById(R.id.patient_gender);
        TextView patient_birth_date = (TextView) view.findViewById(R.id.patient_birth_date);
        TextView patient_age = (TextView) view.findViewById(R.id.patient_age);
        TextView patient_address = (TextView) view.findViewById(R.id.patient_address);
        TextView patient_email = (TextView) view.findViewById(R.id.patient_email);
        if (patient_name != null)
            patient_name.setText(p.getName_first());
        if (patient_id != null)
            patient_id.setText(p.getId());
        if (patient_type != null)
            patient_type.setText(p.getType().toUpperCase().charAt(0) + p.getType().substring(1));
        if (patient_gender != null)
            patient_gender.setText(p.getGender().toUpperCase().charAt(0) + p.getGender().substring(1));
        if (patient_age != null)
            patient_age.setText(String.valueOf(p.getAge()));
        if (patient_birth_date != null)
            patient_birth_date.setText(p.getBirthDate());
        if (patient_address != null)
            patient_address.setText(" " + p.getAddress().toString());
        if (patient_email != null)
            patient_email.setText(" " + p.getEmail());

        // Option to email the patient
        final Button button = (Button) view.findViewById(R.id.patient_email_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendPatientEmail();
            }
        });

        ListView list = (ListView) view.findViewById(R.id.listView_ehr);
        EHRAdapter adapter = new EHRAdapter(getActivity(), R.id.ehr_list_view_row, p, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }

    private void sendPatientEmail() {
        // Email intent
        PatientEmail email = new PatientEmail(p);

        try {
            startActivity(Intent.createChooser(email.getEmailIntent(), "Send mail..."));
            Log.i("Finished email...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
