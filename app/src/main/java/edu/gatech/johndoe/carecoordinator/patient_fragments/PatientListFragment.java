package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.gatech.johndoe.carecoordinator.EHR;
import edu.gatech.johndoe.carecoordinator.Patient;
import edu.gatech.johndoe.carecoordinator.R;

public class PatientListFragment extends Fragment {

    private ArrayList<Patient> patients;

    public PatientListFragment() {
        //Dummy Data
        patients = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            patients.add(new Patient("Patient", String.valueOf(i)));
            for (int j = 0; j < 1 + (int) (Math.random() * 5); j++) {
                patients.get(i).addEHR(new EHR());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        ListView list = (ListView)view.findViewById(R.id.listView_patient);
        PatientAdapter adapter = new PatientAdapter(getActivity(), R.id.patient_list_view_row, patients, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }


}
