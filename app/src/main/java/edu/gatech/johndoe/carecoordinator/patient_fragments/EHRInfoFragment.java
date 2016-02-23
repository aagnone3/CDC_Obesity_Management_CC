package edu.gatech.johndoe.carecoordinator.patient_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.gatech.johndoe.carecoordinator.R;

public class EHRInfoFragment extends Fragment {

    public EHRInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ehr_info, container, false);
        TextView temp = (TextView) view.findViewById(R.id.temp_label);
        temp.setText("EHR INFO & FINAL DECISION OPTIONS");
        return view;
    }

}
