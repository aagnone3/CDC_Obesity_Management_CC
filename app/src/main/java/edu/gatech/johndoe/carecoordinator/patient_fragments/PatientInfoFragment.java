package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.gatech.johndoe.carecoordinator.EHR;
import edu.gatech.johndoe.carecoordinator.R;


public class PatientInfoFragment extends Fragment {

    private ArrayList<EHR> ehr;

    public PatientInfoFragment() {
        //Dummy Data
        ehr = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            ehr.add(new EHR());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_info, container, false);
        ListView list = (ListView)view.findViewById(R.id.listView_ehr);
        EHRAdapter adapter = new EHRAdapter(getActivity(), R.id.ehr_list_view_row, ehr, getActivity().getSupportFragmentManager());
        list.setAdapter(adapter);
        return view;
    }

}
