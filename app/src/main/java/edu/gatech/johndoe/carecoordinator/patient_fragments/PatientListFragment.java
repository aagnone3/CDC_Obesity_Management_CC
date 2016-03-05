package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.johndoe.carecoordinator.R;
import edu.gatech.johndoe.carecoordinator.patient.EHR;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.PatientEmail;
import edu.gatech.johndoe.carecoordinator.util.Utility;

public class PatientListFragment extends Fragment {

    private ListView list;
    private List<Patient> patients;
    private PatientAdapter adapter;
    private int threshold = 10;
    private int dummy_cnt;
    private Patient patient;
    private TextView patient_name, patient_id, patient_type, patient_gender, patient_birth_date, patient_age,
            patient_address, patient_email;

    public PatientListFragment() {
        if (patients == null) {
            patients = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                patients.add(new Patient(Utility.get_patient_info_by_id(i)));
                for (int j = 0; j < 1 + (int) (Math.random() * 10); j++) {
                    patients.get(i - 1).addEHR(new EHR());
                }
                dummy_cnt++;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        patient_name = (TextView) view.findViewById(R.id.patient_name);
        patient_id = (TextView) view.findViewById(R.id.patient_id);
        patient_type = (TextView) view.findViewById(R.id.patient_type);
        patient_gender = (TextView) view.findViewById(R.id.patient_gender);
        patient_birth_date = (TextView) view.findViewById(R.id.patient_birth_date);
        patient_age = (TextView) view.findViewById(R.id.patient_age);
        patient_address = (TextView) view.findViewById(R.id.patient_address);
        patient_email = (TextView) view.findViewById(R.id.patient_email);

        final Button button = (Button) view.findViewById(R.id.patient_email_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendPatientEmail();
            }
        });
        list = (ListView)view.findViewById(R.id.listView_patient);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (list.getLastVisiblePosition() >= list.getCount() - threshold) {
                        new DummyGenerator().execute("");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) { }
        });
        adapter = new PatientAdapter(getActivity(), R.id.patient_list_view_row,
                patients, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }


    private class DummyGenerator extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 5; i++) {
                patients.add(new Patient(Utility.get_patient_info_by_id(dummy_cnt + i)));
                for (int j = 0; j < (int) (Math.random() * 10); j++) {
                    patients.get(dummy_cnt + i - 1).addEHR(new EHR());
                }
                dummy_cnt++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }
    }


   private class PatientAdapter extends ArrayAdapter<Patient> {

        private android.support.v4.app.FragmentManager fragment_manager;

        public PatientAdapter(Context context, int resource, List<Patient> patients, android.support.v4.app.FragmentManager fragment_manager) {
            super(context, resource, patients);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {

                v = getActivity().getLayoutInflater().inflate(R.layout.listview_referral, parent, false);
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.patient_listview_row, null);
                v.setClickable(false);
                v.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            final Patient p = getItem(position);

            if (p != null) {
                final TextView patient_name = (TextView) v.findViewById(R.id.patient_name);
                final TextView patient_status = (TextView) v.findViewById(R.id.patient_status);
                System.out.println(p.getName_first());
                patient_name.setText(p.getName_first());

                patient_name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        patient = p;
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
                    }
                });
                patient_status.setText(p.isPending() ? "Pending" : "Closed");
            }
            return v;
        }

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
}
